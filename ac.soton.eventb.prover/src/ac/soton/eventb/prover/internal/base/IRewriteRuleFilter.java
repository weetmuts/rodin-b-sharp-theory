package ac.soton.eventb.prover.internal.base;


/**
 * Common protocol for a filter of rewrite rules.
 * <p>A filter provides a mechansim to verify whether a given rewrite rule satisfies a criterion.</p>
 * @author maamria
 *
 */
interface IRewriteRuleFilter {
	
	/**
	 * Returns whether the rewrite rule <code>rule</code> satisfies a specifies criterion.
	 * @param rule to check
	 * @return whether <code>rule</code> satisfies the criterion
	 */
	public boolean filter(IDRewriteRule rule);
	
}
