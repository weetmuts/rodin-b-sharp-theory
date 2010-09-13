package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.VALIDATED_ATTRIBUTE;
import static org.eventb.theory.internal.ui.Messages.element_isNotValidated;
import static org.eventb.theory.internal.ui.Messages.element_isValidated;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.theory.core.IValidatedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ValidatedAttributeManipulation extends AbstractBooleanManipulation {

	/**
	 * @param trueText
	 * @param falseText
	 */
	public ValidatedAttributeManipulation() {
		super(element_isValidated, element_isNotValidated);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asValidatedElement(element).setValidated(false, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asValidatedElement(element).hasValidatedAttribute();
	}

	
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asValidatedElement(element).isValidated()
			?element_isValidated
					:element_isNotValidated;
	}
	
	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(element_isValidated)){
			asValidatedElement(element).setValidated(true, monitor);
		}
		else
			asValidatedElement(element).setValidated(false, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asValidatedElement(element).removeAttribute(VALIDATED_ATTRIBUTE, monitor);
		
	}

	protected IValidatedElement asValidatedElement(IRodinElement element){
		assert element instanceof IValidatedElement;
		return (IValidatedElement) element;
	}

}
