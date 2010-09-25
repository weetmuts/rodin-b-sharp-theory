/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.ParseProblem;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITypeElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IDatatypeTable.ERROR_CODE;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Utilities used by this plug-in.
 * 
 * @author maamria
 * 
 */
public class CoreUtilities {

	private static final Object[] NO_OBJECT = new Object[0];

	public static final int SC_STARTING_INDEX = 1;

	public static final String BACKWARD_REASONING_TYPE = "backward";
	public static final String FORWARD_REASONING_TYPE = "forward";
	public static final String BACKWARD_AND_FORWARD_REASONING_TYPE = "both";

	// ///////////////////////
	// / GENERAL
	// ///////////////////////

	/**
	 * Creates a sorted list of the given element type.
	 * 
	 * @param <E>
	 *            the type of the elements
	 * @param collection
	 *            the original collection
	 * @return the sorted list
	 */
	public static <E extends Comparable<E>> List<E> getSortedList(
			Collection<E> collection) {
		List<E> list = new ArrayList<E>();
		for (E item : collection) {
			list.add(item);
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * <p>
	 * A utility to check if an object is present in an array of objects. This
	 * method uses <code>Object.equals(Object)</code>
	 * </p>
	 * 
	 * @param objs
	 *            the container array of objects
	 * @param o
	 *            the object to check
	 * @return whether <code>o</code> is in <code>objs</code>
	 */
	public static boolean contains(Object[] objs, Object o) {
		for (Object obj : objs) {
			if (obj.equals(o))
				return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Utility to check whether <code>objs</code> contains all of
	 * <code>os</code>.
	 * </p>
	 * 
	 * @param objs
	 *            the container array of objects
	 * @param os
	 *            the array of objects
	 * @return whether <code>objs</code> contains all of <code>os</code>.
	 */
	public static boolean subset(Object[] objs, Object[] os) {
		for (Object o : os) {
			if (!contains(objs, o))
				return false;
		}
		return true;
	}

	/**
	 * Returns a singleton set containing the given element.
	 * 
	 * @param <E>
	 *            the type of the element
	 * @param element
	 * @return a singleton set
	 */
	public static <E> Set<E> singletonSet(E element) {
		Set<E> set = new HashSet<E>();
		set.add(element);
		return set;
	}

	// ///////////////////////
	// / AST
	// ///////////////////////

	/**
	 * Returns the string type expression with the given name and type
	 * parameters e.g., List(A).
	 * 
	 * @param identifierString
	 *            the name of the type
	 * @param argsList
	 *            the list of type arguments
	 * @param the
	 *            formula factory tha knows about this datatype
	 * @return the type expression
	 */
	public static Type createTypeExpression(String identifierString,
			List<String> argsList, FormulaFactory ff) {
		String result = identifierString;
		if (argsList.size() != 0) {
			result += "(";
			for (int i = 0; i < argsList.size(); i++) {
				result += argsList.get(i);
				if (i < argsList.size() - 1) {
					result += ",";
				}
			}
			result += ")";

		}
		// TODO this should be guaranteed to parse
		return ff.parseType(result, LanguageVersion.V2).getParsedType();

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
	 * Returns a predicate resulting from conjuncting the given predicates.
	 * 
	 * @param preds
	 *            the list of predicates
	 * @param ff
	 *            the formula factor
	 * @return the predicate
	 */
	public static Predicate conjunctPredicates(List<Predicate> preds,
			FormulaFactory ff) {
		while (preds.contains(MathExtensionsUtilities.BTRUE)) {
			preds.remove(MathExtensionsUtilities.BTRUE);
		}
		if (preds.size() == 0) {
			return MathExtensionsUtilities.BTRUE;
		}
		if (preds.size() == 1) {
			return preds.get(0);
		}
		return ff.makeAssociativePredicate(Formula.LAND, preds, null);
	}

	/**
	 * Returns a predicate resulting from conjuncting the given predicates.
	 * 
	 * @param preds
	 *            the array of predicates
	 * @param ff
	 *            the formula factor
	 * @return the predicate
	 */
	public static Predicate conjunctPredicates(Predicate[] preds,
			FormulaFactory ff) {
		List<Predicate> pList = new ArrayList<Predicate>();
		for (Predicate p : preds) {
			if (!p.equals(MathExtensionsUtilities.BTRUE)) {
				pList.add(p);
			}
		}
		if (pList.size() == 0) {
			return MathExtensionsUtilities.BTRUE;
		}
		if (pList.size() == 1) {
			return pList.get(0);
		}
		return ff.makeAssociativePredicate(Formula.LAND, preds, null);
	}

	// ///////////////////////////
	// // Theory CORE
	// ///////////////////////////

	/**
	 * Checks whether the given predicate refers only to the given types in
	 * <code>typeEnvironment</code>.
	 * 
	 * @param element
	 *            the predicate element
	 * @param pred
	 *            the predicate
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display
	 * @return whether given predicates does not reference illegal identifiers
	 * @throws CoreException
	 */
	public static boolean checkAgainstTypeParameters(IPredicateElement element,
			Predicate pred, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws CoreException {
		return checkAgainstTypeParameters(element, pred,
				EventBAttributes.PREDICATE_ATTRIBUTE, typeEnvironment, display);
	}

	/**
	 * Checks whether the given formula refers only to the given types in
	 * <code>typeEnvironment</code>.
	 * 
	 * @param element
	 *            the formula element
	 * @param formula
	 *            the formula
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display
	 * @return whether given formula does not reference illegal identifiers
	 * @throws CoreException
	 */
	public static boolean checkAgainstTypeParameters(IFormulaElement element,
			Formula<?> formula, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws CoreException {
		return checkAgainstTypeParameters(element, formula,
				TheoryAttributes.FORMULA_ATTRIBUTE, typeEnvironment, display);
	}

	protected static boolean checkAgainstTypeParameters(
			IInternalElement element, Formula<?> formula,
			IAttributeType attrType, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws RodinDBException {
		Set<GivenType> types = formula.getGivenTypes();
		boolean ok = true;
		for (GivenType type : types) {
			if (!CoreUtilities.isGivenSet(typeEnvironment, type.getName())) {
				display.createProblemMarker(element, attrType,
						TheoryGraphProblem.NonTypeParOccurError, type.getName());
				ok = false;
			}
		}
		return ok;
	}

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
	 * @param display
	 *            a marker display for problem markers
	 * @param primeAllowed
	 *            <code>true</code> if primed names are allowed,
	 *            <code>false</code> otherwise
	 * @return a free identifier with the given name, or <code>null</code> if
	 *         there was a problem making the identifier
	 * @throws RodinDBException
	 *             if there is a problem accessing the Rodin database
	 */
	public static FreeIdentifier parseIdentifier(String name,
			IInternalElement element, IAttributeType.String attrType,
			FormulaFactory factory, IMarkerDisplay display)
			throws RodinDBException {

		IParseResult pResult = factory.parseExpression(name, V2, element);
		Expression expr = pResult.getParsedExpression();
		if (pResult.hasProblem() || !(expr instanceof FreeIdentifier)) {
			display.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		FreeIdentifier identifier = (FreeIdentifier) expr;
		if (identifier.isPrimed()) {
			display.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		if (!name.equals(identifier.getName())) {
			display.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierSpacesError, name);
			return null;
		}
		return identifier;
	}

	/**
	 * Parses the typing string that is an attribute of the given internal
	 * element.
	 * 
	 * @param typingElmnt
	 *            the rodin element
	 * @param factory
	 *            the formula factory
	 * @param display
	 *            the marker display for error reporting
	 * @return the type
	 * @throws CoreException
	 */
	public static Type parseTypeExpression(ITypeElement typingElmnt,
			FormulaFactory factory, IMarkerDisplay display)
			throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.TYPE_ATTRIBUTE;
		String expString = typingElmnt.getType();

		IParseResult parseResult = factory.parseType(expString, V2);

		if (issueASTProblemMarkers(typingElmnt, attributeType, parseResult,
				display)) {
			return null;
		}
		Type type = parseResult.getParsedType();
		return type;
	}

	/**
	 * Parses and type checks the formula occurring as an attribute to the given
	 * element.
	 * 
	 * @param element
	 *            the rodin element
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
			FormulaFactory ff, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		IParseResult result = ff.parsePredicate(form, V2, null);
		if (result.hasProblem()) {
			result = ff.parseExpression(form, V2, null);
			if (issueASTProblemMarkers(element, attributeType, result, display)) {
				return null;
			} else {
				formula = result.getParsedExpression();
			}
		} else {
			formula = result.getParsedPredicate();
		}

		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				display.createProblemMarker(element, attributeType,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if (issueASTProblemMarkers(element, attributeType, tcResult, display)) {
			return null;
		}
		return formula;
	}

	/**
	 * Parses and type checks the formula occurring as an attribute to the given
	 * element. The formula may contain predicate variables.
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factor
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed formula
	 * @throws CoreException
	 */
	public static Formula<?> parseAndCheckPatternFormula(
			IFormulaElement element, FormulaFactory ff,
			ITypeEnvironment typeEnvironment, IMarkerDisplay display)
			throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		IParseResult result = ff.parsePredicatePattern(form, V2, null);
		if (result.hasProblem()) {
			result = ff.parseExpressionPattern(form, V2, null);
			if (issueASTProblemMarkers(element, attributeType, result, display)) {
				return null;
			} else {
				formula = result.getParsedExpression();
			}
		} else {
			formula = result.getParsedPredicate();
		}

		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				display.createProblemMarker(element, attributeType,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if (issueASTProblemMarkers(element, attributeType, tcResult, display)) {
			return null;
		}
		return formula;
	}

	/**
	 * Parses and type checks the predicate occuring as an attribute to the
	 * given element
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed predicate
	 * @throws CoreException
	 */
	public static Predicate parseAndCheckPredicate(IPredicateElement element,
			FormulaFactory ff, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = EventBAttributes.PREDICATE_ATTRIBUTE;
		String pred = element.getPredicateString();
		IParseResult result = ff.parsePredicate(pred, V2, null);
		if (issueASTProblemMarkers(element, attributeType, result, display)) {
			return null;
		}
		Predicate predicate = result.getParsedPredicate();
		FreeIdentifier[] idents = predicate.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				display.createProblemMarker(element, attributeType,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = predicate.typeCheck(typeEnvironment);
		if (issueASTProblemMarkers(element, attributeType, tcResult, display)) {
			return null;
		}
		return predicate;
	}

	/**
	 * Creates a statically checked identifier element of the type
	 * <code>T</code>.
	 * 
	 * @param <T>
	 *            the type of the element
	 * @param type
	 *            intenal element type
	 * @param source
	 *            the source element
	 * @param parent
	 *            the parent rodin element
	 * @param monitor
	 *            the progress monitor
	 * @return the statically checked identifier element
	 * @throws CoreException
	 */
	public static <T extends IIdentifierElement> T createSCIdentifierElement(
			IInternalElementType<T> type, IIdentifierElement source,
			IInternalElement parent, IProgressMonitor monitor)
			throws CoreException {
		T scElement = parent.getInternalElement(type,
				source.getIdentifierString());
		scElement.create(null, monitor);
		return scElement;
	}

	/**
	 * Returns whether <code>result</code> has any problems. Issues AST related
	 * problems.
	 * 
	 * @param element
	 *            the rodin element
	 * @param attributeType
	 *            the attribute
	 * @param result
	 *            the parse result
	 * @param display
	 *            the marker display
	 * @return whether an error is encountered
	 * @throws RodinDBException
	 */
	public static boolean issueASTProblemMarkers(IInternalElement element,
			IAttributeType.String attributeType, IResult result,
			IMarkerDisplay display) throws RodinDBException {

		boolean errorIssued = false;
		for (ASTProblem parserProblem : result.getProblems()) {
			final SourceLocation location = parserProblem.getSourceLocation();
			final ProblemKind problemKind = parserProblem.getMessage();
			final Object[] args = parserProblem.getArgs();

			final IRodinProblem problem;
			final Object[] objects; // parameters for the marker

			switch (problemKind) {

			case FreeIdentifierHasBoundOccurences:
				problem = ParseProblem.FreeIdentifierHasBoundOccurencesWarning;
				objects = new Object[] { args[0] };
				break;

			case BoundIdentifierHasFreeOccurences:
				// ignore
				// this is just the symmetric message to
				// FreeIdentifierHasBoundOccurences
				continue;

			case BoundIdentifierIsAlreadyBound:
				problem = ParseProblem.BoundIdentifierIsAlreadyBoundWarning;
				objects = new Object[] { args[0] };
				break;

			case Circularity:
				problem = ParseProblem.CircularityError;
				objects = NO_OBJECT;
				break;

			case InvalidTypeExpression:
				// internal error
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
				break;

			case LexerError:
				problem = ParseProblem.LexerError;
				objects = new Object[] { args[0] };
				break;

			case TypeCheckFailure:
				problem = ParseProblem.TypeCheckError;
				objects = NO_OBJECT;
				break;

			case TypesDoNotMatch:
				problem = ParseProblem.TypesDoNotMatchError;
				objects = new Object[] { args[0], args[1] };
				break;

			case TypeUnknown:
				problem = ParseProblem.TypeUnknownError;
				objects = NO_OBJECT;
				break;

			case MinusAppliedToSet:
				problem = ParseProblem.MinusAppliedToSetError;
				objects = NO_OBJECT;
				break;

			case MulAppliedToSet:
				problem = ParseProblem.MulAppliedToSetError;
				objects = NO_OBJECT;
				break;

			// syntax errors
			case BECMOAppliesToOneIdent:
			case DuplicateIdentifierInPattern:
			case ExtensionPreconditionError:
			case FreeIdentifierExpected:
			case IncompatibleIdentExprNumbers:
			case IncompatibleOperators:
			case IntegerLiteralExpected:
			case InvalidAssignmentToImage:
			case InvalidGenericType:
			case MisplacedLedOperator:
			case MisplacedNudOperator:
			case NotUpgradableError:
			case PredicateVariableNotAllowed:
			case PrematureEOF:
			case UnexpectedOftype:
			case UnexpectedSubFormulaKind:
			case UnexpectedSymbol:
			case UnknownOperator:
			case UnmatchedTokens:
			case VariousPossibleErrors:

				problem = ParseProblem.SyntaxError;

				objects = new Object[] { parserProblem.toString() };
				break;
			default:

				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;

				break;
			}

			if (location == null) {
				display.createProblemMarker(element, attributeType, problem,
						objects);
			} else {
				display.createProblemMarker(element, attributeType,
						location.getStart(), location.getEnd(), problem,
						objects);
			}

			errorIssued |= problem.getSeverity() == IMarker.SEVERITY_ERROR;
		}

		return errorIssued;
	}

	/**
	 * Returns appropriate rodin problem for <code>code</code>. The error codes
	 * correspond to problems of name clashes of datatype related identifiers.
	 * 
	 * @param code
	 *            the error code
	 * @return the rodin problem
	 */
	public static IRodinProblem getAppropriateProblemForCode(ERROR_CODE code) {
		switch (code) {
		case NAME_IS_A_CONSTRUCTOR:
			return TheoryGraphProblem.IdenIsAConsNameError;
		case NAME_IS_A_DATATYPE:
			return TheoryGraphProblem.IdenIsADatatypeNameError;
		case NAME_IS_A_DESTRUCTOR:
			return TheoryGraphProblem.IdenIsADesNameError;
		}
		return null;
	}

	/**
	 * Returns the argument of an operator. This method assumes that all given
	 * sets are theory type parameters, and all other names must be operator
	 * arguments.
	 * 
	 * @param typeEnvironment
	 *            the type environment
	 * @return list of operator arguments
	 */
	public static List<String> getOperatorArguments(
			ITypeEnvironment typeEnvironment) {
		Set<String> all = typeEnvironment.clone().getNames();
		all.removeAll(getGivenSetsNames(typeEnvironment));

		return new ArrayList<String>(all);
	}

	public static List<FreeIdentifier> getMetavariables(
			ITypeEnvironment typeEnvironment) {
		FormulaFactory factory = typeEnvironment.getFormulaFactory();
		Set<String> all = typeEnvironment.clone().getNames();
		all.removeAll(getGivenSetsNames(typeEnvironment));
		List<FreeIdentifier> vars = new ArrayList<FreeIdentifier>();
		for (String name : all) {
			vars.add(factory.makeFreeIdentifier(name, null,
					typeEnvironment.getType(name)));
		}
		return vars;
	}

	/**
	 * Returns the given types in <code>typeEnvironment</code>.
	 * 
	 * @param typeEnvironment
	 *            the type environment
	 * @return all given types
	 */
	public static List<String> getGivenSetsNames(
			ITypeEnvironment typeEnvironment) {
		List<String> result = new ArrayList<String>();
		for (String name : typeEnvironment.getNames()) {
			if (isGivenSet(typeEnvironment, name)) {
				result.add(name);
			}
		}
		return result;
	}

	/**
	 * Checks whether the name <code>name</code> is a given set in the given
	 * type environment.
	 * 
	 * @param typeEnvironment
	 *            the type environment
	 * @param name
	 *            the name
	 * @return whether <code>name</code> is a given set
	 */
	public static boolean isGivenSet(ITypeEnvironment typeEnvironment,
			String name) {
		Type type = typeEnvironment.getType(name);
		if (type == null) {
			return false;
		}
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(name);
		}
		return false;
	}

	/**
	 * Returns a string representation of the given list of strings e.g.,
	 * ["a","b"] is represented as "(a,b)".
	 * 
	 * @param list
	 *            the list of strings
	 * @return the representing string
	 */
	public static String toString(List<String> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i);
			if (i < list.size() - 1) {
				result += ", ";
			}
		}
		return result;
	}

	/**
	 * Returns the given types occurring in <code>type</code>.
	 * 
	 * @param type
	 *            the type
	 * @param factory
	 *            the formula factory
	 * @return the given types
	 */
	public static GivenType[] getTypesOccurringIn(Type type,
			FormulaFactory factory) {
		List<GivenType> types = new ArrayList<GivenType>();
		FreeIdentifier[] idents = type.toExpression(factory)
				.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			types.add(factory.makeGivenType(ident.getName()));
		}
		return types.toArray(new GivenType[types.size()]);
	}

	/**
	 * Returns the information message appropriate for the given reasoning type.
	 * 
	 * @param type
	 *            the reasoning type
	 * @return the rodin problem
	 */
	public static final IRodinProblem getInformationMessageFor(
			ReasoningType type) {
		switch (type) {
		case BACKWARD:
			return TheoryGraphProblem.InferenceRuleBackward;
		case FORWARD:
			return TheoryGraphProblem.InferenceRuleForward;
		case BACKWARD_AND_FORWARD:
			return TheoryGraphProblem.InferenceRuleBoth;
		}
		return null;
	}

	/**
	 * Returns whether the formula type is an expression type.
	 * 
	 * @param type
	 *            the formula type
	 * @return whether the type is an expression
	 */
	public static final boolean isExpressionOperator(FormulaType type) {
		return type.equals(FormulaType.EXPRESSION);
	}

	/**
	 * Gets the string representation of the given reasoning type.
	 * 
	 * @param type
	 *            the reasoning type
	 * @return the string representation
	 */
	public static final String getStringReasoningType(ReasoningType type) {
		switch (type) {
		case BACKWARD:
			return CoreUtilities.BACKWARD_REASONING_TYPE;
		case FORWARD:
			return CoreUtilities.FORWARD_REASONING_TYPE;
		default:
			return CoreUtilities.BACKWARD_AND_FORWARD_REASONING_TYPE;
		}
	}

	/**
	 * Returns the reasoning type corresponding to the type string.
	 * 
	 * @param type
	 *            in string format
	 * @return the reasoning type
	 */
	public static final ReasoningType getReasoningTypeFor(String type) {
		if (type.equals(CoreUtilities.BACKWARD_REASONING_TYPE))
			return ReasoningType.BACKWARD;
		else if (type.equals(CoreUtilities.FORWARD_REASONING_TYPE))
			return ReasoningType.FORWARD;
		else if (type.equals(CoreUtilities.BACKWARD_AND_FORWARD_REASONING_TYPE))
			return ReasoningType.BACKWARD_AND_FORWARD;
		throw new IllegalArgumentException("unknown reasoning type " + type);
	}

	/**
	 * Returns whether the given root was generated from a theory file, that is
	 * indeed true if the returned theory is not <code>null</code>.
	 * 
	 * @param root
	 *            the Event-B root
	 * @return the corresponding SC theory or <code>null</code>
	 */
	public static ISCTheoryRoot correspondsToSCTheory(IEventBRoot root)
			throws CoreException {
		if (root instanceof ISCTheoryRoot) {
			return (ISCTheoryRoot) root;
		}
		String name = root.getElementName();
		IRodinProject project = root.getRodinProject();
		IRodinFile[] files = project.getRodinFiles();
		for (IRodinFile file : files) {
			if (file.getRoot() instanceof ISCTheoryRoot) {
				ISCTheoryRoot thisRoot = (ISCTheoryRoot) file.getRoot();
				if (thisRoot.getElementName().equals(name)) {
					return (ISCTheoryRoot) file.getRoot();
				}
			}
		}
		return null;
	}
	
	public static List<IDeployedTheoryRoot> normaliseDeployedTheories(List<IDeployedTheoryRoot> rawList)
	throws CoreException{
		List<IDeployedTheoryRoot> normalised = new ArrayList<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot root: rawList){
			List<IDeployedTheoryRoot> rawClone = new ArrayList<IDeployedTheoryRoot>(rawList);
			rawClone.remove(root);
			boolean toInclude = true;
			for (IDeployedTheoryRoot rawRoot : rawClone){
				if (TheoryCoreFacade.doesTheoryUseTheory(rawRoot, root)){
					toInclude = false;
				}
			}
			if(toInclude){
				normalised.add(root);
			}
		}
		return normalised;
	}
	
	public static List<ISCTheoryRoot> normaliseSCTheories(List<ISCTheoryRoot> rawList)
	throws CoreException{
		List<ISCTheoryRoot> normalised = new ArrayList<ISCTheoryRoot>();
		for (ISCTheoryRoot root: rawList){
			List<ISCTheoryRoot> rawClone = new ArrayList<ISCTheoryRoot>(rawList);
			rawClone.remove(root);
			boolean toInclude = true;
			for (ISCTheoryRoot rawRoot : rawClone){
				if (TheoryCoreFacade.doesSCTheoryImportSCTheory(rawRoot, root)){
					toInclude = false;
				}
			}
			if(toInclude){
				normalised.add(root);
			}
		}
		return normalised;
	}
}
