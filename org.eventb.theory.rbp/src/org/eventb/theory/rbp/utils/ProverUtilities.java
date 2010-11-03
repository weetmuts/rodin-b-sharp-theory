package org.eventb.theory.rbp.utils;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.rbp.internal.base.IDeployedRule;

/**
 * Some utilities used by RbP.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class ProverUtilities {

	public static boolean DEBUG = true;

	/**
	 * Literal predicate true.
	 */
	public static final Predicate BTRUE = FormulaFactory.getDefault()
			.makeLiteralPredicate(Formula.BTRUE, null);

	/**
	 * Make sure tag is for an associative expression.
	 * <p>
	 * This method checks whether the operator is ac.
	 * 
	 * @param tag
	 * @return
	 */
	public static boolean isAssociativeCommutative(int tag) {
		if (tag == AssociativeExpression.BCOMP
				|| tag == AssociativeExpression.FCOMP) {
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * Utility to check whether a given formula is an expression.
	 * </p>
	 * 
	 * @param form
	 * @return whether form is an expression
	 */
	public static boolean isExpression(Formula<?> form) {
		return form instanceof Expression;
	}

	/**
	 * <p>
	 * Utility to check whether the given formula is a theory formula.
	 * </p>
	 * 
	 * @param form
	 *            to check
	 * @return whether <code>form</code> is a theory formula
	 */
	public static boolean isTheoryFormula(Formula<?> form) {
		return (form instanceof Expression) || (form instanceof Predicate);
	}

	/**
	 * <p>
	 * Merges all the lists of rules in the <code>Map</code>
	 * <code>allRules</code>.
	 * </p>
	 * 
	 * @param allRules
	 * @return the merged list
	 */
	public static <E, F extends IDeployedRule> List<F> mergeLists(
			Map<E, List<F>> allRules) {
		List<F> result = new ArrayList<F>();
		for (E key : allRules.keySet()) {
			result.addAll(allRules.get(key));
		}
		return result;
	}

	/**
	 * <p>
	 * Utility method to parse a string as a formula knowing beforehand whether
	 * it is a an expression or predicate.
	 * </p>
	 * <p>
	 * Use only for theory formulas.
	 * </p>
	 * 
	 * @param formStr
	 *            the formula string
	 * @param isExpression
	 *            whether to parse an expression or a predicate
	 * @return the parsed formula or <code>null</code> if there was an error
	 */
	public static Formula<?> parseFormulaString(String formStr,
			boolean isExpression, FormulaFactory factory) {

		Formula<?> form = null;
		if (isExpression) {
			IParseResult r = factory.parseExpressionPattern(formStr, V2, null);
			form = r.getParsedExpression();
		} else {
			IParseResult r = factory.parsePredicatePattern(formStr, V2, null);
			form = r.getParsedPredicate();
		}
		return form;
	}

	// to parse a Theory formula i.e. predicate or expression
	public static Formula<?> parseFormula(String formula, FormulaFactory factory) {
		IParseResult res = factory.parseExpressionPattern(formula, V2, null);
		if (res.hasProblem()) {
			res = factory.parsePredicatePattern(formula, V2, null);
			if (res.hasProblem()) {
				return null;
			} else
				return res.getParsedPredicate();
		} else
			return res.getParsedExpression();

	}

	/**
	 * <p>
	 * Utility to print items in a list in a displayable fashion.
	 * </p>
	 * <p>
	 * The return of this method will be of the shape: {<}item0,...,itemn{>}
	 * </p>
	 * 
	 * @param items
	 * @return the displayable string
	 */
	public static String printListedItems(List<String> items) {
		if (items.size() == 0) {
			return "";
		}
		String result = "";
		int i = 0;
		for (String str : items) {
			if (i == 0) {

				result = str;
			} else {
				result += "," + str;
			}
			i++;
		}
		result = "<" + result + ">";
		return result;
	}

	/**
	 * <p>
	 * Checks whether two objects are of the same class.
	 * </p>
	 * 
	 * @param o1
	 * @param o2
	 * @return whether the two objects are of the same class
	 */
	public static boolean sameClass(Object o1, Object o2) {
		return o1.getClass().equals(o2.getClass());
	}

	/**
	 * Returns the associative expression that fit the given details.
	 * @param tag
	 * @param exps
	 * @param factory
	 * @return the expression
	 */
	public static Expression makeAssociativeExpression(int tag,
			Expression[] exps, FormulaFactory factory) {
		List<Expression> es = new ArrayList<Expression>();
		for (Expression e : exps) {
			if (e != null) {
				es.add(e);
			}
		}
		if(es.size() < 1){
			throw 
			 new IllegalArgumentException("Cannot make associative expression from empty array of children.");
		}
		if (es.size() == 1)
			return es.get(0);
		else {
			return factory.makeAssociativeExpression(tag,
					es.toArray(new Expression[es.size()]), null);
		}
	}

	/**
	 * Returns the associative predicate that fit the given details.
	 * @param tag
	 * @param preds
	 * @param factory
	 * @return the predicate
	 */
	public static Predicate makeAssociativePredicate(int tag,
			Predicate[] preds, FormulaFactory factory) {
		List<Predicate> es = new ArrayList<Predicate>();
		for (Predicate e : preds) {
			if (e != null) {
				es.add(e);
			}
		}
		if(es.size() < 1){
			throw 
			 new IllegalArgumentException("Cannot make associative predicate from empty array of children.");
		}
		if (es.size() == 1)
			return es.get(0);
		else {
			return factory.makeAssociativePredicate(tag,
					es.toArray(new Predicate[es.size()]), null);
		}
	}

	/**
	 * Returns the formula factory with the currently deployed extensions.
	 * 
	 * @return the current formula factory
	 */
	public static FormulaFactory getCurrentFormulaFactory() {
		return DB_TCFacade.getCurrentFormulaFactory();
	}

	/**
	 * Combines the hashcodes of the given objects.
	 * @param os the objects
	 * @return the combined hashcodes
	 */
	public static int combineHashCode(Object... os) {
		int result = 0;
		int i = 1;
		for (Object o : os) {
			result += o.hashCode() * i * 7;
			i++;
		}
		return result;
	}

	/**
	 * Returns the given list if it is not <code>null</code>, and en empty list otherwise.
	 * @param <E> the type of elements
	 * @param list the list
	 * @return a safe list
	 */
	public static <E> List<E> safeList(List<E> list) {
		if (list == null) {
			return new ArrayList<E>();
		}
		return list;
	}

	/**
	 * Returns the given collection if it is not <code>null</code>, and en empty collection otherwise.
	 * @param <E> the type of elements
	 * @param col the collection
	 * @return a safe collection
	 */
	public static <E> Collection<E> safeCollection(Collection<E> col) {
		if (col == null)
			return new LinkedHashSet<E>();
		return col;
	}

	/**
	 * Returns the integer that is represented in <code>str</code>.
	 * @param str the string representation
	 * @return the integer
	 */
	public static int parseInteger(String str) {
		try {
			int num = Integer.parseInt(str);
			return num;
		} catch (NumberFormatException e) {
			return -1;
		}

	}
	
	/**
	 * Returns a string representation of the given list of objects.
	 * 
	 * @param list
	 *            the list of strings
	 * @return the representing string
	 */
	public static <E> String toString(List<E> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).toString();
			if (i < list.size() - 1) {
				result += ", ";
			}
		}
		return result;
	}

}
