/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import static org.eventb.core.EventBAttributes.SOURCE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.HAS_ERROR_ATTRIBUTE;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.TheoryCoreFacadeDB;
import org.eventb.theory.core.TheoryCoreFacadeGeneral;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class DeployUtilities {

	/**
	 * Duplicates the source element as a child of the new parent element. It
	 * copies all of the source details ignoring the given attributes.
	 * 
	 * @param <E>
	 *            the type of the source
	 * @param source
	 *            the source element
	 * @param type
	 *            the type
	 * @param newParent
	 *            the new parent element
	 * @param monitor
	 *            the progress monitor
	 * @param toIgnore
	 *            the attribute types to ignore when copying
	 * @return the new element that is a copy of the source and has the new
	 *         parent
	 * @throws CoreException
	 */
	public static final <E extends IInternalElement> E duplicate(E source,
			IInternalElementType<E> type, IInternalElement newParent,
			IProgressMonitor monitor, IAttributeType... toIgnore)
			throws CoreException {
		assert source.exists();
		assert newParent.exists();
		List<IAttributeType> toIgnoreList = Arrays.asList(toIgnore);
		IAttributeType[] attrTypes = source.getAttributeTypes();
		E newElement = newParent.getInternalElement(type,
				source.getElementName());
		newElement.create(null, monitor);
		for (IAttributeType attr : attrTypes) {
			if (toIgnoreList.contains(attr)) {
				continue;
			}
			IAttributeValue value = source.getAttributeValue(attr);
			newElement.setAttributeValue(value, monitor);
		}

		return newElement;
	}

	/**
	 * Calculates the soundness of the given labelled element.
	 * 
	 * @param root
	 *            the parent SC theory
	 * @param element
	 *            the labelled element
	 * @throws RodinDBException
	 */
	public static boolean calculateSoundness(ISCTheoryRoot root,
			ILabeledElement element) throws RodinDBException {
		IPSRoot psRoot = root.getPSRoot();
		if (psRoot == null || !psRoot.exists()) {
			return false;
		}
		IPSStatus[] sts = psRoot.getStatuses();
		boolean isSound = true;
		for (IPSStatus s : sts) {
			if (s.getElementName().startsWith(element.getLabel())) {
				if (!TheoryCoreFacadeGeneral.isDischarged(s)
						&& !TheoryCoreFacadeGeneral.isReviewed(s)) {
					isSound = false;
				}
			}
		}
		return isSound;

	}

	/**
	 * Copies the mathematical extensions in the source to the target Event-B
	 * element.
	 * 
	 * @param target
	 *            the target
	 * @param source
	 *            the source of mathematical extensions
	 * @throws CoreException
	 */
	public static boolean copyMathematicalExtensions(
			IFormulaExtensionsSource target,
			IFormulaExtensionsSource source, IProgressMonitor monitor)
			throws CoreException {
		boolean isFaithful = true;
		// copy type parameters
		// //////////////////////////////
		ISCTypeParameter[] typeParameters = source.getSCTypeParameters();
		for (ISCTypeParameter typeParameter : typeParameters) {
			copyTypeParameter(typeParameter, target, monitor);
		}
		// copy datatypes
		// //////////////////////////////
		ISCDatatypeDefinition[] datatypeDefinitions = source
				.getSCDatatypeDefinitions();
		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			if (definition.hasHasErrorAttribute() && definition.hasError()) {
				isFaithful = false;
				continue;
			}
			copyDatatype(definition, target);
		}
		// copy operators
		// //////////////////////////////
		ISCNewOperatorDefinition[] operatorDefinitions = source
				.getSCNewOperatorDefinitions();
		for (ISCNewOperatorDefinition operatorDefinition : operatorDefinitions) {
			if (operatorDefinition.hasHasErrorAttribute()
					&& operatorDefinition.hasError()) {
				isFaithful = false;
				continue;
			}
			copyOperatorDefinition(operatorDefinition, target, monitor);
		}
		return isFaithful;
	}

	/**
	 * Copies the prover extensions in the source to the target Event-B element.
	 * 
	 * @param target
	 *            the target
	 * @param source
	 *            the source of prover extensions
	 * @throws CoreException
	 */
	public static boolean copyProverExtensions(IExtensionRulesSource target,
			IExtensionRulesSource source, IProgressMonitor monitor)
			throws CoreException {
		boolean isFaithful = true;
		// copy proof blocks
		// ////////////////////////////////
		ISCProofRulesBlock[] rulesBlocks = source.getProofRulesBlocks();
		for (ISCProofRulesBlock rulesBlock : rulesBlocks) {
			ISCProofRulesBlock newRulesBlock = duplicate(rulesBlock,
					ISCProofRulesBlock.ELEMENT_TYPE, target, null,
					SOURCE_ATTRIBUTE);
			ISCMetavariable[] vars = rulesBlock.getMetavariables();
			for (ISCMetavariable var : vars) {
				copyMetavariables(var, newRulesBlock, monitor);
			}
			ISCRewriteRule[] rewRules = rulesBlock.getRewriteRules();
			for (ISCRewriteRule rewRule : rewRules) {
				if (!rewRule.isAccurate()) {
					isFaithful = false;
				}
				copyRewriteRule(rewRule, newRulesBlock, monitor);

			}
			ISCInferenceRule[] infRules = rulesBlock.getInferenceRules();
			for (ISCInferenceRule infRule : infRules) {
				if (!infRule.isAccurate()) {
					isFaithful = false;
				}
				copyInferenceRule(infRule, newRulesBlock, monitor);

			}
		}
		// copy theorems
		// ////////////////////////////////
		ISCTheorem[] theorems = source.getTheorems();
		for (ISCTheorem theorem : theorems) {
			ISCTheorem newTheorem = duplicate(theorem, ISCTheorem.ELEMENT_TYPE,
					target, monitor);
			if (!theorem.hasValidatedAttribute()) {
				boolean isSound = DeployUtilities.calculateSoundness(
						TheoryCoreFacadeDB.getSCTheoryParent(theorem), theorem);
				newTheorem.setValidated(isSound, monitor);

			}
		}
		return isFaithful;
	}

	private static void copyInferenceRule(ISCInferenceRule infRule,
			ISCProofRulesBlock newRulesBlock, IProgressMonitor monitor)
			throws CoreException {
		ISCInferenceRule newInfRule = duplicate(infRule,
				ISCInferenceRule.ELEMENT_TYPE, newRulesBlock, monitor,
				SOURCE_ATTRIBUTE);
		ISCInfer[] infers = infRule.getInfers();
		for (ISCInfer infer : infers) {
			duplicate(infer, ISCInfer.ELEMENT_TYPE, newInfRule, monitor,
					SOURCE_ATTRIBUTE);
		}
		ISCGiven[] givens = infRule.getGivens();
		for (ISCGiven given : givens) {
			duplicate(given, ISCGiven.ELEMENT_TYPE, newInfRule, monitor,
					SOURCE_ATTRIBUTE);
		}
		if (!infRule.hasValidatedAttribute()) {
			boolean isSound = DeployUtilities.calculateSoundness(
					TheoryCoreFacadeDB.getSCTheoryParent(infRule), infRule);
			newInfRule.setValidated(isSound, monitor);

		}

	}

	private static void copyRewriteRule(ISCRewriteRule rewRule,
			ISCProofRulesBlock newRulesBlock, IProgressMonitor monitor)
			throws CoreException {
		ISCRewriteRule newRewRule = duplicate(rewRule,
				ISCRewriteRule.ELEMENT_TYPE, newRulesBlock, monitor,
				SOURCE_ATTRIBUTE);
		ISCRewriteRuleRightHandSide[] ruleRHSs = rewRule.getRuleRHSs();
		for (ISCRewriteRuleRightHandSide rhs : ruleRHSs) {
			duplicate(rhs, ISCRewriteRuleRightHandSide.ELEMENT_TYPE,
					newRewRule, monitor, SOURCE_ATTRIBUTE);
		}
		if (!rewRule.hasValidatedAttribute()) {
			boolean isSound = DeployUtilities.calculateSoundness(
					TheoryCoreFacadeDB.getSCTheoryParent(rewRule), rewRule);
			newRewRule.setValidated(isSound, monitor);

		}

	}

	private static void copyMetavariables(ISCMetavariable var,
			ISCProofRulesBlock newRulesBlock, IProgressMonitor monitor)
			throws CoreException {
		duplicate(var, ISCMetavariable.ELEMENT_TYPE, newRulesBlock, monitor,
				SOURCE_ATTRIBUTE);

	}

	private static void copyOperatorDefinition(
			ISCNewOperatorDefinition operatorDefinition,
			IFormulaExtensionsSource target, IProgressMonitor monitor)
			throws CoreException {
		ISCNewOperatorDefinition newDefinition = duplicate(operatorDefinition,
				ISCNewOperatorDefinition.ELEMENT_TYPE, target, null,
				HAS_ERROR_ATTRIBUTE);
		ISCOperatorArgument[] operatorArguments = operatorDefinition
				.getOperatorArguments();
		for (ISCOperatorArgument operatorArgument : operatorArguments) {
			copyOperatorArgument(operatorArgument, newDefinition, monitor);

		}
		ISCDirectOperatorDefinition[] directDefinitions = operatorDefinition
				.getDirectOperatorDefinitions();
		for (ISCDirectOperatorDefinition directDefinition : directDefinitions) {
			copyDirectDefinition(directDefinition, newDefinition, monitor);

		}
		if (!newDefinition.hasValidatedAttribute()) {
			boolean isSound = DeployUtilities.calculateSoundness(
					TheoryCoreFacadeDB.getSCTheoryParent(operatorDefinition),
					operatorDefinition);
			newDefinition.setValidated(isSound, monitor);

		}

	}

	private static void copyDirectDefinition(
			ISCDirectOperatorDefinition directDefinition,
			ISCNewOperatorDefinition newDefinition, IProgressMonitor monitor)
			throws CoreException {
		duplicate(directDefinition, ISCDirectOperatorDefinition.ELEMENT_TYPE,
				newDefinition, monitor);
	}

	private static void copyOperatorArgument(
			ISCOperatorArgument operatorArgument,
			ISCNewOperatorDefinition newDefinition, IProgressMonitor monitor)
			throws CoreException {
		duplicate(operatorArgument, ISCOperatorArgument.ELEMENT_TYPE,
				newDefinition, monitor);

	}

	private static void copyTypeParameter(ISCTypeParameter typeParameter,
			IFormulaExtensionsSource target, IProgressMonitor monitor)
			throws CoreException {
		duplicate(typeParameter, ISCTypeParameter.ELEMENT_TYPE, target, monitor);

	}

	private static void copyDatatype(ISCDatatypeDefinition definition,
			IFormulaExtensionsSource target) throws CoreException {
		ISCDatatypeDefinition newDefinition = duplicate(definition,
				ISCDatatypeDefinition.ELEMENT_TYPE, target, null,
				HAS_ERROR_ATTRIBUTE);
		ISCTypeArgument[] typeArguments = definition.getTypeArguments();

		for (ISCTypeArgument typeArgument : typeArguments) {
			copyTypeArguments(typeArgument, newDefinition);
		}

		ISCDatatypeConstructor[] datatypeConstructors = definition
				.getConstructors();
		for (ISCDatatypeConstructor constructor : datatypeConstructors) {
			copyConstructor(constructor, newDefinition);
		}

	}

	private static void copyConstructor(ISCDatatypeConstructor constructor,
			ISCDatatypeDefinition newDefinition) throws CoreException {
		ISCDatatypeConstructor newConstructor = duplicate(constructor,
				ISCDatatypeConstructor.ELEMENT_TYPE, newDefinition, null);
		ISCConstructorArgument arguments[] = constructor
				.getConstructorArguments();
		for (ISCConstructorArgument argument : arguments) {
			duplicate(argument, ISCConstructorArgument.ELEMENT_TYPE,
					newConstructor, null);
		}

	}

	private static void copyTypeArguments(ISCTypeArgument typeArgument,
			ISCDatatypeDefinition newDefinition) throws CoreException {
		duplicate(typeArgument, ISCTypeArgument.ELEMENT_TYPE, newDefinition,
				null);

	}

}
