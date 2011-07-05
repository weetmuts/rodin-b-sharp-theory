/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

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
import org.eventb.theory.core.sc.states.InferenceIdentifiers;
import org.eventb.theory.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public abstract class InferenceClausesModule<C extends IInferenceClause, S extends ISCInferenceClause> 
extends SCProcessorModule{

	protected ITypeEnvironment typeEnvironment;
	protected FormulaFactory factory;
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
	
	protected final void processClauses(C[] clauses, IInferenceRule rule,
			ISCInferenceRule scRule, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		boolean accurate= true;
		for(int i = 0 ; i < clauses.length; i++){
			C clause = clauses[i];
			if(!clause.hasPredicateString()){
				createProblemMarker(clause, EventBAttributes.PREDICATE_ATTRIBUTE, GraphProblem.PredicateUndefError);
				accurate = false;
				continue;
			}
			Predicate predicate = CoreUtilities.parseAndCheckPredicate(clause, factory, typeEnvironment, this);
			if(predicate != null && checkPredicate(predicate, clause)){
				S scClause = createSCClause(predicate, clause, scRule, repository, monitor);
				if(scClause != null){
					scClause.setPredicate(predicate, monitor);
					addIdentifiers(predicate);
				}
				else {
					accurate = false;
				}
			}
			else {
				accurate = false;
			}
		}
		if(!accurate)
			ruleAccuracyInfo.setNotAccurate();
		
	}

	protected abstract boolean checkPredicate(Predicate predicate, C clause) throws CoreException;

	protected abstract void addIdentifiers(Predicate predicate) throws CoreException;
	
	protected abstract S getSCClause(ISCInferenceRule parent, String name) throws CoreException;
	
	protected abstract C[] getClauses(IInferenceRule rule) throws CoreException;
	
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
	
	private S createSCClause(Predicate predicate, C clause,
			ISCInferenceRule scRule, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		S scClause = getSCClause(scRule, clause.getElementName());
		scClause.create(null, monitor);
		return scClause;
		
	}
}
