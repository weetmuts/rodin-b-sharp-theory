package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.COMPLETE_ATTRIBUTE;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ICompleteElement;
import ac.soton.eventb.ruleBase.theory.ui.util.Messages;

public class CompleteAttributeManipulation extends AbstractBooleanManipulation{

	public CompleteAttributeManipulation() {
		super(Messages.rewriteRule_isComplete, Messages.rewriteRule_isIncomplete);
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return getText(asComplete(element).getAttributeValue(COMPLETE_ATTRIBUTE));
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asComplete(element).hasComplete();
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asComplete(element).removeAttribute(COMPLETE_ATTRIBUTE, monitor);

	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asComplete(element).setComplete(false, monitor);

	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(TRUE)){
			asComplete(element).setComplete(true, monitor);
		}
		else if(value.equals(FALSE)){
			asComplete(element).setComplete(false, monitor);
		}
		else {
			logNotPossibleValues(COMPLETE_ATTRIBUTE, value);
		}

	}

	ICompleteElement asComplete(IRodinElement element){
		assert element instanceof ICompleteElement;
		return (ICompleteElement) element;
	}
}
