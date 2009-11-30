package ac.soton.eventb.prover.reasoner;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * <p>The input to this reasoner includes the predicate, the position as well as rule-related information.</p>
 * @author maamria
 *
 */
public class Input implements IReasonerInput{
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
	public Input(String theoryName, String ruleName, String ruleDesc,
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
