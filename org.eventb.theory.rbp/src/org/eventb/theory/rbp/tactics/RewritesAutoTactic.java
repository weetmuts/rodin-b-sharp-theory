package org.eventb.theory.rbp.tactics;

import static org.eventb.theory.rbp.tactics.CombinableTactic.sequentialCompose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.internal.rbp.reasoners.input.AutoRewriteInput;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadataReasonerInput;
import org.eventb.theory.rbp.reasoners.AutoRewriteReasoner;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;


/**
 * The automatic tactic for applying automatic rewrite rules.
 * 
 * <p> Only unconditional rewrite rules can be applied automatically.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class RewritesAutoTactic extends ContextDependentTactic implements
		ITactic {

	private static Class<?>[] clazzes = {
		AssociativeExpression.class,
		AtomicExpression.class,
		BinaryExpression.class,
		BoolExpression.class,
		ExtendedExpression.class,
		BoundIdentifier.class,
		FreeIdentifier.class,
		IntegerLiteral.class,
		QuantifiedExpression.class,
		SetExtension.class,
		UnaryExpression.class,
		AssociativePredicate.class,
		BinaryPredicate.class,
		ExtendedPredicate.class,
		LiteralPredicate.class,
		MultiplePredicate.class,
		PredicateVariable.class,
		QuantifiedPredicate.class,
		RelationalPredicate.class,
		SimplePredicate.class,
		UnaryPredicate.class
	};
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ContextDependentTactic#getTactic(IPOContext)
	 */
	@Override
	protected ITactic getTactic(IPOContext context) {
		List<ICombinableTactic> tactics = getRewriteTactics(context);
		// If there is some inference rule tactic then create the composed tactic.
		if (tactics.size() != 0) {
			return sequentialCompose(tactics.toArray(new ICombinableTactic[tactics
					.size()]));
		} else {
			return BasicTactics
					.failTac("There are no applicable rewrite rules");
		}		
	}

	/**
	 * @param context
	 * @return
	 */
	private List<ICombinableTactic> getRewriteTactics(IPOContext context) {
		List<ICombinableTactic> tactics = new ArrayList<ICombinableTactic>();
		// Get the list of forward inference rules. For each rule, create a
		// combinable tactic associated with it.
		for (Class<?> clazz : clazzes) {
			tactics.addAll(getRewriteTactics(context, clazz));
		}

		return tactics;
	}

	/**
	 * @param context
	 * @param clazz
	 * @return
	 */
	private Collection<? extends ICombinableTactic> getRewriteTactics(
			IPOContext context, Class<?> clazz) {
		List<ICombinableTactic> tactics = new ArrayList<ICombinableTactic>();

		BaseManager manager = BaseManager.getDefault();
		List<IGeneralRule> rules = manager.getRewriteRules(true, clazz, context);
		for (IGeneralRule rule : rules) {
			IDeployedRewriteRule rwRule = (IDeployedRewriteRule) rule;
			ICombinableTactic tactic = getRewriteTactic(rwRule);
			if (tactic != null)
				tactics.add(tactic);
		}
		return tactics;
	}

	/**
	 * @param rwRule
	 * @return
	 */
	private ICombinableTactic getRewriteTactic(IDeployedRewriteRule rule) {
		// Create the auto inference reasoner and input.
		final IReasoner reasoner = new AutoRewriteReasoner();
		String projectName = rule.getProjectName();
		String theoryName = rule.getTheoryName();
		String ruleName = rule.getRuleName();
		IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
				ruleName);
		final AutoRewriteInput reasonerInput = new AutoRewriteInput(
				prMetadata);
		
		// Construct and return the proof rule with the reasoner and input.
		return new ProofRuleTactic() {

			@Override
			public PRMetadataReasonerInput getReasonerInput() {
				return reasonerInput;
			}

			@Override
			public IReasoner getReasoner() {
				return reasoner;
			}
		};
	}

}
