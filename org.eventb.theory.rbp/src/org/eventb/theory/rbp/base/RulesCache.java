package org.eventb.theory.rbp.base;

import static org.eventb.theory.rbp.utils.ProverUtilities.safeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.internal.base.DeployedTheoriesManager;
import org.eventb.theory.rbp.internal.base.IDeployedInferenceRule;
import org.eventb.theory.rbp.internal.base.IDeployedRewriteRule;
import org.eventb.theory.rbp.internal.base.IDeployedTheoriesManager;
import org.eventb.theory.rbp.internal.base.IDeployedTheoryFile;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 * A simple cache implementation. This class is internal API.
 * </p>
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("rawtypes")
public class RulesCache {

	/**
	 * The list of all rules
	 */
	private List<IDeployedRewriteRule> allRewriteRules;

	private List<IDeployedInferenceRule> allInferenceRules;
	/**
	 * The cache of recently used rules
	 */
	private Map<String, IDeployedRewriteRule> rewriteRulesCache;

	private Map<String, IDeployedInferenceRule> inferenceRulesCache;
	/**
	 * The maximum number of entries in the rules cache
	 */
	private static final int MAX_RULES_CACHE = 20;
	/**
	 * The list of automatic unconditional rules
	 */
	
	private Map<Class<? extends Formula>, List<IDeployedRewriteRule>> autoRewritesMap;
	/**
	 * The list of interactive rewrite rules
	 */
	private Map<Class<? extends Formula>, List<IDeployedRewriteRule>> interRewriteMap;

	private Map<ReasoningType, List<IDeployedInferenceRule>> autoTypedInferenceMap;

	private Map<ReasoningType, List<IDeployedInferenceRule>> interTypedInferenceMap;

	private IDeployedTheoriesManager manager;

	/**
	 * Creates a rules cache loading all the rules from the all available
	 * validated theory files.
	 */
	public RulesCache(FormulaFactory factory) {
		manager = new DeployedTheoriesManager(factory);

		autoRewritesMap = new LinkedHashMap<Class<? extends Formula>, List<IDeployedRewriteRule>>();
		interRewriteMap = new LinkedHashMap<Class<? extends Formula>, List<IDeployedRewriteRule>>();
		autoTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IDeployedInferenceRule>>();
		interTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IDeployedInferenceRule>>();

		rewriteRulesCache = new Cache<IDeployedRewriteRule>();
		inferenceRulesCache = new Cache<IDeployedInferenceRule>();

		allRewriteRules = new ArrayList<IDeployedRewriteRule>();
		allInferenceRules = new ArrayList<IDeployedInferenceRule>();

		populateAllRules();
	}

	/**
	 * Returns the list of rewrite rules.
	 * 
	 * @return rewrite rules
	 */
	public List<IDeployedRewriteRule> getRewriteRules() {
		return allRewriteRules;
	}

