package org.eventb.theory.rbp.reasoners.input;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * <p>The input to this reasoner includes the predicate, the position as well as rule-related information.</p>
 * @since 1.0
 * @author maamria
 *
 */
public class RewriteInput extends ContextualInput{
	
	public IPosition position;
	public String description;
	public String ruleName;
	public String theoryName;
	public String projectName;
	public Predicate predicate;
	
	/**
	 * Constructs an input with the given parameters.
	 * @param theoryName the parent theory
	 * @param ruleName the name of the rule to apply
	 * @param ruleDesc the description to display if rule applied successfully
	 * @param pred the predicate
	 * @param position the position
	 * @param context the context
	 */
	public RewriteInput(String projectName, String theoryName, String ruleName, String ruleDesc,
			Predicate predicate, IPosition position, IPOContext context){
		super(context);
		this.projectName = projectName;
		this.position = position;
		this.description = ruleDesc;
		this.ruleName = ruleName;
		this.theoryName = theoryName;
		this.predicate = predicate;
	}
	
	@Override
	public void applyHints(ReplayHints renaming) {
		if (predicate != null) {
			predicate = renaming.applyHints(predicate);
		}
	}
}
