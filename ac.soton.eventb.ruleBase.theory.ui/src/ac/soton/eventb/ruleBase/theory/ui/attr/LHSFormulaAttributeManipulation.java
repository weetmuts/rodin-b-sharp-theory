package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.LHS_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ILeftHandSideElement;

public class LHSFormulaAttributeManipulation extends
		AbstractAttributeManipulation {

	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		logCantGetPossibleValues(LHS_ATTRIBUTE);
		return null;
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asFormula(element).getLHSString();
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asFormula(element).hasLHSString();

	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(LHS_ATTRIBUTE);
	}

	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asFormula(element).setLHSString("", monitor);
	}

	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asFormula(element).setLHSString(newValue, monitor);
	}

	private ILeftHandSideElement asFormula(IRodinElement element) {
		assert element instanceof ILeftHandSideElement;
		return (ILeftHandSideElement) element;
	}
}
