/*******************************************************************************
 * Copyright (c) 2012, 2022 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.theory.core.IConstructorArgument;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.IGiven;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.IInfer;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.IMetavariable;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.ITypeParameter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * @author asiehsalehi
 * 
 */
public abstract class TheoryTest extends BuilderTest {

	public ITypeEnvironment emptyEnv = defaultFactory.makeTypeEnvironment();
	public final Type intType = defaultFactory.makeIntegerType();
	public final Type boolType = defaultFactory.makeBooleanType();
	public final Type powIntType = defaultFactory.makePowerSetType(intType);
	public final Type relIntType = defaultFactory.makePowerSetType(defaultFactory.makeProductType(intType, intType));

	public TheoryTest() {
		super();
	}

	public void addTypeParameters(IRodinFile rodinFile, String... names) throws RodinDBException {
		final ITheoryRoot root = (ITheoryRoot) rodinFile.getRoot();
		addTypeParameters(root, names);
	}

	public void addTypeParameters(ITheoryRoot root, String... names) throws RodinDBException {
		for (String name : names) {
			ITypeParameter set = root.createChild(ITypeParameter.ELEMENT_TYPE, null, null);
			set.setIdentifierString(name, null);
		}
	}

	public IImportTheory addImportTheory(ITheoryRoot root, IDeployedTheoryRoot root1) throws RodinDBException {

		IImportTheoryProject[] importTheoryProjects = root.getChildrenOfType(IImportTheoryProject.ELEMENT_TYPE);
		for (IImportTheoryProject prj : importTheoryProjects) {
			if (root1.getRodinProject().equals(prj.getRodinProject())) {
				final IImportTheoryProject impThyPrj = prj;
				IImportTheory importThy = impThyPrj.createChild(IImportTheory.ELEMENT_TYPE, null, null);
				importThy.setImportTheory(root1, null);
				return importThy;		
			}
		}
			
		final IImportTheoryProject impThyPrj = root.createChild(
				IImportTheoryProject.ELEMENT_TYPE, null, null);
		impThyPrj.setTheoryProject(root1.getRodinProject(), null);
		IImportTheory importThy = impThyPrj.createChild(IImportTheory.ELEMENT_TYPE, null, null);
		importThy.setImportTheory(root1, null);
		return importThy;
	}
	
	public IImportTheory addImportTheory(ITheoryRoot root, ITheoryRoot theory) throws RodinDBException {
		final IImportTheoryProject impThyPrj = root.createChild(
				IImportTheoryProject.ELEMENT_TYPE, null, null);
		impThyPrj.setTheoryProject(theory.getRodinProject(), null);
		IImportTheory importThy = impThyPrj.createChild(IImportTheory.ELEMENT_TYPE, null, null);
		importThy.setImportTheory(theory.getDeployedTheoryRoot(), null);
		return importThy;
	}

	public void addTheorems(ITheoryRoot root, String[] names, String[] thms) throws RodinDBException {
		for (int i = 0; i < names.length; i++) {
			addTheorem(root, names[i], thms[i]);
		}
	}

	public ITheorem addTheorem(ITheoryRoot root, String name, String predicate) throws RodinDBException {
		final ITheorem thm = root.createChild(ITheorem.ELEMENT_TYPE, null, null);
		thm.setPredicateString(predicate, null);
		thm.setLabel(name, null);
		return thm;
	}

	public void addTheorems(ITheoryRoot root, String... strings) throws RodinDBException {
		final int length = strings.length;
		assert length % 2 == 0;
		for (int i = 0; i < length; i += 2) {
			final ITheorem thm = root.createChild(ITheorem.ELEMENT_TYPE, null, null);
			thm.setLabel(strings[i], null);
			thm.setPredicateString(strings[i + 1], null);
		}
	}

	public void addTheorems(IRodinFile rodinFile, String[] names, String[] thms) throws RodinDBException {
		final ITheoryRoot root = (ITheoryRoot) rodinFile.getRoot();
		addTheorems(root, names, thms);
	}

	public void addTheorems(IRodinFile rodinFile, String... strings) throws RodinDBException {
		final ITheoryRoot root = (ITheoryRoot) rodinFile.getRoot();
		addTheorems(root, strings);
	}

