package ac.soton.eventb.prover.utils;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

import ac.soton.eventb.prover.internal.base.IDRewriteRule;

public class ProverUtilities {
	
	public static boolean DEBUG = true;
	
	/**
	 * <p>Utility to check whether two types can be considered as matchable.</p>
	 * @param expressionType 
	 * @param patternType
	 * @return whether the two types are unifyable
	 */
	public static boolean canUnifyTypes(Type expressionType, Type patternType){
		if(patternType instanceof IntegerType){
			return expressionType instanceof IntegerType;
		}
		else if(patternType instanceof BooleanType){
			return expressionType instanceof BooleanType;
		}
		else if(patternType instanceof GivenType){
			return true;
		}
		else if(patternType instanceof PowerSetType){
			if(expressionType instanceof PowerSetType){
				Type pBase = patternType.getBaseType();
				Type fBase = expressionType.getBaseType();
				return canUnifyTypes(fBase, pBase);
			}
			else{
				return false;
			}
		}
		else if(patternType instanceof ProductType){
			if(expressionType instanceof ProductType){
				Type pLeft = ((ProductType)patternType).getLeft();
				Type fLeft = ((ProductType)expressionType).getLeft();
				
				Type pRight = ((ProductType)patternType).getRight();
				Type fRight = ((ProductType)expressionType).getRight();
				
				return canUnifyTypes(fLeft, pLeft) && canUnifyTypes(fRight, pRight);
			}
			else {
				return false;
			}
		}
		// unification not possible
		return false;
	}

	/**
	 * Make sure tag is for an associative expression.<p>
	 * This method checks whether the operator is ac.
	 * @param tag
	 * @return
	 */
	public static boolean isAssociativeCommutative(int tag){
		if(tag==AssociativeExpression.BCOMP || 
				tag==AssociativeExpression.FCOMP){
			return false;
		}
		return true;
	}
	
	/**
	 * <p>Utility to check whether a given formula is an expression.</p>
	 * @param form
	 * @return whether form is an expression
	 */
	public static boolean isExpression(Formula<?> form){
		return form instanceof Expression;
	}

	/**
	 * <p>Utility to check whether the given formula is a theory formula.</p>
	 * @param form to check
	 * @return whether <code>form</code> is a theory formula
	 */
	public static boolean isTheoryFormula(Formula<?> form){
		return (form instanceof Expression) || (form instanceof Predicate);
	}
	/**
	 * <p> Merges all the lists of rules in the <code>Map</code> <code>allRules</code>.</p>
	 * @param allRules
	 * @return the merged list
	 */
	public static List<IDRewriteRule> mergeLists(Map<String, List<IDRewriteRule>> allRules ){
		List<IDRewriteRule> result = new ArrayList<IDRewriteRule>();
		for(String key : allRules.keySet()){
			result.addAll(allRules.get(key));
		}
		return result;
	}
	
	/**
	 * <p>Utility method to parse a string as a formula knowing beforehand whether it is a an expression or predicate.</p>
	 * <p>Use only for theory formulas.</p>
	 * @param formStr the formula string
	 * @param isExpression whether to parse an expression or a predicate
	 * @return the parsed formula or <code>null</code> if there was an error
	 */
	public static Formula<?> parseFormulaString(String formStr, boolean isExpression, FormulaFactory factory){
		
		Formula<?> form = null;
		if(isExpression){
			IParseResult r = factory.parseExpressionPattern(formStr, V2, null);
			form = r.getParsedExpression();
		}
		else {
			IParseResult r = factory.parsePredicatePattern(formStr, V2, null);
			form = r.getParsedPredicate();
		}
		return form;
	}
	
	// to parse a Theory formula i.e. predicate or expression
	public static Formula<?> parseFormula(String formula, FormulaFactory factory){
		IParseResult res = factory.parseExpressionPattern(formula, V2, null);
		if(res.hasProblem()){
			res = factory.parsePredicatePattern(formula, V2, null);
			if (res.hasProblem()){
				return null;
			}
			else 
				return res.getParsedPredicate();
		}
		else
			return res.getParsedExpression();
			
	}
	/**
	 * <p>Utility to print items in a list in a displayable fashion.</p>
	 * <p>The return of this method will be of the shape: {<}item0,...,itemn{>}</p>
	 * @param items
	 * @return the displayable string
	 */
	public static String printListedItems(List<String> items){
		if(items.size() == 0){
			return "";
		}
		String result = "";
		int i = 0;
		for(String str : items){
			if(i==0){
				
				result=str;
			}
			else {
				result+=","+str;
			}
			i++;
		}
		result = "<"+result+">";
		return result;
	}

	

	/**
	 * <p>Checks whether two objects are of the same class.</p>
	 * @param o1
	 * @param o2
	 * @return whether the two objects are of the same class
	 */
	public static boolean sameClass(Object o1, Object o2){
		return o1.getClass().equals(o2.getClass());
	}
	
	public static Expression makeAssociativeExpression(int tag, Expression[] exps, FormulaFactory factory){
		List<Expression> es = new ArrayList<Expression>();
		for (Expression e : exps){
			if(e != null){
				es.add(e);
			}
		}
		if(es.size() == 1)
			return es.get(0);
		else {
			return factory.makeAssociativeExpression(tag, es.toArray(new Expression[es.size()]), null);
		}
	}
}
