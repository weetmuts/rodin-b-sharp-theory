/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog.states;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoremsAccumulator extends State implements IPOGState {

	public final static IStateType<TheoremsAccumulator> STATE_TYPE = 
		POGCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".theoremsAccumulator");
	
	private Map<String, Predicate> hypotheses;
	
	public TheoremsAccumulator(){
		hypotheses = new LinkedHashMap<String, Predicate>();
	}
	
	public void addHypothesis(Predicate hyp, String theoremName){
		hypotheses.put(theoremName, hyp);
	}
	
	public Map<String, Predicate> getHypotheses(){
		return hypotheses;
	}
	
	@Override
	public IStateType<?> getStateType() {
		// TODO Auto-generated method stub
		return STATE_TYPE;
	}

}
