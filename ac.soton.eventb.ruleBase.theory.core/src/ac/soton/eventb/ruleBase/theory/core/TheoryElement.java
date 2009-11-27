package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;


/**
 * Common implementation for Event-B Theory elements.
 * <p>
 * Clients may sublass this class.
 * </p>
 * 
 * @author maamria
 * 
 */

public abstract class TheoryElement extends EventBElement implements
	ILeftHandSideElement, 
	IAutomaticElement, 
	IInteractiveElement, 
	ICompleteElement,
	IRightHandSideElement, 
	ICategoryElement,
	IToolTipElement,
	IDescriptionElement{

	public TheoryElement(String name, IRodinElement parent) {
		super(name, parent);
	} 


	public String getLHSString() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.LHS_ATTRIBUTE);
	}

	public String getRHSString() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.RHS_ATTRIBUTE);
	}

	public boolean hasAutomatic() throws RodinDBException {
		return hasAttribute(TheoryAttributes.AUTOMATIC_ATTRIBUTE);
	}

	@Override
	public boolean hasComplete() throws RodinDBException {
		return hasAttribute(TheoryAttributes.COMPLETE_ATTRIBUTE);
	}
	
	@Override
	public boolean hasInteractive() throws RodinDBException {
		return hasAttribute(TheoryAttributes.INTERACTIVE_ATTRIBUTE);
	}

	public boolean hasLHSString() throws RodinDBException {
		return hasAttribute(TheoryAttributes.LHS_ATTRIBUTE);
	}

	public boolean hasRHSString() throws RodinDBException {
		return (hasAttribute(TheoryAttributes.RHS_ATTRIBUTE));
	}
	
	public boolean isAutomatic() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.AUTOMATIC_ATTRIBUTE);
	}
	
	@Override
	public boolean isComplete() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.COMPLETE_ATTRIBUTE);
	}
	
	@Override
	public boolean isInteractive() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.INTERACTIVE_ATTRIBUTE);
	}

	public void setAutomatic(boolean auto, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.AUTOMATIC_ATTRIBUTE, auto, pm);
	}
	
	@Override
	public void setComplete(boolean isComplete, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.COMPLETE_ATTRIBUTE, isComplete, pm);
		
	}

	@Override
	public void setInteractive(boolean isInteractive, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.INTERACTIVE_ATTRIBUTE, isInteractive, pm);
		
	}

	public void setLHSString(String form, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.LHS_ATTRIBUTE, form, pm);

	}


	@Override
	public void setRHSString(String form, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.RHS_ATTRIBUTE, form, pm);
		
	}
	
	@Override
	public String getCategory() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.CATEGORY_ATTRIBUTE);
	}

	@Override
	public boolean hasCategory() throws RodinDBException {
		return hasAttribute(TheoryAttributes.CATEGORY_ATTRIBUTE);
	}

	@Override
	public void setCategory(String newCat, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.CATEGORY_ATTRIBUTE, newCat, pm);
		
	}
	
	public boolean hasToolTip() throws RodinDBException{
		return hasAttribute(TheoryAttributes.TOOL_TIP_ATTRIBUTE);
	}
	
	public String getToolTip() throws RodinDBException{
		return getAttributeValue(TheoryAttributes.TOOL_TIP_ATTRIBUTE);
	}
	
	public void setToolTip(String newToolTip, IProgressMonitor monitor) throws RodinDBException{
		setAttributeValue(TheoryAttributes.TOOL_TIP_ATTRIBUTE,newToolTip, monitor);
	}

	public boolean hasDescription() throws RodinDBException{
		return hasAttribute(TheoryAttributes.DESC_ATTRIBUTE);
	}
	
	public String getDescription() throws RodinDBException{
		return getAttributeValue(TheoryAttributes.DESC_ATTRIBUTE);
	}
	
	public void setDescription(String newDescription, IProgressMonitor monitor) throws RodinDBException{
		setAttributeValue(TheoryAttributes.DESC_ATTRIBUTE, newDescription, monitor);
	}
}
