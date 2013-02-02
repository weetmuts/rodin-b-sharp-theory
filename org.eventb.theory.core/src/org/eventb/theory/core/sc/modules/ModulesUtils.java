/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * This class contains static methods that can be used by theory static checker
 * modules.
 * 
 * @author maamria
 * 
 */
public class ModulesUtils {

	public static final int IDENT_SYMTAB_SIZE = 2047;

	public static final int LABEL_SYMTAB_SIZE = 2047;

	public static final String AXM_NAME_PREFIX = "Axm";
	
	public static final String THM_NAME_PREFIX = "Thm";

	public static final String PRB_NAME_PREFIX = "PRB";
	
	public static final String ADB_NAME_PREFIX = "ADB";

	/**
	 * Makes a free identifier from the given name.
	 * <p>
	 * Returns <code>null</code> if one of the following happens:
	 * <li>the given name cannot be parsed as an expression</li>
	 * <li>the given name is not a valid free identifier name</li>
	 * <li>the given name is primed and prime is not allowed</li>
	 * <li>the given name contains leading or trailing spaces</li>
	 * For the first problem encountered, if any, a problem marker is added and
	 * <code>null</code> is returned immediately.
	 * </p>
	 * 
	 * @param name
	 *            a name to parse
	 * @param element
	 *            the element associated the the name
	 * @param attrType
	 *            the attribute type of the element where the name is located
	 * @param factory
	 *            a formula factory to parse the name
	 * @param markerDisplay
	 *            a marker display for problem markers
	 * @return a free identifier with the given name, or <code>null</code> if
	 *         there was a problem making the identifier
	 * @throws RodinDBException
	 *             if there is a problem accessing the Rodin database
	 */
	public static FreeIdentifier parseIdentifier(String name,
			IInternalElement element, IAttributeType.String attrType,
			FormulaFactory factory, IMarkerDisplay markerDisplay)
			throws RodinDBException {

		IParseResult pResult = factory.parseExpression(name, V2, element);
		Expression expr = pResult.getParsedExpression();
		if (pResult.hasProblem() || !(expr instanceof FreeIdentifier)) {
			markerDisplay.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		FreeIdentifier identifier = (FreeIdentifier) expr;
		if (identifier.isPrimed()) {
			markerDisplay.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		if (!name.equals(identifier.getName())) {
			markerDisplay.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierSpacesError, name);
			return null;
		}
		return identifier;
	}

	/**
	 * Creates a statically checked identifier element of the type
	 * <code>T</code>.
	 * 
	 * @param <T>
	 *            the type of the element
	 * @param type
	 *            internal element type
	 * @param source
	 *            the source element
	 * @param parent
	 *            the parent rodin element
	 * @param monitor
	 *            the progress monitor
	 * @return the statically checked identifier element
	 * @throws CoreException
	 */
	public static <T extends ISCIdentifierElement> T createSCIdentifierElement(
			IInternalElementType<T> type, IIdentifierElement source,
			IInternalElement parent, IProgressMonitor monitor)
			throws CoreException {
		T scElement = parent.getInternalElement(type,
				source.getIdentifierString());
		scElement.create(null, monitor);
		return scElement;
	}

	/**
	 * Returns appropriate rodin problem for <code>code</code>. The error codes
	 * correspond to problems of name clashes of datatype related identifiers.
	 * 
	 * @param errorCode
	 *            the error code
	 * @return the rodin problem
	 */
	public static IRodinProblem getAppropriateProblemForCode(String errorCode) {
		if (errorCode.equals(Messages.scuser_IdenIsAConsNameError)) {
			return TheoryGraphProblem.IdenIsAConsNameError;
		}
		if (errorCode.equals(Messages.scuser_IdenIsADatatypeNameError)) {
			return TheoryGraphProblem.IdenIsADatatypeNameError;
		}
		if (errorCode.equals(Messages.scuser_IdenIsADesNameError)) {
			return TheoryGraphProblem.IdenIsADesNameError;
		}
		return null;
	}

	/**
	 * Parses and type checks the non-pattern formula occurring as an attribute
	 * to the given formula element.
	 * 
	 * @param element
	 *            the rodin element
	 * @param isExpression
	 *            whether to parse an expression or a predicate
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed formula
	 * @throws CoreException
	 */
	public static Formula<?> parseAndCheckFormula(IFormulaElement element,
			boolean isExpression, boolean issueErrors, FormulaFactory ff,
			ITypeEnvironment typeEnvironment, IMarkerDisplay markerDisplay)
			throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		if (isExpression) {
			IParseResult result = ff.parseExpression(form, V2, null);
			if (issueErrors) {
				if (CoreUtilities.issueASTProblemMarkers(element,
						attributeType, result, markerDisplay)) {
					return null;
				}
			} else {
				if (result.hasProblem()) {
					return null;
				}
			}
			formula = result.getParsedExpression();

		} else {
			IParseResult result = ff.parsePredicate(form, V2, null);
			if (issueErrors) {
				if (CoreUtilities.issueASTProblemMarkers(element,
						attributeType, result, markerDisplay)) {
					return null;
				}
			} else {
				if (result.hasProblem()) {
					return null;
				}
			}
			formula = result.getParsedPredicate();
		}
		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				markerDisplay.createProblemMarker(element, attributeType,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if (issueErrors) {
			if (CoreUtilities.issueASTProblemMarkers(element, attributeType,
					tcResult, markerDisplay)) {
				return null;
			}
		} else {
			if (tcResult.hasProblem()) {
				return null;
			}
		}
		return formula;
	}
	
	/**
	 * Parses the formula stored in the given element.
	 * @param element the formula element
	 * @param ff the formula factory
	 * @param typeEnvironment 
	 * @param markerDisplay the marker display
	 * @return the parsed formula, or <code>null</code>
	 * @throws CoreException
	 */
	public static Formula<?> parseFormula(IFormulaElement element,
			FormulaFactory ff, IMarkerDisplay markerDisplay)throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		IParseResult result = ff.parseExpression(form, V2, null);
		if(result.hasProblem()){
			result = ff.parsePredicate(form, V2, null);
			if (CoreUtilities.issueASTProblemMarkers(element,
					attributeType, result, markerDisplay)){
				return null;
			}
			return result.getParsedPredicate();
		}
		return result.getParsedExpression();
	}
	
