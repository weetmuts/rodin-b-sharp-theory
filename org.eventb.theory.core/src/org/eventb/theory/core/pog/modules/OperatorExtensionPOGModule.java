/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.pog.modules;

import static org.eventb.core.seqprover.eventbExtensions.DLib.True;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeConj;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeEq;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeImp;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeUnivQuant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class OperatorExtensionPOGModule extends UtilityPOGModule {

	private final IModuleType<OperatorExtensionPOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".operatorExtensionPOGModule");

	protected FormulaFactory factory;
	protected ITypeEnvironment typeEnvironment;
	protected POGNatureFactory natureFactory;
	protected IPORoot target;

	protected static final String OPERATOR_WD_PO = "Operator Well-Definedness Preservation";
	protected static final String OPERATOR_WD_POSTFIX = "/Op-WD";
	protected static final String OPERATOR_COMMUT_PO = "Operator Commutativity";
	protected static final String OPERATOR_COMMUT_POSTFIX = "/Op-COMMUT";
	protected static final String OPERATOR_ASSOC_PO = "Operator Associativity";
	protected static final String OPERATOR_ASSOC_POSTFIX = "/Op-ASSOC";

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		target = repository.getTarget();
		natureFactory = POGNatureFactory.getInstance();
		IRodinFile file = (IRodinFile) element;
		ISCTheoryRoot theory = (ISCTheoryRoot) file.getRoot();
		ISCNewOperatorDefinition definitions[] = theory
				.getSCNewOperatorDefinitions();
		for (ISCNewOperatorDefinition definition : definitions) {
			generateCorrespondingPOs(definition, monitor);
		}
	}

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		factory = null;
		target = null;
		super.endModule(element, repository, monitor);
	}

	protected void generateCorrespondingPOs(
			ISCNewOperatorDefinition definition, IProgressMonitor monitor)
					throws CoreException {
		if (definition.hasError()) {
			return;
		}
		final ITypeEnvironmentBuilder localTypeEnvironment = typeEnvironment.makeBuilder();
		// get the arguments
		ISCOperatorArgument[] arguments = definition.getOperatorArguments();
		Set<FreeIdentifier> identifiers = new LinkedHashSet<FreeIdentifier>();
		for (ISCOperatorArgument argument : arguments) {
			FreeIdentifier ident = argument.getIdentifier(factory);
			identifiers.add(ident);
			localTypeEnvironment.add(ident);
		}
		// get the well-definedness condition
		final Predicate wdCondition = definition.getPredicate(localTypeEnvironment);
		// get the definition
		final ISCDirectOperatorDefinition[] directDefinitions = definition
				.getDirectOperatorDefinitions();
		if (directDefinitions.length != 1) {
			return;
		}
		Formula<?> defFormula = directDefinitions[0].getSCFormula(
				factory, localTypeEnvironment);
		IPOGSource[] sources = new IPOGSource[] {
				makeSource(IPOSource.DEFAULT_ROLE, definition),
				makeSource(IPOSource.DEFAULT_ROLE, definition.getSource()) };
		IPOPredicateSet hyp = target
				.getPredicateSet(TypeParametersPOGModule.ABS_HYP_NAME);
		// ///////////////////////////////////
		// ////////WD Strength
		String poName = definition.getLabel()
				+ OPERATOR_WD_POSTFIX;
		Predicate wdStrengthPredicate = getClosedPOPredicate(
				wdCondition, defFormula.getWDPredicate(),
				identifiers, localTypeEnvironment);
		if (!isTrivial(wdStrengthPredicate)) {
			createPO(
					target,
					poName,
					natureFactory.getNature(OPERATOR_WD_PO),
					hyp,
					EMPTY_PREDICATES,
					makePredicate(wdStrengthPredicate,
							definition.getSource()),
							sources,
							new IPOGHint[] { getLocalHypothesisSelectionHint(
									target, poName, hyp) }, true, null);
		}
		// ///////////////////////////////////
		// ////////Associativity
		if (definition.isAssociative()) {
			Predicate assocChecker = getAssociativityChecker(
					(Expression) defFormula, identifiers,
					localTypeEnvironment);
			if (!isTrivial(assocChecker)) {
				poName = definition.getLabel()
						+ OPERATOR_ASSOC_POSTFIX;
				createPO(
						target,
						poName,
						natureFactory.getNature(OPERATOR_ASSOC_PO),
						hyp,
						EMPTY_PREDICATES,
						makePredicate(assocChecker,
								definition.getSource()),
								sources,
								new IPOGHint[] { getLocalHypothesisSelectionHint(
										target, poName, hyp) }, true, null);
			}
		}
		// ///////////////////////////////////
		// ////////Commutativity
		if (definition.isCommutative()) {
			Predicate commutChecker = getCommutativityChecker(
					(Expression) defFormula, identifiers,
					localTypeEnvironment);
			if (!isTrivial(commutChecker)) {
				poName = definition.getLabel()
						+ OPERATOR_COMMUT_POSTFIX;
				createPO(
						target,
						poName,
						natureFactory.getNature(OPERATOR_COMMUT_PO),
						hyp,
						EMPTY_PREDICATES,
						makePredicate(commutChecker,
								definition.getSource()),
								sources,
								new IPOGHint[] { getLocalHypothesisSelectionHint(
										target, poName, hyp) }, true, null);
			}
		}
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// All done in initialisation of module

	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected Predicate getClosedPOPredicate(Predicate wdCondition,
			Predicate otherPredicate, Set<FreeIdentifier> identifiers,
			ITypeEnvironment typeEnvironment) {
		if (otherPredicate.equals(wdCondition)
				|| otherPredicate.equals(True(factory))) {
			return True(factory);
		}
		Predicate boundPred = makeImp(wdCondition, otherPredicate);
		List<Predicate> typingPreds = new ArrayList<Predicate>();
		List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>();
		for (FreeIdentifier identifier : identifiers) {
			typingPreds.add(factory.makeRelationalPredicate(Formula.IN,
					identifier, identifier.getType().toExpression(),
					null));
			identifiers.add(identifier);
			decls.add(factory.makeBoundIdentDecl(identifier.getName(), null,
					identifier.getType()));
		}
		Predicate initial = makeImp(makeConj(factory, typingPreds),
				boundPred);

		initial = initial.bindTheseIdents(identifiers);
		Predicate pred = null;
		if (decls.size() == 0) {
			pred = initial;
		} else {
			pred = makeUnivQuant(
					decls.toArray(new BoundIdentDecl[decls.size()]), initial);
		}
		ITypeCheckResult result = pred.typeCheck(typeEnvironment);
		assert !result.hasProblem();
		return pred;
	}

	private static String VAR_TEMP_NAME = "_z_";

	// swaps ident1 for ident2 and ident2 for ident1 in formula
	protected Formula<?> swap(FreeIdentifier ident1, FreeIdentifier ident2,
			Formula<?> formula) {
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		FreeIdentifier temp = factory.makeFreeIdentifier(VAR_TEMP_NAME, null,
				ident1.getType());
		subs.put(ident1, temp);
		Formula<?> form1 = formula.substituteFreeIdents(subs);
		subs.clear();
		subs.put(ident2, ident1);
		Formula<?> form2 = form1.substituteFreeIdents(subs);
		subs.clear();
		subs.put(temp, ident2);
		Formula<?> form3 = form2.substituteFreeIdents(subs);
		return form3;
	}

	protected Predicate getAssociativityChecker(Expression directDefinition,
			Set<FreeIdentifier> arguments, ITypeEnvironment typeEnvironment) {
		FreeIdentifier[] opArgs = arguments
				.toArray(new FreeIdentifier[arguments.size()]);
		FreeIdentifier x = opArgs[0];
		FreeIdentifier y = opArgs[1];
		FreeIdentifier z = factory.makeFreeIdentifier(VAR_TEMP_NAME, null,
				y.getType());
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		// left (x op y) op z
		subs.put(y, z);
		Expression y_by_z = directDefinition
				.substituteFreeIdents(subs);
		subs.clear();
		subs.put(x, directDefinition);
		Expression left = y_by_z.substituteFreeIdents(subs);
		subs.clear();
		// right x op (y op z)
		subs.put(y, z);
		Expression y_by_z2 = directDefinition.substituteFreeIdents(subs);
		subs.clear();
		subs.put(x, y);
		Expression x_by_y2 = y_by_z2.substituteFreeIdents(subs);
		subs.clear();
		subs.put(y, x_by_y2);
		Expression right = directDefinition.substituteFreeIdents(subs);
		Predicate assocCond = makeEq(left, right);

		List<FreeIdentifier> identsToBind = new ArrayList<FreeIdentifier>();
		Predicate[] typingPreds = new Predicate[3];
		BoundIdentDecl[] decls = new BoundIdentDecl[3];
		identsToBind.add(x);
		decls[0] = factory.makeBoundIdentDecl(x.getName(), null, x.getType());
		typingPreds[0] = factory.makeRelationalPredicate(Formula.IN, x, x
				.getType().toExpression(), null);
		identsToBind.add(y);
		decls[1] = factory.makeBoundIdentDecl(y.getName(), null, y.getType());
		typingPreds[1] = factory.makeRelationalPredicate(Formula.IN, y, y
				.getType().toExpression(), null);
		identsToBind.add(z);
		decls[2] = factory.makeBoundIdentDecl(z.getName(), null, z.getType());
		typingPreds[2] = factory.makeRelationalPredicate(Formula.IN, z, z
				.getType().toExpression(), null);

		Predicate rawCondition = makeImp(makeConj(factory, typingPreds),
				makeImp(assocCond.getWDPredicate(), assocCond));

		rawCondition = rawCondition.bindTheseIdents(identsToBind);
		rawCondition = makeUnivQuant(decls, rawCondition);
		ITypeCheckResult result = rawCondition.typeCheck(typeEnvironment);
		assert !result.hasProblem();
		return rawCondition;

	}

	public Predicate getCommutativityChecker(Formula<?> defFormula,
			Set<FreeIdentifier> arguments, ITypeEnvironment typeEnvironment) {
		FreeIdentifier[] opArgs = arguments
				.toArray(new FreeIdentifier[arguments.size()]);
		FreeIdentifier x = opArgs[0];
		FreeIdentifier y = opArgs[1];
		Formula<?> commutForm = swap(x, y, defFormula);
		Predicate commutPred = null;
		if (commutForm instanceof Expression) {
			commutPred = makeEq((Expression) defFormula,
					(Expression) commutForm);
		} else {
			commutPred = factory.makeBinaryPredicate(Formula.LEQV,
					(Predicate) defFormula, (Predicate) commutForm, null);
		}
		return getClosedPOPredicate(commutPred.getWDPredicate(),
				commutPred, arguments, typeEnvironment);
	}

}
