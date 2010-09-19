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
import org.eventb.core.EventBAttributes;
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
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
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

	public TheoryDeployer(ISCTheoryRoot theoryRoot, boolean force){
		assert theoryRoot.exists();
		this.theoryRoot = theoryRoot;
		this.force = force;
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
	public synchronized boolean deploy(IProgressMonitor monitor) throws CoreException {
		IRodinProject project = theoryRoot.getRodinProject();
		String theoryName = theoryRoot.getComponentName();
		boolean accurate = true;
		IRodinFile targetFile = project.getRodinFile(CoreUtilities.getDeployedTheoryFileName(theoryName));
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
		CoreUtilities.copyMathematicalExtensions(deployedTheoryRoot, theoryRoot);
		
		// copy proof blocks
		//////////////////////////////////
		IAttributeType[] toIgnore = new IAttributeType[]{EventBAttributes.SOURCE_ATTRIBUTE, TheoryAttributes.HAS_ERROR_ATTRIBUTE};
		ISCProofRulesBlock[] rulesBlocks = theoryRoot.getProofRulesBlocks();
		for(ISCProofRulesBlock rulesBlock: rulesBlocks){
			ISCProofRulesBlock newRulesBlock = CoreUtilities.duplicate(
					rulesBlock, ISCProofRulesBlock.ELEMENT_TYPE, deployedTheoryRoot, monitor, toIgnore);
			ISCMetavariable[] vars = rulesBlock.getMetavariables();
			for(ISCMetavariable var : vars){
				CoreUtilities.duplicate(var, ISCMetavariable.ELEMENT_TYPE, newRulesBlock, monitor, toIgnore);
			}
			ISCRewriteRule[] rewRules = rulesBlock.getRewriteRules();
			for (ISCRewriteRule rewRule : rewRules){
				ISCRewriteRule newRewRule = duplicate(rewRule, 
						ISCRewriteRule.ELEMENT_TYPE, newRulesBlock, monitor, toIgnore);
				ISCRewriteRuleRightHandSide[] ruleRHSs = rewRule.getRuleRHSs();
				for(ISCRewriteRuleRightHandSide rhs : ruleRHSs){
					duplicate(rhs, ISCRewriteRuleRightHandSide.ELEMENT_TYPE, newRewRule, monitor, toIgnore);
				}
			}
			ISCInferenceRule[] infRules = rulesBlock.getInferenceRules();
			for(ISCInferenceRule infRule : infRules){
				ISCInferenceRule newInfRule = duplicate(infRule, ISCInferenceRule.ELEMENT_TYPE, newRulesBlock, monitor, toIgnore);
				ISCInfer[] infers = infRule.getInfers();
				for(ISCInfer infer : infers){
					duplicate(infer, ISCInfer.ELEMENT_TYPE, newInfRule, monitor, toIgnore);
				}
				ISCGiven[] givens = infRule.getGivens();
				for(ISCGiven given : givens){
					duplicate(given, ISCGiven.ELEMENT_TYPE, newInfRule, monitor, toIgnore);
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
	public synchronized IDeploymentResult getDeploymentResult() {
		return deploymentResult;
	}
	
	protected void checkCancellationRequest(IProgressMonitor monitor){
		if (monitor.isCanceled()){
			
		}
	}

	
}
