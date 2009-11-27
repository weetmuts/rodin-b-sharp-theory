package ac.soton.eventb.ruleBase.theory.core.basis;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.TYPING_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IVariable;

public class Variable extends EventBElement implements IVariable {

	public Variable(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IVariable> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public String getTypingString() throws RodinDBException {
		return getAttributeValue(TYPING_ATTRIBUTE);
	}

	@Override
	public boolean hasTypingString() throws RodinDBException {
		return hasAttribute(TYPING_ATTRIBUTE);
	}

	@Override
	public void setTypingString(String expression, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(TYPING_ATTRIBUTE, expression, monitor);
		
	}
}
