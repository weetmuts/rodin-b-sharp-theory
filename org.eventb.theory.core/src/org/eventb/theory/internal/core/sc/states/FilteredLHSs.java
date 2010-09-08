package org.eventb.theory.internal.core.sc.states;

import java.util.HashMap;

import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * <p>Keeps track of left hand side formulas that are OK from theory SC point of view.</p>
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class FilteredLHSs extends State implements ISCState{

	public final static IStateType<FilteredLHSs> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".filteredLHSs");
	
	HashMap<String, Formula<?>> rulesLhss;
	
	public FilteredLHSs(){
		rulesLhss = new HashMap<String, Formula<?>>();
	}
	
	
	public void addLHS(String rule, Formula<?> lhs) {
		rulesLhss.put(rule, lhs);
		
	}

	
	public HashMap<String, Formula<?>> getRulesLHSs() {
		return rulesLhss;
	}

	
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
