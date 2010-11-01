package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

@SuppressWarnings("restriction")
public class ParsedRHSFormula extends State implements ISCState {

	public final static IStateType<ParsedRHSFormula> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".parsedRHSFormula");
	
	private Formula<?> formula;

	
	public Formula<?> getRHSFormula() {
		return formula;
	}

	
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public void setRHSFormula(Formula<?> f) {
		formula = f;
	}

	
	public String toString() {
		return formula.toString();
	}

}
