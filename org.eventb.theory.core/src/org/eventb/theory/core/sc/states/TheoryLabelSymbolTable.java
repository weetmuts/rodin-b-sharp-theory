package org.eventb.theory.core.sc.states;

import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;

/**
 * A theory label symbol table.
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryLabelSymbolTable extends 
SymbolTable<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo>
implements ILabelSymbolTable{
	
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
