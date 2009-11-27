package ac.soton.eventb.ruleBase.theory.core.basis;

import org.eventb.core.basis.SCIdentifierElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.ISCSet;


public class SCSet extends SCIdentifierElement implements ISCSet {

	public SCSet(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCSet> getElementType() {
		return ISCSet.ELEMENT_TYPE;
	}
}
