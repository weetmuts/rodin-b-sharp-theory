package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>A parsed left hand side formula corresponds to the formula present in a left hand side element.</p>
 * <p>The formula stored is type-checked.</p>
 * @author maamria
 *
 */
public interface IParsedLHSFormula extends ISCState {

	final static IStateType<IParsedLHSFormula> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".parsedLHSFormula");

	Formula<?> getLHSFormula();

	void setLHSFormula(Formula<?> form);

}