	public IAxiomaticDefinitionsBlock addAxiomaticDefinitionsBlock(ITheoryRoot root, 
			String label) throws RodinDBException{
		IAxiomaticDefinitionsBlock block = root.createChild(IAxiomaticDefinitionsBlock.ELEMENT_TYPE, null, null);
		block.setLabel(label, null);
		return block;
	}
	
	public ISCAxiomaticDefinitionsBlock getSCAxiomaticDefinitionsBlock(ISCTheoryRoot scRoot, IAxiomaticDefinitionsBlock block)
	throws RodinDBException{
		for (ISCAxiomaticDefinitionsBlock scBlock : scRoot.getSCAxiomaticDefinitionsBlocks()){
			if (scBlock.getSource().equals(block)){
				return scBlock;
			}
		}
		fail("found no sc axiomatic block with source "+block);
		return null;
	}
	
	public ISCAxiomaticTypeDefinition getSCAxiomaticTypeDefinition(ISCAxiomaticDefinitionsBlock parent, String identifier)
	throws RodinDBException{
		for (ISCAxiomaticTypeDefinition scType : parent.getAxiomaticTypeDefinitions()){
			if (scType.getIdentifierString().equals(identifier)){
				return scType;
			}
		}
		fail("found no type definitions with name "+identifier);
		return null;
	}
	
	public IAxiomaticTypeDefinition addAxiomaticTypeDefinition(IAxiomaticDefinitionsBlock block, String identifier) throws RodinDBException{
		IAxiomaticTypeDefinition def = block.createChild(IAxiomaticTypeDefinition.ELEMENT_TYPE, null, null);
		def.setIdentifierString(identifier, null);
		return def;
	}

	public IAxiomaticOperatorDefinition addAxiomaticOperatorDefinition(IAxiomaticDefinitionsBlock block,
			String identifier, Notation notation, FormulaType formulaType, boolean associative, boolean commutative,
			String[] arguments, String[] argumentsTypes, String type) throws RodinDBException {
		IAxiomaticOperatorDefinition def = block.createChild(IAxiomaticOperatorDefinition.ELEMENT_TYPE, null, null);
		def.setLabel(identifier, null);
		def.setFormulaType(formulaType, null);
		def.setNotationType(notation.toString(), null);
		def.setAssociative(associative, null);
		def.setCommutative(commutative, null);
		assert arguments.length == argumentsTypes.length;
		for (int i = 0; i < arguments.length; i++) {
			IOperatorArgument opArg = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
			opArg.setIdentifierString(arguments[i], null);
			opArg.setExpressionString(argumentsTypes[i], null);
		}
		if (formulaType == FormulaType.EXPRESSION) {
			def.setType(type, null);
		}
		return def;
	}
	
	public IDatatypeDefinition addDatatypeDefinition(ITheoryRoot root, String identifier) throws RodinDBException {
		IDatatypeDefinition datatypeDefinition = root.createChild(IDatatypeDefinition.ELEMENT_TYPE, null, null);
		datatypeDefinition.setIdentifierString(identifier, null);
		return datatypeDefinition;
	}

	public IDatatypeDefinition addDatatypeDefinition(ITheoryRoot root, String identifier, String[] givenTypes,
			String[] constructors, String[][] destructors, String[][] destructorTypes) throws RodinDBException {
		IDatatypeDefinition datatypeDefinition = addDatatypeDefinition(root, identifier);
		addTypeArguments(datatypeDefinition, givenTypes);
		addDatatypeConstructors(datatypeDefinition, constructors, destructors, destructorTypes);
		return datatypeDefinition;
	}

	public void addTypeArguments(IDatatypeDefinition datatypeDefinition, String givenTypes[]) throws RodinDBException {
		for (String givenType : givenTypes) {
			addTypeArgument(datatypeDefinition, givenType);
		}
	}

	public ITypeArgument addTypeArgument(IDatatypeDefinition datatypeDefinition, String givenType)
			throws RodinDBException {
		ITypeArgument typeArg = datatypeDefinition.createChild(ITypeArgument.ELEMENT_TYPE, null, null);
		typeArg.setGivenType(givenType, null);
		return typeArg;
	}

