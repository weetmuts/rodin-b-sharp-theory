/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.RHS_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IRightHandSideElement;

/**
 * @author maamria
 * 
 */
public class RHSFormulaAttributeManipulation extends
		AbstractAttributeManipulation {

	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		logCantGetPossibleValues(RHS_ATTRIBUTE);
		return null;
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asFormula(element).getRHSString();
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asFormula(element).hasRHSString();

	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(RHS_ATTRIBUTE);
	}

	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asFormula(element).setRHSString("", monitor);
	}

	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asFormula(element).setRHSString(newValue, monitor);
	}

	private IRightHandSideElement asFormula(IRodinElement element) {
		assert element instanceof IRightHandSideElement;
		return (IRightHandSideElement) element;
	}
}
