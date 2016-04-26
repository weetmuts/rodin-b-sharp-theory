/*******************************************************************************
 * Copyright (c) 2015 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.tests.rbp.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IConstructorArgument;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.IMetavariable;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.ITypeParameter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since
 */
public class TheoryUtils {

	/**
	 * @param rodinPrj
	 * @param name
	 * @param monitor
	 * @return
	 * @throws RodinDBException
	 */
	public static ITheoryRoot createTheory(IRodinProject rodinPrj, String name,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100%
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		ITheoryRoot theory = DatabaseUtilities.getTheory(name,
				rodinPrj);

		theory.getRodinFile().create(true, subMonitor.newChild(50));

		theory.setConfiguration(DatabaseUtilities.THEORY_CONFIGURATION,
				subMonitor.newChild(50));
		return theory;
	}

	public static IImportTheoryProject createImportTheoryProject(
			ITheoryRoot thyRoot, IRodinProject rodinProject,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100%
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IImportTheoryProject importThyPrj = thyRoot.createChild(
				IImportTheoryProject.ELEMENT_TYPE, null,
				subMonitor.newChild(50));
		importThyPrj.setTheoryProject(rodinProject, subMonitor.newChild(50));

		return importThyPrj;
	}
	
	/**
	 * @param importThyPrj
	 * @param root
	 * @param nullMonitor
	 * @return
	 * @throws RodinDBException 
	 */
	public static IImportTheory createImportTheory(
			IImportTheoryProject importThyPrj, ITheoryRoot root,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100%
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IImportTheory importThy = importThyPrj.createChild(
				IImportTheory.ELEMENT_TYPE, null, subMonitor.newChild(50));
		importThy.setImportTheory(root.getDeployedTheoryRoot(),
				subMonitor.newChild(50));
		return importThy;
	}

	public static IProofRulesBlock createProofRulesBlock(ITheoryRoot thyRoot,
			String blkLabel, IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// Create the proof rules block (50%)
		IProofRulesBlock prfRulesBlk = thyRoot.createChild(
				IProofRulesBlock.ELEMENT_TYPE, null, subMonitor.newChild(50));

		// Set the block name (50%)
		prfRulesBlk.setLabel(blkLabel, subMonitor.newChild(50));

		return prfRulesBlk;
	}

