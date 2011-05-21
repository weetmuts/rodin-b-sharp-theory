/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.rbp.inference.InferenceRuleAutoApplyer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * @author maamria
 *
 */
public class AutoInferer {
	
	private InferenceRuleAutoApplyer applyer;
	private IPOContext context;
	
	public AutoInferer(IPOContext context){
		this.context = context;
	}
	
	public void setFormulaFactory(FormulaFactory factory){
		applyer = new InferenceRuleAutoApplyer(factory, context);
	}
	
	public IAntecedent[] applyInferenceRules(IProverSequent sequent){
		return applyer.applyRules(sequent);
	}

}
