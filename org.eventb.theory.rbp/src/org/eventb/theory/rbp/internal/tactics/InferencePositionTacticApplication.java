/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.tactics;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.ManualInferenceReasoner;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * @author maamria
 *
 */
public class InferencePositionTacticApplication extends ExtendedPositionApplication{

	private String toolTip;
	private InferenceInput input;
	IPOContext context;
	
	public InferencePositionTacticApplication(InferenceInput input , String toolTip, IPOContext context) {
		super(input.pred, IPosition.ROOT);
		this.toolTip = toolTip;
		this.input = input;
		this.context = context;
	}
	
	public Point getHyperlinkBounds(String parsedString,
			Predicate parsedPredicate) {
		return getOperatorPosition(parsedPredicate,
				parsedString);
	}

	public String getHyperlinkLabel() {
		return toolTip;
	}

	public ITactic getTactic(String[] inputs, String globalInput) {
		return BasicTactics.reasonerTac(new ManualInferenceReasoner(context), input);
	}

	public String getTacticID() {
		return RbPPlugin.PLUGIN_ID + ".inferenceTactic";
	}

}
