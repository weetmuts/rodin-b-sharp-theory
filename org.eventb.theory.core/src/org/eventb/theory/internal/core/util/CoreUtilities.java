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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
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
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.ParseProblem;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.ITypeElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IDatatypeTable.ERROR_CODE;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class CoreUtilities {

	private static final Object[] NO_OBJECT = new Object[0];
	
	public static final Predicate BTRUE = FormulaFactory.getDefault().makeLiteralPredicate(Formula.BTRUE, null);

	/**
	 * <p>A utility to check if an object is present in an array of objects. This method uses <code>Object.equals(Object)</code></p>
	 * @param objs the container array of objects
	 * @param o the object to check
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
	 * <p> Utility to check whether <code>objs</code> contains all of <code>os</code>.
	 * </p>
	 * @param objs the container array of objects
	 * @param os the array of objects
	 * @return whether <code>objs</code> contains all of <code>os</code>.
	 */
	public static boolean subset(Object[] objs, Object[] os) {
		for (Object o : os) {
			if (!contains(objs, o))
				return false;
		}
		return true;
	}
	
	public static Predicate conjunctPredicates(List<Predicate> preds, FormulaFactory ff){
		if(preds.size() == 0){
			return BTRUE;
		}
		if(preds.size() == 1){
			return preds.get(0);
		}
		return ff.makeAssociativePredicate(Formula.LAND, preds, null);
	}
	
	
	/**
	 * @param bareName
	 * @return
	 */
	public static String getSCTheoryFileName(String bareName) {
		// TODO Auto-generated method stub
		return bareName + "." + TheoryCoreFacade.SC_THEORY_FILE_EXTENSION;
	}
	

	/**
	 * 
	 * @param element
	 * @param pred
	 * @param typeEnvironment
	 * @param display
	 * @return
	 * @throws CoreException
	 */
	public static boolean checkAgainstTypeParameters(IPredicateElement element, Predicate pred, ITypeEnvironment typeEnvironment, IMarkerDisplay display)
	throws CoreException{
		return checkAgainstTypeParameters(element, pred, EventBAttributes.PREDICATE_ATTRIBUTE, typeEnvironment, display);
	}

	/**
	 * 
	 * @param element
	 * @param formula
	 * @param typeEnvironment
	 * @param display
	 * @return
	 * @throws CoreException
	 */
	public static boolean checkAgainstTypeParameters(IFormulaElement element, Formula<?> formula, 
			ITypeEnvironment typeEnvironment, IMarkerDisplay display)
	throws CoreException{
		return checkAgainstTypeParameters(element, formula, TheoryAttributes.FORMULA_ATTRIBUTE, typeEnvironment, display);
	}
	
	protected static boolean checkAgainstTypeParameters(IInternalElement element, Formula<?> formula, IAttributeType attrType,
			ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws RodinDBException{
		Set<GivenType> types = formula.getGivenTypes();
		boolean ok = true;
		for(GivenType type : types){
			if(!CoreUtilities.isGivenSet(typeEnvironment, type.getName())){
				display.createProblemMarker(element, attrType,TheoryGraphProblem.NonTypeParOccurError, type.getName());
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
		if (identifier.isPrimed()){
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

	public static <E> Set<E> singletonSet(E element){
		Set<E> set = new HashSet<E>();
		set.add(element);
		return set;
	}

	public static Set<IFormulaExtension> singletonCondExtension(IFormulaExtension element){
		Set<IFormulaExtension> set = new HashSet<IFormulaExtension>();
		set.add(element);
		return set;
	}
	
	public static Type parseTypeExpression(ITypeElement typingElmnt,
			FormulaFactory factory, IMarkerDisplay display) 
	throws CoreException{
		IAttributeType.String attributeType = TheoryAttributes.TYPE_ATTRIBUTE;
		String expString = typingElmnt.getType();

		IParseResult parseResult = factory.parseType(expString, V2);

		if (issueASTProblemMarkers(typingElmnt, attributeType,
				parseResult, display)) {
			return null;
		}
		Type type = parseResult.getParsedType();
		return type;
	}
	
	public static Formula<?> parseAndCheckFormula(IFormulaElement element,
			FormulaFactory ff, ITypeEnvironment typeEnvironment, IMarkerDisplay display)
			throws CoreException{
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		IParseResult result = ff.parsePredicate(form, V2, null);
		if(result.hasProblem()){
			result = ff.parseExpression(form, V2, null);
			if(issueASTProblemMarkers(element, attributeType, result, display)){
				return null;
			}
			else{
				formula = result.getParsedExpression();
			}
		}
		else{
			formula = result.getParsedPredicate();
		}
		
		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for(FreeIdentifier ident : idents){
			if(!typeEnvironment.contains(ident.getName())){
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if(issueASTProblemMarkers(element, attributeType, tcResult, display)){
			return null;
		}
		return formula;
	}
	
	public static Formula<?> parseAndCheckPatternFormula(IFormulaElement element,
			FormulaFactory ff, ITypeEnvironment typeEnvironment, IMarkerDisplay display)
			throws CoreException{
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		IParseResult result = ff.parsePredicatePattern(form, V2, null);
		if(result.hasProblem()){
			result = ff.parseExpressionPattern(form, V2, null);
			if(issueASTProblemMarkers(element, attributeType, result, display)){
				return null;
			}
			else{
				formula = result.getParsedExpression();
			}
		}
		else{
			formula = result.getParsedPredicate();
		}
		
		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for(FreeIdentifier ident : idents){
			if(!typeEnvironment.contains(ident.getName())){
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if(issueASTProblemMarkers(element, attributeType, tcResult, display)){
			return null;
		}
		return formula;
	}
	
	public static Predicate parseAndCheckPredicate(IPredicateElement element, 
			FormulaFactory ff, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws CoreException{
		IAttributeType.String attributeType = EventBAttributes.PREDICATE_ATTRIBUTE;
		String pred = element.getPredicateString();
		IParseResult result = ff.parsePredicate(pred, V2, null);
		if(issueASTProblemMarkers(element, attributeType, result, display)){
			return null;
		}
		Predicate predicate = result.getParsedPredicate();
		FreeIdentifier[] idents = predicate.getFreeIdentifiers();
		for(FreeIdentifier ident : idents){
			if(!typeEnvironment.contains(ident.getName())){
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = predicate.typeCheck(typeEnvironment);
		if(issueASTProblemMarkers(element, attributeType, tcResult, display)){
			return null;
		}
		return predicate;
	}
	
	/**
	 * Parse the identifier element
	 * 
	 * @param element
	 *            the element to be parsed
	 * @return a <code>FreeIdentifier</code> in case of success,
	 *         <code>null</code> otherwise
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	public static FreeIdentifier parseIdentifier(IIdentifierElement element, FormulaFactory factory,
			IMarkerDisplay display, IProgressMonitor monitor) throws RodinDBException {

		if (element.hasIdentifierString()) {

			return parseIdentifier(element.getIdentifierString(), element,
					EventBAttributes.IDENTIFIER_ATTRIBUTE, factory, display);
		} else {

			display.createProblemMarker(element, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					GraphProblem.IdentifierUndefError);
			return null;
		}
	}

	
	public static <T extends IIdentifierElement> T createSCIdentifierElement(
			IInternalElementType<T> type, 
			IIdentifierElement source, 
			IInternalElement parent, 
			IProgressMonitor monitor) throws CoreException{
		T scElement = parent.getInternalElement(type, source.getIdentifierString());
		scElement.create(null, monitor);
		return scElement;
	}
	
	// Returns whether an error was issued
	public static boolean issueASTProblemMarkers(IInternalElement element,
			IAttributeType.String attributeType, IResult result, IMarkerDisplay display)
			throws RodinDBException {

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
				display.createProblemMarker(element, attributeType, problem, objects);
			} else {
				display.createProblemMarker(element, attributeType,
						location.getStart(), location.getEnd(), problem,
						objects);
			}

			errorIssued |= problem.getSeverity() == IMarker.SEVERITY_ERROR; 
		}

		return errorIssued;
	}
	
	public static IRodinProblem getAppropriateProblemForCode(ERROR_CODE code){
		switch(code){
		case NAME_IS_A_CONSTRUCTOR: return TheoryGraphProblem.IdenIsAConsNameError;
		case NAME_IS_A_DATATYPE: return TheoryGraphProblem.IdenIsADatatypeNameError;
		case NAME_IS_A_DESTRUCTOR: return TheoryGraphProblem.IdenIsADesNameError;
		}
		return null;
	}
	
	/**
	 * @param identifierString
	 * @param argsList
	 * @return
	 */
	public static Type createTypeExpression(String identifierString,
			List<String> argsList, FormulaFactory ff) {
		String result = identifierString;
		if(argsList.size()!=0){
			result += "(";
			for (int i = 0; i < argsList.size(); i++){
				result += argsList.get(i);
				if(i < argsList.size()-1){
					result += ",";
				}
			}
			result += ")";
			
		}
		// TODO this should be guaranteed to parse
		return ff.parseType(result, LanguageVersion.V2).getParsedType() ;
		
	}
	
	public static Type createTypeExpression(String identifier, 
			String[] argsArray, FormulaFactory ff){
		return createTypeExpression(identifier, Arrays.asList(argsArray), ff);
	}
	
	public static Type parseType(String typeStr, FormulaFactory ff){
		return ff.parseType(typeStr, V2).getParsedType();
		
	}
	
	public static Formula<?> parseFormula(String formStr, FormulaFactory ff, boolean isPattern){
		Formula<?> formula = null;
		if(isPattern){
			IParseResult res = ff.parseExpressionPattern(formStr, V2, null);
			if(!res.hasProblem()){
				formula = res.getParsedExpression();
			}
			else {
				res = ff.parsePredicatePattern(formStr, V2, null);
				if(!res.hasProblem()){
					formula = res.getParsedPredicate();
				}
			}
		}
		else {
			IParseResult res = ff.parseExpression(formStr, V2, null);
			if(!res.hasProblem()){
				formula = res.getParsedExpression();
			}
			else {
				res = ff.parsePredicate(formStr, V2, null);
				if(!res.hasProblem()){
					formula = res.getParsedPredicate();
				}
			}
		}
		
		return formula;
	}
	
	public static List<String> getOperatorArguments(ITypeEnvironment typeEnvironment){
		Set<String> all = typeEnvironment.clone().getNames();
		all.removeAll(getGivenSetsNames(typeEnvironment));
		
		return new ArrayList<String>(all);
	}
	
	public static List<String> getGivenSetsNames(ITypeEnvironment typeEnvironment){
		List<String> result = new ArrayList<String>();
		for (String name : typeEnvironment.getNames()){
			if(isGivenSet(typeEnvironment, name)){
				result .add(name);
			}
		}
		return result;
	}
	
	public static boolean isGivenSet(ITypeEnvironment typeEnvironment, String name) {
		Type type = typeEnvironment.getType(name);
		if(type == null){
			return false;
		}
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(name);
		}
		return false;
	}
	
	public static String toString(List<String> list){
		String result = "";
		for(int i =0 ; i < list.size() ; i++){
			result+=list.get(i);
			if(i < list.size()-1){
				result += ", ";
			}
		}
		return result;
	}
	
	public static ITypeEnvironment getTypeEnvironmentForFactory(
			ITypeEnvironment typeEnvironment, FormulaFactory factory){
		ITypeEnvironment newTypeEnvironment = factory.makeTypeEnvironment();
		for (String name : typeEnvironment.getNames()){
			newTypeEnvironment.addName(name, typeEnvironment.getType(name));
		}
		return newTypeEnvironment;
	}
	
	public static GivenType[] getTypesOccurringIn(Type type, FormulaFactory factory){
		List<GivenType> types = new ArrayList<GivenType>();
		FreeIdentifier[] idents = type.toExpression(factory).getFreeIdentifiers();
		for(FreeIdentifier ident : idents){
			types.add(factory.makeGivenType(ident.getName()));
		}
		return types.toArray(new GivenType[types.size()]);
	}
}
