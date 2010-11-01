package org.eventb.theory.rbp.reasoners;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoning.AutoRewriter;
import org.eventb.theory.rbp.utils.ProverUtilities;

@SuppressWarnings("restriction")
public class AutoRewriteReasoner extends AbstractAutoRewrites implements IReasoner {

	public static List<String> usedTheories = new ArrayList<String>();
	private static final String DISPLAY_NAME = "RbP0";
	
	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".autoRewriteReasoner";
	
	private AutoRewriter rewriter;
	
	public AutoRewriteReasoner() {
		super(true);
		rewriter = new AutoRewriter();
	}

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

	@Override
	protected IFormulaRewriter getRewriter(FormulaFactory formulaFactory) {
		// TODO Auto-generated method stub
		rewriter.setFormulaFactory(formulaFactory);
		return rewriter;
	}
}
