/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.deploy;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public interface IDeployedTheoryRoot extends IEventBRoot, IAccuracyElement {

	IInternalElementType<IDeployedTheoryRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".deployedTheoryRoot");

	public ISCTypeParameter getSCTypeParameter(String name);

	public ISCTypeParameter[] getSCTypeParameters() throws RodinDBException;

	public ISCDatatypeDefinition getSCDatatypeDefinition(String name);

	public ISCDatatypeDefinition[] getSCDatatypeDefinitions() throws RodinDBException;

	public ISCNewOperatorDefinition getSCNewOperatorDefinition(String name);

	public ISCNewOperatorDefinition[] getSCNewOperatorDefinitions() throws RodinDBException;

	public ISCProofRulesBlock getProofRulesBlock(String name);

	public ISCProofRulesBlock[] getProofRulesBlocks() throws RodinDBException;

	public ISCTheorem getTheorem(String name);

	public ISCTheorem[] getTheorems() throws RodinDBException;

	/**
	 * <p>
	 * Returns the global type environment of this deployed theory.
	 * </p>
	 * 
	 * @param factory
	 * @return the type environment
	 * @throws RodinDBException
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException;

}
