package ac.soton.eventb.ruleBase.theory.core.basis;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.TheoryElement;

public class RewriteRuleRightHandSide extends TheoryElement implements IRewriteRuleRightHandSide{

	public RewriteRuleRightHandSide(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	

}
