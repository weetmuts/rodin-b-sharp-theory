package ac.soton.eventb.prover.tactic;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

import ac.soton.eventb.prover.base.RuleBaseManager;

/**
 * A tactic provider to reload the rule-base.
 * @author maamria
 *
 */
public class ReloadBaseTactic extends DefaultTacticProvider implements
		ITacticProvider {

	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs, String globalInput) {
		return new ITactic() {
			
			public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
				RuleBaseManager.getDefault().reload();
				return null;
			}
		};
	}
	
	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null && node.isOpen())
			return new ArrayList<IPosition>();
		return null;
	}
}
