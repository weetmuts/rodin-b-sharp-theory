package ac.soton.eventb.ruleBase.theory.deploy;

import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;

/**
 * <p>Common protocol for a deployed theory.</p>
 * <p>Objects of this type correspond directly to theory files (.thy) in the deployment directory.</p>
 * <p>A theory has a collection of rules. Rewrite rules can either be expression rules (their lhs are expressions) or 
 * predicate rules (their lhs are predicates).</p>
 * @author maamria
 *
 */
public interface IDTheoryFile {

	/**
	 * <p>Returns a list of rewrite rules whose left hand side is an expression.</p>
	 * @return list of rules
	 */
	public List<IDRewriteRule> getExpressionRewriteRules();
	/**
	 * <p>Returns the type environment of this theory.</p>
	 * @return the type environment
	 */
	public ITypeEnvironment getGloablTypeEnvironment();
	
	/**
	 * <p>Returns a list of rewrite rules whose left hand side is a predicate.</p>
	 * @return list of rules
	 */
	public List<IDRewriteRule> getPredicateRewriteRules();
	
	/**
	 * <p>Returns the theory name.</p>
	 * @return the name with extension
	 */
	public String getTheoryName();
}
