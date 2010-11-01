package org.eventb.theory.rbp.tactics;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.rbp.reasoners.AutoRewriteReasoner;


/**
 * The automatic tactic for applying automatic rewrite rules.
 * 
 * <p> At the moment, only unconditional rewrite rules can be applied automatically.
 * @since 1.0
 * @author maamria
 *
 */
public class RewritesAutoTactic extends AutoTactics.AbsractLazilyConstrTactic{

	@Override
	protected ITactic getSingInstance() {
		return BasicTactics.reasonerTac(new AutoRewriteReasoner(), null);
	}
}
