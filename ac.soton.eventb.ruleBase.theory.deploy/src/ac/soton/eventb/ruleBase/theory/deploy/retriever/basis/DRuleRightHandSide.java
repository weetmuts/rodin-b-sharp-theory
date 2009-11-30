package ac.soton.eventb.ruleBase.theory.deploy.retriever.basis;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.ruleBase.theory.deploy.IDRuleRightHandSide;

public class DRuleRightHandSide implements IDRuleRightHandSide{

	private Predicate condition;
	private Formula<?> rhs;
	private String rhsName;
	
	public DRuleRightHandSide(String rhsName, 
			Predicate condition, Formula<?> rhs){
		this.rhsName = rhsName;
		this.condition = condition;
		this.rhs = rhs;
	}
	
	
	public Predicate getCondition() {
		return condition;
	}

	
	public Formula<?> getRHSFormula() {
		return rhs;
	}

	
	public String getRHSName() {
		return rhsName;
	}

}
