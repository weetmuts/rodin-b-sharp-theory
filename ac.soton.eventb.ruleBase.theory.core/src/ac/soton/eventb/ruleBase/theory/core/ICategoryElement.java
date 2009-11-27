package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * Common protocol for an element that can be associated with a category.
 * <p>
 * Theories can belong to a certain category or multiple categories.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @author maamria
 *
 */
public interface ICategoryElement extends IInternalElement{

	/**
	 * <p> Tests whether the category attribute is set.</p>
	 * @return whether the category attribute is present.
	 * @throws RodinDBException
	 */
	boolean hasCategory() throws RodinDBException;
	
	/**
	 * <p>Returns the value of the category.</p>
	 * @return the category
	 * @throws RodinDBException
	 */
	String getCategory() throws RodinDBException;
	
	/**
	 * <p> This can be used to set the value of the category to <code>newCat</code>.</p>
	 * @param newCat the new value
	 * @param pm the progress monitor
	 * @throws RodinDBException
	 */
	void setCategory(String newCat, IProgressMonitor pm) 
		throws RodinDBException;
	
}
