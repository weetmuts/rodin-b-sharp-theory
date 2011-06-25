/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * 
 * @author maamria
 *
 */
public class POContext implements IPOContext{
	
	private IEventBRoot root;

	public POContext(IEventBRoot root) {
		this.root = root;
	}

	@Override
	public IEventBRoot getParentRoot() {
		return root;
	}

	@Override
	public boolean isTheoryRelated() {
		return DatabaseUtilities.originatedFromTheory(root.getRodinFile());
	}

	@Override
	public boolean inMathExtensions() {
		return DatabaseUtilities.isMathExtensionsProject(root.getRodinProject());
	}
	
	@Override
	public FormulaFactory getFormulaFactory() {
		return root.getFormulaFactory();
	}
	
	public String toString(){
		return root.getHandleIdentifier();
	}
}
