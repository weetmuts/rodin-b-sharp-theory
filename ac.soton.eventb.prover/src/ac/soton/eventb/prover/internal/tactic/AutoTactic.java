package ac.soton.eventb.prover.internal.tactic;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.tactics.BasicTactics;

import ac.soton.eventb.prover.reasoner.AutoRewriteReasoner;

/**
 * The rule base automatic tactic for automatic rewrite rules.
 * <p>
 * @author maamria
 *
 */
public class AutoTactic extends AutoTactics.AbsractLazilyConstrTactic{

	@Override
	protected ITactic getSingInstance() {
		return BasicTactics.reasonerTac(new AutoRewriteReasoner(), null);
	}
}
