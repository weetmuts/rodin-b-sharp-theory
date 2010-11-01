/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners.input;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * An implementation of a definition expansion reasoner input.
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class DefinitionExpansionInput implements IReasonerInput{

	public String operatorID;
	public String syntax;
	public IPosition position;
	public Predicate pred;
	
	/**
	 * Constructs an input with the given parameters.
	 * @param operatorID
	 * @param pred 
	 * @param position 
	 */
	public DefinitionExpansionInput(String operatorID,String syntax,
			Predicate pred, IPosition position){
		this.operatorID = operatorID;
		this.syntax = syntax;
		this.position = position;
		this.pred = pred;
	}
	
	public void applyHints(ReplayHints renaming) {
		if(pred !=null){
			renaming.applyHints(pred);
		}
		
	}
	
	public String getError() {
		return null;
	}
	
	public boolean hasError() {
		return false;
	}

}
