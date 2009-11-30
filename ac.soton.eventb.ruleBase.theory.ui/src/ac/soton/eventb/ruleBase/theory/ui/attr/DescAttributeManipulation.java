package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.DESC_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IDescriptionElement;

public class DescAttributeManipulation extends AbstractAttributeManipulation
implements IAttributeManipulation {

	
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		logCantGetPossibleValues(DESC_ATTRIBUTE);
		return null;
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asDesc(element).getDescription();
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asDesc(element).hasDescription();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(DESC_ATTRIBUTE);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asDesc(element).setDescription("Change Me!", monitor);

	}

	
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		asDesc(element).setDescription(value, monitor);

	}

	private IDescriptionElement asDesc(IRodinElement element){
		assert element instanceof IDescriptionElement;
		return (IDescriptionElement) element;
	}
	
}
