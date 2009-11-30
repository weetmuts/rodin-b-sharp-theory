package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ast.Formula;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

@SuppressWarnings("restriction")
public class ParsedRHSFormula extends State implements IParsedRHSFormula {

	private Formula<?> formula;

	
	public Formula<?> getRHSFormula() {
		return formula;
	}

	
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public void setRHSFormula(Formula<?> f) {
		formula = f;
	}

	
	public String toString() {
		return formula.toString();
	}

}
