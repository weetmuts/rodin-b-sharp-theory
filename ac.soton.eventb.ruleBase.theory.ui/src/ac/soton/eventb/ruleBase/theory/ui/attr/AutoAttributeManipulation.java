package ac.soton.eventb.ruleBase.theory.ui.attr;
import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.AUTOMATIC_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IAutomaticElement;
import ac.soton.eventb.ruleBase.theory.ui.util.Messages;

public class AutoAttributeManipulation extends AbstractBooleanManipulation
		implements IAttributeManipulation {

	public AutoAttributeManipulation() {
		super(Messages.rewriteRule_isAutomatic, Messages.rewriteRule_isNotAutomatic);
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return getText(asAutomatic(element).getAttributeValue(AUTOMATIC_ATTRIBUTE));
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asAutomatic(element).hasAutomatic();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asAutomatic(element).removeAttribute(AUTOMATIC_ATTRIBUTE, monitor);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asAutomatic(element).setAutomatic(false, monitor);

	}

	
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(TRUE)){
			asAutomatic(element).setAutomatic(true, monitor);
		}
		else if(value.equals(FALSE)){
			asAutomatic(element).setAutomatic(false, monitor);
		}
		else {
			logNotPossibleValues(AUTOMATIC_ATTRIBUTE, value);
		}
	}

	IAutomaticElement asAutomatic(IRodinElement element){
		assert element instanceof IAutomaticElement;
		return (IAutomaticElement) element;
	}
	
}
