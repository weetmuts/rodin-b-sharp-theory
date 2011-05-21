/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.rbp.inference.InferenceRuleManualApplyer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * @author maamria
 *
 */
public class ManualInferer {

	private InferenceRuleManualApplyer applyer ;
	private IPOContext context;
	
	public ManualInferer(IPOContext context){
		this.context = context;
	}
	
	public void setFormulaFactory(FormulaFactory factory){
		applyer = new InferenceRuleManualApplyer(factory, context);
	}
	
	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * @param pred to which the rule was applicable
	 * @param position 
	 * @param isGoal 
	 * @param theoryName
	 * @param ruleName
	 * @return the antecedents or <code>null</code> if the rule is not found or inapplicable
	 */
	public IAntecedent[] getAntecedents(IProverSequent sequent, Predicate pred, boolean forward, String theoryName, String ruleName){
		return applyer.applyRule(sequent, pred, forward, theoryName, ruleName);
	}
}
