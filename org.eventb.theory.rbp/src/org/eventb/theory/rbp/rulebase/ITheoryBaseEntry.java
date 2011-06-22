/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;

import java.util.List;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;

/**
 * Common protocol for a theory entry that can be queried for various rules it holds.
 * 
 * <p> Each query must supply a <code>FormulaFactory</code> so that parsing is carried out if necessary.
 * 
 * <p> It might be useful to just call <code>IEventbRoot.getFormulaFactory()</code> instead of passing a formula factory.
 * 
 * @author maamria
 * @since 1.0
 *
 */
public interface ITheoryBaseEntry<R extends IEventBRoot & IFormulaExtensionsSource & IExtensionRulesSource> {

	/**
	 * Returns the list of rewrite rules satisfying the given criteria.
	 * 
	 * @param automatic
	 * @param clazz
	 * @return rewrite rules
	 */
	public List<IDeployedRewriteRule> getExpressionRewriteRules(boolean automatic, Class<? extends Expression> clazz, FormulaFactory factory);
	
	/**
	 * Returns the list of rewrite rules satisfying the given criteria.
	 * 
	 * @param automatic
	 * @param clazz
	 * @return rewrite rules
	 */
	public List<IDeployedRewriteRule> getPredicateRewriteRules(boolean automatic, Class<? extends Predicate> clazz, FormulaFactory factory);
	
	/**
	 * Returns the list of inference rules satisfying the given criteria.
	 * 
	 * @param automatic
	 * @param type
	 *            reasoning type
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic, ReasoningType type, FormulaFactory factory);
	
	/**
	 * Returns the interactive deployed inference rule by the given name.
	 * 
	 * @param ruleName
	 *            the name of the rule
	 * @return the deployed rule
	 */
	public IDeployedInferenceRule getInferenceRule(String ruleName, FormulaFactory factory);
	
	/**
	 * Returns the interactive rule specified by its name as well as the runtime class of its lhs.
	 * 
	 * @param ruleName
	 *            name of the rule
	 * @param clazz
	 *            the class of the lhs of the rule
	 * @return the rule or <code>null</code> if not found
	 */
	
	public IDeployedRewriteRule getExpressionRewriteRule(String ruleName, Class<? extends Expression> clazz, FormulaFactory factory);
	
	/**
	 * Returns the interactive rule specified by its name as well as the runtime class of its lhs.
	 * 
	 * @param ruleName
	 *            name of the rule
	 * @param clazz
	 *            the class of the lhs of the rule
	 * @return the rule or <code>null</code> if not found
	 */
	
	public IDeployedRewriteRule getPredicateRewriteRule(String ruleName, Class<? extends Predicate> clazz, FormulaFactory factory);
	
	public boolean hasChanged();
	
	public void setHasChanged(boolean hasChanged) ;
}
