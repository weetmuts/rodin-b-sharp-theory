package ac.soton.eventb.ruleBase.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
/**
 * <p>Common interface for a statically checked theory root element.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ISCTheoryRoot extends IEventBRoot, IAccuracyElement,
		IConfigurationElement{

	IInternalElementType<ISCTheoryRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scTheoryFile");

	/**
	 * <p>Returns the SC rule corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the rule
	 * @return the SC rule
	 */
	ISCRewriteRule getSCRewriteRule(String name);
	/**
	 * <p>Returns all the SC rules that are sub-elements of this root.</p>
	 * @return all SC rules
	 * @throws RodinDBException
	 */
	ISCRewriteRule[] getSCRewriteRules() throws RodinDBException;

	/**
	 * <p>Returns the SC set corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the set
	 * @return
	 */
	ISCSet getSCSet(String name);

	/**
	 * <p>Returns all the SC sets that are sub-elements of this root.</p>
	 * @return all SC sets
	 * @throws RodinDBException
	 */
	ISCSet[] getSCSets() throws RodinDBException;
	/**
	 * <p>Returns the SC variable corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the variable
	 * @return
	 */
	ISCVariable getSCVariable(String name);
	/**
	 * <p>Returns all the SC variables that are sub-elements of this root.</p>
	 * @return all SC variables
	 * @throws RodinDBException
	 */
	ISCVariable[] getSCVariables() throws RodinDBException;
	
	/**
	 * <p>Returns the category corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the category
	 * @return
	 */
	ICategory getCategory(String catName);
	
	/**
	 * <p>Returns all the categories with which this theory is associated.</p>
	 * @return all categories
	 * @throws RodinDBException
	 */
	ICategory[] getCategories() throws RodinDBException;
	/**
	 * <p>Returns the global type environment of this SC theory.</p>
	 * @param factory
	 * @return the type environment
	 * @throws RodinDBException
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException;

}
