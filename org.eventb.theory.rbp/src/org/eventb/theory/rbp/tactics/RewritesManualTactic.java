package org.eventb.theory.rbp.tactics;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.theory.rbp.rewriting.RewritesSelector;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * The manual tactic for applying interactive rewrite rules.
 * @since 1.0
 * @author maamria
 *
 */
public class RewritesManualTactic extends DefaultTacticProvider implements ITacticProvider {

	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		FormulaFactory factory = node.getFormulaFactory();
		boolean isGoal = hyp == null;
		Predicate pred = ( isGoal ? node.getSequent().goal() : hyp);
		List<ITacticApplication> apps = pred.inspect(new RewritesSelector(pred, isGoal, factory));
		return apps;
	}
}
