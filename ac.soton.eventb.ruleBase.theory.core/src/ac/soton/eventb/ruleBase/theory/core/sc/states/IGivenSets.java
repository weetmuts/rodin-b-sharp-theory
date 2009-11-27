package ac.soton.eventb.ruleBase.theory.core.sc.states;

import java.util.List;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.ITypingElement;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
/**
 * <p>Given sets are used to pass the SC theory sets to other processor and filter modules. 
 * This was necessary as we don't have axioms to infer types from. This is used
 * to filter out typing elements {@link ITypingElement} to make sure they refer only to given sets.</p>
 * <p>TODO do it differently. The problem is at the point when we check variables, sets identifiers are not commited yet.</p>
 * @author maamria
 *
 */
public interface IGivenSets extends ISCState {

	final static IStateType<IGivenSets> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".givenSets");
	
	List<String> getGivenSets();
	
	void addGivenSet(String set);
}
