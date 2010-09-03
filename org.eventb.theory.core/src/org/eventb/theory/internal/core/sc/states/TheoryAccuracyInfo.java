package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

@SuppressWarnings("restriction")
public class TheoryAccuracyInfo extends State implements ISCState{

	
	public final static IStateType<TheoryAccuracyInfo> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".theoryAccuracyInfo");
	
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

