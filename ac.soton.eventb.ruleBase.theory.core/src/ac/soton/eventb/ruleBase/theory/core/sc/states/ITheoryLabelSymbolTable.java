package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 *
 */
public interface ITheoryLabelSymbolTable extends ILabelSymbolTable {

	final static IStateType<ITheoryLabelSymbolTable> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID
					+ ".theoryLabelSymbolTable");
}
