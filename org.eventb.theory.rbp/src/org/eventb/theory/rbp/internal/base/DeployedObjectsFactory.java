/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.base;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.rbp.base.IRuleBaseManager;

/**
 * @author maamria
 *
 */
public class DeployedObjectsFactory {
	
	public static List<IDeployedRewriteRule> getDeployedRewriteRules(ISCProofRulesBlock block,
			FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException{
		List<IDeployedRewriteRule> result = new ArrayList<IDeployedRewriteRule>();
		ITypeEnvironment augTypeEnvironment = typeEnvironment.clone();
		ISCMetavariable[] vars = block.getMetavariables();
		for (ISCMetavariable var : vars){
			augTypeEnvironment.add(var.getIdentifier(factory));
		}
		ISCRewriteRule[] rules = block.getRewriteRules();
		for (ISCRewriteRule rule : rules){
			if(!IRuleBaseManager.loadDefinitionalRules && rule.hasDefinitionalAttribute() &&rule.isDefinitional()){
				continue;
			}
			result.add(getDeployedRewriteRule(rule, factory, augTypeEnvironment));
		}
		
		return result;
	}
	
	public static List<IDeployedInferenceRule> getDeployedInferenceRules(ISCProofRulesBlock block,
			FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException{
		List<IDeployedInferenceRule> result = new ArrayList<IDeployedInferenceRule>();
		ITypeEnvironment augTypeEnvironment = typeEnvironment.clone();
		ISCMetavariable[] vars = block.getMetavariables();
		for (ISCMetavariable var : vars){
			augTypeEnvironment.add(var.getIdentifier(factory));
		}
		ISCInferenceRule[] rules = block.getInferenceRules();
		for (ISCInferenceRule rule : rules){
			result.add(getDeployedInferenceRule(rule, factory, augTypeEnvironment));
		}
		
		return result;
	}

	public static IDeployedInferenceRule getDeployedInferenceRule(ISCInferenceRule rule,
			FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException{
		String ruleName = rule.getElementName();
		String theoryName = rule.getAncestor(IDeployedTheoryRoot.ELEMENT_TYPE).getComponentName();
		boolean isAutomatic = rule.isAutomatic();
		boolean isInteractive = rule.isInteractive();
		boolean isSound = rule.isValidated();
		String toolTip = rule.getDescription();
		String description = rule.getDescription();
		
		List<IDeployedGiven> givens = new ArrayList<IDeployedGiven>();
		for (ISCGiven given : rule.getGivens()){
			givens.add(getDeployedGiven(given, factory, typeEnvironment));
		}
		IDeployedInfer infer = getDeployedInfer(rule.getInfers()[0], factory, typeEnvironment);
		
		IDeployedInferenceRule infRule = new DeployedInferenceRule(
				ruleName, theoryName, isAutomatic, isInteractive, 
				isSound, toolTip, description, rule.isSuitableForBackwardReasoning(),
				rule.isSuitableForForwardReasoning(), givens, infer, typeEnvironment);
		return infRule;
	}
	
	
	public static IDeployedRewriteRule getDeployedRewriteRule(ISCRewriteRule rule, 
			FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException{
		String ruleName = rule.getElementName();
		String theoryName = rule.getAncestor(IDeployedTheoryRoot.ELEMENT_TYPE).getComponentName();
		Formula<?> lhs = rule.getSCFormula(factory, typeEnvironment);
		List<IDeployedRuleRHS> ruleRHSs = new ArrayList<IDeployedRuleRHS>();
		for (ISCRewriteRuleRightHandSide rhs : rule.getRuleRHSs()){
			ruleRHSs.add(getDeployedRuleRHS(rhs, factory, typeEnvironment));
		}
		boolean isAutomatic = rule.isAutomatic();
		boolean isInteractive = rule.isInteractive();
		boolean isComplete = rule.isComplete();
		boolean isSound = rule.isValidated();
		String toolTip = rule.getDescription();
		String description = rule.getDescription();
		
		IDeployedRewriteRule depRule = new DeployedRewriteRule(
				ruleName, theoryName, lhs, ruleRHSs, isAutomatic, 
				isInteractive, isComplete, isSound, toolTip, 
				description, typeEnvironment);
		return depRule;
	}
	
	public static IDeployedRuleRHS getDeployedRuleRHS(ISCRewriteRuleRightHandSide rhs,
			FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException{
		String name = rhs.getElementName();
		Formula<?> rhsForm = rhs.getSCFormula(factory, typeEnvironment);
		Predicate cond = rhs.getPredicate(factory, typeEnvironment);
		IDeployedRuleRHS depRHS = new DeployedRuleRHS(name, rhsForm, cond);
		return depRHS;
	}
	
	public static IDeployedGiven getDeployedGiven(ISCGiven given, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) throws CoreException{
		IDeployedGiven dep = new DeployedGiven(given.getPredicate(factory, typeEnvironment));
		return dep;
	}
	
	public static IDeployedInfer getDeployedInfer(ISCInfer given, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) throws CoreException{
		IDeployedInfer dep = new DeployedInfer(given.getPredicate(factory, typeEnvironment));
		return dep;
	}
	
}
