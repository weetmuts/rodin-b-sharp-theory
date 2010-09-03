package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.plugin.TheoryPlugin;

public class TheoryLabelSymbolTable
	extends AbstractTheoryLabelSymbolTable{
	
	public final static IStateType<TheoryLabelSymbolTable> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID
			+ ".theoryLabelSymbolTable");

	public TheoryLabelSymbolTable(int size) {
		super(size);
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
