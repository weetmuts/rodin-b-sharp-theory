package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.INTERACTIVE_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IInteractiveElement;
import ac.soton.eventb.ruleBase.theory.ui.util.Messages;

public class InteractiveAttributeManipulation extends
		AbstractBooleanManipulation implements IAttributeManipulation {

	public InteractiveAttributeManipulation() {
		super(Messages.rewriteRule_isInteractive, Messages.rewriteRule_isUnInteractive);
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return getText(asInteractive(element).getAttributeValue(INTERACTIVE_ATTRIBUTE));
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asInteractive(element).hasInteractive();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asInteractive(element).removeAttribute(INTERACTIVE_ATTRIBUTE, monitor);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asInteractive(element).setInteractive(true, monitor);

	}

	
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(TRUE)){
			asInteractive(element).setInteractive(true, monitor);
		}
		else if(value.equals(FALSE)){
			asInteractive(element).setInteractive(false, monitor);
		}
		else {
			logNotPossibleValues(INTERACTIVE_ATTRIBUTE, value);
		}
	}

	IInteractiveElement asInteractive(IRodinElement element){
		assert element instanceof IInteractiveElement;
		return (IInteractiveElement) element;
	}
}
