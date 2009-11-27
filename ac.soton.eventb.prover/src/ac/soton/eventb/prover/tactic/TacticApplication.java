package ac.soton.eventb.prover.tactic;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.TacticProviderUtils;

import ac.soton.eventb.prover.plugin.ProverPlugIn;
import ac.soton.eventb.prover.reasoner.Input;
import ac.soton.eventb.prover.reasoner.ManualRewriteReasoner;

public class TacticApplication implements IPositionApplication{

	private final Input input;
	private final String linkLabel;
	
	public TacticApplication(Input input, String linklabel){
		this.input = input;
		this.linkLabel = linklabel;
	}
	
	@Override
	public Point getHyperlinkBounds(String parsedString,
			Predicate parsedPredicate) {
		return TacticProviderUtils.getOperatorPosition(parsedPredicate,
				parsedString, input.position);
	}

	@Override
	public String getHyperlinkLabel() {
		return linkLabel;
	}

	@Override
	public ITactic getTactic(String[] inputs, String globalInput) {
		return BasicTactics.reasonerTac(new ManualRewriteReasoner(), input);
	}

	@Override
	public String getTacticID() {
		if(input.pred == null)
			return ProverPlugIn.PLUGIN_ID + "ruleBaseGoalTactic";
		else
			return ProverPlugIn.PLUGIN_ID + "ruleBaseHypTactic";
	}

}
