/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.explorer.model;

import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class ModelRulesBlock implements IModelElement{

	
	private IProofRulesBlock block;
	private IModelElement parent;
	
	public ModelRulesBlock(IProofRulesBlock block, IModelElement parent){
		this.block = block;
		this.parent =parent;
	}
	
	@Override
	public IModelElement getModelParent() {
		return parent;
	}

	@Override
	public IRodinElement getInternalElement() {
		return block;
	}

	@Override
	public Object getParent(boolean complex) {
		return parent;
	}

	
	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type == IRewriteRule.ELEMENT_TYPE) {
			try {
				return block.getRewriteRules();
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
		if (type == IInferenceRule.ELEMENT_TYPE) {
			try {
				return block.getInferenceRules();
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
		else {
		
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for rules block: " +type);
			}
		}
		return new Object[0];
	}

}
