package org.eventb.theory.rbp.internal.tactics;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.theory.rbp.rewriting.RbPAbstractApplicationInspector;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

public abstract class AbstractRewriteManualTactic extends DefaultTacticProvider implements ITacticProvider {

	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		FormulaFactory factory = node.getFormulaFactory();
		boolean isGoal = hyp == null;
		Predicate pred = ( isGoal ? node.getSequent().goal() : hyp);
		List<ITacticApplication> apps = pred.inspect(getSelector(pred, isGoal, factory));
		return apps;
	}
	
	protected abstract RbPAbstractApplicationInspector getSelector(
			Predicate predicate, boolean isGoal, FormulaFactory factory);
}
