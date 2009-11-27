package ac.soton.eventb.ruleBase.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>Common protocol for a theory root.</p>
 * <p> This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ITheoryRoot extends IEventBRoot, ICommentedElement,
		IConfigurationElement{

	IInternalElementType<ITheoryRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".theoryFile");
	/**
	 * <p>Returns the rewrite rule corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the rule
	 * @return the rule
	 */
	IRewriteRule getRewriteRule(String ruleName);
	/**
	 * <p>Returns all the rules that are sub-elements of this root.</p>
	 * @return all rules
	 * @throws RodinDBException
	 */
	IRewriteRule[] getRewriteRules() throws RodinDBException;
	/**
	 * <p>Returns the SC theory file corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the rodin file
	 */
	IRodinFile getSCTheoryFile(String bareName);
	/**
	 * <p>Returns the SC theory root corresponding to this element.</p>
	 * <p>This is handle-only method.</p>
	 * @return the SC theory root
	 */
	ISCTheoryRoot getSCTheoryRoot();
	/**
	 * <p>Returns the SC theory root corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the SC theory root
	 */
	ISCTheoryRoot getSCTheoryRoot(String bareName);
	/**
	 * <p>Returns the set corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the set
	 * @return
	 */
	ISet getSet(String setName);
	/**
	 * <p>Returns all the sets that are sub-elements of this root.</p>
	 * @return all sets
	 * @throws RodinDBException
	 */
	ISet[] getSets() throws RodinDBException;
	/**
	 * <p>Returns the variable corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the variable
	 * @return
	 */
	IVariable getVariable(String varName);
	/**
	 * <p>Returns all the variables that are sub-elements of this root.</p>
	 * @return all variables
	 * @throws RodinDBException
	 */
	IVariable[] getVariables() throws RodinDBException;
	/**
	 * <p>Returns all the categories with which this theory is associated.</p>
	 * @return all categories
	 * @throws RodinDBException
	 */
	ICategory[] getCategories() throws RodinDBException;
	
	/**
	 * <p>Returns the category corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the category
	 * @return
	 */
	ICategory getCategory(String catName);
}
