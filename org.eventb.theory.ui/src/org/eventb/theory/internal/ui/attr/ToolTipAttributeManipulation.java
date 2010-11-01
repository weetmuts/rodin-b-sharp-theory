package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.TOOL_TIP_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.theory.core.IToolTipElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ToolTipAttributeManipulation extends AbstractAttributeManipulation 
implements IAttributeManipulation {

	
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		logCantGetPossibleValues(TOOL_TIP_ATTRIBUTE);
		return null;
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asTipElement(element).getToolTip();
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asTipElement(element).hasToolTip();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(TOOL_TIP_ATTRIBUTE);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asTipElement(element).setToolTip("Change Me!", monitor);

	}

	
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		asTipElement(element).setToolTip(value, monitor);

	}

	private IToolTipElement asTipElement(IRodinElement element){
		assert element instanceof IToolTipElement;
		return (IToolTipElement) element;
	}
}
