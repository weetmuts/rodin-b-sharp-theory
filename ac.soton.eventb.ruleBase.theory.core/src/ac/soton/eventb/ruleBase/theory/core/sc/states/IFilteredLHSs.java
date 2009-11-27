package ac.soton.eventb.ruleBase.theory.core.sc.states;

import java.util.HashMap;

import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>Keeps track of left hand side formulas that are OK from theory SC point of view.</p>
 * @author maamria
 *
 */
public interface IFilteredLHSs extends ISCState {

	final static IStateType<IFilteredLHSs> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".filteredLHSs");
	
	HashMap<IRewriteRule, Formula<?>> getRulesLHSs();
	
	void addLHS(IRewriteRule rule, Formula<?> lhs);

}
