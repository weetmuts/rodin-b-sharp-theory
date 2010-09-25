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
import static org.eventb.theory.core.TheoryAttributes.INTERACTIVE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.NOTATION_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.REASONING_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.TOOL_TIP_ATTRIBUTE;
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
import org.eventb.theory.core.IAssociativeElement;
import org.eventb.theory.core.IAutomaticElement;
import org.eventb.theory.core.ICommutativeElement;
import org.eventb.theory.core.ICompleteElement;
import org.eventb.theory.core.IDefinitionalElement;
import org.eventb.theory.core.IDescriptionElement;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.IFormulaTypeElement;
import org.eventb.theory.core.IGivenTypeElement;
import org.eventb.theory.core.IInteractiveElement;
import org.eventb.theory.core.INotationTypeElement;
import org.eventb.theory.core.IOperatorGroupElement;
import org.eventb.theory.core.IReasoningTypeElement;
import org.eventb.theory.core.ISCFormulaElement;
import org.eventb.theory.core.ISCGivenTypeElement;
import org.eventb.theory.core.ISyntaxSymbolElement;
import org.eventb.theory.core.IToolTipElement;
import org.eventb.theory.core.ITypeElement;
import org.eventb.theory.core.IValidatedElement;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.core.util.CoreUtilities;
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
	IFormulaTypeElement, INotationTypeElement, ISyntaxSymbolElement,
	ITypeElement, IAutomaticElement, ICompleteElement, IDescriptionElement,
	IInteractiveElement, IToolTipElement, IDefinitionalElement, IGivenTypeElement,
	ISCGivenTypeElement, ISCFormulaElement, IReasoningTypeElement, IValidatedElement,
	IOperatorGroupElement
{

	public TheoryElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasOperatorGroup() throws RodinDBException {
		// TODO Auto-generated method stub
		return hasAttribute(GROUP_ID_ATTRIBUTE);
	}

	@Override
	public String getOperatorGroup() throws RodinDBException {
		// TODO Auto-generated method stub
		return getAttributeValue(GROUP_ID_ATTRIBUTE);
	}

	@Override
	public void setOperatorGroup(String newGroup, IProgressMonitor monitor) 
	throws RodinDBException{
		// TODO Auto-generated method stub
		setAttributeValue(GROUP_ID_ATTRIBUTE, newGroup, monitor);
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
		return (getAttributeValue(FORMULA_TYPE_ATTRIBUTE)? FormulaType.EXPRESSION : FormulaType.PREDICATE);
	}

	@Override
	public boolean hasFormulaType() throws RodinDBException {
		return hasAttribute(FORMULA_TYPE_ATTRIBUTE);
	}

	@Override
	public void setFormulaType(FormulaType type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(FORMULA_TYPE_ATTRIBUTE, type.equals(FormulaType.EXPRESSION) ? true : false, monitor);
	}

	@Override
	public Notation getNotationType() throws RodinDBException {
		return TheoryCoreFacade.getNotation(getAttributeValue(NOTATION_TYPE_ATTRIBUTE));
	}

	
	@Override
	public boolean hasNotationType() throws RodinDBException {
		return hasAttribute(NOTATION_TYPE_ATTRIBUTE);
	}

	
	@Override
	public void setNotationType(String notation, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(NOTATION_TYPE_ATTRIBUTE, 
				notation, monitor);
	}

	
	@Override
	public String getSyntaxSymbol() throws RodinDBException {
		// remove trailing spaces
		return getAttributeValue(SYNTAX_SYMBOL_ATTRIBUTE).trim();
	}

	
	@Override
	public boolean hasSyntaxSymbol() throws RodinDBException {
		return hasAttribute(SYNTAX_SYMBOL_ATTRIBUTE);
	}

	
	@Override
	public void setSyntaxSymbol(String newSymbol, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(SYNTAX_SYMBOL_ATTRIBUTE, newSymbol.trim(), monitor);
		
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
	public void setType(String type, IProgressMonitor monitor) throws RodinDBException {
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
	
	public boolean hasToolTip() throws RodinDBException{
		return hasAttribute(TOOL_TIP_ATTRIBUTE);
	}
	
	public String getToolTip() throws RodinDBException{
		return getAttributeValue(TOOL_TIP_ATTRIBUTE);
	}
	
	public void setToolTip(String newToolTip, IProgressMonitor monitor) throws RodinDBException{
		setAttributeValue(TOOL_TIP_ATTRIBUTE,newToolTip, monitor);
	}

	public boolean hasDescription() throws RodinDBException{
		return hasAttribute(DESC_ATTRIBUTE);
	}
	
	public String getDescription() throws RodinDBException{
		return getAttributeValue(DESC_ATTRIBUTE);
	}
	
	public void setDescription(String newDescription, IProgressMonitor monitor) throws RodinDBException{
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
					Messages.database_SCIdentifierTypeParseFailure,
					this
			);
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
	public Formula<?> getSCFormula(FormulaFactory ff, ITypeEnvironment typeEnvironment) throws RodinDBException {
		String form = getFormula();
		Formula<?> formula = CoreUtilities.parseFormula(form, ff, false);
		ITypeCheckResult result = formula.typeCheck(typeEnvironment);
		if(result.hasProblem()){
			return null;
		}
		return formula;
	}

	@Override
	public void setSCFormula(Formula<?> formula, IProgressMonitor monitor)
	throws RodinDBException{
		setFormula(formula.toString(), monitor);
	}


	@Override
	public boolean hasReasoningAttribute() throws RodinDBException {
		return hasAttribute(REASONING_TYPE_ATTRIBUTE);
	}

	@Override
	public boolean isSuitableForBackwardReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return CoreUtilities.getReasoningTypeFor(type).equals(ReasoningType.BACKWARD);
	}

	@Override
	public boolean isSuitableForForwardReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return CoreUtilities.getReasoningTypeFor(type).equals(ReasoningType.FORWARD);
	}

	@Override
	public boolean isSuitableForAllReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return CoreUtilities.getReasoningTypeFor(type).equals(ReasoningType.BACKWARD_AND_FORWARD);
	}

	@Override
	public void setReasoningType(ReasoningType type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(REASONING_TYPE_ATTRIBUTE, CoreUtilities.getStringReasoningType(type), monitor);
		
	}
	
	@Override
	public boolean hasValidatedAttribute() throws RodinDBException{
		return hasAttribute(VALIDATED_ATTRIBUTE);
	}
	
	@Override
	public boolean isValidated() throws RodinDBException{
		return getAttributeValue(VALIDATED_ATTRIBUTE);
	}
	
	@Override
	public void setValidated(boolean isValidated, IProgressMonitor monitor) throws RodinDBException{
		setAttributeValue(VALIDATED_ATTRIBUTE, isValidated, monitor);
	}
	
}
