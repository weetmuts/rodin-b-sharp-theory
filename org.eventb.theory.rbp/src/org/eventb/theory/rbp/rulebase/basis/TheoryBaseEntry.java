/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.rulebase.ITheoryBaseEntry;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * 
 */
public class TheoryBaseEntry<R extends IEventBRoot & IFormulaExtensionsSource & IExtensionRulesSource> implements ITheoryBaseEntry<R> {

	private boolean hasChanged;
	private R theoryRoot;

	/**
	 * All rules.
	 */
	private List<IDeployedRewriteRule> rewriteRules;
	private List<IDeployedInferenceRule> inferenceRules;
	private List<ISCTheorem> theorems;
	/**
	 * Definitional rules
	 */
	private List<IDeployedRewriteRule> definitionalRules;
	
	/**
	 * Mapped automatic rules by runtime class of formula.
	 */
	private Map<Class<?>, List<IDeployedRewriteRule>> autoRewRules;

	/**
	 * Mapped interactive rules by runtime class of formula.
	 */
	private Map<Class<?>, List<IDeployedRewriteRule>> interRewRules;

	/**
	 * Mapped inference rules by reasoning type runtime class.
	 */
	private Map<ReasoningType, List<IDeployedInferenceRule>> autoTypedInferenceMap;
	private Map<ReasoningType, List<IDeployedInferenceRule>> interTypedInferenceMap;

	public TheoryBaseEntry(R theoryRoot) {
		this.theoryRoot = theoryRoot;
		autoRewRules = new LinkedHashMap<Class<?>, List<IDeployedRewriteRule>>();
		interRewRules = new LinkedHashMap<Class<?>, List<IDeployedRewriteRule>>();
		
		autoTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IDeployedInferenceRule>>();
		interTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IDeployedInferenceRule>>();
		definitionalRules = new ArrayList<IDeployedRewriteRule>();
		// set to true to initiate an reload
		this.hasChanged = true;
	}

