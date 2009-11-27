package ac.soton.eventb.prover.internal.tactic;

import java.util.StringTokenizer;

import org.eventb.core.ast.Formula;

import ac.soton.eventb.prover.plugin.ProverPlugIn;

/**
 * <p>A placeholder for applicability information of a rewrite rule.</p>
 *
 * @author maamria
 *
 */
public class RewriteRuleApplicabilityInfo{

	/**
	 * Helper enumeration.
	 * @author maamria
	 *
	 */
	public static enum InfoType{
		/**
		 * for the description of the rewrite rule
		 */
		Description, 
		/**
		 * for the rule name
		 */
		RuleName, 
		/**
		 * for the parent theory name
		 */
		TheoryName, 
		/**
		 * for the rule tool tip
		 */
		ToolTip
	}
	
	// choose a different separator maybe?
	private static final String PATH_SEP = "-::-";
	
	private String desc = null;
	private boolean isConditional = false;
	private String ruleName = null;
	private String rulePath = null;
	private Formula<?> subFormula = null;
	private String theoryName = null;
	private String toolTip = null;
	
	/**
	 * <p>Constructs an object to hold the applicability of the given rule.</p>
	 * @param subFormula for which the rule was applicable
	 * @param theory 
	 * @param rule
	 * @param toolTip
	 * @param desc
	 */
	public RewriteRuleApplicabilityInfo(Formula<?> subFormula, String  theory, String rule, boolean isConditional, String toolTip, String desc){
		this.ruleName = rule;
		this.theoryName = theory;
		this.toolTip = toolTip;
		this.desc = desc;
		this.subFormula = subFormula;
		this.isConditional = isConditional;
		this.rulePath = 
			ProverPlugIn.PLUGIN_ID+PATH_SEP+theory+PATH_SEP+rule+PATH_SEP+toolTip+PATH_SEP+desc;
	}
	
	
	/**
	 * Two <code>RewriteRuleApplicabilityInfo</code> objects are equal if they refer to the same rule and store the same subformula.
	 * <p>
	 * @param other another object
	 * @return whether this object equals <code>other</code>
	 */
	public boolean equals(Object other){
		if(other instanceof RewriteRuleApplicabilityInfo){
			RewriteRuleApplicabilityInfo info = 
				((RewriteRuleApplicabilityInfo) other);
			return 
				info.ruleName.equals(ruleName) &&
				info.theoryName.equals(theoryName) &&
				info.subFormula.equals(subFormula) ;
		}
		return false;
	}
	/**
	 * Returns the description of the applicable rule.
	 * @return the description
	 */
	public String getDescription() {
		return desc;
	}
	/**
	 * Returns the name of the applicable rule.
	 * @return the name of rule
	 */
	public String getRuleName() {
		return ruleName;
	}
	/**
	 * Returns the rule path that can be used to trace the rule.
	 * <p>The format is: <pre>plugin-::-theory-::-rule-::-toolTip-::-desc</pre></p>
	 * @return the rule path in the rule base
	 */
	public String getRulePath() {
		return rulePath;
	}
	/**
	 * Returns the subformula for which the rule was applicable.
	 * @return the subformula
	 */
	public Formula<?> getSubFormula() {
		return subFormula;
	}
	/**
	 * Returns the parent theory name of the applicable rule.
	 * @return theory name
	 */
	public String getTheoryName() {
		return theoryName;
	}

	/**
	 * Returns the tool tip associated with the applicable rule.
	 * @return the tool tip
	 */
	public String getToolTip() {
		return toolTip;
	}
	/**
	 * Modified to maintain <code>Object.hashcode()</code> contract.
	 * <p>
	 * @return the hashcode
	 */
	public int hashCode(){
		return (int)(ruleName.hashCode()+theoryName.hashCode() * 0.2);
	}
	/**
	 * Returns whether the applicable rule is conditional.
	 * @return rule conditionality
	 */
	public boolean isConditional(){
		return isConditional;
	}
	
	/**
	 * Decodes the given path to return the information fo the given type <code>type</code>.
	 * @param rulePath the rule path
	 * @param type the type of information required
	 * @return the information string or <code>null</code> if <code>rulePath</code> is not of correct format
	 */
	public static String getInfo(String rulePath, InfoType type){
		// the format is : plugin-::-theory-::-rule-::-toolTip-::-desc
		StringTokenizer tokeniser = new StringTokenizer(rulePath, 
				PATH_SEP);
		if(tokeniser.countTokens()!=5){
			return null;
		}
		switch (type){
			case TheoryName: return getToken(tokeniser, 1);
			case RuleName: return getToken(tokeniser, 2);
			case ToolTip: return getToken(tokeniser, 3);
			case Description: return getToken(tokeniser, 4);
		}
		return null;
	}
	
	/**
	 * Utility to get the token given by <code>index</code>.
	 * @param tokeniser the string tokeniser that holds all inforamtion
	 * @param index the index of the token 
	 * @return the token at <code>index</code>
	 */
	private static String getToken (StringTokenizer tokeniser , int index){
		assert index >= 0 && index < tokeniser.countTokens();
		for(int i = 0; i < index; i++){
			tokeniser.nextToken();
		}
		return tokeniser.nextToken();
	}
	
}
