/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ISignatureReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInputReasoner;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * 
 * @author maamria
 *
 */
public class InstantiateTheoremReasoner extends SingleStringInputReasoner implements ISignatureReasoner{
	
	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".instantiateTheoremReasoner";
	
	private static final String DISPLAY_NAME = "Instantiate Theorem";
	
	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		SingleStringInput stringInput = (SingleStringInput) input;
		String theoremStr = stringInput.getString();
		if (theoremStr == null){
			return ProverFactory.reasonerFailure(this, stringInput, "No theorem to add");
		}
		FormulaFactory factory = seq.getFormulaFactory();
		ITypeEnvironment typeEnvironment = seq.typeEnvironment();
		// parse the string
		IParseResult parseResult = factory.parsePredicate(theoremStr, LanguageVersion.V2, null);
		if (parseResult.hasProblem()){
			return ProverFactory.reasonerFailure(this, stringInput, "Cannot parse '"+theoremStr + "'");
		}
		// check the free identifiers
		Predicate theoremPredicate = parseResult.getParsedPredicate();
		for (FreeIdentifier identifier : theoremPredicate.getFreeIdentifiers()){
			if (!typeEnvironment.contains(identifier.getName())){
				return ProverFactory.reasonerFailure(this, stringInput, "Cannot type check '"+theoremStr + "'");
			}
		}
		// type check the predicate
		ITypeCheckResult tcResult = theoremPredicate.typeCheck(typeEnvironment);
		if(tcResult.hasProblem()){
			return ProverFactory.reasonerFailure(this, stringInput, "Cannot type check '"+theoremStr + "'");
		}
		// check given types
		for (GivenType givenType : theoremPredicate.getGivenTypes()){
			if (!typeEnvironment.contains(givenType.getName())){
				return ProverFactory.reasonerFailure(this, stringInput, "Cannot type check '"+theoremStr + "'");
			}
		}
		// make the forward inference step
		Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		addedHyps.add(theoremPredicate);
		Predicate wdPredicate = theoremPredicate.getWDPredicate(factory);
		if(!wdPredicate.equals(ProverUtilities.BTRUE)){
			addedHyps.add(wdPredicate);
		}
		// make the antecedent
		IAntecedent antecedent = ProverFactory.makeAntecedent(null, addedHyps,
				ProverFactory.makeSelectHypAction(Collections.singleton(theoremPredicate)));
		// make the proof rule
		return ProverFactory.makeProofRule(this, stringInput, null, DISPLAY_NAME, antecedent);
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	public String getSignature() {
		return REASONER_ID;
	}

}
