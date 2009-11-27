package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

@SuppressWarnings("restriction")
public class RuleAccuracyInfo extends State implements
	IRuleAccuracyInfo{

	private boolean accurate;
	
	public RuleAccuracyInfo() {
		accurate = true;
	}
	
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public boolean isAccurate() {
		return accurate;
	}

	public void setNotAccurate() {
		accurate = false;
	}
}
