/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * @author maamria
 *
 */
public final class DeployedInferenceRule extends AbstractDeployedRule implements IDeployedInferenceRule{

	private List<IDeployedGiven> givens;
	private IDeployedInfer infer;
	private boolean backward;
	private boolean forward;
	
	public DeployedInferenceRule(String projectName, String ruleName, String theoryName,
			boolean isAutomatic, boolean isInteractive, boolean isSound,
			String toolTip, String description, boolean backward, boolean forward, 
			List<IDeployedGiven> givens, IDeployedInfer infer, ITypeEnvironment typeEnv) {
		super(ruleName, theoryName,projectName, isAutomatic, isInteractive, isSound, toolTip,
				description, typeEnv);
		this.backward = backward;
		this.forward = forward;
		this.givens = unmodifiableList(givens);
		this.infer = infer;
	}

	@Override
	public List<IDeployedGiven> getGivens() {
		return givens;
	}

	
	@Override
	public IDeployedInfer getInfer() {
		return infer;
	}

	@Override
	public boolean isSuitableForBackwardReasoning() {
		return backward;
	}

	@Override
	public boolean isSuitableForForwardReasoning() {
		return forward;
	}

	@Override
	public boolean isSuitableForAllReasoning() {
		return backward && forward;
	}
	
	public boolean equals(Object o){
		if(o==null || !(o instanceof DeployedInferenceRule)){
			return false;
		}
		if(this == o){
			return true;
		}
		DeployedInferenceRule deployedInferenceRule = (DeployedInferenceRule)o;
		return ruleName.equals(deployedInferenceRule.ruleName)&& theoryName.equals(deployedInferenceRule.theoryName)&& 
			toolTip.equals(deployedInferenceRule.toolTip) && description.equals(deployedInferenceRule.description)&&
			givens.equals(deployedInferenceRule.givens) && infer.equals(deployedInferenceRule.infer) &&
			backward == deployedInferenceRule.backward && forward == deployedInferenceRule.forward;
	}
	
	public int hashCode(){
		return ProverUtilities.combineHashCode(
				ruleName, theoryName, givens, infer, 
				toolTip, description, new Boolean(backward && forward));
	}

	@Override
	public ReasoningType getReasoningType() {
		if(backward && forward)
			return ReasoningType.BACKWARD_AND_FORWARD;
		else if(forward)
			return ReasoningType.FORWARD;
		return ReasoningType.BACKWARD;
	}
	
	public String toString(){
		return ProverUtilities.toString(givens) + " |=> " + infer.toString() +" :: " +getReasoningType()+"\n";
	}

}
