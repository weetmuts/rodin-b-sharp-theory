package org.eventb.theory.rbp.internal.tactics;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.ManualRewriteReasoner;
import org.eventb.theory.rbp.reasoners.input.RewriteInput;
import org.eventb.ui.prover.IPositionApplication;

public class RewriteTacticApplication extends ExtendedPositionApplication
		implements IPositionApplication {

	private final RewriteInput input;
	private final String linkLabel;

	public RewriteTacticApplication(RewriteInput input, String linklabel) {
		super(input.pred, input.position);
		this.input = input;
		this.linkLabel = linklabel;
	}

	public Point getHyperlinkBounds(String parsedString,
			Predicate parsedPredicate) {
		return getOperatorPosition(parsedPredicate,
				parsedString);
	}

	public String getHyperlinkLabel() {
		return linkLabel;
	}

	public ITactic getTactic(String[] inputs, String globalInput) {
		return BasicTactics.reasonerTac(new ManualRewriteReasoner(), input);
	}

	public String getTacticID() {
		return RbPPlugin.PLUGIN_ID + ".rewriteTactic";
	}
}