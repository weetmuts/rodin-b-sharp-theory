/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.rulebase;

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
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public Predicate getTheorem() {
		// TODO Auto-generated method stub
		return theorem;
	}

}
