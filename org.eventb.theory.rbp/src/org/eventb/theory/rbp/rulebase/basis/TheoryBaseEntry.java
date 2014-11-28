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

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.basis.NewOperatorDefinition;
import org.eventb.theory.core.basis.SCProofRulesBlock;
import org.eventb.theory.core.basis.SCRewriteRule;
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
	private ITypeEnvironmentBuilder typeEnv;

	/**
	 * All rules.
	 */
	private List<IGeneralRule> rewriteRules;
	private List<IGeneralRule> inferenceRules;
	private List<ISCTheorem> theorems;
	/**
	 * Definitional rules
	 */
	private List<IGeneralRule> definitionalRules;
	
	/**
	 * Mapped automatic rules by runtime class of formula.
	 */
	private Map<Class<?>, List<IGeneralRule>> autoRewRules;

	/**
	 * Mapped interactive rules by runtime class of formula.
	 */
	private Map<Class<?>, List<IGeneralRule>> interRewRules;

	/**
	 * Mapped inference rules by reasoning type runtime class.
	 */
	private Map<ReasoningType, List<IGeneralRule>> autoTypedInferenceMap;
	private Map<ReasoningType, List<IGeneralRule>> interTypedInferenceMap;

	public TheoryBaseEntry(R theoryRoot) {
		this.theoryRoot = theoryRoot;
		rewriteRules = new ArrayList<IGeneralRule>();
		inferenceRules = new ArrayList<IGeneralRule>();
		autoRewRules = new LinkedHashMap<Class<?>, List<IGeneralRule>>();
		interRewRules = new LinkedHashMap<Class<?>, List<IGeneralRule>>();
		
		autoTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IGeneralRule>>();
		interTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IGeneralRule>>();
		definitionalRules = new ArrayList<IGeneralRule>();
		// set to true to initiate an reload
		this.hasChanged = true;
	}

	protected void reload(FormulaFactory factory) {
		// clear all
		autoRewRules.clear();
		interRewRules.clear();
		autoTypedInferenceMap.clear();
		interTypedInferenceMap.clear();
		definitionalRules.clear();
		
		if (!theoryRoot.exists()) {
			// happens if theories have been undeployed
			return;
		}
		
		if (theoryRoot instanceof IDeployedTheoryRoot) {
			IDeployedTheoryFile file = new DeployedTheoryFile<R>(theoryRoot, factory);
			rewriteRules = (List<IGeneralRule>)(List<?>) file.getRewriteRules();
			inferenceRules = (List<IGeneralRule>)(List<?>) file.getInferenceRules();
			try {
				theorems = Arrays.asList(theoryRoot.getTheorems());
			} catch (RodinDBException e) {
				e.printStackTrace();
			}

			for (IGeneralRule rule : rewriteRules) {
				if(((IDeployedRewriteRule) rule).isDefinitional()){
					definitionalRules.add(rule);
				}
				Formula<?> leftHandSide = ((IDeployedRewriteRule) rule).getLeftHandSide();
				// only automatic + unconditional rewrites
				if (((IDeployedRewriteRule) rule).isAutomatic() && !((IDeployedRewriteRule) rule).isConditional()) {
					if (autoRewRules.get(leftHandSide.getClass()) == null) {
						List<IGeneralRule> list = new ArrayList<IGeneralRule>();
						autoRewRules.put(leftHandSide.getClass(), list);
					}
					autoRewRules.get(leftHandSide.getClass()).add(rule);
				} 
				// interactive rewrites + conditional
				if (((IDeployedRewriteRule) rule).isInteracive() || ((IDeployedRewriteRule) rule).isConditional()) {
					if (interRewRules.get(leftHandSide.getClass()) == null) {
						List<IGeneralRule> list = new ArrayList<IGeneralRule>();
						interRewRules.put(leftHandSide.getClass(), list);
					}
					interRewRules.get(leftHandSide.getClass()).add(rule);
				}
			}
			for (IGeneralRule rule : inferenceRules) {
				// automatic inference
				if (( (IDeployedInferenceRule) rule).isAutomatic()) {
					ReasoningType type = ( (IDeployedInferenceRule) rule).getReasoningType();
					if (!autoTypedInferenceMap.containsKey(type)) {
						List<IGeneralRule> list = new ArrayList<IGeneralRule>();
						autoTypedInferenceMap.put(type, list);
					}
					autoTypedInferenceMap.get(type).add(rule);
				} 
				// interactive inference
				if(( (IDeployedInferenceRule) rule).isInteracive()){
					ReasoningType type = ( (IDeployedInferenceRule) rule).getReasoningType();
					if (!interTypedInferenceMap.containsKey(type)) {
						List<IGeneralRule> list = new ArrayList<IGeneralRule>();
						interTypedInferenceMap.put(type, list);
					}
					interTypedInferenceMap.get(type).add(rule);
				}
			}
		}
		else {// if (theoryRoot instanceof ISCTheoryRoot))
			try {
				typeEnv = factory.makeTypeEnvironment();
				ISCTheoryRoot SCtheoryRoot = DatabaseUtilities.getSCTheory(theoryRoot.getElementName(), theoryRoot.getRodinProject());
				ISCTypeParameter[] types = SCtheoryRoot.getSCTypeParameters();
				for (ISCTypeParameter par : types) {
					typeEnv.addGivenSet(par.getIdentifier(factory).getName());
				}
				ISCProofRulesBlock[] blocks = theoryRoot.getProofRulesBlocks();
				for (ISCProofRulesBlock block : blocks) {
					rewriteRules.addAll(Arrays.asList(block.getRewriteRules()));
					inferenceRules.addAll(Arrays.asList(block.getInferenceRules()));
//					ISCMetavariable[] vars = block.getMetavariables();
//					for (ISCMetavariable var : vars) {
//						typeEnv.add(var.getIdentifier(factory));
//					}
				}
				
				
				theorems = Arrays.asList(theoryRoot.getTheorems());

				for (IGeneralRule rule : rewriteRules) {
					
					//update typeEnv
					ITypeEnvironmentBuilder augTypeEnvironment = typeEnv.makeBuilder();
					SCProofRulesBlock block = (SCProofRulesBlock) ((SCRewriteRule) rule).getParent();
					ISCMetavariable[] vars = block.getMetavariables();
					for (ISCMetavariable var : vars) {
						augTypeEnvironment.add(var.getIdentifier(factory));
					}
					if (block.getSource() instanceof NewOperatorDefinition) {
						ISCNewOperatorDefinition[] operatorDefinitions = theoryRoot.getSCNewOperatorDefinitions();
						for (ISCNewOperatorDefinition definition : operatorDefinitions) {
							if (definition.getLabel().equals(((SCRewriteRule) rule).getLabel().replaceFirst(block.getParent().getElementName()+".", ""))) {
								ISCOperatorArgument[] args = definition.getOperatorArguments();
								for (ISCOperatorArgument var : args) {
									augTypeEnvironment.add(var.getIdentifier(factory));
								}
								break;
							}
						}
					}
					
					if(( (ISCRewriteRule) rule).hasDefinitionalAttribute() && ( (ISCRewriteRule) rule).isDefinitional()){
						definitionalRules.add(rule);
					}
					Formula<?> leftHandSide = ( (ISCRewriteRule) rule).getSCFormula(factory, augTypeEnvironment);
					// only automatic + unconditional rewrites
					if (( (ISCRewriteRule) rule).isAutomatic() && !ProverUtilities.isConditional((ISCRewriteRule)rule, factory, augTypeEnvironment)) {
						if (autoRewRules.get(leftHandSide.getClass()) == null) {
							List<IGeneralRule> list = new ArrayList<IGeneralRule>();
							autoRewRules.put(leftHandSide.getClass(), list);
						}
						autoRewRules.get(leftHandSide.getClass()).add(rule);
					} 
					// interactive rewrites + conditional
					if (( (ISCRewriteRule) rule).isInteractive() || ProverUtilities.isConditional((ISCRewriteRule)rule, factory, augTypeEnvironment)) {
						if (interRewRules.get(leftHandSide.getClass()) == null) {
							List<IGeneralRule> list = new ArrayList<IGeneralRule>();
							interRewRules.put(leftHandSide.getClass(), list);
						}
						interRewRules.get(leftHandSide.getClass()).add(rule);
					}
				}
				for (IGeneralRule rule : inferenceRules) {
					// automatic inference
					if (( (ISCInferenceRule) rule).isAutomatic()) {
						ReasoningType type = ProverUtilities.getReasoningType((ISCInferenceRule)rule, ( (ISCInferenceRule) rule).isSuitableForBackwardReasoning(), ( (ISCInferenceRule) rule).isSuitableForForwardReasoning());
						if (!autoTypedInferenceMap.containsKey(type)) {
							List<IGeneralRule> list = new ArrayList<IGeneralRule>();
							autoTypedInferenceMap.put(type, list);
						}
						autoTypedInferenceMap.get(type).add(rule);
					} 
					// interactive inference
					if(( (ISCInferenceRule) rule).isInteractive()){
						ReasoningType type = ProverUtilities.getReasoningType((ISCInferenceRule)rule, ( (ISCInferenceRule) rule).isSuitableForBackwardReasoning(), ( (ISCInferenceRule) rule).isSuitableForForwardReasoning());
						if (!interTypedInferenceMap.containsKey(type)) {
							List<IGeneralRule> list = new ArrayList<IGeneralRule>();
							interTypedInferenceMap.put(type, list);
						}
						interTypedInferenceMap.get(type).add(rule);
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
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
	public List<IGeneralRule> getRewriteRules(boolean automatic, Class<?> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (automatic) {
			if (autoRewRules.containsKey(clazz))
				return getList(autoRewRules.get(clazz));
		} else {
			if (interRewRules.containsKey(clazz))
				return getList(interRewRules.get(clazz));
		}
		return new ArrayList<IGeneralRule>();
	}

	@Override
	public IGeneralRule getRewriteRule(String ruleName, Class<?> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (interRewRules.get(clazz) == null) {
			return null;
		}
		for (IGeneralRule rule : interRewRules.get(clazz)) {
			String name = null;
			if (rule instanceof IDeployedRewriteRule) {
				name = ((IDeployedRewriteRule) rule).getRuleName();
			}
			else {
				try { // if (rule instanceof ISCRewriteRule)
					name = ((ISCRewriteRule) rule).getLabel();
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (name.equals(ruleName)) {
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
	public List<IGeneralRule> getInferenceRules(boolean automatic, ReasoningType type, FormulaFactory factory) {
		checkStatus(factory);
		List<IGeneralRule> toReturn = new ArrayList<IGeneralRule>();
		if (automatic) {
			if (!autoTypedInferenceMap.containsKey(type)) {
				List<IGeneralRule> list = new ArrayList<IGeneralRule>();
				autoTypedInferenceMap.put(type, list);
			}
			List<IGeneralRule> bfRules = ProverUtilities.safeList(autoTypedInferenceMap.get(ReasoningType.BACKWARD_AND_FORWARD));
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
				List<IGeneralRule> list = new ArrayList<IGeneralRule>();
				interTypedInferenceMap.put(type, list);
			}
			List<IGeneralRule> bfRules = ProverUtilities.safeList(interTypedInferenceMap.get(ReasoningType.BACKWARD_AND_FORWARD));
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
	public IGeneralRule getInferenceRule(String ruleName, FormulaFactory factory) {
		checkStatus(factory);
		for (IGeneralRule rule : inferenceRules) {
			String name = null;
			if (rule instanceof IDeployedInferenceRule) {
				name = ((IDeployedInferenceRule) rule).getRuleName();
			}
			else { // if (rule instanceof ISCInferenceRule)
				try {
					name = ((ISCInferenceRule) rule).getLabel();
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (name.equals(ruleName)) {
				return rule;
			}
		}
		return null;
	}

	@Override
	public List<IGeneralRule> getDefinitionalRules(FormulaFactory factory) {
		checkStatus(factory);
		return definitionalRules;
	}

	@Override
	public List<IGeneralRule> getDefinitionalRules(Class<?> clazz, FormulaFactory factory) {
		checkStatus(factory);
		List<IGeneralRule> rewriteRules = new ArrayList<IGeneralRule>();
		for (IGeneralRule rule : definitionalRules){
			Formula<?> lhs = null;
			if (rule instanceof IDeployedRewriteRule) {
				lhs = ((IDeployedRewriteRule) rule).getLeftHandSide();
			}
			else {
				try { // if (rule instanceof ISCRewriteRule)
					
					//update typeEnv
					ITypeEnvironmentBuilder augTypeEnvironment = typeEnv.makeBuilder();
					SCProofRulesBlock block = (SCProofRulesBlock) ((SCRewriteRule) rule).getParent();
					ISCMetavariable[] vars = block.getMetavariables();
					for (ISCMetavariable var : vars) {
						augTypeEnvironment.add(var.getIdentifier(factory));
					}
					if (block.getSource() instanceof NewOperatorDefinition) {
						ISCNewOperatorDefinition[] operatorDefinitions = theoryRoot.getSCNewOperatorDefinitions();
						for (ISCNewOperatorDefinition definition : operatorDefinitions) {
							if (definition.getLabel().equals(((SCRewriteRule) rule).getLabel().replaceFirst(block.getParent().getElementName()+".", ""))) {
								ISCOperatorArgument[] args = definition.getOperatorArguments();
								for (ISCOperatorArgument var : args) {
									augTypeEnvironment.add(var.getIdentifier(factory));
								}
								break;
							}
						}
					}
					lhs = ((ISCRewriteRule) rule).getSCFormula(factory, augTypeEnvironment);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(lhs.getClass().equals(clazz)){
				rewriteRules.add(rule);
			}
		}
		return rewriteRules;
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
