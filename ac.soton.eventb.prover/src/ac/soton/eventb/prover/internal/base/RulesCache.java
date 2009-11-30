package ac.soton.eventb.prover.internal.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ac.soton.eventb.ruleBase.theory.deploy.DeployedTheoriesManager;
import ac.soton.eventb.ruleBase.theory.deploy.IDRewriteRule;
import ac.soton.eventb.ruleBase.theory.deploy.IDTheoryFile;
import ac.soton.eventb.ruleBase.theory.deploy.IDeployedTheoriesManager;

/**
 * <p>
 * A simple cache implementation. This class is internal API.
 * </p>
 * 
 * @author maamria
 * 
 */
public class RulesCache {

	/**
	 * The lis of all rules
	 */
	private List<IDRewriteRule> allRules;
	/**
	 * The cache of recently used rules
	 */
	private LinkedHashMap<String, IDRewriteRule> rulesCache;
	/**
	 * The maximum number of entries in the rules cache
	 */
	private final int MAX_RULES_CACHE = 20;
	/**
	 * The list of automatic conditional rules
	 */
	private LinkedHashMap<Class<?>, List<IDRewriteRule>> autoCondCache;
	/**
	 * The list of automatic unconditional rules
	 */
	private LinkedHashMap<Class<?>, List<IDRewriteRule>> autoUncondCache;
	/**
	 * The list of interactive rewrite rules
	 */
	private LinkedHashMap<Class<?>, List<IDRewriteRule>> interCache;

	private IDeployedTheoriesManager manager;

	/**
	 * Creates a rules cache loading all the rules from the all available validated theory files.
	 */
	public RulesCache() {
		manager = DeployedTheoriesManager.getDefault();
		
		autoCondCache =  new LinkedHashMap<Class<?>, List<IDRewriteRule>>();
		autoUncondCache = new LinkedHashMap<Class<?>, List<IDRewriteRule>>();
		interCache = new LinkedHashMap<Class<?>, List<IDRewriteRule>>();
		// a map that removes the least recently accessed entry when larger than MAX_RULES_CACHE
		rulesCache = new LinkedHashMap<String, IDRewriteRule>(16, 0.75f, true){
			
			private static final long serialVersionUID = 1L;
			
			protected boolean removeEldestEntry(Map.Entry<String, IDRewriteRule> eldest) {
		        return size() > MAX_RULES_CACHE;
		     }

		};
		allRules = new ArrayList<IDRewriteRule>();
		
		populateAllRules();
	}

	public List<IDRewriteRule> getInteractiveRules(Class<?> clazz){
		return getRules(clazz, 
				interCache, 
				new IRewriteRuleFilter(){

					public boolean filter(IDRewriteRule rule) {
						// TODO Auto-generated method stub
						return rule.isInteracive();
					}
			
		});
	}
	
	public List<IDRewriteRule> getAutoUnconditionalRules(Class<?> clazz){
		return getRules(clazz, 
				autoUncondCache, 
				new IRewriteRuleFilter(){

					public boolean filter(IDRewriteRule rule) {
						// TODO Auto-generated method stub
						return rule.isAutomatic() && 
								!rule.isConditional();
					}
			
		});
	}
	
	public List<IDRewriteRule> getAutoConditionalRules(Class<?> clazz){
		return getRules(clazz, 
				autoCondCache, 
				new IRewriteRuleFilter(){

					public boolean filter(IDRewriteRule rule) {
						// TODO Auto-generated method stub
						return rule.isAutomatic() &&
								rule.isConditional();
					}
			
		});
	}
	
	public IDRewriteRule getInteractiveRule(String theoryName, String ruleName, Class<?> clazz){
		String key = makeRuleKey(theoryName, ruleName);
		if(rulesCache.containsKey(key)){
			return rulesCache.get(key);
		}
		for (IDRewriteRule rule : getInteractiveRules(clazz)){
			if(rule.getTheoryName().equals(theoryName) &&
					rule.getRuleName().equals(ruleName)){
				rulesCache.put(key, 
						rule);
				return rule;
			}
		}
		return null;
	}
	
	protected void populateAllRules() {
		List<IDTheoryFile> theories = manager.getTheories();
		for (IDTheoryFile theory : theories) {
			allRules.addAll(theory.getExpressionRewriteRules());
			allRules.addAll(theory.getPredicateRewriteRules());
		}
	}
	
	//****************************************************************
	//****************************************************************
	//***************Utilities
	//****************************************************************
	/**
	 * A unique key for the rule using the given parameters
	 */
	private String makeRuleKey(String theoryName, String ruleName){
		return theoryName+"."+ruleName;
	}

	/**
	 * Returns a list of rewrite rules that: 
	 * <ul>
	 * 	<li>passes the check of <code>filter</code>;
	 * 	<li>belongs to <code>cache</code> ;
	 * 	<li>its left hand side is of the class <code>clazz</code>.
	 * </ul>
	 * <p>If the supplied cache does not contain an entry for <code>clazz</code>, it will be added.</p>
	 * @param clazz
	 * @param cache
	 * @param filter
	 * @return
	 */
	private List<IDRewriteRule> getRules(Class<?> clazz, 
			LinkedHashMap<Class<?>, List<IDRewriteRule>> cache, 
			IRewriteRuleFilter filter ){
		if(cache.containsKey(clazz)){
			return cache.get(clazz);
		}
		List<IDRewriteRule> list = new ArrayList<IDRewriteRule>();
		for(IDRewriteRule rule : allRules){
			if(
				filter.filter(rule) && 
				rule.getLeftHandSide().getClass().equals(clazz))
			{
				list.add(rule);
			}
		}
		list = Collections.unmodifiableList(list);
		cache.put(clazz, list);
		return list;
	}
	
}