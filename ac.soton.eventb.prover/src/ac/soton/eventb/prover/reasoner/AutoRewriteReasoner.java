package ac.soton.eventb.prover.reasoner;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

import ac.soton.eventb.prover.plugin.ProverPlugIn;
import ac.soton.eventb.prover.rewriter.RuleBaseAutoRewriter;
import ac.soton.eventb.prover.utils.ProverUtilities;

@SuppressWarnings("restriction")
public class AutoRewriteReasoner extends AbstractAutoRewrites implements IReasoner {

	public static List<String> usedTheories = new ArrayList<String>();
	private static final String DISPLAY_NAME = "Rule-based Auto Prover";
	
	private static final String REASONER_ID = ProverPlugIn.PLUGIN_ID + ".ruleBaseAutoReasoner";
	
	private static final IFormulaRewriter rewriter = new RuleBaseAutoRewriter();
	
	public AutoRewriteReasoner() {
		super(rewriter, true);
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		String toDisplay = DISPLAY_NAME + ProverUtilities.printListedItems(usedTheories);
		// clear the list of used theories now
		usedTheories.clear();
		return toDisplay;
	}
}
