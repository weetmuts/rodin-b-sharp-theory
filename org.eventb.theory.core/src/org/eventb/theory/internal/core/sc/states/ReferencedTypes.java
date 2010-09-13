/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * An implementation of a state holding information about which types (i.e., theory type parameters) a specific datatype is polymorphic on.
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class ReferencedTypes extends State implements ISCState{

	public final static IStateType<ReferencedTypes> STATE_TYPE = SCCore.getToolStateType(
			TheoryPlugin.PLUGIN_ID + ".referencedTypes");
	
	private List<String> referencedTypes;
	
	public ReferencedTypes(){
		referencedTypes = new ArrayList<String>();
	}
	
	/**
	 * Returns the referenced types of the datatype.
	 * 
	 * @return referenced type paramters
	 */
	public List<String> getReferencedTypes(){
		return referencedTypes;
	}
	
	/**
	 * Adds a referenced type.
	 * @param type
	 */
	public void addReferencedType(String type){
		referencedTypes.add(type);
	}
	
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
