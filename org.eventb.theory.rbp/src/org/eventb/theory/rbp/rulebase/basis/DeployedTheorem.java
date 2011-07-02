/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.Predicate;

/**
 * 
 * @author maamria
 *
 */
public class DeployedTheorem implements IDeployedTheorem{

	private String name;
	private Predicate theorem;

	public DeployedTheorem(String name, Predicate theorem){
		this.name = name;
		this.theorem = theorem;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Predicate getTheorem() {
		return theorem;
	}

	@Override
	public boolean hasTypeParameters() {
		return theorem.getGivenTypes().size() != 0;
	}

}