	public void addDatatypeConstructors(IDatatypeDefinition datatypeDefinition, String constructors[],
			String[][] destructors, String[][] destructorTypes) throws RodinDBException {
		assert constructors.length == destructors.length;
		for (int i = 0; i < constructors.length; i++) {
			addDatatypeConstructor(datatypeDefinition, constructors[i], destructors[i], destructorTypes[i]);
		}
	}

	public IDatatypeConstructor addDatatypeConstructor(IDatatypeDefinition dt,
			String name) throws RodinDBException {
		IDatatypeConstructor constructor = dt.createChild(
				IDatatypeConstructor.ELEMENT_TYPE, null, null);
		constructor.setIdentifierString(name, null);
		return constructor;
	}

	public IDatatypeConstructor addDatatypeConstructor(IDatatypeDefinition dt,
			String name, String[] destructors, String[] destructorTypes)
			throws RodinDBException {
		IDatatypeConstructor constructor = addDatatypeConstructor(dt, name);
		addDatatypeDestructors(constructor, destructors, destructorTypes);
		return constructor;
	}

	public void addDatatypeDestructors(IDatatypeConstructor constructor, String[] destructors, String[] destructorTypes)
			throws RodinDBException {
		assert destructors.length == destructorTypes.length;
		for (int i = 0; i < destructors.length; i++) {
			addDatatypeDestructor(constructor, destructors[i], destructorTypes[i]);
		}
	}

	public IConstructorArgument addDatatypeDestructor(IDatatypeConstructor constructor, String destructor,
			String destructorType) throws RodinDBException {
		IConstructorArgument arg = constructor.createChild(IConstructorArgument.ELEMENT_TYPE, null, null);
		arg.setIdentifierString(destructor, null);
		arg.setType(destructorType, null);
		return arg;
	}

	public INewOperatorDefinition addOperatorDefinitionWithDirectDef(ITheoryRoot root, String label, Notation notation,
			FormulaType type, boolean assoc, boolean commut, String[] arguments, String[] argumentTypeExpressions,
			String[] wdConditions, String directDef) throws RodinDBException {
		INewOperatorDefinition operatorDefinition = addRawOperatorDefinition(root, label, notation, type, assoc,
				commut, arguments, argumentTypeExpressions, wdConditions);
		addDirectOperatorDefinition(operatorDefinition, directDef);
		return operatorDefinition;
	}

	public INewOperatorDefinition addOperatorDefinitionWithRecDef(ITheoryRoot root, String label, Notation notation,
			FormulaType type, boolean assoc, boolean commut, String[] arguments, String[] argumentTypeExpressions,
			String[] wdConditions, String inductiveArgument, String cases[], String values[]) throws RodinDBException {
		INewOperatorDefinition operatorDefinition = addRawOperatorDefinition(root, label, notation, type, assoc,
				commut, arguments, argumentTypeExpressions, wdConditions);
		addRecursiveOperatorDefinition(operatorDefinition, inductiveArgument, cases, values);
		return operatorDefinition;
	}

