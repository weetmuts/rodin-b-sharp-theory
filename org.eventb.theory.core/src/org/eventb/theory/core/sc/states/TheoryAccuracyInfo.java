package org.eventb.theory.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.sc.AccuracyInfo;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * Overall theory accuracy information state holder.
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryAccuracyInfo extends AccuracyInfo implements IAccuracyInfo{
	
	public final static IStateType<TheoryAccuracyInfo> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".theoryAccuracyInfo");
	
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
}