	/**
	 * Returns the list of rewrite rules satisfying the given criteria.
	 * 
	 * @param automatic
	 * @param conditional
	 * @param clazz
	 * @return rewrite rules
	 */
	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic,
			Class<? extends Formula> clazz) {
		if (automatic) {
			return getRules(clazz, autoRewritesMap);
		} else {
			return getRules(clazz, interRewriteMap);
		}
	}

	/**
	 * Returns the list of rewrite rules satisfying the given criteria.
	 * 
	 * @param automatic
	 * @param conditional
	 * @return rewrite rules
	 */
	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic){
		if(automatic){
			return safeList(ProverUtilities.mergeLists(autoRewritesMap));
		}
		else {
			return safeList(ProverUtilities.mergeLists(interRewriteMap));
		}
	}

	/**
	 * Returns the list of deployed inference rules.
	 * 
	 * @return deployed rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(){
		return allInferenceRules;
	}

	/**
	 * Returns the list of inference rules suitable for the given reasoning
	 * type.
	 * 
	 * @param type
	 *            the reasoning type
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(ReasoningType type){
		List<IDeployedInferenceRule> result = new ArrayList<IDeployedInferenceRule>();
		result.addAll(getRules(type, autoTypedInferenceMap));
		result.addAll(getRules(type, interTypedInferenceMap)); 
		return result;
	}

	/**
	 * Returns the list of inference rules that are either automatic or
	 * interactive.
	 * 
	 * @param automatic
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic){
		if(automatic){
			return safeList(ProverUtilities.mergeLists(autoTypedInferenceMap));
		}
		else 
			return safeList(ProverUtilities.mergeLists(interTypedInferenceMap));
	}

	/**
	 * Returns the list of inference rules satisfying the given criteria.
	 * 
	 * @param type
	 *            reasoning type
	 * @param automatic
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(ReasoningType type,
			boolean automatic){
		if (automatic){
			return safeList(autoTypedInferenceMap.get(type));
		}
		else {
			return safeList(interTypedInferenceMap.get(type));
		}
	}

	/**
	 * Returns the interactive deployed inference rule by the given name.
	 * 
	 * @param ruleName
	 *            the name of the rule
	 * @param theoryName
	 *            the theory name
	 * @return the deployed rule
	 */
	public IDeployedInferenceRule getInferenceRule(String ruleName,
			String theoryName){
		String key = makeRuleKey(theoryName, ruleName);
		if(!inferenceRulesCache.containsKey(key)){
			for (IDeployedInferenceRule inf : allInferenceRules){
				if(inf.getTheoryName().equals(theoryName) && inf.getRuleName().equals(ruleName)){
					inferenceRulesCache.put(key, inf);
					break;
				}
			}
		}
		return inferenceRulesCache.get(key);
	}

	/**
	 * Returns the interactive rule specified by its name, the name of its
	 * parent theory as well as the runtime class of its lhs.
	 * 
	 * @param ruleName
	 *            name of the rule
	 * @param theoryName
	 *            name of the parent theory
	 * @param clazz
	 *            the class of the lhs of the rule
	 * @return the rule or <code>null</code> if not found
	 */
	public <E extends Formula<? extends Formula<?>>> IDeployedRewriteRule getInteractiveRule(String ruleName,
			String theoryName, Class<E> clazz){
		String key = makeRuleKey(theoryName, ruleName);
		if(!rewriteRulesCache.containsKey(key)){
			for (IDeployedRewriteRule rule : getRewriteRules(false, clazz)){
				if(rule.getTheoryName().equals(theoryName) &&
						rule.getRuleName().equals(ruleName)){
					rewriteRulesCache.put(key, rule);
					break;
				}
			}
		}
		return rewriteRulesCache.get(key);
	}

	protected synchronized void populateAllRules() {
		List<IDeployedTheoryFile> theories = manager.getTheories();
		for (IDeployedTheoryFile theory : theories) {
			List<IDeployedRewriteRule> rewRules = theory.getRewriteRules();
			List<IDeployedInferenceRule> infRules = theory.getInferenceRules();
			allRewriteRules.addAll(rewRules);
			allInferenceRules.addAll(infRules);
			for(IDeployedRewriteRule rule : rewRules){
				if(rule.isAutomatic()){
					Class<? extends Formula> clazz = rule.getLeftHandSide().getClass();
					if(!autoRewritesMap.containsKey(clazz)){
						autoRewritesMap.put(clazz, new ArrayList<IDeployedRewriteRule>());
					}
					autoRewritesMap.get(clazz).add(rule);
				}
				else{
					Class<? extends Formula> clazz = rule.getLeftHandSide().getClass();
					if(!interRewriteMap.containsKey(clazz)){
						interRewriteMap.put(clazz, new ArrayList<IDeployedRewriteRule>());
					}
					interRewriteMap.get(clazz).add(rule);
				}
			}
			for(IDeployedInferenceRule rule : infRules){
				if (rule.isAutomatic()){
					ReasoningType type = rule.getReasoningType();
					if(type.equals(ReasoningType.BACKWARD_AND_FORWARD)){
						handleDualInfRule(rule, autoTypedInferenceMap);
					}
					if(autoTypedInferenceMap.get(type) == null){
						autoTypedInferenceMap.put(type, new ArrayList<IDeployedInferenceRule>());
					}
					autoTypedInferenceMap.get(type).add(rule);
				}
				else {
					ReasoningType type = rule.getReasoningType();
					if(type.equals(ReasoningType.BACKWARD_AND_FORWARD)){
						handleDualInfRule(rule, interTypedInferenceMap);
					}
					if(interTypedInferenceMap.get(type) == null){
						interTypedInferenceMap.put(type, new ArrayList<IDeployedInferenceRule>());
					}
					interTypedInferenceMap.get(type).add(rule);
				}
			}
		}
	}

	// ****************************************************************
	// ****************************************************************
	// ***************Utilities
	// ****************************************************************
	/**
	 * A unique key for the rule using the given parameters
	 */
	private String makeRuleKey(String theoryName, String ruleName) {
		return theoryName + "." + ruleName;
	}

	/**
	 * Returns a list of rewrite rules that:
	 * <ul>
	 * <li>belongs to <code>cache</code> ;
	 * <li>its left hand side is of the class <code>clazz</code>.
	 * </ul>
	 * <p>
	 * If the supplied cache does not contain an entry for <code>clazz</code>,
	 * it will be added.
	 * </p>
	 * 
	 * @param clazz
	 * @param cache
	 * @return rewrite rules
	 */
	protected List<IDeployedRewriteRule> getRules(
			Class<? extends Formula> clazz,
			Map<Class<? extends Formula>, List<IDeployedRewriteRule>> cache) {
		return Collections.unmodifiableList(safeList(cache.get(clazz)));
	}
	
	protected void handleDualInfRule(IDeployedInferenceRule rule, 
			Map<ReasoningType, List<IDeployedInferenceRule>> map){
		assert rule.getReasoningType().equals(ReasoningType.BACKWARD_AND_FORWARD);
		if(map.get(ReasoningType.BACKWARD) == null){
			map.put(ReasoningType.BACKWARD, new ArrayList<IDeployedInferenceRule>());
		}
		map.get(ReasoningType.BACKWARD).add(rule);
		if(map.get(ReasoningType.FORWARD) == null){
			map.put(ReasoningType.FORWARD, new ArrayList<IDeployedInferenceRule>());
		}
		map.get(ReasoningType.FORWARD).add(rule);
	}

	/**
	 * Returns a list of inference rules that:
	 * <ul>
	 * <li>has reasoning type <code>type</code>;
	 * <li>might belongs to <code>cache</code> ;
	 * </ul>
	 * <p>
	 * If the supplied cache does not contain an entry for <code>type</code>, it
	 * will be added.
	 * </p>
	 * 
	 * @param type
	 * @param cache
	 * @return inference rules
	 */
	protected List<IDeployedInferenceRule> getRules(ReasoningType type,
			Map<ReasoningType, List<IDeployedInferenceRule>> cache) {
		
		return Collections.unmodifiableList(safeList(cache.get(type)));
		
	}
	
	protected static class Cache<E> extends LinkedHashMap<String, E> {

		private static final long serialVersionUID = 759349349276800846L;

		public Cache() {
			super(16, 0.75f);
		}

		protected boolean removeEldestEntry(Map.Entry<String, E> eldest) {
			return size() > MAX_RULES_CACHE;
		}

	}
}