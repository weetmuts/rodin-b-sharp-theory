/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.base;

import static java.util.Collections.unmodifiableList;
import static org.eventb.theory.core.TheoryCoreFacadeDB.getDeployedTheory;
import static org.eventb.theory.core.TheoryCoreFacadeDB.getDeploymentProject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTypeParameter;

/**
 * @author maamria
 *
 */
public final class DeployedTheoryFile implements IDeployedTheoryFile{
	
	private FormulaFactory factory;
	private List<IDeployedRewriteRule> rewriteRules;
	private List<IDeployedInferenceRule> inferenceRules;
	
	private String theoryName;
	
	private ITypeEnvironment typeEnvironment;
	
	/**
	 * <p>Constructs a deployed theory object with the given name (including extension).</p>
	 * @param theoryName with extension .thy
	 * @param factory
	 */
	public DeployedTheoryFile(String theoryName, FormulaFactory factory){
		this.theoryName = theoryName;
		this.factory = factory;
		this.typeEnvironment = factory.makeTypeEnvironment();
		this.rewriteRules = new ArrayList<IDeployedRewriteRule>();
		this.inferenceRules = new ArrayList<IDeployedInferenceRule>();
		try {
			loadTheory();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<IDeployedRewriteRule> getRewriteRules() {
		// TODO Auto-generated method stub
		return unmodifiableList(rewriteRules);
	}

	@Override
	public ITypeEnvironment getGloablTypeEnvironment() {
		// TODO Auto-generated method stub
		return typeEnvironment.clone();
	}

	@Override
	public List<IDeployedInferenceRule> getInferenceRules() {
		// TODO Auto-generated method stub
		return unmodifiableList(inferenceRules);
	}

	@Override
	public String getTheoryName() {
		// TODO Auto-generated method stub
		return theoryName;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return rewriteRules.size() + inferenceRules.size() == 0;
	}
	
	private void loadTheory() throws CoreException{
		IDeployedTheoryRoot root = getDeployedTheory(theoryName, getDeploymentProject(null));
		ISCTypeParameter[] types = root.getSCTypeParameters();
		for (ISCTypeParameter par : types){
			typeEnvironment.addGivenSet(par.getIdentifier(factory).getName());
		}
		ISCProofRulesBlock[] blocks = root.getProofRulesBlocks();
		for (ISCProofRulesBlock b : blocks){
			for (IDeployedRewriteRule rule : DeployedObjectsFactory.getDeployedRewriteRules(b, factory, typeEnvironment)){
					rewriteRules.add(rule);
			}
			inferenceRules.addAll(DeployedObjectsFactory.getDeployedInferenceRules(b, factory, typeEnvironment));
		}
	}
	
	public String toString(){
		return theoryName 
			+"\n rew: "+rewriteRules
			+"\n inf: "+inferenceRules +"\n";
	}

}