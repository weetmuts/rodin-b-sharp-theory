package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

@SuppressWarnings("restriction")
public class TheoryAccuracyInfo extends State implements
		ITheoryAccuracyInfo {

	private boolean accurate;
	
	public TheoryAccuracyInfo() {
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

