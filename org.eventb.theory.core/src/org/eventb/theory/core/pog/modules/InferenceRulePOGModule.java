/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.pog.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
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
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class InferenceRulePOGModule extends UtilityPOGModule {

	private final IModuleType<InferenceRulePOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".inferenceRulePOGModule"); //$NON-NLS-1

	private final static String RULE_SB_SUFFIX = "/S-INF_B";
	private final static String RULE_SF_SUFFIX = "/S-INF_F";
	private final static String RULE_WDB_SUFFIX = "/WD-INF_B";
	private final static String RULE_WDF_SUFFIX = "/WD-INF_F";

	private final static String RULE_SOUNDNESS_DESC_F = "Inference FORWARD Rule Soundness";
	private final static String RULE_SOUNDNESS_DESC_B = "Inference BACKWARD Rule Soundness";
	private final static String RULE_WD_DESC_F = "Inference FORWARD Rule WD";
	private final static String RULE_WD_DESC_B = "Inference BACKWARD Rule WD";

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
		IPOPredicateSet hyp = target
				.getPredicateSet(TypeParametersPOGModule.ABS_HYP_NAME);
		for (ISCInferenceRule inferenceRule : inferenceRules) {
			if (!inferenceRule.isAccurate()) {
				continue;
			}
			IPOGSource[] sources = new IPOGSource[] { makeSource(
					IPOSource.DEFAULT_ROLE, inferenceRule.getSource()) };
			ISCGiven givens[] = inferenceRule.getGivens();
			List<Predicate> givensPredicates = new ArrayList<Predicate>();
			List<Predicate> givensPredicatesWDs = new ArrayList<Predicate>();
			for (ISCGiven given : givens) {
				Predicate pred = given.getPredicate(factory, typeEnvironment);
				if (pred != null) {
					givensPredicates.add(pred);
					givensPredicatesWDs.add(pred.getWDPredicate(factory));
				}
			}
			ISCInfer infers[] = inferenceRule.getInfers();
			List<Predicate> infersPredicates = new ArrayList<Predicate>();
			List<Predicate> infersPredicatesWDs = new ArrayList<Predicate>();
			for (ISCInfer infer : infers) {
				Predicate pred = infer.getPredicate(factory, typeEnvironment);
				if (pred != null) {
					infersPredicates.add(pred);
					infersPredicatesWDs.add(pred.getWDPredicate(factory));
				}
			}
			Predicate conj1 = MathExtensionsUtilities.conjunctPredicates(
					givensPredicates, factory);
			Predicate conj1WD = MathExtensionsUtilities.conjunctPredicates(
					givensPredicatesWDs, factory);
			Predicate conj2 = MathExtensionsUtilities.conjunctPredicates(
					infersPredicates, factory);
			Predicate conj2WD = MathExtensionsUtilities.conjunctPredicates(
					infersPredicatesWDs, factory);
			Predicate poPredicate = library.makeImp(conj1, conj2);
			if (!isTrivial(poPredicate)) {
				String label = inferenceRule.getLabel();
				if (inferenceRule.isSuitableForBackwardReasoning()) {
					String poName = label
							+ RULE_SB_SUFFIX;
					Predicate finalPO = library.makeImp(conj2WD, poPredicate);
					createPO(
							target,
							poName,
							natureFactory.getNature(RULE_SOUNDNESS_DESC_B),
							hyp,
							EMPTY_PREDICATES,
							makePredicate(
									makeClosedPredicate(finalPO,
											typeEnvironment), inferenceRule
											.getSource()),
							sources,
							new IPOGHint[] { getLocalHypothesisSelectionHint(
									target, poName, hyp) }, true, monitor);
					if (!isTrivial(conj1WD)) {
						String poWDName = label
								+ RULE_WDB_SUFFIX;
						Predicate finalWDPO = library.makeImp(conj2WD, conj1WD);
						createPO(
								target,
								poWDName,
								natureFactory.getNature(RULE_WD_DESC_B),
								hyp,
								EMPTY_PREDICATES,
								makePredicate(
										makeClosedPredicate(finalWDPO,
												typeEnvironment), inferenceRule
												.getSource()),
								sources,
								new IPOGHint[] { getLocalHypothesisSelectionHint(
										target, poWDName, hyp) }, true, monitor);
					}

				}
				if (inferenceRule.isSuitableForForwardReasoning()) {
					String poName = label
							+ RULE_SF_SUFFIX;
					Predicate finalPO = library.makeImp(conj1WD, poPredicate);
					createPO(
							target,
							poName,
							natureFactory.getNature(RULE_SOUNDNESS_DESC_F),
							hyp,
							EMPTY_PREDICATES,
							makePredicate(
									makeClosedPredicate(finalPO,
											typeEnvironment), inferenceRule
											.getSource()),
							sources,
							new IPOGHint[] { getLocalHypothesisSelectionHint(
									target, poName, hyp) }, true, monitor);
					if (!isTrivial(conj2WD)) {
						String poWDName = label
								+ RULE_WDF_SUFFIX;
						Predicate finalWDPO = library.makeImp(
								library.makeConj(conj1WD, conj1), conj2WD);
						createPO(
								target,
								poWDName,
								natureFactory.getNature(RULE_WD_DESC_F),
								hyp,
								EMPTY_PREDICATES,
								makePredicate(
										makeClosedPredicate(finalWDPO,
												typeEnvironment), inferenceRule
												.getSource()),
								sources,
								new IPOGHint[] { getLocalHypothesisSelectionHint(
										target, poWDName, hyp) }, true, monitor);
					}
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

	protected Predicate makeClosedPredicate(Predicate predicate,
			ITypeEnvironment typeEnvironment) {
		List<FreeIdentifier> metavars = getMetavariables(typeEnvironment);
		FreeIdentifier[] predicateIdents = predicate.getFreeIdentifiers();
		List<FreeIdentifier> nonSetsIdents = new ArrayList<FreeIdentifier>();
		List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>();
		for (FreeIdentifier ident : predicateIdents) {
			if (metavars.contains(ident)) {
				nonSetsIdents.add(ident);
				decls.add(factory.makeBoundIdentDecl(ident.getName(), null,
						ident.getType()));
			}
		}

		if (nonSetsIdents.size() == 0) {
			return predicate;
		}
		predicate = predicate.bindTheseIdents(nonSetsIdents, factory);
		predicate = DLib.mDLib(factory).makeUnivQuant(
				decls.toArray(new BoundIdentDecl[decls.size()]), predicate);
		return predicate;
	}

}