	public void addRecursiveOperatorDefinition(INewOperatorDefinition operatorDefinition, String inductiveArgument,
			String cases[], String values[]) throws RodinDBException {
		IRecursiveOperatorDefinition recDef = operatorDefinition.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE,
				null, null);
		recDef.setInductiveArgument(inductiveArgument, null);
		addRecursiveCases(recDef, cases, values);
	}

	public void addRecursiveCases(IRecursiveOperatorDefinition recDef, String[] cases, String[] values)
			throws RodinDBException {
		assert cases.length == values.length;
		for (int i = 0; i < cases.length; i++) {
			IRecursiveDefinitionCase recCase = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
			recCase.setExpressionString(cases[i], null);
			recCase.setFormula(values[i], null);
		}

	}

	public void addDirectOperatorDefinition(INewOperatorDefinition operatorDefinition, String directDef)
			throws RodinDBException {
		IDirectOperatorDefinition direct = operatorDefinition.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null,
				null);
		direct.setFormula(directDef, null);
	}

	public INewOperatorDefinition addRawOperatorDefinition(ITheoryRoot root, String label, Notation notation,
			FormulaType type, boolean assoc, boolean commut, String[] arguments, String[] argumentTypeExpressions,
			String[] wdConditions) throws RodinDBException {
		INewOperatorDefinition operatorDefinition = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		operatorDefinition.setLabel(label, null);
		operatorDefinition.setNotationType(notation.toString(), null);
		operatorDefinition.setFormulaType(type, null);
		operatorDefinition.setAssociative(assoc, null);
		operatorDefinition.setCommutative(commut, null);
		addOperatorArguments(operatorDefinition, arguments, argumentTypeExpressions);
		addWDConditions(operatorDefinition, wdConditions);
		return operatorDefinition;
	}

	public void addOperatorArguments(INewOperatorDefinition operatorDefinition, String[] arguments,
			String[] argumentTypeExpressions) throws RodinDBException {
		assert arguments.length == argumentTypeExpressions.length;
		for (int i = 0; i < arguments.length; i++) {
			IOperatorArgument opArg = operatorDefinition.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
			opArg.setIdentifierString(arguments[i], null);
			opArg.setExpressionString(argumentTypeExpressions[i], null);
		}
	}

	public void addWDConditions(INewOperatorDefinition operatorDefinition, String[] wdConditions)
			throws RodinDBException {
		for (String wd : wdConditions) {
			IOperatorWDCondition wdCondition = operatorDefinition.createChild(IOperatorWDCondition.ELEMENT_TYPE, null,
					null);
			wdCondition.setPredicateString(wd, null);
		}
	}

	public IProofRulesBlock addProofRulesBlock(ITheoryRoot root, String label) throws RodinDBException {
		IProofRulesBlock block = root.createChild(IProofRulesBlock.ELEMENT_TYPE, null, null);
		block.setLabel(label, null);
		return block;
	}

	public IMetavariable addMetavariable(IProofRulesBlock block, String ident, String type) throws RodinDBException {
		IMetavariable var = block.createChild(IMetavariable.ELEMENT_TYPE, null, null);
		var.setIdentifierString(ident, null);
		var.setType(type, null);
		return var;
	}

	public IRewriteRule addRewriteRule(ITheoryRoot root, String blockName, String label, String formula,
			boolean complete, RuleApplicability applic, String desc, String[] rhsLabels, String[] rhsConds,
			String[] rhsRewrites) throws RodinDBException {
		IProofRulesBlock block = addProofRulesBlock(root, blockName);
		return addRewriteRule(block, label, formula, complete, applic, desc, rhsLabels, rhsConds, rhsRewrites);
	}

	public IRewriteRule addRewriteRule(IProofRulesBlock block, String label, String formula, boolean complete,
			RuleApplicability applic, String desc, String[] rhsLabels, String[] rhsConds, String[] rhsRewrites)
			throws RodinDBException {
		IRewriteRule rule = block.createChild(IRewriteRule.ELEMENT_TYPE, null, null);
		rule.setLabel(label, null);
		rule.setFormula(formula, null);
		rule.setComplete(complete, null);
		rule.setApplicability(applic, null);
		rule.setDescription(desc, null);
		addRuleRhs(rule, rhsLabels, rhsConds, rhsRewrites);
		return rule;
	}

	public void addRuleRhs(IRewriteRule rule, String[] rhsLabels, String[] rhsConds, String[] rhsRewrites)
			throws RodinDBException {
		assert rhsLabels.length == rhsConds.length && rhsConds.length == rhsRewrites.length;
		for (int i = 0; i < rhsLabels.length; i++) {
			addRuleRhs(rule, rhsLabels[i], rhsConds[i], rhsRewrites[i]);
		}
	}

	public IRewriteRuleRightHandSide addRuleRhs(IRewriteRule rule, String label, String cond, String rhsRew)
			throws RodinDBException {
		IRewriteRuleRightHandSide rhs = rule.createChild(IRewriteRuleRightHandSide.ELEMENT_TYPE, null, null);
		rhs.setLabel(label, null);
		rhs.setPredicateString(cond, null);
		rhs.setFormula(rhsRew, null);
		return rhs;
	}

	public IInferenceRule addInferenceRule(IProofRulesBlock block, String label, RuleApplicability applicability,
			String desc, String[] infers, String givens[], boolean[] hyps) throws RodinDBException {
		IInferenceRule rule = block.createChild(IInferenceRule.ELEMENT_TYPE, null, null);
		rule.setLabel(label, null);
		rule.setApplicability(applicability, null);
		rule.setDescription(desc, null);
		addInfers(rule, infers);
		addGivens(rule, givens, hyps);
		return rule;
	}

	public IInferenceRule addInferenceRule(ITheoryRoot root, String blockName, String label,
			RuleApplicability applicability, String desc, String[] infers, String givens[], boolean[] hyps)
			throws RodinDBException {
		IProofRulesBlock block = addProofRulesBlock(root, blockName);
		return addInferenceRule(block, label, applicability, desc, infers, givens, hyps);
	}

	public void addGivens(IInferenceRule rule, String[] givens, boolean hyps[]) throws RodinDBException {
		for (int i = 0; i < givens.length; i++) {
			IGiven given = rule.createChild(IGiven.ELEMENT_TYPE, null, null);
			given.setPredicateString(givens[i], null);
			if (hyps[i]) {
				given.setHyp(true, null);
			}
		}
	}

	public void addInfers(IInferenceRule rule, String[] infers) throws RodinDBException {
		for (int i = 0; i < infers.length; i++) {
			IInfer infer = rule.createChild(IInfer.ELEMENT_TYPE, null, null);
			infer.setPredicateString(infers[i], null);
		}
	}

	public ISCGiven getGiven(ISCInferenceRule parent, IGiven source) throws CoreException {
		ISCGiven[] gs = parent.getGivens();
		for (ISCGiven g : gs) {
			if (g.getSource().equals(source)) {
				return g;
			}
		}
		return null;
	}

	public ISCInfer getInfer(ISCInferenceRule parent, IInfer source) throws CoreException {
		ISCInfer[] is = parent.getInfers();
		for (ISCInfer i : is) {
			if (i.getSource().equals(source)) {
				return i;
			}
		}
		return null;
	}

	public Set<IEventBRoot> roots;

	protected void addRoot(IEventBRoot root) {
		if (!roots.contains(root))
			roots.add(root);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		roots = new HashSet<IEventBRoot>();
	}

	@Override
	public void tearDown() throws Exception {
		roots = null;
		super.tearDown();
	}

	@Override
	protected void runBuilder() throws CoreException {
		super.runBuilder();
		checkSources();
		roots.clear(); // forget
	}

	private void checkSources() throws RodinDBException {
		for (IEventBRoot root : roots) {
			if (root.exists())
				checkSources(root);
		}
	}

	private boolean isTheory(IOpenable element) {
		if (element instanceof IRodinFile) {
			IInternalElement root = ((IRodinFile) element).getRoot();
			return (root instanceof ITheoryRoot);
		} else {
			return false;
		}
	}

	public IGiven addGiven(IInferenceRule inf, String predicate, boolean hyp) throws CoreException {
		IGiven given = inf.createChild(IGiven.ELEMENT_TYPE, null, null);
		given.setPredicateString(predicate, null);
		given.setHyp(hyp, null);
		return given;
	}

	public IInfer addInfer(IInferenceRule inf, String predicate) throws CoreException {
		IInfer infer = inf.createChild(IInfer.ELEMENT_TYPE, null, null);
		infer.setPredicateString(predicate, null);
		return infer;
	}

	private void checkSources(IRodinElement element) throws RodinDBException {
		if (element instanceof ITraceableElement) {
			IRodinElement sourceElement = ((ITraceableElement) element).getSource();

			assertTrue("source reference must be in unchecked file", isTheory(sourceElement.getOpenable()));
		}
		if (element instanceof IInternalElement) {

			IInternalElement parent = (IInternalElement) element;

			IRodinElement[] elements = parent.getChildren();

			for (IRodinElement child : elements)
				checkSources(child);

		}
	}

	public static String[] makeSList(String... strings) {
		return strings;
	}

	public static boolean[] makeBList(boolean... bools) {
		return bools;
	}
}
