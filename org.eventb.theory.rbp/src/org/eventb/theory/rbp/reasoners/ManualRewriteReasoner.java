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
import org.eventb.theory.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.reasoning.ManualRewriter;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * <p>An implementation of a manual reasoner for the rule base.</p>
 * <p>It takes as input the predicate (to rewrite), the position, the rule name and its parent theory' name.</p>
 * @author maamria
 *
 */
public class ManualRewriteReasoner implements IContextAwareReasoner{

	
	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".manualRewriteReasoner";
	private static final String DESC_KEY = "ruleDesc";
	private static final String POSITION_KEY = "pos";
	private static final String RULE_KEY = "rewRule";
	private static final String THEORY_KEY = "theory";
	
	private ManualRewriter rewriter;
	
	public void setContext(IPOContext context){
		rewriter = new ManualRewriter(context);
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput,
			IProofMonitor pm) {
		final RewriteInput input = (RewriteInput) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;
		final String theoryName = input.theoryName;
		final String ruleName = input.ruleName;
		final String displayName = input.ruleDesc;
		
		final Predicate goal = seq.goal();
		if (hyp == null) {
			IAntecedent[] antecedents = getAntecedents(goal, position, true, theoryName, ruleName);
			if(antecedents == null){
				return ProverFactory.reasonerFailure(this, input, 
						"Rule "+ruleName+" is not applicable to "+goal +" at position "+position);
			}
			return ProverFactory.makeProofRule(this, input, goal,
					displayName +" on goal", antecedents);
		} else {
			// Hypothesis rewriting
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
			IAntecedent[] antecedents = getAntecedents(hyp, position, false, theoryName, ruleName);
			if(antecedents == null){
				return ProverFactory.reasonerFailure(this, input, 
						"Rule "+ruleName+" is not applicable to "+hyp +" at position "+position);
			}
			return ProverFactory.makeProofRule(this, input, null, hyp,displayName+ " on "+hyp, antecedents);
		}
	}

	protected IAntecedent[] getAntecedents(Predicate pred, IPosition position, boolean isGoal, String theoryName, String ruleName){
		return rewriter.getAntecedents(pred, position, isGoal, theoryName, ruleName);
	}
	
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final String posString = reader.getString(POSITION_KEY);
		final String theoryString = reader.getString(THEORY_KEY);
		final String ruleString = reader.getString(RULE_KEY);
		final String ruleDesc = reader.getString(DESC_KEY);
		final IPosition position = FormulaFactory.makePosition(posString);
		
		Set<Predicate> neededHyps = reader.getNeededHyps();

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new RewriteInput(theoryString, ruleString, ruleDesc, null, position);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new RewriteInput(theoryString, ruleString, ruleDesc,pred, position);
	}
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		writer.putString(POSITION_KEY, ((RewriteInput) input).position.toString());
		writer.putString(THEORY_KEY, ((RewriteInput) input).theoryName);
		writer.putString(RULE_KEY, ((RewriteInput) input).ruleName);
		writer.putString(DESC_KEY, ((RewriteInput) input).ruleDesc);
	}

	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return "";
	}

}
