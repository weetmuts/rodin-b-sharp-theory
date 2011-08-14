/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners.input;

import java.util.List;

import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * An implementation of a multiple strings input for context-aware reasoners.
 * 
 * @since 1.2
 * @author maamria
 *
 */
public class MultipleStringInput extends ContextualInput{

	public List<String> strings;
	
	public MultipleStringInput(IPOContext context, List<String> strings) {
		super(context);
		this.strings = strings;
	}

}
