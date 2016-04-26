package org.eventb.theory.rbp.reasoners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoning.AutoRewriter;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * 
 * @author maamria
 *
 */
public class AutoRewriteReasoner extends AbstractContextDependentReasoner
		implements IReasoner {

	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".autoRewriteReasoner";
	
	private static final String DISPLAY_NAME = "RbP0";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		IPOContext context = getContext(seq);
		final FormulaFactory ff = seq.getFormulaFactory();
		final IFormulaRewriter rewriter = getRewriter(context);
		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			// Rewrite the hypothesis
			Predicate inferredHyp = recursiveRewrite(hyp, rewriter);
			Collection<Predicate> inferredHyps = Lib.breakPossibleConjunct(inferredHyp);
			// Check if rewriting made a change
			if (inferredHyp == hyp && inferredHyps.size() == 1)
				continue;
			// Check if rewriting generated something interesting
			inferredHyps.remove(DLib.True(ff));
			Collection<Predicate> originalHyps = Collections.singleton(hyp);
			// Hide the original if the inferredHyps is empty, i.e. the
			// hypothesis get rewritten to Lib.True.
			if (inferredHyps.isEmpty()) {
				hypActions.add(ProverFactory.makeHideHypAction(originalHyps));
				continue;
			}
			// Check if rewriting generated something new
			if (seq.containsHypotheses(inferredHyps)) {
				// IMPORTANT: Do NOT de-select the original if the inferred
				// hypotheses already exist.

				// Do NOT re-select the inferred hyps
				continue;
			}
			// make the forward inference action
			if (!inferredHyps.isEmpty())
				hypActions.add(ProverFactory.makeForwardInfHypAction(originalHyps, inferredHyps));
			hypActions.add(ProverFactory.makeHideHypAction(originalHyps));
		}
		Predicate goal = seq.goal();
		Predicate newGoal = recursiveRewrite(goal, rewriter);

		if (newGoal != goal) {
			IAntecedent[] antecedent = new IAntecedent[] { ProverFactory.makeAntecedent(newGoal, null, null, hypActions) };
			return ProverFactory.makeProofRule(this, input, goal, null, null, getDisplayName(), antecedent);
		}
		if (!hypActions.isEmpty()) {
			return ProverFactory.makeProofRule(this, input, getDisplayName(), hypActions);
		}
		return ProverFactory.reasonerFailure(this, input, "No rewrites applicable");
	}

	// can be overridden to provide alternative display name
	protected String getDisplayName() {
		return DISPLAY_NAME;
	}
	
	// can be overridden to provide alternative rewriter
	protected IFormulaRewriter getRewriter(IPOContext context){
		 return new AutoRewriter(context);
	}

	/**
	 * An utility method which try to rewrite a predicate recursively until
	 * reaching a fix-point.
	 * <p>
	 * If no rewrite where performed on this predicate, then a reference to this
	 * predicate is returned (rather than a copy of this predicate). This allows
	 * to test efficiently (using <code>==</code>) whether rewriting made any
	 * change.
	 * </p>
	 * 
	 * <p>
	 * 
	 * @param pred
	 *            the input predicate
	 * @return the resulting predicate after rewrite.
	 */
	private Predicate recursiveRewrite(Predicate pred, IFormulaRewriter rewriter) {
		Predicate resultPred;
		resultPred = pred.rewrite(rewriter);
		while (resultPred != pred) {
			pred = resultPred;
			resultPred = pred.rewrite(rewriter);
		}
		return resultPred;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		// Do nothing
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new EmptyInput();
	}
}
