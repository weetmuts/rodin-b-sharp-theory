package ac.soton.eventb.prover.reasoner;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

import ac.soton.eventb.prover.plugin.ProverPlugIn;

public class AutoConditionalRewriteReasoner extends EmptyInputReasoner{

	private static final String DISPLAY_NAME = "Rule-based Auto Prover";
	
	private static final String REASONER_ID = ProverPlugIn.PLUGIN_ID + ".ruleBaseAutoCondReasoner";
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		IAntecedent[] antecidents = getAntecedents(seq);
		if (antecidents == null)
			return ProverFactory.reasonerFailure(this, input,
					"No conditional rewrite rule is applicable");

		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, input, seq.goal(),
				getDisplayName(), antecidents);

	}

	protected IAntecedent[] getAntecedents(IProverSequent seq){
		return null;
	}

	protected String getDisplayName(){
		return DISPLAY_NAME;
	}
	
	public String getReasonerID() {
		return REASONER_ID;
	}
}
