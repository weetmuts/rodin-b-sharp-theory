package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.TYPING_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ITypingElement;

public class TypingAttributeManipulation extends AbstractAttributeManipulation 
implements IAttributeManipulation {

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		logCantGetPossibleValues(TYPING_ATTRIBUTE);
		return null;
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asTypingElmnt(element).getTypingString();
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asTypingElmnt(element).hasTypingString();
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(TYPING_ATTRIBUTE);

	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asTypingElmnt(element).setTypingString("", monitor);

	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		asTypingElmnt(element).setAttributeValue(TYPING_ATTRIBUTE, value, monitor);

	}
	
	private ITypingElement asTypingElmnt(IRodinElement e){
		assert e instanceof ITypingElement;
		return (ITypingElement) e;
	}

}
