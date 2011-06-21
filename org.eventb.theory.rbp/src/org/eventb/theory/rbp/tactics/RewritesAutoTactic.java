package org.eventb.theory.rbp.tactics;

import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.theory.rbp.internal.rulebase.POContext;
import org.eventb.theory.rbp.reasoners.AutoRewriteReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;


/**
 * The automatic tactic for applying automatic rewrite rules.
 * 
 * <p> At the moment, only unconditional rewrite rules can be applied automatically.
 * @since 1.0
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class RewritesAutoTactic implements ITactic{

	@Override
	public Object apply(IProofTreeNode node, IProofMonitor pm) {
		if (node.getProofTree().getOrigin() instanceof IProofAttempt){
			if (!node.isOpen()){
				return "Root already has children";
			}
			IProofAttempt attempt = (IProofAttempt) node.getProofTree().getOrigin();
			IPSStatus status = attempt.getStatus();
			IPOContext poContext = new POContext(status);
			AutoRewriteReasoner reasoner = new AutoRewriteReasoner();
			reasoner.setContext(poContext);
			IReasonerOutput reasonerOutput = reasoner.apply(node.getSequent(), null, pm);
			if (reasonerOutput == null) return "! Plugin returned null !";
			if (!(reasonerOutput instanceof IProofRule)) return reasonerOutput;
			IProofRule rule = (IProofRule)reasonerOutput;
			if (node.applyRule(rule)) return null;
			else return "Rule "+rule.getDisplayName()+" is not applicable";
		}
		return "Contextual information of PO is required";
	}
}
