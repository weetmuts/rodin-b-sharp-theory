/*******************************************************************************
 * Copyright (c) 2010, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - repair missing project key
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IRepairableInputReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.input.ContextualInput;
import org.eventb.theory.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.reasoning.ManualRewriter;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.rodinp.core.IRodinProject;

/**
 * <p>
 * An implementation of a manual reasoner for the rule base.
 * </p>
 * 
 * @author maamria
 * 
 */
public class ManualRewriteReasoner extends ContextAwareReasoner implements IRepairableInputReasoner {

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".manualRewriteReasoner";
	
	private static final String DESC_KEY = "ruleDesc";
	private static final String POSITION_KEY = "pos";
	private static final String RULE_KEY = "rewRule";
	private static final String THEORY_KEY = "theory";
	private static final String PROJECT_KEY = "project";

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm) {
		final RewriteInput input = (RewriteInput) reasonerInput;
		final Predicate hyp = input.predicate;
		final IPosition position = input.position;
		final String theoryName = input.theoryName;
		final String projectName = input.projectName;
		final String ruleName = input.ruleName;
		final String displayName = input.description;
		final IPOContext context = input.context;

		ManualRewriter rewriter = new ManualRewriter(context);

		final Predicate goal = seq.goal();
		if (hyp == null) {
			IAntecedent[] antecedents = rewriter.getAntecedents(goal, position, true, projectName, theoryName, ruleName);
			if (antecedents == null) {
				return ProverFactory.reasonerFailure(this, input, "Rule " + ruleName + " is not applicable to " + goal + " at position " + position);
			}
			return ProverFactory.makeProofRule(this, input, goal, displayName + " on goal", antecedents);
		} else {
			// Hypothesis rewriting
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input, "Nonexistent hypothesis: " + hyp);
			}
			IAntecedent[] antecedents = rewriter.getAntecedents(hyp, position, false, projectName, theoryName, ruleName);
			if (antecedents == null) {
				return ProverFactory.reasonerFailure(this, input, "Rule " + ruleName + " is not applicable to " + hyp + " at position " + position);
			}
			return ProverFactory.makeProofRule(this, input, null, hyp, displayName + " on " + hyp, antecedents);
		}
	}

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) throws SerializeException {
		super.serializeInput(input, writer); // processes CONTEXT_INPUT_KEY
		writer.putString(POSITION_KEY, ((RewriteInput) input).position.toString());
		writer.putString(THEORY_KEY, ((RewriteInput) input).theoryName);
		writer.putString(RULE_KEY, ((RewriteInput) input).ruleName);
		writer.putString(DESC_KEY, ((RewriteInput) input).description);
		writer.putString(PROJECT_KEY, ((RewriteInput) input).projectName);
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		return deserializeInput(reader, false);
	}

	private static String findMissingProjectKey(IPOContext context,
			String theoryString) {
		final Set<IRodinProject> theoryProjects = BaseManager.getDefault()
				.findTheoryProjects(context, theoryString);
		System.out.println("");
		if (theoryProjects.size() != 1) {
			final String reason;
			if (theoryProjects.size() == 0) {
				reason = "no accessible project defines theory " + theoryString
						+ ", might be caused by a missing theory path";
			} else {
				reason = "ambiguous theory " + theoryString
						+ ", found in several projects: "
						+ theoryProjects.toString();
			}
			final String message = "Failed to repair missing project key in "
					+ context.getParentRoot().getPRRoot() + ": " + reason;
			CoreUtilities.log(null, message);
			return null;
		}

		final IRodinProject theoryProject = theoryProjects.iterator().next();
		return theoryProject.getElementName();
	}
	
	private IReasonerInput deserializeInput(IReasonerInputReader reader,
			boolean repair) throws SerializeException {
		final ContextualInput contextual = (ContextualInput) super.deserializeInput(reader);
		final String posString = reader.getString(POSITION_KEY);
		final String theoryString = reader.getString(THEORY_KEY);
		final String ruleString = reader.getString(RULE_KEY);
		final String ruleDesc = reader.getString(DESC_KEY);
		final IPOContext context = contextual.context;
		final IPosition position = FormulaFactory.makePosition(posString);

		final String projectString;
		if (repair) {
			projectString = findMissingProjectKey(context, theoryString);
			if (projectString == null) {
				return null;
			}
		} else {
			projectString = reader.getString(PROJECT_KEY);
		}

		Set<Predicate> neededHyps = reader.getNeededHyps();

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new RewriteInput(projectString, theoryString, ruleString, ruleDesc, null, position, context);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException("Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new RewriteInput(projectString, theoryString, ruleString, ruleDesc, pred, position, context);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public String getSignature() {
		return REASONER_ID;
	}

	@Override
	public IReasonerInput repair(IReasonerInputReader reader) {
		try {
			return deserializeInput(reader, true);
		} catch (Throwable t) {
			// repair failed, log and resign
			CoreUtilities.log(t, "While repairing broken input for reasoner " + getReasonerID());
			return null;
		}
	}

}
