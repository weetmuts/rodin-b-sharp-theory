/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.maths.IOperatorExtension;

/**
 * Utilities from the Theory Core that are mostly useful for the Rule-based Prover.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class AST_TCFacade {

	public static final String POSTFIX = Notation.POSTFIX.toString();

	public static final String INFIX = Notation.INFIX.toString();

	public static final String PREFIX = Notation.PREFIX.toString();

	public static final String[] POSSIBLE_NOTATION_TYPES = new String[] {
			PREFIX, INFIX };
	
	/**
	 * Converts a string (eg. "POSTFIX") to the corresponding notation.
	 * 
	 * @param type
	 *            in string format
	 * @return the corresponding notation
	 */
	public static Notation getNotation(String type) {
		if (type.equalsIgnoreCase(POSTFIX)) {
			return Notation.POSTFIX;
		} else if (type.equalsIgnoreCase(INFIX)) {
			return Notation.INFIX;
		} else {
			return Notation.PREFIX;
		}
	}
	
	/**
	 * Returns whether the given extended expression is associative.
	 * @param expression the extended expression
	 * @return whether the given extended expression is associative
	 */
	public static boolean isAssociative(ExtendedExpression expression){
		return expression != null &&
			expression.getExtension() instanceof IOperatorExtension &&
			((IOperatorExtension<?>) expression.getExtension()).isAssociative();
	}
	
	/**
	 * Returns whether the given extended expression is associative commutative.
	 * @param expression the extended expression
	 * @return whether the given extended expression is associative commutative
	 */
	public static boolean isAC(ExtendedExpression expression){
		return isAssociative(expression) &&
			((IOperatorExtension<?>) expression.getExtension()).isCommutative();
	}
	
	/**
	 * Returns the unflattened version of the the extended expression with the
	 * given children.
	 * @param extension the formula extension
	 * @param children the extended expression children
	 * @param factory the formula factory
	 * @return the unflattened extended expression
	 * TODO the result is right-associative/ make left-associative
	 */
	public static ExtendedExpression unflatten(IFormulaExtension extension,
			Expression[] children, FormulaFactory factory) {
		if(!isATheoryExtension(extension)){
			if (extension instanceof IExpressionExtension){
				return factory.makeExtendedExpression(
						(IExpressionExtension)extension, 
						children, new Predicate[0], null);
			}
	
		}
		if(isATheoryExtension(extension) && !((IOperatorExtension<?>) extension).isAssociative()){
			return factory.makeExtendedExpression((IExpressionExtension)extension, children, new Predicate[0], null);
		}
		// only works for extended expressions resulting from theory extensions
		// children length has to be 2 or more
		IExpressionExtension expreExtension = (IExpressionExtension) extension;
		if (children.length == 2) {
			return factory.makeExtendedExpression(expreExtension, children,
					new Predicate[0], null);
		} else {
			Expression[] toWorkWith = subChildren(1, children);
			return factory.makeExtendedExpression(
					expreExtension,
					new Expression[] { children[0],
							unflatten(extension, toWorkWith, factory) },
					new Predicate[0], null);
		}
	}
	
	/**
	 * Returns the unflattened version of the the associative expression with the
	 * given children.
	 * @param tag the expression tag
	 * @param children the extended expression children
	 * @param factory the formula factory
	 * @return the unflattened associative expression
	 * 
	 * TODO the result is right-associative/ make left-associative
	 */
	public static AssociativeExpression unflatten(int tag,
			Expression[] children, FormulaFactory factory) {
		// only works for associative expressions (TODO check tag)
		if (children.length == 2) {
			return factory.makeAssociativeExpression(tag, children, null);
		} else {
			Expression[] toWorkWith = subChildren(1, children);
			return factory.makeAssociativeExpression(
					tag,
					new Expression[] { children[0],
							unflatten(tag, toWorkWith, factory)}, null);
		}
	}
	
	/**
	 * Returns the unflattened version of the the associative predicate with the
	 * given children.
	 * @param tag the predicate tag
	 * @param children the predicate children
	 * @param factory the formula factory
	 * @return the unflattened associative predicate
	 * 
	 * TODO the result is right-associative/ make left-associative
	 */
	public static AssociativePredicate unflatten(int tag,
			Predicate[] children, FormulaFactory factory) {
		// only works for associative predicates (TODO check tag)
		if (children.length == 2) {
			return factory.makeAssociativePredicate(tag, children, null);
		} else {
			Predicate[] toWorkWith = subChildren(1, children);
			return factory.makeAssociativePredicate(
					tag,
					new Predicate[] { children[0],
							unflatten(tag, toWorkWith, factory)}, null);
		}
	}

	/**
	 * This method returns the array resulting from taking the elements of
	 * children in the same order starting from the (zero-based) given starting
	 * index (inclusive).
	 * 
	 * @param start
	 *            the starting index
	 * @param children
	 *            the original array
	 * @return the sub-array
	 */
	public static Predicate[] subChildren(int start, Predicate[] children) {
		if (start > children.length - 1) {
			return new Predicate[0];
		} else {
			Predicate[] result = new Predicate[children.length - start];
			for (int i = 0; i < result.length; i++) {
				result[i] = children[i + start];
			}
			return result;
		}
	}
	/**
	 * This method returns the array resulting from taking the elements of
	 * children in the same order starting from the (zero-based) given starting
	 * index (inclusive).
	 * 
	 * @param start
	 *            the starting index
	 * @param children
	 *            the original array
	 * @return the sub-array
	 */
	public static Expression[] subChildren(int start, Expression[] children) {
		if (start > children.length - 1) {
			return new Expression[0];
		} else {
			Expression[] result = new Expression[children.length - start];
			for (int i = 0; i < result.length; i++) {
				result[i] = children[i + start];
			}
			return result;
		}
	}

	/**
	 * Returns the position of the operator in the given extended expression.
	 * 
	 * @param eexp
	 *            the expression
	 * @param predStr
	 *            the string of the expression
	 * @return operator position
	 */
	@SuppressWarnings("unchecked")
	public static PositionPoint getPositionOfOperator(ExtendedExpression eexp,
			String predStr) {
		assert eexp.getExtension() instanceof IOperatorExtension;
		PositionPoint point = null;
		IOperatorExtension<Expression> extension = (IOperatorExtension<Expression>) eexp.getExtension();
		Notation notation = extension.getNotation();

		switch (notation) {
		case INFIX:
			Expression ie1 = eexp.getChildExpressions()[0];
			Expression ie2 = eexp.getChildExpressions()[1];
			point = getOperatorPosition(predStr, ie1.getSourceLocation()
					.getEnd() + 1, ie2.getSourceLocation().getStart());
			break;

		default:
			if (eexp.getChildExpressions().length == 0) {
				point = getOperatorPosition(predStr, eexp.getSourceLocation()
						.getStart(), eexp.getSourceLocation().getEnd() + 1);
			} else {
				Expression pe1 = eexp.getChildExpressions()[0];
				point = getOperatorPosition(predStr, eexp.getSourceLocation()
						.getStart(), pe1.getSourceLocation().getStart());
			}
			break;
		}
		return point;
	}

	/**
	 * Returns the position of the operator in the given extended predicate.
	 * 
	 * @param epred
	 *            the predicate
	 * @param predStr
	 *            the string of the predicate
	 * @return operator position
	 */
	public static PositionPoint getPositionOfOperator(ExtendedPredicate epred,
			String predStr) {
		assert epred.getExtension() instanceof IOperatorExtension;
		PositionPoint point = null;
		Expression pe1 = epred.getChildExpressions()[0];
		point = getOperatorPosition(predStr, epred.getSourceLocation()
				.getStart(), pe1.getSourceLocation().getStart());
		return point;
	}

	/**
	 * Returns whether the given extension is a theory extension.
	 * 
	 * @param extension
	 * @return whether the given extension is a theory extension
	 */
	public static boolean isATheoryExtension(IFormulaExtension extension) {
		return extension instanceof IOperatorExtension;
	}
	
	/**
	 * Returns whether the given extension is an associative theory extension.
	 * @param extension the theory extension
	 * @return whether the extension declares an associative operator
	 */
	public static boolean isAnAssociativeExtension(IFormulaExtension extension){
		if(isATheoryExtension(extension)){
			if(((IOperatorExtension<?>) extension).isAssociative()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Delegates the definition expansion request to the operator extension of the given extended formula.
	 * 
	 * @param extendedFormula the extended formula
	 * @param factory the formula factory
	 * @return the expanded definition or <code>null</code> if the extension of the formula is not a theory extension
	 */
	@SuppressWarnings("unchecked")
	public static  Formula<?> delegateDefinitionExpansion(
			Formula<?> extendedFormula, FormulaFactory factory) {
		if (extendedFormula == null
				|| !(extendedFormula instanceof IExtendedFormula)) {
			return null;
		}
		IFormulaExtension extension = ((IExtendedFormula) extendedFormula)
				.getExtension();
		if (isATheoryExtension(extension)) {
			if(extendedFormula instanceof Expression){
				IOperatorExtension<Expression> opExtension = 
					(IOperatorExtension<Expression>) extension;
				return opExtension.expandDefinition((Expression)extendedFormula, factory);
			}
			else {
				IOperatorExtension<Predicate> opExtension = 
					(IOperatorExtension<Predicate>) extension;
				return opExtension.expandDefinition((Predicate)extendedFormula, factory);
			
			}
		}
		return extendedFormula;
	}

	/**
	 * An utility method to return the operator source location within the range
	 * (start, end).
	 * <p>
	 * 
	 * @param predStr
	 *            the actual predicate string.
	 * @param start
	 *            the starting index for searching.
	 * @param end
	 *            the last index for searching
	 * @return the location in the predicate string ignore the empty spaces or
	 *         brackets in the beginning and in the end.
	 */
	protected static PositionPoint getOperatorPosition(String predStr,
			int start, int end) {
		int i = start;
		int x = start;
		int y;
		boolean letter = false;
		while (i < end) {
			char c = predStr.charAt(i);
			if (letter == false && !isSpaceOrBracket(c)) {
				x = i;
				letter = true;
			} else if (letter == true && isSpaceOrBracket(c)) {
				y = i;
				return new PositionPoint(x, y);
			}
			++i;
		}
		if (letter == true)
			return new PositionPoint(x, end);
		else
			return new PositionPoint(start, end);
	}

	/**
	 * A private utility method to check if a character is either a space or a
	 * bracket.
	 * <p>
	 * 
	 * @param c
	 *            the character to check.
	 * @return <code>true</code> if the character is a space or bracket,
	 *         otherwise return <code>false</code>.
	 */
	private static boolean isSpaceOrBracket(char c) {
		return (c == '\t' || c == '\n' || c == ' ' || c == '(' || c == ')');
	}

	/**
	 * An implementation of a point.
	 * 
	 * <p> This is a hook to avoid using the SWT point implementation.
	 * 
	 * @author maamria
	 * 
	 */
	public static class PositionPoint {
		int x;
		int y;

		public PositionPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}
}
