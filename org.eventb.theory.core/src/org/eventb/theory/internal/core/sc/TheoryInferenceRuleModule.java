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
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.eventb.theory.internal.core.sc.states.InferenceIdentifiers;
import org.eventb.theory.internal.core.sc.states.RuleAccuracyInfo;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author maamria
 * 
 */
public class TheoryInferenceRuleModule extends
		TheoryRuleModule<IInferenceRule, ISCInferenceRule> {

	public static final IModuleType<TheoryInferenceRuleModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".theoryInferenceRuleModule");

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected void processRules(ISCInferenceRule[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			if (infos[i] != null && !infos[i].hasError()) {
				IInferenceRule rule = rules[i];
				String label = rule.getLabel();
				// 1- rule accuracy
				RuleAccuracyInfo ruleAccuracyInfo = new RuleAccuracyInfo();
				repository.setState(ruleAccuracyInfo);
				InferenceIdentifiers inferenceIdentifiers = new InferenceIdentifiers();
				repository.setState(inferenceIdentifiers);
				// call the children processor module
				initProcessorModules(rule, repository, null);
				processModules(rule, scRules[i], repository, monitor);
				endProcessorModules(rule, repository, null);
				if (scRules[i] != null)
					scRules[i].setAccuracy(ruleAccuracyInfo.isAccurate(),
							monitor);
				if(!inferenceIdentifiers.isRuleApplicable()){
					createProblemMarker(rule, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.InferenceRuleNotApplicableError);
					ruleAccuracyInfo.setNotAccurate();
				}
				else if (ruleAccuracyInfo.isAccurate()){
					ReasoningType reasoningType = null;
					if(inferenceIdentifiers.isRuleApplicableInBothDirections()){
						reasoningType = ReasoningType.BACKWARD_AND_FORWARD;
					}
					else if(inferenceIdentifiers.isRuleBackwardApplicable()){
						reasoningType = ReasoningType.BACKWARD;
					}
					else if(inferenceIdentifiers.isRuleForwardApplicable()){
						reasoningType = ReasoningType.FORWARD;
					}
					if(reasoningType != null){
						scRules[i].setReasoningType(reasoningType, monitor);
						createProblemMarker(rule, EventBAttributes.LABEL_ATTRIBUTE, 
								getInformationMessageFor(reasoningType), label);
					}
				}
				// if rule not accurate
				boolean ruleAccurate = ruleAccuracyInfo.isAccurate();
				if (scRules[i] != null)
					scRules[i].setAccuracy(ruleAccurate, monitor);
			}
			else {
				if (scRules[i] != null)
					scRules[i].setAccuracy(false, monitor);
				accuracyInfo.setNotAccurate();
			}

		}
		monitor.worked(1);

	}
	
	protected void checkClauses(IInferenceRule rule) throws CoreException{
		
	}

	@SuppressWarnings("restriction")
	@Override
	protected ILabelSymbolInfo[] fetchRules(String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		ILabelSymbolInfo[] symbolInfos = new ILabelSymbolInfo[rules.length];
		initFilterModules(repository, monitor);
		for (int i = 0; i < rules.length; i++) {
			symbolInfos[i] = fetchLabel(rules[i], theoryName, monitor);
			if (symbolInfos[i] == null)
				continue;
			if (!filterModules(rules[i], repository, monitor)) {
				symbolInfos[i].setError();
			}
		}
		endFilterModules(repository, monitor);
		return symbolInfos;
	}

	@Override
	protected String getMessage() {
		// TODO Auto-generated method stub
		return Messages.progress_TheoryInferenceRules;
	}

	@Override
	protected ISCInferenceRule[] createSCRulesArray() {
		// TODO Auto-generated method stub
		return new ISCInferenceRule[rules.length];
	}

	@Override
	protected IInferenceRule[] getRuleElements(IRodinElement element)
			throws CoreException {
		// TODO Auto-generated method stub
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		return rulesBlock.getInferenceRules();
	}

	@Override
	protected ISCInferenceRule cast(ILabeledElement scRule) {
		// TODO Auto-generated method stub
		return (ISCInferenceRule) scRule;
	}

	@Override
	protected ILabelSymbolInfo makeLocalRule(String symbol,
			ILabeledElement element, String component) throws CoreException {
		// TODO Auto-generated method stub
		return TheorySymbolFactory.getInstance().makeLocalInferenceRule(symbol,
				true, element, component);
	}

	/**
	 * Returns the information message appropriate for the given reasoning type.
	 * 
	 * @param type
	 *            the reasoning type
	 * @return the rodin problem
	 */
	protected final IRodinProblem getInformationMessageFor(
			ReasoningType type) {
		switch (type) {
		case BACKWARD:
			return TheoryGraphProblem.InferenceRuleBackward;
		case FORWARD:
			return TheoryGraphProblem.InferenceRuleForward;
		case BACKWARD_AND_FORWARD:
			return TheoryGraphProblem.InferenceRuleBoth;
		}
		return null;
	}

}
