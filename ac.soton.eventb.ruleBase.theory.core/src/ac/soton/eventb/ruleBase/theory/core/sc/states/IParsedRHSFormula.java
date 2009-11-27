package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>A parsed right hand side formula corresponds to the formula present in a right hand side element.</p>
 * <p>The formula stored is type-checked.</p>
 * @author maamria
 *
 */
public interface IParsedRHSFormula extends ISCState {

	final static IStateType<IParsedRHSFormula> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".parsedRHSFormula");

	Formula<?> getRHSFormula();

	void setRHSFormula(Formula<?> form);

}
