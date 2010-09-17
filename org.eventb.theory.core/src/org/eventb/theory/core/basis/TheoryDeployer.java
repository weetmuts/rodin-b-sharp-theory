/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.internal.core.util.CoreUtilities.duplicate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 *
 */
public class TheoryDeployer implements ITheoryDeployer{

	protected ISCTheoryRoot theoryRoot;
	protected boolean force; 
	protected IDeploymentResult deploymentResult;
	protected String targetName;

	public TheoryDeployer(ISCTheoryRoot theoryRoot, String targetName ,boolean force){
		assert theoryRoot.exists();
		this.theoryRoot = theoryRoot;
		this.force = force;
		this.targetName =targetName;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		checkCancellationRequest(monitor);
		try{
			deploy(monitor);
		}catch(CoreException exception){
			deploymentResult =  new DeploymentResult(false, exception.getMessage());
		}
		
	}

	@Override
	public boolean deploy(IProgressMonitor monitor) throws CoreException {
		IRodinProject project = theoryRoot.getRodinProject();
		String theoryName = theoryRoot.getComponentName();
		boolean accurate = true;
		IRodinFile targetFile = project.getRodinFile(CoreUtilities.getDeployedTheoryFileName(targetName));
		// if force not requested
		if(targetFile.exists() && !force){
			deploymentResult = new DeploymentResult(false, 
					"Deployed theory " + theoryName+" already exists in the project "+ project.getElementName()+".");
			return false;
		}
		// if force requested
		if(targetFile.exists()){
			targetFile.delete(true, monitor);
		}
		targetFile.create(true, monitor);
		IDeployedTheoryRoot deployedTheoryRoot = (IDeployedTheoryRoot) targetFile.getRoot();
		if(!deployedTheoryRoot.exists()){
			deployedTheoryRoot.create(null, monitor);
		}
		// copy to root
		CoreUtilities.copyMathematicalExtensions(deployedTheoryRoot, theoryRoot, false);
		
		// copy proof blocks
		//////////////////////////////////
		ISCProofRulesBlock[] rulesBlocks = theoryRoot.getProofRulesBlocks();
		for(ISCProofRulesBlock rulesBlock: rulesBlocks){
			ISCProofRulesBlock newRulesBlock = CoreUtilities.duplicate(
					rulesBlock, ISCProofRulesBlock.ELEMENT_TYPE, deployedTheoryRoot, monitor);
			ISCMetavariable[] vars = rulesBlock.getMetavariables();
			for(ISCMetavariable var : vars){
				CoreUtilities.duplicate(var, ISCMetavariable.ELEMENT_TYPE, newRulesBlock, monitor);
			}
			ISCRewriteRule[] rewRules = rulesBlock.getRewriteRules();
			for (ISCRewriteRule rewRule : rewRules){
				ISCRewriteRule newRewRule = duplicate(rewRule, 
						ISCRewriteRule.ELEMENT_TYPE, newRulesBlock, monitor);
				ISCRewriteRuleRightHandSide[] ruleRHSs = rewRule.getRuleRHSs();
				for(ISCRewriteRuleRightHandSide rhs : ruleRHSs){
					duplicate(rhs, ISCRewriteRuleRightHandSide.ELEMENT_TYPE, newRewRule, monitor);
				}
			}
			ISCInferenceRule[] infRules = rulesBlock.getInferenceRules();
			for(ISCInferenceRule infRule : infRules){
				ISCInferenceRule newInfRule = duplicate(infRule, ISCInferenceRule.ELEMENT_TYPE, newRulesBlock, monitor);
				ISCInfer[] infers = infRule.getInfers();
				for(ISCInfer infer : infers){
					duplicate(infer, ISCInfer.ELEMENT_TYPE, newInfRule, monitor);
				}
				ISCGiven[] givens = infRule.getGivens();
				for(ISCGiven given : givens){
					duplicate(given, ISCGiven.ELEMENT_TYPE, newInfRule, monitor);
				}
			}
		}
		// copy theorems
		//////////////////////////////////
		ISCTheorem[] theorems = theoryRoot.getTheorems();
		for(ISCTheorem theorem: theorems ){
				duplicate(theorem, ISCTheorem.ELEMENT_TYPE, 
						deployedTheoryRoot, monitor);
		}
		
		deployedTheoryRoot.setAccuracy(accurate, monitor);
		targetFile.save(monitor, true);
		deploymentResult = new DeploymentResult(true, null);
		return true;
	}

	@Override
	public IDeploymentResult getDeploymentResult() {
		return deploymentResult;
	}
	
	protected void checkCancellationRequest(IProgressMonitor monitor){
		if (monitor.isCanceled()){
			
		}
	}

	
}
