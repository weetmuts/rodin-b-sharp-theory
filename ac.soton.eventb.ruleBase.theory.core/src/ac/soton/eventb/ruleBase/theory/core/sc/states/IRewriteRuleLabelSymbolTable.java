package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 *
 */
public interface IRewriteRuleLabelSymbolTable extends ILabelSymbolTable {

	final static IStateType<IRewriteRuleLabelSymbolTable> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID
			+ ".rewriteRuleLabelSymbolTable");
	
}
