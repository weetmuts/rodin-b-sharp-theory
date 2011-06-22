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
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;

/**
 * 
 * @author maamria
 *
 */
public interface IProjectBaseEntry {

	public void setHasChanged(ISCTheoryRoot scRoot);
	
	public void setHasChanged(IDeployedTheoryRoot depRoot);
	
	public List<IDeployedRewriteRule> getExpressionRewriteRules(boolean automatic, Class<? extends Expression> clazz, 
			IEventBRoot root, FormulaFactory factory);
	
	public List<IDeployedRewriteRule> getPredicateRewriteRules(boolean automatic, Class<? extends Predicate> clazz, 
			IEventBRoot root,FormulaFactory factory);
	
	public IDeployedRewriteRule getExpressionRewriteRule(String ruleName, String theoryName, Class<? extends Expression> clazz, 
			IEventBRoot root, FormulaFactory factory);
	
	public IDeployedRewriteRule getPredicateRewriteRule(String ruleName, String theoryName, Class<? extends Predicate> clazz, 
			IEventBRoot root, FormulaFactory factory);
	
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic, ReasoningType type, IEventBRoot root,FormulaFactory factory);
	
	public IDeployedInferenceRule getInferenceRule(String theoryName, String ruleName, IEventBRoot root, FormulaFactory factory);
	
	public boolean managingMathExtensionsProject();
	
}
