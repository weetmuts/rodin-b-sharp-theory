/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.theory.core.IInferenceClause;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.ISCInferenceClause;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.sc.states.InferenceIdentifiers;
import org.eventb.theory.internal.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public abstract class TheoryInferenceClausesModule<C extends IInferenceClause, S extends ISCInferenceClause> 
extends SCProcessorModule{

	private ITypeEnvironment typeEnvironment;
	private FormulaFactory factory;
	private int index = TheoryPlugin.SC_STARTING_INDEX;
	protected InferenceIdentifiers inferenceIdentifiers;
	protected RuleAccuracyInfo ruleAccuracyInfo;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IInferenceRule rule = (IInferenceRule) element;
		ISCInferenceRule scRule = (ISCInferenceRule) target;
		C[] clauses = getClauses(rule);
		processClauses(clauses, rule, scRule, repository, monitor);
	}
	
	
	
	protected void processClauses(C[] clauses, IInferenceRule rule,
			ISCInferenceRule scRule, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		for(int i = 0 ; i < clauses.length; i++){
			C clause = clauses[i];
			if(!clause.hasPredicateString()){
				createProblemMarker(clause, EventBAttributes.PREDICATE_ATTRIBUTE, GraphProblem.PredicateUndefError);
				continue;
			}
			Predicate predicate = CoreUtilities.parseAndCheckPredicate(clause, factory, typeEnvironment, this);
			if(predicate != null && checkPredicate(predicate, clause)){
				S scClause = createSCClause(predicate, clause, scRule, repository, monitor);
				if(scClause != null){
					scClause.setPredicate(predicate, monitor);
					addIdentifiers(predicate);
				}
			}
		}
		
	}

	protected abstract boolean checkPredicate(Predicate predicate, C clause) throws CoreException;



	private S createSCClause(Predicate predicate, C clause,
			ISCInferenceRule scRule, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		S scClause = getSCClause(scRule, getPrefix()+index++);
		scClause.create(null, monitor);
		return scClause;
		
	}

	protected abstract void addIdentifiers(Predicate predicate);
	
	protected abstract S getSCClause(ISCInferenceRule parent, String name);
	
	protected abstract C[] getClauses(IInferenceRule rule) throws CoreException;
	
	protected abstract String getPrefix(); 
	
	@Override
	public void initModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		factory = repository.getFormulaFactory();	
		inferenceIdentifiers = (InferenceIdentifiers) repository.getState(InferenceIdentifiers.STATE_TYPE);
		ruleAccuracyInfo = (RuleAccuracyInfo) repository.getState(RuleAccuracyInfo.STATE_TYPE);
	}
	

	@Override
	public void endModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		factory = null;
		inferenceIdentifiers = null;
		ruleAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}
}
