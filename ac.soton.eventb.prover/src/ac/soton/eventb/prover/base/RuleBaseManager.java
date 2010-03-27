package ac.soton.eventb.prover.base;

import java.util.List;

import ac.soton.eventb.prover.internal.base.IDRewriteRule;
import ac.soton.eventb.prover.internal.base.RulesCache;

/**
 * <p>An implementation of a rule base manager.</p>
 * <p>Clients should refrain from using this class directly and use <code>IRuleBaseManager</code> instead.</p>
 * @author maamria
 *
 */
public class RuleBaseManager implements IRuleBaseManager{

	private static RuleBaseManager instance;
	private RulesCache cache ;
	
	private RuleBaseManager(){
		cache = new RulesCache();
	}

	/**
	 * Returns the default instance of the rule base manager.
	 * @return the singeleton instance
	 */
	public static RuleBaseManager getDefault(){
		if(instance == null)
			instance = new RuleBaseManager();
		return instance;
	}

	public List<IDRewriteRule> getAutoConditionalRules(
			Class<?> clazz) {
		// TODO Auto-generated method stub
		return cache.getAutoConditionalRules(clazz);
	}

	public List<IDRewriteRule> getAutoUnconditionalRules(
			Class<?> clazz) {
		// TODO Auto-generated method stub
		return cache.getAutoUnconditionalRules(clazz);
	}

	public List<IDRewriteRule> getInteractiveRules(
			Class<?> clazz) {
		// TODO Auto-generated method stub
		return cache.getInteractiveRules(clazz);
	}

	public IDRewriteRule getInteractiveRule(String ruleName, String theoryName, Class<?> clazz) {
		// TODO Auto-generated method stub
		return cache.getInteractiveRule(ruleName, theoryName, clazz);
	}

	public void reload() {
		cache = new RulesCache();
		
	}

}
