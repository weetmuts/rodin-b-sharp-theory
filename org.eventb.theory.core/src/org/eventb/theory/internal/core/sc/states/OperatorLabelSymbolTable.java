/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 * 
 */
public class OperatorLabelSymbolTable extends AbstractTheoryLabelSymbolTable{

	public final static IStateType<OperatorLabelSymbolTable> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID
					+ ".operatorLabelSymbolTable");

	public OperatorLabelSymbolTable(int size) {
		super(size);
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
