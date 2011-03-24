/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.basis.EventBRoot;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.DB_TCFacade;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class SCTheoryRoot extends EventBRoot implements ISCTheoryRoot {

	public SCTheoryRoot(String name, IRodinElement parent) {
		super(name, parent);
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
	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();

		for (ISCTypeParameter par : getSCTypeParameters()) {
			typeEnvironment.addGivenSet(par.getIdentifierString());
		}

		return typeEnvironment;
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
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
	public IRodinFile getDeployedTheoryFile(String bareName) {
		String fileName = DB_TCFacade.getDeployedTheoryFullName(bareName);
		IRodinFile file = getRodinProject().getRodinFile(fileName);
		return file;
	}

	@Override
	public IDeployedTheoryRoot getDeployedTheoryRoot() {
		return getDeployedTheoryRoot(getElementName());
	}

	@Override
	public IDeployedTheoryRoot getDeployedTheoryRoot(String bareName) {
		IDeployedTheoryRoot root = (IDeployedTheoryRoot) getDeployedTheoryFile(
				bareName).getRoot();
		return root;
	}

	@Override
	public ISCImportTheory getImportTheory(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCImportTheory.ELEMENT_TYPE,name);
	}

	@Override
	public ISCImportTheory[] getImportTheories() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCImportTheory.ELEMENT_TYPE);
	}
}
