/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.deploy.basis;

import static org.eventb.theory.internal.core.util.CoreUtilities.duplicate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.deploy.IDeployedTheoryRoot;
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

	public TheoryDeployer(ISCTheoryRoot theoryRoot, boolean force){
		assert theoryRoot.exists();
		this.theoryRoot = theoryRoot;
		this.force = force;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		analyse();
		checkCancellationRequest(monitor);
		try{
			deploy(monitor);
		}catch(CoreException exception){
			deploymentResult =  new DeploymentResult(false, exception.getMessage());
		}
		
	}

	@Override
	public void analyse() throws CoreException {
		// TODO need to do some prepossessing
		
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
		// copy carrier sets
		////////////////////////////////
		ISCTypeParameter[] typeParameters = theoryRoot.getSCTypeParameters();
		for(ISCTypeParameter typeParameter: typeParameters ){
				duplicate(typeParameter, 
						ISCTypeParameter.ELEMENT_TYPE, 
						deployedTheoryRoot, monitor);
		}
		// copy datatypes
		////////////////////////////////
		ISCDatatypeDefinition[] datatypeDefinitions = theoryRoot.getSCDatatypeDefinitions();
		for(ISCDatatypeDefinition definition : datatypeDefinitions){
			if(!definition.hasError()){
				ISCDatatypeDefinition newDefinition = duplicate(definition, 
						ISCDatatypeDefinition.ELEMENT_TYPE, 
						deployedTheoryRoot, monitor, TheoryAttributes.HAS_ERROR_ATTRIBUTE);
				ISCTypeArgument[] typeArguments = definition.getTypeArguments();
				for(ISCTypeArgument typeArgument : typeArguments){
					duplicate(typeArgument, ISCTypeArgument.ELEMENT_TYPE, newDefinition, monitor);
				}
				
				ISCDatatypeConstructor[] datatypeConstructors = definition.getConstructors();
				for(ISCDatatypeConstructor constructor : datatypeConstructors){
					ISCDatatypeConstructor newConstructor = duplicate(constructor, 
							ISCDatatypeConstructor.ELEMENT_TYPE, newDefinition, monitor);
					ISCConstructorArgument arguments[] = constructor.getConstructorArguments();
					for(ISCConstructorArgument argument : arguments){
						duplicate(argument, ISCConstructorArgument.ELEMENT_TYPE, newConstructor, monitor);
					}
				}
			}
			else {
				accurate = false;
			}
		}
		// copy operators
		////////////////////////////////
		ISCNewOperatorDefinition[] operatorDefinitions = theoryRoot.getSCNewOperatorDefinitions();
		for(ISCNewOperatorDefinition operatorDefinition: operatorDefinitions){
			if(!operatorDefinition.hasError()){
				ISCNewOperatorDefinition newDefinition = duplicate(operatorDefinition, 
						ISCNewOperatorDefinition.ELEMENT_TYPE, deployedTheoryRoot, monitor, 
						TheoryAttributes.HAS_ERROR_ATTRIBUTE);
				ISCOperatorArgument[] operatorArguments = operatorDefinition.getOperatorArguments();
				for(ISCOperatorArgument operatorArgument : operatorArguments){
					duplicate(operatorArgument, 
							ISCOperatorArgument.ELEMENT_TYPE, 
							newDefinition, monitor);
				}
				ISCDirectOperatorDefinition[] directDefinitions = operatorDefinition.getDirectOperatorDefinitions();
				for(ISCDirectOperatorDefinition directDefinition : directDefinitions){
					duplicate(directDefinition, 
							ISCDirectOperatorDefinition.ELEMENT_TYPE,
							newDefinition, monitor);
				}
			}
			else {
				accurate = false;
			}
		}
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
				ISCInferenceRule newInfRule = duplicate(infRule, ISCInferenceRule.ELEMENT_TYPE, rulesBlock, monitor);
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
		notifyAll();
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
