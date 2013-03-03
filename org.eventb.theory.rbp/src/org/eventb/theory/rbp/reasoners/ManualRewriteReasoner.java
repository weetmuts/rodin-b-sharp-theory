package org.eventb.theory.rbp.reasoners;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.input.ContextualInput;
import org.eventb.theory.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.reasoning.ManualRewriter;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * <p>
 * An implementation of a manual reasoner for the rule base.
 * </p>
 * 
 * @author maamria
 * 
 */
public class ManualRewriteReasoner extends ContextAwareReasoner {

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".manualRewriteReasoner";
	
	private static final String DESC_KEY = "ruleDesc";
	private static final String POSITION_KEY = "pos";
	private static final String RULE_KEY = "rewRule";
	private static final String THEORY_KEY = "theory";
	private static final String PROJECT_KEY = "project";

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm) {
		final RewriteInput input = (RewriteInput) reasonerInput;
		final Predicate hyp = input.predicate;
		final IPosition position = input.position;
		final String theoryName = input.theoryName;
		final String projectName = input.projectName;
		final String ruleName = input.ruleName;
		final String displayName = input.description;
		final IPOContext context = input.context;

		ManualRewriter rewriter = new ManualRewriter(context);

		final Predicate goal = seq.goal();
		if (hyp == null) {
			IAntecedent[] antecedents = rewriter.getAntecedents(goal, position, true, projectName, theoryName, ruleName);
			if (antecedents == null) {
				return ProverFactory.reasonerFailure(this, input, "Rule " + ruleName + " is not applicable to " + goal + " at position " + position);
			}
			return ProverFactory.makeProofRule(this, input, goal, displayName + " on goal", antecedents);
		} else {
			// Hypothesis rewriting
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input, "Nonexistent hypothesis: " + hyp);
			}
			IAntecedent[] antecedents = rewriter.getAntecedents(hyp, position, false, projectName, theoryName, ruleName);
			if (antecedents == null) {
				return ProverFactory.reasonerFailure(this, input, "Rule " + ruleName + " is not applicable to " + hyp + " at position " + position);
			}
			return ProverFactory.makeProofRule(this, input, null, hyp, displayName + " on " + hyp, antecedents);
		}
	}

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) throws SerializeException {
		super.serializeInput(input, writer);
		writer.putString(POSITION_KEY, ((RewriteInput) input).position.toString());
		writer.putString(THEORY_KEY, ((RewriteInput) input).theoryName);
		writer.putString(RULE_KEY, ((RewriteInput) input).ruleName);
		writer.putString(DESC_KEY, ((RewriteInput) input).description);
		writer.putString(PROJECT_KEY, ((RewriteInput) input).projectName);
		writer.putString(CONTEXT_INPUT_KEY, ((RewriteInput) input).context.toString());
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		final String posString = reader.getString(POSITION_KEY);
		final String theoryString = reader.getString(THEORY_KEY);
		final String projectString = reader.getString(PROJECT_KEY);
		final String ruleString = reader.getString(RULE_KEY);
		final String ruleDesc = reader.getString(DESC_KEY);
		final String poContextStr = reader.getString(CONTEXT_INPUT_KEY);
		final IPOContext context = ContextualInput.deserialise(poContextStr);
		if (context == null) {
			throw new SerializeException(new IllegalStateException("PO contextual information cannot be retrieved!"));
		}
		final IPosition position = FormulaFactory.makePosition(posString);

		Set<Predicate> neededHyps = reader.getNeededHyps();

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new RewriteInput(projectString, theoryString, ruleString, ruleDesc, null, position, context);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException("Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new RewriteInput(projectString, theoryString, ruleString, ruleDesc, pred, position, context);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public String getSignature() {
		return REASONER_ID;
	}

}
