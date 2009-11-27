package ac.soton.eventb.ruleBase.theory.core.basis;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.IRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.TheoryElement;


/**
 * @author maamria
 * 
 */
public class RewriteRule extends TheoryElement implements IRewriteRule {

	public RewriteRule(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IRewriteRule> getElementType() {
		return ELEMENT_TYPE;
	}
	

	public IRewriteRuleRightHandSide getRuleRHS(String name) {
		return getInternalElement(IRewriteRuleRightHandSide.ELEMENT_TYPE, name);
	}

	public IRewriteRuleRightHandSide[] getRuleRHSs() throws RodinDBException {
		return getChildrenOfType(IRewriteRuleRightHandSide.ELEMENT_TYPE);
		
	}
}
