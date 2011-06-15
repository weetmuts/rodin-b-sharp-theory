/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IExpressionElement;
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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.ParseProblem;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITypeElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Utilities used by this plug-in.
 * <p>
 * TODO double check <code>getDeployedSyntaxSymbolsOfOtherHierarchies(ISCTheoryRoot)</code>
 * 
 * @author maamria
 * 
 */
public class CoreUtilities {

	private static final Object[] NO_OBJECT = new Object[0];

	/**
	 * <p>
	 * Facility to log the given exception alongside the given message.
	 * </p>
	 * 
	 * @param exc
	 *            the exception
	 * @param message
	 *            the error message
	 */
	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, TheoryPlugin.PLUGIN_ID, IStatus.ERROR, message, exc);
		TheoryPlugin.getDefault().getLog().log(status);
	}

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
	public static boolean checkAgainstTypeParameters(IPredicateElement element, Predicate pred, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		return checkAgainstTypeParameters(element, pred, EventBAttributes.PREDICATE_ATTRIBUTE, typeEnvironment, display);
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
	public static boolean checkAgainstTypeParameters(IFormulaElement element, Formula<?> formula, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		return checkAgainstTypeParameters(element, formula, TheoryAttributes.FORMULA_ATTRIBUTE, typeEnvironment, display);
	}

	/**
	 * Checks whether the given formula refers only to the given types in
	 * <code>typeEnvironment</code>.
	 * 
	 * @param element
	 *            the internal element
	 * @param formula
	 *            the formula
	 * @param attrType
	 *            the attribute type
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return whether formula refers only to type parameters
	 * @throws RodinDBException
	 */
	protected static boolean checkAgainstTypeParameters(IInternalElement element, Formula<?> formula, IAttributeType attrType, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws RodinDBException {
		Set<GivenType> types = formula.getGivenTypes();
		boolean ok = true;
		for (GivenType type : types) {
			if (!MathExtensionsUtilities.isGivenSet(typeEnvironment, type.getName())) {
				display.createProblemMarker(element, attrType, TheoryGraphProblem.NonTypeParOccurError, type.getName());
				ok = false;
			}
		}
		return ok;
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
	public static Type parseTypeExpression(ITypeElement typingElmnt, FormulaFactory factory, IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.TYPE_ATTRIBUTE;
		String expString = typingElmnt.getType();
		IParseResult parseResult = factory.parseType(expString, V2);
		if (issueASTProblemMarkers(typingElmnt, attributeType, parseResult, display)) {
			return null;
		}
		Type type = parseResult.getParsedType();
		return type;
	}

	/**
	 * Parses and type checks the predicate occurring as an attribute to the
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
	public static Predicate parseAndCheckPredicate(IPredicateElement element, FormulaFactory ff, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
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
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
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
	 * Parses and type checks the expression occurring as an attribute to the
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
	 * @return the parsed expression
	 * @throws CoreException
	 */
	public static Expression parseAndCheckExpression(IExpressionElement element, FormulaFactory ff, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = EventBAttributes.EXPRESSION_ATTRIBUTE;
		String exp = element.getExpressionString();
		IParseResult result = ff.parseExpression(exp, V2, null);
		if (issueASTProblemMarkers(element, attributeType, result, display)) {
			return null;
		}
		Expression expression = result.getParsedExpression();
		FreeIdentifier[] idents = expression.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = expression.typeCheck(typeEnvironment);
		if (issueASTProblemMarkers(element, attributeType, tcResult, display)) {
			return null;
		}
		return expression;
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
	public static boolean issueASTProblemMarkers(IInternalElement element, IAttributeType.String attributeType, IResult result, IMarkerDisplay display) throws RodinDBException {

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
				problem = ParseProblem.InvalidTypeExpressionError;
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
				display.createProblemMarker(element, attributeType, problem, objects);
			} else {
				display.createProblemMarker(element, attributeType, location.getStart(), location.getEnd(), problem, objects);
			}

			errorIssued |= problem.getSeverity() == IMarker.SEVERITY_ERROR;
		}

		return errorIssued;
	}

	/**
	 * Returns the set of all syntax symbols specified in the given source.
	 * 
	 * @param source
	 *            the formula extensions source
	 * @return the set of syntactic symbols
	 * @throws CoreException
	 */
	public static Set<String> getSyntaxSymbols(IFormulaExtensionsSource source) throws CoreException {
		Set<String> set = new TreeSet<String>();
		// start by datatypes
		ISCDatatypeDefinition[] datatypeDefinitions = source.getSCDatatypeDefinitions();
		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			set.add(definition.getIdentifierString());
			ISCDatatypeConstructor[] constructors = definition.getConstructors();
			for (ISCDatatypeConstructor constructor : constructors) {
				set.add(constructor.getIdentifierString());
				ISCConstructorArgument arguments[] = constructor.getConstructorArguments();
				for (ISCConstructorArgument argument : arguments) {
					set.add(argument.getIdentifierString());
				}
			}
		}
		// next operators
		ISCNewOperatorDefinition[] operatorDefinitions = source.getSCNewOperatorDefinitions();
		for (ISCNewOperatorDefinition definition : operatorDefinitions) {
			set.add(definition.getLabel());
		}
		return set;
	}

	/**
	 * Returns the syntactic symbols of the hierarchy up to the specified leaf theory.
	 * 
	 * @param leaf
	 *            the leaf theory
	 * @return all syntactic symbols
	 * @throws CoreException
	 */
	public static Set<String> getSyntacticSymbolsOfHierarchy(ISCTheoryRoot leaf) throws CoreException {
		Set<ISCTheoryRoot> imported = DatabaseUtilities.importClosure(leaf);
		Set<String> set = new TreeSet<String>();
		set.addAll(getSyntaxSymbols(leaf));
		for (ISCTheoryRoot root : imported) {
			Set<String> rootSet = getSyntaxSymbols(root);
			if (rootSet != null)
				set.addAll(rootSet);
		}
		return set;
	}

	/**
	 * Returns the set of syntax symbols defined in other deployed hierarchies with respect to <code>hierarchyToIgnoreLeaf</code>.
	 * <p> As an example, given a SC theory <code>T1</code> that import SC theory <code>T2</code>, and <code>T3</code> is a deployed theory.
	 * From the standpoint of <code>T1</code>, this method returns the syntactic contributions of <code>T3</code> but not <code>T2</code>.
	 * @param hierarchyToIgnoreLeaf the SC theory 
	 * @return the set of syntax symbols from other deployed hierarchies
	 * @throws CoreException
	 */
	public static Map<String, Set<String>> getDeployedSyntaxSymbolsOfOtherHierarchies(ISCTheoryRoot hierarchyToIgnoreLeaf) throws CoreException {
		Set<ISCTheoryRoot> hierarchyToIgnore = DatabaseUtilities.importClosure(hierarchyToIgnoreLeaf);
		Set<String> names = DatabaseUtilities.getNames(hierarchyToIgnore);
		IRodinProject project = hierarchyToIgnoreLeaf.getRodinProject();
		IDeployedTheoryRoot[] deployedRoots = project.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
		Map<String, Set<String>> deployedMap = new TreeMap<String, Set<String>>();
		for (IDeployedTheoryRoot root : deployedRoots) {
			if (!names.contains(root.getComponentName())) {
				Set<String> contrib = getSyntaxSymbols(root);
				deployedMap.put(root.getComponentName(), contrib);
			}
		}
		return deployedMap;
	}

}
