package ac.soton.eventb.ruleBase.theory.core.sc.states;

import java.util.HashMap;

import org.eventb.core.ast.Formula;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;

@SuppressWarnings("restriction")
public class FilteredLHSs extends State implements IFilteredLHSs{

	HashMap<IRewriteRule, Formula<?>> rulesLhss;
	
	public FilteredLHSs(){
		rulesLhss = new HashMap<IRewriteRule, Formula<?>>();
	}
	
	
	public void addLHS(IRewriteRule rule, Formula<?> lhs) {
		rulesLhss.put(rule, lhs);
		
	}

	
	public HashMap<IRewriteRule, Formula<?>> getRulesLHSs() {
		return rulesLhss;
	}

	
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
