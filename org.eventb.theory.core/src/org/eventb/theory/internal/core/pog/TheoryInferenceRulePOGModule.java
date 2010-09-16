/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.eventb.internal.core.pog.modules.UtilityModule;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryInferenceRulePOGModule extends UtilityModule {

	public static final IModuleType<TheoryInferenceRulePOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryInferenceRuleModule"); //$NON-NLS-1
	
	private final static String RULE_WD_SUFFIX = "/WD-INF";
	private final static String RULE_S_SUFFIX = "/S-INF";

	private final static String RULE_WD_DESC = "Well-Definedness of Inference Rule";
	private final static String RULE_SOUNDNESS_DESC = "Inference Rule Soundness";
	
	protected ITypeEnvironment typeEnvironment;
	protected POGNatureFactory natureFactory;
	protected DLib library;
	
	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		natureFactory = POGNatureFactory.getInstance();
		library = DLib.mDLib(factory);
	}
	
	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IPORoot target = repository.getTarget();
		ISCProofRulesBlock rulesBlock = (ISCProofRulesBlock) element;
		ISCInferenceRule[] inferenceRules = rulesBlock.getInferenceRules();
		for(ISCInferenceRule inferenceRule : inferenceRules){
			IPOGSource[] sources = new IPOGSource[] { makeSource(
					IPOSource.DEFAULT_ROLE, inferenceRule.getSource()) };
			ISCGiven givens[] = inferenceRule.getGivens();
			List<Predicate> givensPredicates = new ArrayList<Predicate>();
			for(ISCGiven given : givens){
				Predicate pred = given.getPredicate(factory, typeEnvironment);
				if(pred != null){
					givensPredicates.add(pred);
				}
			}
			ISCInfer infers[] = inferenceRule.getInfers();
			List<Predicate> infersPredicates = new ArrayList<Predicate>();
			for(ISCInfer infer : infers){
				Predicate pred = infer.getPredicate(factory, typeEnvironment);
				if(pred != null){
					infersPredicates.add(pred);
				}
			}
			Predicate conj1 = CoreUtilities.conjunctPredicates(givensPredicates, factory);
			Predicate conj2 = CoreUtilities.conjunctPredicates(infersPredicates, factory);
			Predicate poPredicate = library.makeImp(conj1, conj2);
			if(!isTrivial(poPredicate)){
				createPO(target, inferenceRule.getLabel()+ RULE_S_SUFFIX,
						natureFactory.getNature(RULE_SOUNDNESS_DESC),
						null, null,
						makePredicate(makeClosedPredicate(poPredicate, typeEnvironment), inferenceRule.getSource()),
						sources, new IPOGHint[0],
						true, monitor);
				Predicate wdPredicate = poPredicate.getWDPredicate(factory);
				if(!isTrivial(wdPredicate)){
					createPO(target, inferenceRule.getLabel()+ RULE_WD_SUFFIX,
							natureFactory.getNature(RULE_WD_DESC),
							null, null,
							makePredicate(makeClosedPredicate(wdPredicate, typeEnvironment), inferenceRule.getSource()),
							sources, new IPOGHint[0],
							true, monitor);
				}
			}
			
		}

	}

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		natureFactory = null;
		library = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
	
	protected Predicate makeClosedPredicate(
			Predicate predicate, 
			ITypeEnvironment typeEnvironment){
		List<FreeIdentifier> metavars = CoreUtilities.getMetavariables(typeEnvironment);
		FreeIdentifier[] predicateIdents = predicate.getFreeIdentifiers();
		List<FreeIdentifier> nonSetsIdents = new ArrayList<FreeIdentifier>();
		List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>();
		for(FreeIdentifier ident : predicateIdents){
			if(metavars.contains(ident)){
				nonSetsIdents.add(ident);
				decls.add(factory.makeBoundIdentDecl(ident.getName(), null, ident.getType()));
			}
		}
		
		if(nonSetsIdents.size() == 0){
			return predicate;
		}
		predicate = predicate.bindTheseIdents(nonSetsIdents, factory);
		predicate = DLib.mDLib(factory).makeUnivQuant(decls.toArray(new BoundIdentDecl[decls.size()]), predicate);
		return predicate;
	}

}
