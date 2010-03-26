package ac.soton.eventb.ruleBase.theory.core.deploy.basis;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.TYPING_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.deploy.basis.IMetaVariable;

/**
 * An implementation of a meta variable internal element.
 * @author maamria
 *
 */
public class MetaVariable extends EventBElement implements IMetaVariable{

	public MetaVariable(String name, IRodinElement parent) {
		super(name, parent);
	}

	
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	
	public String getTypingString() throws RodinDBException {
		return getAttributeValue(TYPING_ATTRIBUTE);
	}

	
	public boolean hasTypingString() throws RodinDBException {
		return hasAttribute(TYPING_ATTRIBUTE);
	}

	
	public void setTypingString(String expression, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(TYPING_ATTRIBUTE, expression, monitor);
		
	}

}
