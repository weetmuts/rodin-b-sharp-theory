/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.tactics;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.DefinitionExpansionReasoner;
import org.eventb.theory.rbp.reasoners.input.DefinitionExpansionInput;

/**
 * @author maamria
 *
 */
public class ExpansionTacticApplication extends ExtendedPositionApplication{

	private final DefinitionExpansionInput input;
	private final String linkLabel;

	public ExpansionTacticApplication(DefinitionExpansionInput input) {
		super(input.pred, input.position);
		this.input = input;
		this.linkLabel = "expand definition of " + input.syntax;
	}

	public Point getHyperlinkBounds(String parsedString,
			Predicate parsedPredicate) {
		return getOperatorPosition(parsedPredicate,
				parsedString);
	}

	public String getHyperlinkLabel() {
		return linkLabel;
	}

	public ITactic getTactic(String[] inputs, String globalInput) {
		return BasicTactics.reasonerTac(new DefinitionExpansionReasoner(), input);
	}

	public String getTacticID() {
		return RbPPlugin.PLUGIN_ID + ".expansionTactic";
	}
}
