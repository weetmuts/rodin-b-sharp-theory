package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.plugin.TheoryPlugin;

public class RewriteRuleLabelSymbolTable
		extends AbstractTheoryLabelSymbolTable{

	public final static IStateType<RewriteRuleLabelSymbolTable> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID
		+ ".rewriteRuleLabelSymbolTable");
	
	public RewriteRuleLabelSymbolTable(int size) {
		super(size);
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
