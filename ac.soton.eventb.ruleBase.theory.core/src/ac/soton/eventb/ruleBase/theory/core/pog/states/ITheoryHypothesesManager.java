package ac.soton.eventb.ruleBase.theory.core.pog.states;

import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 *
 */
public interface ITheoryHypothesesManager extends IHypothesisManager {

	final static IStateType<ITheoryHypothesesManager> STATE_TYPE = 
		POGCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".theoryHypothesisManager");

	/**
	 * Returns whether the theory is accurate.
	 * 
	 * @return whether the theory is accurate
	 */
	boolean theoryIsAccurate();

}
