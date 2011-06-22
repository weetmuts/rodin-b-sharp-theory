package org.eventb.theory.rbp.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.theory.rbp.internal.rulebase.POContext;
import org.eventb.theory.rbp.reasoning.RewritesSelector;
import org.eventb.theory.rbp.rulebase.IPOContext;
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
		if (node.getProofTree().getOrigin() instanceof IProofAttempt){
			IProofAttempt attempt = (IProofAttempt) node.getProofTree().getOrigin();
			IPSStatus status = attempt.getStatus();
			IPOContext poContext = new POContext(status, attempt.getFormulaFactory());
			FormulaFactory factory = node.getFormulaFactory();
			boolean isGoal = hyp == null;
			Predicate pred = ( isGoal ? node.getSequent().goal() : hyp);
			return pred.inspect(new RewritesSelector(pred, isGoal, factory, poContext));
		}
		// Contextual information needed
		return new ArrayList<ITacticApplication>();
		
	}
}
