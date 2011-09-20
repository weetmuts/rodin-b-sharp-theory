/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.basis.EventBRoot;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryAttributes;
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
		return getInternalElement(IUseTheory.ELEMENT_TYPE, name);
	}

	@Override
	public IUseTheory[] getUsedTheories() throws RodinDBException {
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
		return getInternalElement(ISCProofRulesBlock.ELEMENT_TYPE, name);
	}

	@Override
	public ISCProofRulesBlock[] getProofRulesBlocks() throws RodinDBException {
		return getChildrenOfType(ISCProofRulesBlock.ELEMENT_TYPE);
	}

	@Override
	public ISCTheorem getTheorem(String name) {
		return getInternalElement(ISCTheorem.ELEMENT_TYPE, name);
	}

	@Override
	public ISCTheorem[] getTheorems() throws RodinDBException {
		return getChildrenOfType(ISCTheorem.ELEMENT_TYPE);
	}

	@Override
	public ISCNewOperatorDefinition getSCNewOperatorDefinition(String name) {
		return getInternalElement(ISCNewOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public ISCNewOperatorDefinition[] getSCNewOperatorDefinitions()
			throws RodinDBException {
		return getChildrenOfType(ISCNewOperatorDefinition.ELEMENT_TYPE);
	}
	
	@Override
	public boolean hasOutdatedAttribute() throws RodinDBException {
		return hasAttribute(TheoryAttributes.OUTDATED_ATTRIBUTE);
	}

	@Override
	public boolean isOutdated() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.OUTDATED_ATTRIBUTE);
	}

	@Override
	public void setOutdated(boolean isOutdated, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(TheoryAttributes.OUTDATED_ATTRIBUTE, isOutdated, monitor);
	}

	@Override
	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();

		for (ISCTypeParameter par : getSCTypeParameters()) {
			typeEnvironment.addGivenSet(par.getIdentifierString());
		}

		return typeEnvironment;
	}
}