	/**
	 * Type checks the formula.
	 * @param element the formula element
	 * @param formula the actual formula
	 * @param typeEnvironment the type environment
	 * @param markerDisplay the marker display
	 * @return the type checked formula, or <code>null</code>
	 * @throws CoreException
	 */
	public static Formula<?> checkFormula(IFormulaElement element, Formula<?> formula,
			ITypeEnvironment typeEnvironment,
			IMarkerDisplay markerDisplay)throws CoreException {
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if(CoreUtilities.issueASTProblemMarkers(element,
					TheoryAttributes.FORMULA_ATTRIBUTE, tcResult, markerDisplay)){
			return null;
		}
		return formula;
	}
	
	/**
	 * Attempts to parse and check the formula string.
	 * @param form the formula string
	 * @param ff the formula factory
	 * @param env the type environment
	 * @return the parsed formula or <code>null</code> if the passed string cannot be parsed, or if parsed cannot be typechecked 
	 */
	public static Formula<?> parseAndTypeCheckFormula(String form, FormulaFactory ff, ITypeEnvironment env){
		Formula<?> formula = null;
		IParseResult result = ff.parseExpression(form, V2, null);
		if(result.hasProblem()){
			result = ff.parsePredicate(form, V2, null);
			if (result.hasProblem()){
				return null;
			}
			formula = result.getParsedPredicate();
		}
		else {
			formula = result.getParsedExpression();
		}
		ITypeCheckResult typeCheck = formula.typeCheck(env);
		if (typeCheck.hasProblem()){
			return null;
		}
		return formula;
	}
}