	/**
	 * @param rule
	 * @param nullMonitor
	 * @throws RodinDBException
	 */
	public static IMetavariable createMetavariable(IProofRulesBlock prfRuleBlk,
			String identifier, String type, IProgressMonitor monitor)
			throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IMetavariable mVar = prfRuleBlk.createChild(IMetavariable.ELEMENT_TYPE,
				null, subMonitor.newChild(80));
		mVar.setIdentifierString(identifier, subMonitor.newChild(10));
		mVar.setType(type, subMonitor.newChild(10));
		return mVar;
	}

	public static IRewriteRule createAutoRewriteRule(
			IProofRulesBlock prfRuleBlk, String label, String formula,
			boolean isComplete, RuleApplicability applicability,
			String description, IProgressMonitor monitor)
			throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IRewriteRule rule = prfRuleBlk.createChild(IRewriteRule.ELEMENT_TYPE,
				null, subMonitor.newChild(50));
		rule.setLabel(label, subMonitor.newChild(10));
		rule.setFormula(formula, subMonitor.newChild(10));
		rule.setComplete(isComplete, subMonitor.newChild(10));
		rule.setApplicability(applicability, subMonitor.newChild(10));
		rule.setDescription(description, subMonitor.newChild(10));

		return rule;
	}

	public static IRewriteRuleRightHandSide createRewriteRuleRHS(
			IRewriteRule rule, String label, String predicate, String formula,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IRewriteRuleRightHandSide rhs = rule.createChild(
				IRewriteRuleRightHandSide.ELEMENT_TYPE, null,
				subMonitor.newChild(70));
		rhs.setLabel(label, subMonitor.newChild(10));
		rhs.setPredicateString(predicate, subMonitor.newChild(10));
		rhs.setFormula(formula, subMonitor.newChild(10));

		return rhs;
	}

	/**
	 * @param ebPrj
	 * @param string
	 * @param nullMonitor
	 * @throws RodinDBException
	 */
	public static ITheoryPathRoot createTheoryPath(IRodinProject rodinPrj,
			String string, IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// Create the file (80%)
		IRodinFile rodinFile = rodinPrj.getRodinFile("TheoryPath.tul");
		rodinFile.create(false, subMonitor.newChild(80));

		// Set the configuration (10%)
		final IInternalElement rodinRoot = rodinFile.getRoot();
		((IConfigurationElement) rodinRoot).setConfiguration(
				DatabaseUtilitiesTheoryPath.THEORY_PATH_CONFIGURATION,
				subMonitor.newChild(10));

		// Save the file (10%)
		rodinFile.save(subMonitor.newChild(10), true);

		return (ITheoryPathRoot) rodinFile.getRoot();
	}

	/**
	 * @param thyPathRoot
	 * @param string
	 * @param nullMonitor
	 * @throws RodinDBException
	 */
	public static IAvailableTheoryProject createAvailableTheoryProject(
			ITheoryPathRoot thyPathRoot, IRodinProject thyPrj,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// Create the element (50%)
		IAvailableTheoryProject availableThyPrj = thyPathRoot.createChild(
				IAvailableTheoryProject.ELEMENT_TYPE, null,
				subMonitor.newChild(50));

		// Set the project name (50%)
		availableThyPrj.setTheoryProject(thyPrj, subMonitor.newChild(50));

		return availableThyPrj;
	}

	/**
	 * @param availableThyPrj
	 * @param elementName
	 * @param nullMonitor
	 * @throws RodinDBException
	 */
	public static IAvailableTheory createAvailableTheory(
			IAvailableTheoryProject availableThyPrj,
			IDeployedTheoryRoot deployedThyRoot, IProgressMonitor monitor)
			throws RodinDBException {

		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// Create the element (50%)
		IAvailableTheory availableThy = availableThyPrj.createChild(
				IAvailableTheory.ELEMENT_TYPE, null, subMonitor.newChild(50));

		// Set the theory name
		availableThy.setAvailableTheory(deployedThyRoot,
				subMonitor.newChild(50));

		return availableThy;
	}

	public static ITheorem createTheorem(ITheoryRoot thyRoot, String label,
			String predStr, IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		ITheorem thm = thyRoot.createChild(ITheorem.ELEMENT_TYPE, null,
				subMonitor.newChild(30));
		thm.setLabel(label, subMonitor.newChild(30));
		thm.setPredicateString(predStr, subMonitor.newChild(40));
		return thm;
	}

	/**
	 * @param thyRoot
	 * @param string
	 * @return
	 * @throws RodinDBException
	 */
	public static ITypeParameter createTypeParameter(ITheoryRoot thyRoot,
			String identifier, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// 1. Create the element 
		ITypeParameter typeParam = thyRoot.createChild(
				ITypeParameter.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(50));

		// 2. Set the identifier string.
		typeParam.setIdentifierString(identifier, subMonitor.newChild(50));
		return typeParam;
	}

	/**
	 * @param thyRoot
	 * @param string
	 * @param object
	 * @param nullMonitor
	 * @throws RodinDBException 
	 */
	public static IDatatypeDefinition createDataType(ITheoryRoot thyRoot,
			String identifier, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// 1. Create the element (50%)
		IDatatypeDefinition dataTypeDef = thyRoot.createChild(
				IDatatypeDefinition.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(50));

		// 2. Set the identifier string (50%)
		dataTypeDef.setIdentifierString(identifier, subMonitor.newChild(50));
		return dataTypeDef;
	}

	/**
	 * @param listDT
	 * @param identifier
	 * @param nextSibling
	 * @param nullMonitor
	 * @return 
	 * @throws RodinDBException 
	 */
	public static ITypeArgument createTypeArgument(IDatatypeDefinition datatype,
			String givenType, IInternalElement nextSibling, IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// 1. Create the element (50%).
		ITypeArgument typeParam = datatype.createChild(
				ITypeArgument.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(50));

		// 2. Set the given type (50%).
		typeParam.setGivenType(givenType, subMonitor.newChild(50));
		return typeParam;
	}

	/**
	 * @param listDT
	 * @param string
	 * @param object
	 * @param nullMonitor
	 * @return
	 * @throws RodinDBException
	 */
	public static IDatatypeConstructor createConstructor(
			IDatatypeDefinition datatype, String identifier,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// 1. Create the element (50%).
		IDatatypeConstructor constructor = datatype.createChild(
				IDatatypeConstructor.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(50));

		// 2. Set the identifier string (50%).
		constructor.setIdentifierString(identifier, subMonitor.newChild(50));
		return constructor;
	}

	/**
	 * @param appendConstr
	 * @param string
	 * @param string2
	 * @param object
	 * @param nullMonitor
	 * @throws RodinDBException 
	 */
	public static void createDestructor(IDatatypeConstructor appendConstr,
			String identifier, String type, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// 1. Create the element (30%).
		IConstructorArgument destructor = appendConstr.createChild(
				IConstructorArgument.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(30));
		
		// 2. Set the identifier string (30%)
		destructor.setIdentifierString(identifier, subMonitor.newChild(30));
		
		// 3. Set the type (30%)
		destructor.setType(type, subMonitor.newChild(40));
	}

	/**
	 * @param thyRoot
	 * @param string
	 * @param object
	 * @param nullMonitor
	 * @return 
	 * @throws RodinDBException 
	 */
	public static INewOperatorDefinition createOperator(ITheoryRoot thyRoot,
			String label, boolean isAssociative, boolean isCommutative,
			FormulaType type, Notation prefix, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		// 1. Create the element (20%).
		INewOperatorDefinition newOp = thyRoot.createChild(
				INewOperatorDefinition.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(50));
		
		// 2. Set the label (20%)
		newOp.setLabel(label, subMonitor.newChild(20));
		
		newOp.setAssociative(isAssociative, subMonitor.newChild(20));
		
		newOp.setCommutative(isCommutative, subMonitor.newChild(20));
		
		newOp.setFormulaType(type, subMonitor.newChild(10));
		
		newOp.setNotationType(prefix.toString(), subMonitor.newChild(10));

		return newOp;
	}

	/**
	 * @param length
	 * @param string
	 * @param string2
	 * @param object
	 * @param nullMonitor
	 * @return 
	 * @throws RodinDBException 
	 */
	public static IOperatorArgument createArgument(INewOperatorDefinition op,
			String identifier, String expression, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IOperatorArgument argument = op.createChild(
				IOperatorArgument.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(30));
		
		argument.setIdentifierString(identifier, subMonitor.newChild(30));
		
		argument.setExpressionString(expression, subMonitor.newChild(40));
		
		return argument;
	}

	/**
	 * @param length
	 * @param string
	 * @param object
	 * @param nullMonitor
	 * @return
	 * @throws RodinDBException
	 */
	public static IRecursiveOperatorDefinition createRecursiveDefinition(
			INewOperatorDefinition op, String inductiveArgument,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IRecursiveOperatorDefinition recursiveDef = op.createChild(
				IRecursiveOperatorDefinition.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(50));

		recursiveDef.setInductiveArgument(inductiveArgument,
				subMonitor.newChild(50));
		
		return recursiveDef;
	}

	/**
	 * @param recDef
	 * @param string
	 * @param string2
	 * @param object
	 * @param nullMonitor
	 * @return
	 * @throws RodinDBException
	 */
	public static IRecursiveDefinitionCase createRecursiveCase(
			IRecursiveOperatorDefinition recDef, String expressionStr,
			String formula, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		// Split the monitor into 100
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IRecursiveDefinitionCase recCase = recDef.createChild(
				IRecursiveDefinitionCase.ELEMENT_TYPE, nextSibling,
				subMonitor.newChild(30));

		recCase.setExpressionString(expressionStr, subMonitor.newChild(30));

		recCase.setFormula(formula, subMonitor.newChild(40));
		return recCase;
	}

}
