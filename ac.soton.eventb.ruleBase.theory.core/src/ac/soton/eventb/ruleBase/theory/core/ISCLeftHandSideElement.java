package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an internal element that has a statically checked left hand side theory formula.
 * <p>Theory formulas can either be an expression or a predicate.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ISCLeftHandSideElement extends IInternalElement {
	/**
	 * <p>Returns the left hand side formula parsed and type-checked.</p>
	 * @param factory
	 * @param typenv
	 * @return the lhs formula
	 * @throws RodinDBException
	 */
	Formula<?> getLHSFormula(FormulaFactory factory, ITypeEnvironment typenv)
			throws RodinDBException;
	/**
	 * <p>Returns the value of the lhs as a string.</p>
	 * @return the lhs string value
	 * @throws RodinDBException
	 */
	String getLHSString() throws RodinDBException;
	/**
	 * <p>Sets the SC lhs attribute to the given theory formula.</p>
	 * @param form the new formula
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setLHSFormula(Formula<?> form, IProgressMonitor monitor)
			throws RodinDBException;

}
