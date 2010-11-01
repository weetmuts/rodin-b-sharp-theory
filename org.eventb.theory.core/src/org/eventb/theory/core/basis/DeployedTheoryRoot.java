/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.basis.EventBRoot;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.IUseTheory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class DeployedTheoryRoot extends EventBRoot implements IDeployedTheoryRoot{

	public DeployedTheoryRoot(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	public IUseTheory getUsedTheory(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(IUseTheory.ELEMENT_TYPE, name);
	}

	@Override
	public IUseTheory[] getUsedTheories() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(IUseTheory.ELEMENT_TYPE);
	}
	
	@Override
	public ISCTypeParameter getSCTypeParameter(String name) {
		return getInternalElement(ISCTypeParameter.ELEMENT_TYPE, name);
	}

	@Override
	public ISCTypeParameter[] getSCTypeParameters() throws RodinDBException {
		return getChildrenOfType(ISCTypeParameter.ELEMENT_TYPE);
	}

	@Override
	public ISCDatatypeDefinition getSCDatatypeDefinition(String name) {
		return getInternalElement(ISCDatatypeDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public ISCDatatypeDefinition[] getSCDatatypeDefinitions()
			throws RodinDBException {
		return getChildrenOfType(ISCDatatypeDefinition.ELEMENT_TYPE);
	}

	@Override
	public ISCProofRulesBlock getProofRulesBlock(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCProofRulesBlock.ELEMENT_TYPE, name);
	}

	@Override
	public ISCProofRulesBlock[] getProofRulesBlocks() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCProofRulesBlock.ELEMENT_TYPE);
	}

	@Override
	public ISCTheorem getTheorem(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCTheorem.ELEMENT_TYPE, name);
	}

	@Override
	public ISCTheorem[] getTheorems() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCTheorem.ELEMENT_TYPE);
	}

	@Override
	public ISCNewOperatorDefinition getSCNewOperatorDefinition(String name) {
		return getInternalElement(ISCNewOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public ISCNewOperatorDefinition[] getSCNewOperatorDefinitions()
			throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCNewOperatorDefinition.ELEMENT_TYPE);
	}

	@Override
	public IDeployedTheoryRoot[] getRelatedSources() throws CoreException {
		// TODO Auto-generated method stub
		Set<IDeployedTheoryRoot> sources = new LinkedHashSet<IDeployedTheoryRoot>();
		IUseTheory[] used = getUsedTheories();
		for (IUseTheory use : used){
			if(use.hasUseTheory()){
				if(use.getUsedTheory().exists())
					sources.add(use.getUsedTheory());
			}
		}
		return sources.toArray(new IDeployedTheoryRoot[sources.size()]);
	}
}
