package org.eventb.theory.rbp.tactics;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.internal.tactics.AbstractRewriteManualTactic;
import org.eventb.theory.rbp.rewriting.RbPAbstractApplicationInspector;
import org.eventb.theory.rbp.rewriting.RewritesSelector;

/**
 * The manual tactic for applying interactive rewrite rules.
 * @since 1.0
 * @author maamria
 *
 */
public class RewritesManualTactic extends AbstractRewriteManualTactic {

	@Override
	protected RbPAbstractApplicationInspector getSelector(Predicate predicate,
			boolean isGoal, FormulaFactory factory) {
		// TODO Auto-generated method stub
		return new RewritesSelector(predicate, isGoal, factory);
	}

	
}
