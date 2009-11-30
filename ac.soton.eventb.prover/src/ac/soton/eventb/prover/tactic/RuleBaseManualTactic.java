package ac.soton.eventb.prover.tactic;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider2;

import ac.soton.eventb.prover.internal.tactic.RewriteRuleApplicabilityInfo;
import ac.soton.eventb.prover.reasoner.Input;
import ac.soton.eventb.prover.rewriter.RuleBaseFormulaFilter;

public class RuleBaseManualTactic implements ITacticProvider2 {

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		Predicate pred = (hyp == null ? node.getSequent().goal() : hyp);
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		List<RewriteRuleApplicabilityInfo> infos = new ArrayList<RewriteRuleApplicabilityInfo>();
		List<IPosition> positions = pred
				.getPositions(new RuleBaseFormulaFilter(infos));
		for (IPosition pos : positions) {
			Formula<?> subFormula = pred.getSubFormula(pos);
			List<String> usedInfos = new ArrayList<String>();
			for (RewriteRuleApplicabilityInfo info : infos) {
				if (subFormula.equals(info.getSubFormula())) {
					// check for top level constraints
					if(info.isConditional()){
						if(!Tactics.isParentTopLevelPredicate(pred, pos)){
							continue;
						}
					}
					if(usedInfos.contains(info.getTheoryName()+"."+info.getRuleName()))
						continue;
					ITacticApplication tApp = new TacticApplication(
								new Input(info.getTheoryName(), 
									info.getRuleName(), 
									info.getDescription(), 
									hyp, 
									pos), 
								info.getToolTip());
					apps.add(tApp);
					usedInfos.add(info.getTheoryName()+"."+info.getRuleName());
				}
			}
			

		}
		return apps;
	}

}
