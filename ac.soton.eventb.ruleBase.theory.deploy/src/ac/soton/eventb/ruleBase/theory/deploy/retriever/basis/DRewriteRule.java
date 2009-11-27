package ac.soton.eventb.ruleBase.theory.deploy.retriever.basis;

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.ruleBase.theory.deploy.IDRewriteRule;
import ac.soton.eventb.ruleBase.theory.deploy.IDRuleRightHandSide;

public class DRewriteRule implements IDRewriteRule{

	private String description;
	private ITypeEnvironment globalTypeEnv;
	private boolean isAutomatic;
	private boolean isComplete;
	private boolean isConditional;
	private boolean isInteractive;
	private boolean isSound;
	private Formula<?> lhs;
	private String ruleName;
	private List<IDRuleRightHandSide> ruleRHSs;
	private String theoryName;
	private String toolTip;
	
	public DRewriteRule(String ruleName, String theoryName,
			Formula<?> lhs, List<IDRuleRightHandSide> ruleRHSs,
			boolean isAutomatic, boolean isInteractive, boolean isComplete,
			boolean isSound, String toolTip, String description,
			ITypeEnvironment typeEnv){
		this.ruleName = ruleName;
		this.theoryName = removeThyExtension(theoryName);
		this.lhs = lhs;
		this.ruleRHSs = ruleRHSs;
		this.isAutomatic = isAutomatic;
		this.isSound = isSound;
		this.isInteractive = isInteractive;
		this.isComplete = isComplete;
		this.toolTip = toolTip;
		this.description = description;
		this.isConditional = computeConditionality();
		this.globalTypeEnv = typeEnv;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Formula<?> getLeftHandSide() {
		return lhs;
	}

	@Override
	public List<IDRuleRightHandSide> getRightHandSides() {
		return ruleRHSs;
	}

	@Override
	public String getRuleName() {
		return ruleName;
	}

	@Override
	public String getTheoryName() {
		return theoryName;
	}

	@Override
	public String getToolTip() {
		return toolTip;
	}

	@Override
	public ITypeEnvironment getTypeEnvironment() {
		return globalTypeEnv.clone();
	}

	@Override
	public boolean isAutomatic() {
		return isAutomatic;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public boolean isConditional() {
		return isConditional;
	}
	
	@Override
	public boolean isExpression() {
		return lhs instanceof Expression;
	}

	@Override
	public boolean isInteracive() {
		return isInteractive;
	}

	@Override
	public boolean isSound() {
		return isSound;
	}

	private boolean computeConditionality(){
		boolean isCond = true;
		if(ruleRHSs.size() == 1){
			IDRuleRightHandSide rhs0 = ruleRHSs.get(0);
			Predicate cond = rhs0.getCondition();
			Predicate truePred = FormulaFactory.getDefault().makeLiteralPredicate(Formula.BTRUE, null);
			if(cond.equals(truePred)){
				isCond = false;
			}
		}
		return isCond;
	}
	
	private String removeThyExtension(String nameWithExt){
		return nameWithExt.substring(0, nameWithExt.length()-4);
	}
}
