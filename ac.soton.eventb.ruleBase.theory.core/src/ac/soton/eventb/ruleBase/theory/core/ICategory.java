package ac.soton.eventb.ruleBase.theory.core;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p> Common protocol for a category internal element.</p>
 * <p> This is used to associate its parent theory with a pre-defined category.</p>
 * @author maamria
 *
 */
public interface ICategory extends ICategoryElement{

	IInternalElementType<ICategory> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".category");
	
	/**
	 * <p> Returns the category string value of this element.</p>
	 * @return the category string
	 */
	String getCategory() throws RodinDBException;

}