	protected void reload(FormulaFactory factory) {
		IDeployedTheoryFile file = new DeployedTheoryFile<R>(theoryRoot, factory);
		rewriteRules = file.getRewriteRules();
		inferenceRules = file.getInferenceRules();
		try {
			theorems = Arrays.asList(theoryRoot.getTheorems());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		// clear all
		autoRewRules.clear();
		interRewRules.clear();
		autoTypedInferenceMap.clear();
		interTypedInferenceMap.clear();
		definitionalRules.clear();

		for (IDeployedRewriteRule rule : rewriteRules) {
			if(rule.isDefinitional()){
				definitionalRules.add(rule);
			}
			Formula<?> leftHandSide = rule.getLeftHandSide();
			// only automatic + unconditional rewrites
			if (rule.isAutomatic() && !rule.isConditional()) {
				if (autoRewRules.get(leftHandSide.getClass()) == null) {
					List<IDeployedRewriteRule> list = new ArrayList<IDeployedRewriteRule>();
					autoRewRules.put(leftHandSide.getClass(), list);
				}
				autoRewRules.get(leftHandSide.getClass()).add(rule);
			} 
			// interactive rewrites + conditional
			if (rule.isInteracive() || rule.isConditional()) {
				if (interRewRules.get(leftHandSide.getClass()) == null) {
					List<IDeployedRewriteRule> list = new ArrayList<IDeployedRewriteRule>();
					interRewRules.put(leftHandSide.getClass(), list);
				}
				interRewRules.get(leftHandSide.getClass()).add(rule);
			}
		}
		for (IDeployedInferenceRule rule : inferenceRules) {
			// automatic inference
			if (rule.isAutomatic()) {
				ReasoningType type = rule.getReasoningType();
				if (!autoTypedInferenceMap.containsKey(type)) {
					List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
					autoTypedInferenceMap.put(type, list);
				}
				autoTypedInferenceMap.get(type).add(rule);
			} 
			// interactive inference
			if(rule.isInteracive()){
				ReasoningType type = rule.getReasoningType();
				if (!interTypedInferenceMap.containsKey(type)) {
					List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
					interTypedInferenceMap.put(type, list);
				}
				interTypedInferenceMap.get(type).add(rule);
			}
		}
	}

	protected void checkStatus(FormulaFactory factory) {
		if (hasChanged) {
			reload(factory);
			setHasChanged(false);
		}
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
	
	@Override
	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic, Class<?> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (automatic) {
			if (autoRewRules.containsKey(clazz))
				return getList(autoRewRules.get(clazz));
		} else {
			if (interRewRules.containsKey(clazz))
				return getList(interRewRules.get(clazz));
		}
		return new ArrayList<IDeployedRewriteRule>();
	}

	@Override
	public IDeployedRewriteRule getRewriteRule(String ruleName, Class<?> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (interRewRules.get(clazz) == null) {
			return null;
		}
		for (IDeployedRewriteRule rule : interRewRules.get(clazz)) {
			if (rule.getRuleName().equals(ruleName)) {
				return rule;
			}
		}
		return null;
	}
	
	@Override
	public List<ISCTheorem> getSCTheorems(FormulaFactory factory) {
		checkStatus(factory);
		return theorems;
	}

	@Override
	public List<ISCTheorem> getSCTheorems(boolean axm, int order, FormulaFactory factory) {
		List<ISCTheorem> SCTheorems = new ArrayList<ISCTheorem>();
		checkStatus(factory);
		try {
		for (ISCTheorem SCTheorem : theorems){	
			if (axm && !(SCTheorem.getSource() instanceof ISCAxiomaticDefinitionAxiom)) {
				continue;
			}
			
			if (!axm && (SCTheorem.getSource() instanceof ISCAxiomaticDefinitionAxiom)) {
				SCTheorems.add(SCTheorem);
				continue;
			}
			
			if (SCTheorem.getOrder() < order)
				SCTheorems.add(SCTheorem);
		}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return SCTheorems;
	}

	@Override
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic, ReasoningType type, FormulaFactory factory) {
		checkStatus(factory);
		List<IDeployedInferenceRule> toReturn = new ArrayList<IDeployedInferenceRule>();
		if (automatic) {
			if (!autoTypedInferenceMap.containsKey(type)) {
				List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
				autoTypedInferenceMap.put(type, list);
			}
			List<IDeployedInferenceRule> bfRules = ProverUtilities.safeList(autoTypedInferenceMap.get(ReasoningType.BACKWARD_AND_FORWARD));
			switch (type) {
			case BACKWARD:
			case FORWARD: {
				toReturn.addAll(ProverUtilities.safeList(autoTypedInferenceMap.get(type)));
				toReturn.addAll(bfRules);
				break;
			}
			case BACKWARD_AND_FORWARD: {
				toReturn.addAll(bfRules);
			}
			}
		} else {
			if (!interTypedInferenceMap.containsKey(type)) {
				List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
				interTypedInferenceMap.put(type, list);
			}
			List<IDeployedInferenceRule> bfRules = ProverUtilities.safeList(interTypedInferenceMap.get(ReasoningType.BACKWARD_AND_FORWARD));
			switch (type) {
			case BACKWARD:
			case FORWARD: {
				toReturn.addAll(ProverUtilities.safeList(interTypedInferenceMap.get(type)));
				toReturn.addAll(bfRules);
				break;
			}
			case BACKWARD_AND_FORWARD: {
				if (bfRules != null)
					toReturn.addAll(bfRules);
			}
			}
		}
		return toReturn;
	}

	@Override
	public IDeployedInferenceRule getInferenceRule(String ruleName, FormulaFactory factory) {
		checkStatus(factory);
		for (IDeployedInferenceRule rule : inferenceRules) {
			if (rule.getRuleName().equals(ruleName)) {
				return rule;
			}
		}
		return null;
	}

	@Override
	public List<IDeployedRewriteRule> getDefinitionalRules(FormulaFactory factory) {
		checkStatus(factory);
		return definitionalRules;
	}

	@Override
	public List<IDeployedRewriteRule> getDefinitionalRules(Class<?> clazz, FormulaFactory factory) {
		checkStatus(factory);
		List<IDeployedRewriteRule> deployedRewriteRules = new ArrayList<IDeployedRewriteRule>();
		for (IDeployedRewriteRule rule : definitionalRules){
			if(rule.getLeftHandSide().getClass().equals(clazz)){
				deployedRewriteRules.add(rule);
			}
		}
		return deployedRewriteRules;
	}
	
	/**
	 * Returns a list with same element but different reference.
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param list
	 *            the original list
	 * @return same list with different reference
	 */
	private <E> List<E> getList(List<E> list) {
		return new ArrayList<E>(list);
	}
}
