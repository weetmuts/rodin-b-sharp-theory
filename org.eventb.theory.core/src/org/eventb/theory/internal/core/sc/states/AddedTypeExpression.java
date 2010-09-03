/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class AddedTypeExpression extends State implements ISCState{
	
	public final static IStateType<AddedTypeExpression> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".addedTypeExpression");
	
	private Type type;

	public AddedTypeExpression(Type type){
		this.type = type;
	}

	public Type getType(){
		return type;
	}
	
	public void setType(Type type){
		this.type = type;
	}
	
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
}
