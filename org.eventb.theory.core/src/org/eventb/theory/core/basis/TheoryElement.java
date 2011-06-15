/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.theory.core.TheoryAttributes.ASSOCIATIVE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.AUTOMATIC_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.COMMUTATIVE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.COMPLETE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.DEFINITIONAL_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.DESC_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.FORMULA_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.GIVEN_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.GROUP_ID_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.IMPORT_THEORY_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.INTERACTIVE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.NOTATION_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.REASONING_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.VALIDATED_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.basis.EventBElement;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.eventb.theory.core.AstUtilities;
import org.eventb.theory.core.IAssociativeElement;
import org.eventb.theory.core.IAutomaticElement;
import org.eventb.theory.core.ICommutativeElement;
import org.eventb.theory.core.ICompleteElement;
import org.eventb.theory.core.IDefinitionalElement;
import org.eventb.theory.core.IDescriptionElement;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.IFormulaTypeElement;
import org.eventb.theory.core.IGivenTypeElement;
import org.eventb.theory.core.IImportTheoryElement;
import org.eventb.theory.core.IInductiveArgumentElement;
import org.eventb.theory.core.IInteractiveElement;
import org.eventb.theory.core.INotationTypeElement;
import org.eventb.theory.core.IOperatorGroupElement;
import org.eventb.theory.core.IReasoningTypeElement;
import org.eventb.theory.core.ISCFormulaElement;
import org.eventb.theory.core.ISCGivenTypeElement;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeElement;
import org.eventb.theory.core.ITypeElement;
import org.eventb.theory.core.IValidatedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation for Event-B Theory elements.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class TheoryElement extends EventBElement implements
		IAssociativeElement, ICommutativeElement, IFormulaElement,
		IFormulaTypeElement, INotationTypeElement, 
		ITypeElement, IAutomaticElement, ICompleteElement, IDescriptionElement,
		IInteractiveElement, IDefinitionalElement, ISCTypeElement,
		IGivenTypeElement, ISCGivenTypeElement, ISCFormulaElement,
		IReasoningTypeElement, IValidatedElement, IOperatorGroupElement,
		IImportTheoryElement, IInductiveArgumentElement {

	public static final String BACKWARD_REASONING_TYPE = "backward";
	public static final String FORWARD_REASONING_TYPE = "forward";
	public static final String BACKWARD_AND_FORWARD_REASONING_TYPE = "both";

	public TheoryElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasOperatorGroup() throws RodinDBException {
		return hasAttribute(GROUP_ID_ATTRIBUTE);
	}

	@Override
	public String getOperatorGroup() throws RodinDBException {
		return getAttributeValue(GROUP_ID_ATTRIBUTE);
	}

	@Override
	public void setOperatorGroup(String newGroup, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(GROUP_ID_ATTRIBUTE, newGroup, monitor);
	}
	
	@Override
	public Type getType(FormulaFactory factory) throws RodinDBException {
		String type = getType();
		IParseResult result = factory.parseType(type, V2);
		if (result.hasProblem()){
			throw Util.newRodinDBException(org.eventb.theory.core.sc.Messages.database_SCTypeParseFailure, this);
		}
		return result.getParsedType();
	}

	@Override
	public void setType(Type type, IProgressMonitor monitor)
			throws RodinDBException {
		setType(type.toString(), monitor);
	}

	@Override
	public boolean hasAssociativeAttribute() throws RodinDBException {
		return hasAttribute(ASSOCIATIVE_ATTRIBUTE);
	}

	@Override
	public boolean isAssociative() throws RodinDBException {
		return getAttributeValue(ASSOCIATIVE_ATTRIBUTE);
	}

	public void setAssociative(boolean isAssociative, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(ASSOCIATIVE_ATTRIBUTE, isAssociative, monitor);
	}

	@Override
	public boolean hasCommutativeAttribute() throws RodinDBException {
		return hasAttribute(COMMUTATIVE_ATTRIBUTE);
	}

	@Override
	public boolean isCommutative() throws RodinDBException {
		return getAttributeValue(COMMUTATIVE_ATTRIBUTE);
	}

	@Override
	public void setCommutative(boolean isCommutative, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(COMMUTATIVE_ATTRIBUTE, isCommutative, monitor);
	}

	@Override
	public String getFormula() throws RodinDBException {
		return getAttributeValue(FORMULA_ATTRIBUTE);
	}

	@Override
	public boolean hasFormula() throws RodinDBException {
		return hasAttribute(FORMULA_ATTRIBUTE);
	}

	@Override
	public void setFormula(String formula, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(FORMULA_ATTRIBUTE, formula, monitor);
	}

	@Override
	public FormulaType getFormulaType() throws RodinDBException {
		return (getAttributeValue(FORMULA_TYPE_ATTRIBUTE) ? FormulaType.EXPRESSION
				: FormulaType.PREDICATE);
	}

	@Override
	public boolean hasFormulaType() throws RodinDBException {
		return hasAttribute(FORMULA_TYPE_ATTRIBUTE);
	}

	@Override
	public void setFormulaType(FormulaType type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(FORMULA_TYPE_ATTRIBUTE,
				type.equals(FormulaType.EXPRESSION) ? true : false, monitor);
	}

	@Override
	public Notation getNotationType() throws RodinDBException {
		return AstUtilities
				.getNotation(getAttributeValue(NOTATION_TYPE_ATTRIBUTE));
	}

	@Override
	public boolean hasNotationType() throws RodinDBException {
		return hasAttribute(NOTATION_TYPE_ATTRIBUTE);
	}

	@Override
	public void setNotationType(String notation, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(NOTATION_TYPE_ATTRIBUTE, notation, monitor);
	}

	@Override
	public String getType() throws RodinDBException {
		return getAttributeValue(TYPE_ATTRIBUTE);
	}

	@Override
	public boolean hasType() throws RodinDBException {
		return hasAttribute(TYPE_ATTRIBUTE);
	}

	@Override
	public void setType(String type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(TYPE_ATTRIBUTE, type, monitor);
	}

	public boolean hasAutomatic() throws RodinDBException {
		return hasAttribute(AUTOMATIC_ATTRIBUTE);
	}

	public boolean hasComplete() throws RodinDBException {
		return hasAttribute(COMPLETE_ATTRIBUTE);
	}

	public boolean hasInteractive() throws RodinDBException {
		return hasAttribute(INTERACTIVE_ATTRIBUTE);
	}

	public boolean isAutomatic() throws RodinDBException {
		return getAttributeValue(AUTOMATIC_ATTRIBUTE);
	}

	public boolean isComplete() throws RodinDBException {
		return getAttributeValue(COMPLETE_ATTRIBUTE);
	}

	public boolean isInteractive() throws RodinDBException {
		return getAttributeValue(INTERACTIVE_ATTRIBUTE);
	}

	public void setAutomatic(boolean auto, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(AUTOMATIC_ATTRIBUTE, auto, pm);
	}

	public void setComplete(boolean isComplete, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(COMPLETE_ATTRIBUTE, isComplete, pm);

	}

	public void setInteractive(boolean isInteractive, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(INTERACTIVE_ATTRIBUTE, isInteractive, pm);

	}

	public boolean hasDescription() throws RodinDBException {
		return hasAttribute(DESC_ATTRIBUTE);
	}

	public String getDescription() throws RodinDBException {
		return getAttributeValue(DESC_ATTRIBUTE);
	}

	public void setDescription(String newDescription, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(DESC_ATTRIBUTE, newDescription, monitor);
	}

	@Override
	public boolean hasDefinitionalAttribute() throws RodinDBException {
		return hasAttribute(DEFINITIONAL_ATTRIBUTE);
	}

	@Override
	public boolean isDefinitional() throws RodinDBException {
		return getAttributeValue(DEFINITIONAL_ATTRIBUTE);
	}

	@Override
	public void setDefinitional(boolean isDefinitional, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(DEFINITIONAL_ATTRIBUTE, isDefinitional, monitor);
	}

	@Override
	public boolean hasGivenType() throws RodinDBException {
		return hasAttribute(GIVEN_TYPE_ATTRIBUTE);
	}

	@Override
	public String getGivenType() throws RodinDBException {
		return getAttributeValue(GIVEN_TYPE_ATTRIBUTE);
	}

	@Override
	public void setGivenType(String type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(GIVEN_TYPE_ATTRIBUTE, type, monitor);

	}

	@Override
	public Type getSCGivenType(FormulaFactory factory) throws RodinDBException {
		String typeStr = getAttributeValue(GIVEN_TYPE_ATTRIBUTE);
		IParseResult parserResult = factory.parseType(typeStr, V2);
		if (parserResult.getProblems().size() != 0) {
			throw Util.newRodinDBException(
					Messages.database_SCIdentifierTypeParseFailure, this);
		}
		return parserResult.getParsedType();
	}

	@Override
	public void setSCGivenType(Type type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(GIVEN_TYPE_ATTRIBUTE, type.toString(), monitor);

	}

	@Override
	public boolean hasSCFormula() throws RodinDBException {
		return hasFormula();
	}

	@Override
	public Formula<?> getSCFormula(FormulaFactory ff,
			ITypeEnvironment typeEnvironment) throws RodinDBException {
		String form = getFormula();
		Formula<?> formula = parseFormula(form, ff, false);
		if (formula == null) {
			return null;
		}
		ITypeCheckResult result = formula.typeCheck(typeEnvironment);
		if (result.hasProblem()) {
			return null;
		}
		return formula;
	}

	@Override
	public void setSCFormula(Formula<?> formula, IProgressMonitor monitor)
			throws RodinDBException {
		setFormula(formula.toStringWithTypes(), monitor);
	}

	@Override
	public boolean hasReasoningAttribute() throws RodinDBException {
		return hasAttribute(REASONING_TYPE_ATTRIBUTE);
	}

	@Override
	public boolean isSuitableForBackwardReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return isSuitableForAllReasoning()
				|| getReasoningTypeFor(type).equals(ReasoningType.BACKWARD);
	}

	@Override
	public boolean isSuitableForForwardReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return isSuitableForAllReasoning()
				|| getReasoningTypeFor(type).equals(ReasoningType.FORWARD);
	}

	@Override
	public boolean isSuitableForAllReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return getReasoningTypeFor(type).equals(
				ReasoningType.BACKWARD_AND_FORWARD);
	}

	@Override
	public void setReasoningType(ReasoningType type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(REASONING_TYPE_ATTRIBUTE,
				getStringReasoningType(type), monitor);

	}

	@Override
	public boolean hasValidatedAttribute() throws RodinDBException {
		return hasAttribute(VALIDATED_ATTRIBUTE);
	}

	@Override
	public boolean isValidated() throws RodinDBException {
		return getAttributeValue(VALIDATED_ATTRIBUTE);
	}

	@Override
	public void setValidated(boolean isValidated, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(VALIDATED_ATTRIBUTE, isValidated, monitor);
	}

	public boolean hasImportTheory() throws RodinDBException {
		return hasAttribute(IMPORT_THEORY_ATTRIBUTE);
	}

	public ISCTheoryRoot getImportTheory() throws RodinDBException {
		return (ISCTheoryRoot) getAttributeValue(IMPORT_THEORY_ATTRIBUTE);
	}

	public void setImportTheory(ISCTheoryRoot root, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(IMPORT_THEORY_ATTRIBUTE, root, monitor);
	}

	public boolean hasInductiveArgument() throws RodinDBException{
		return hasAttribute(INDUCTIVE_ARGUMENT_ATTRIBUTE);
	}

	public String getInductiveArgument() throws RodinDBException{
		return getAttributeValue(INDUCTIVE_ARGUMENT_ATTRIBUTE);
	}

	public void setInductiveArgument(String inductiveArgument, IProgressMonitor monitor)
			throws RodinDBException{
		setAttributeValue(INDUCTIVE_ARGUMENT_ATTRIBUTE, inductiveArgument, monitor);
	}

	/**
	 * Parses the formula string provided using the given formula factory. The
	 * formula string may contain predicate variables.
	 * 
	 * @param formStr
	 *            the formula string
	 * @param ff
	 *            the formula factory
	 * @param isPattern
	 *            whether the formula is expected to have predicate variables
	 * @return the parsed formula
	 */
	public static Formula<?> parseFormula(String formStr, FormulaFactory ff,
			boolean isPattern) {
		Formula<?> formula = null;
		if (isPattern) {
			IParseResult res = ff.parseExpressionPattern(formStr, V2, null);
			if (!res.hasProblem()) {
				formula = res.getParsedExpression();
			} else {
				res = ff.parsePredicatePattern(formStr, V2, null);
				if (!res.hasProblem()) {
					formula = res.getParsedPredicate();
				}
			}
		} else {
			IParseResult res = ff.parseExpression(formStr, V2, null);
			if (!res.hasProblem()) {
				formula = res.getParsedExpression();
			} else {
				res = ff.parsePredicate(formStr, V2, null);
				if (!res.hasProblem()) {
					formula = res.getParsedPredicate();
				}
			}
		}

		return formula;
	}

	/**
	 * Gets the string representation of the given reasoning type.
	 * 
	 * @param type
	 *            the reasoning type
	 * @return the string representation
	 */
	protected final String getStringReasoningType(ReasoningType type) {
		switch (type) {
		case BACKWARD:
			return BACKWARD_REASONING_TYPE;
		case FORWARD:
			return FORWARD_REASONING_TYPE;
		default:
			return BACKWARD_AND_FORWARD_REASONING_TYPE;
		}
	}

	/**
	 * Returns the reasoning type corresponding to the type string.
	 * 
	 * @param type
	 *            in string format
	 * @return the reasoning type
	 */
	protected final ReasoningType getReasoningTypeFor(String type) {
		if (type.equals(BACKWARD_REASONING_TYPE))
			return ReasoningType.BACKWARD;
		else if (type.equals(FORWARD_REASONING_TYPE))
			return ReasoningType.FORWARD;
		else if (type.equals(BACKWARD_AND_FORWARD_REASONING_TYPE))
			return ReasoningType.BACKWARD_AND_FORWARD;
		throw new IllegalArgumentException("unknown reasoning type " + type);
	}
}
