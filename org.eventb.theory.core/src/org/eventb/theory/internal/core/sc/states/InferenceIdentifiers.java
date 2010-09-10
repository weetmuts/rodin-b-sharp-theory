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

import org.eventb.core.ast.FreeIdentifier;
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
public class InferenceIdentifiers extends State implements ISCState{

	public final static IStateType<InferenceIdentifiers> STATE_TYPE = SCCore.getToolStateType(
			TheoryPlugin.PLUGIN_ID + ".inferenceIdentifiers");
	
	private List<FreeIdentifier> inferIdents;
	private List<FreeIdentifier> givenIdents;
	
	public InferenceIdentifiers(){
		inferIdents = new ArrayList<FreeIdentifier>();
		givenIdents = new ArrayList<FreeIdentifier>();
	}
	
	public void addInferIdentifiers(FreeIdentifier[] idents){
		for(FreeIdentifier ident : idents){
			if(!inferIdents.contains(ident)){
				inferIdents.add(ident);
			}
		}
	}

	public void addGivenIdentifiers(FreeIdentifier[] idents){
		for(FreeIdentifier ident : idents){
			if(!givenIdents.contains(ident)){
				givenIdents.add(ident);
			}
		}
	}
	
	public boolean isRuleApplicable(){
		return givenIdents.containsAll(inferIdents) ||
			inferIdents.containsAll(givenIdents);
	}
	
	public boolean isRuleBackwardApplicable(){
		return inferIdents.containsAll(givenIdents);
	}
	
	public boolean isRuleForwardApplicable(){
		return givenIdents.containsAll(inferIdents);
	}
	
	public boolean isRuleApplicableInBothDirections(){
		return isRuleBackwardApplicable() &&
			isRuleForwardApplicable();
	}
	
	@Override
	public IStateType<?> getStateType() {
		// TODO Auto-generated method stub
		return STATE_TYPE;
	}

}
