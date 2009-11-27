/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * Parsed formulas cannot be passed as parameters to filter modules.
 * They are accessible by means of this state component instead.
 * <p>The formula stored is type-checked.</p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IParsedFormula extends ISCState {
	
	final static IStateType<IParsedFormula> STATE_TYPE = 
		SCCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".parsedFormula");
	
	Formula<?> getFormula();
}
