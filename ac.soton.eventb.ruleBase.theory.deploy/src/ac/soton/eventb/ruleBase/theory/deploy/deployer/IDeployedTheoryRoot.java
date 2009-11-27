package ac.soton.eventb.ruleBase.theory.deploy.deployer;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ICategoryElement;
import ac.soton.eventb.ruleBase.theory.deploy.plugin.TheoryDeployPlugIn;

/**
 * <p>Common protocol for a deployed theory root.</p>
 * @author maamria
 *
 */
public interface IDeployedTheoryRoot extends ICategoryElement{

	IInternalElementType<IDeployedTheoryRoot> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryDeployPlugIn.PLUGIN_ID + ".deployedTheory");
	
	/**
	 * <p>Returns a meta set with the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name
	 * @return the meta set with the given name
	 */
	IMetaSet getMetaSet(String name);
	
	/**
	 * <p>Returns all met sets of the theory.</p>
	 * @return all meta sets
	 * @throws RodinDBException if a problem occurred
	 */
	IMetaSet[] getMetaSets() throws RodinDBException;
	
	/**
	 * <p>Returns a meta variable with the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name
	 * @return the meta variable with the given name
	 */
	IMetaVariable getMetaVariable(String name);
	
	/**
	 * <p>Returns all meta variables of the theory.</p>
	 * @return all meta variables
	 * @throws RodinDBException if a problem occurred
	 */
	IMetaVariable[] getMetaVariables() throws RodinDBException;
	
	/**
	 * <p>Returns a rewrite rule with the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name
	 * @return the rewrite rule with the given name
	 */
	IDeployedRewriteRule getRewriteRule(String ruleName);
	
	/**
	 * <p>Returns all rewrite rules of the theory.</p>
	 * @return all rewrite rules
	 * @throws RodinDBException if a problem occurred
	 */
	IDeployedRewriteRule[] getRewriteRules() throws RodinDBException;
	
}
