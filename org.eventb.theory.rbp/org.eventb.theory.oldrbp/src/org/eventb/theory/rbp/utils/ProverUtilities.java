package org.eventb.theory.rbp.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCRule;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.basis.NewOperatorDefinition;
import org.eventb.theory.core.basis.SCRewriteRule;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRule;
import org.rodinp.core.RodinDBException;

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
	 * <p>
	 * Merges all the lists of rules in the <code>Map</code>
	 * <code>allRules</code>.
	 * </p>
	 * 
	 * @param allRules
	 * @return the merged list
	 */
	public static <E, F extends IDeployedRule> List<F> mergeLists(Map<E, List<F>> allRules) {
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
	public static Formula<?> parseFormula(String formStr,boolean isExpression, FormulaFactory factory) {
		Formula<?> form = null;
		if (isExpression) {
			IParseResult r = factory.parseExpressionPattern(formStr, null);
			form = r.getParsedExpression();
		} else {
			IParseResult r = factory.parsePredicatePattern(formStr, null);
			form = r.getParsedPredicate();
		}
		return form;
	}

	// to parse a Theory formula i.e. predicate or expression
	public static Formula<?> parseFormulaPattern(String formula, FormulaFactory factory) {
		IParseResult res = factory.parseExpressionPattern(formula, null);
		if (res.hasProblem()) {
			res = factory.parsePredicatePattern(formula, null);
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
	 * The return of this method will be of the shape: {<}item_0,...,item_n{>}
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
	 * Returns the given collection if it is not <code>null</code>, and an empty collection otherwise.
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
	 * Returns the integer that is represented in <code>string</code>.
	 * @param string the string representation
	 * @return the integer
	 */
	public static int parseInteger(String string) {
		try {
			int num = Integer.parseInt(string);
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
	
	/**
	 * Logs the given exception with the message.
	 * @param exc the exception
	 * @param message the message
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
		IStatus status = new Status(IStatus.ERROR, RbPPlugin.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		RbPPlugin.getDefault().getLog().log(status);
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
	public static boolean isGivenSet(ITypeEnvironment typeEnvironment, String name) {
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
	
	public static ITypeEnvironment makeTypeEnvironment(FormulaFactory factory, ISCRule rule) throws CoreException {
		ITypeEnvironmentBuilder typeEnvironment = factory.makeTypeEnvironment();
		try {
		ISCTheoryRoot SCtheoryRoot = DatabaseUtilities.getSCTheory(rule.getRoot().getElementName(), rule.getRoot().getRodinProject());
		ISCTypeParameter[] types = SCtheoryRoot.getSCTypeParameters();
		for (ISCTypeParameter par : types) {
			typeEnvironment.addGivenSet(par.getIdentifier(factory).getName());
		}
		
		//update typeEnv
		ISCProofRulesBlock block = ((ISCProofRulesBlock) rule.getParent());
		ISCMetavariable[] vars = block.getMetavariables();
		for (ISCMetavariable var : vars) {
			typeEnvironment.add(var.getIdentifier(factory));
		}
		if (block.getSource() instanceof NewOperatorDefinition) {
			ISCTheoryRoot deployedRoot = (ISCTheoryRoot) block.getParent();
			ISCNewOperatorDefinition[] operatorDefinitions = deployedRoot.getSCNewOperatorDefinitions();
			for (ISCNewOperatorDefinition definition : operatorDefinitions) {
				if (definition.getLabel().equals(((SCRewriteRule) rule).getLabel().replaceFirst(block.getParent().getElementName()+".", ""))) {
					ISCOperatorArgument[] args = definition.getOperatorArguments();
					for (ISCOperatorArgument var : args) {
						typeEnvironment.add(var.getIdentifier(factory));
					}
					break;
				}
			}
		}
		
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return typeEnvironment;
	}
	
	public static boolean isConditional(ISCRewriteRule rule, FormulaFactory factory, ITypeEnvironment typeEnv) throws CoreException {
		boolean isCond = true;
		List<ISCRewriteRuleRightHandSide> ruleRHSs = Arrays.asList(rule.getRuleRHSs());
		if(ruleRHSs.size() == 1){
			ISCRewriteRuleRightHandSide rhs0 = ruleRHSs.get(0);
			Predicate cond = rhs0.getPredicate(typeEnv);
			if(cond.equals(ProverUtilities.BTRUE)){
				isCond = false;
			}
		}
		return isCond;
	}

	public static ReasoningType getReasoningType(ISCInferenceRule rule, boolean backward, boolean forward) {
		if(backward && forward)
			return ReasoningType.BACKWARD_AND_FORWARD;
		else if(forward)
			return ReasoningType.FORWARD;
		return ReasoningType.BACKWARD;
	}
}