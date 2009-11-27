package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 *
 */
public interface ITheoryAccuracyInfo extends IAccuracyInfo {

	final static IStateType<ITheoryAccuracyInfo> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".theoryAccuracyInfo");
}
