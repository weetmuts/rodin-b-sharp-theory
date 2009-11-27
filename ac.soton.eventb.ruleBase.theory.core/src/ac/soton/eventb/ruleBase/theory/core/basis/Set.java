package ac.soton.eventb.ruleBase.theory.core.basis;

import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.ISet;


public class Set extends EventBElement implements ISet {

	public Set(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISet> getElementType() {
		return ELEMENT_TYPE;
	}

}
