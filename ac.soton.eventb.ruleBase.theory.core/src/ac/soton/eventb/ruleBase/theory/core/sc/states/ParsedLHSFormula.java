package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ast.Formula;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

@SuppressWarnings("restriction")
public class ParsedLHSFormula extends State implements IParsedLHSFormula {

	private Formula<?> formula;

	@Override
	public Formula<?> getLHSFormula() {
		return formula;
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public void setLHSFormula(Formula<?> f) {
		formula = f;
	}

	@Override
	public String toString() {
		return formula.toString();
	}

}
