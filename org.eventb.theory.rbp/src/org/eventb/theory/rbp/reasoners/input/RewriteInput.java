package org.eventb.theory.rbp.reasoners.input;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * <p>The input to this reasoner includes the predicate, the position as well as rule-related information.</p>
 * @since 1.0
 * @author maamria
 *
 */
public class RewriteInput implements IReasonerInput{
	
	public IPosition position;
	public Predicate pred;
	public String ruleDesc;
	public String ruleName;
	public String theoryName;
	
	/**
	 * Constructs an input with the given parameters.
	 * @param theoryName the parent theory
	 * @param ruleName the name of the rule to apply
	 * @param ruleDesc the description to display if rule applied successfully
	 * @param pred 
	 * @param position 
	 */
	public RewriteInput(String theoryName, String ruleName, String ruleDesc,
			Predicate pred, IPosition position){
		this.position = position;
		this.pred = pred;
		this.ruleDesc = ruleDesc;
		this.ruleName = ruleName;
		this.theoryName = theoryName;
	}
	
	public void applyHints(ReplayHints renaming) {
		if(pred !=null){
			renaming.applyHints(pred);
		}
		
	}
	
	public String getError() {
		return null;
	}
	
	public boolean hasError() {
		return false;
	}
}
