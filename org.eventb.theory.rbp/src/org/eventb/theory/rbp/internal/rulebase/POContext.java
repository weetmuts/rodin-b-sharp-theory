/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.rulebase;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * 
 * @author maamria
 *
 */
public class POContext implements IPOContext{
	
	private IPSStatus psStatus;
	private FormulaFactory factory;

	public POContext(IPSStatus psStatus, FormulaFactory factory) {
		this.psStatus = psStatus;
		this.factory = factory;
	}

	@Override
	public IEventBRoot getParentRoot() {
		return (IEventBRoot) psStatus.getRoot();
	}

	@Override
	public boolean isTheoryRelated() {
		return DatabaseUtilities.originatedFromTheory(psStatus.getRodinFile());
	}

	@Override
	public boolean inMathExtensions() {
		return DatabaseUtilities.isMathExtensionsProject(psStatus.getRodinProject());
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return factory;
	}

}
