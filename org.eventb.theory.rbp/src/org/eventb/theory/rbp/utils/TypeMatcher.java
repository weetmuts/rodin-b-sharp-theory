package org.eventb.theory.rbp.utils;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.theory.rbp.engine.IBinding;

public class TypeMatcher {

	/**
	 * Checks whether two types (a pattern and an instance) can be considered as matchable.
	 * <p>
	 * If the two types are matchable, the binding will be augmented with any infered information.
	 * @param expressionType the type of the instance
	 * @param patternType the type of the pattern
	 * @param binding the binding to work with
	 * @return whether the two types are unifyable
	 */
	public static boolean canUnifyTypes(Type expressionType, Type patternType,
			IBinding binding) {
		FormulaFactory factory = binding.getFormulaFactory();
		if (patternType instanceof IntegerType) {
			return expressionType instanceof IntegerType;
		} else if (patternType instanceof BooleanType) {
			return expressionType instanceof BooleanType;
		} else if (patternType instanceof GivenType) {
			binding.putTypeMapping(factory.makeFreeIdentifier(
					((GivenType) patternType).getName(), null, patternType
							.toExpression(factory).getType()), expressionType);
			return true;
		} else if (patternType instanceof PowerSetType) {
			if (expressionType instanceof PowerSetType) {
				Type pBase = patternType.getBaseType();
				Type fBase = expressionType.getBaseType();
				return canUnifyTypes(fBase, pBase, binding);
			}
		} else if (patternType instanceof ProductType) {
			if (expressionType instanceof ProductType) {
				Type pLeft = ((ProductType) patternType).getLeft();
				Type fLeft = ((ProductType) expressionType).getLeft();

				Type pRight = ((ProductType) patternType).getRight();
				Type fRight = ((ProductType) expressionType).getRight();

				return canUnifyTypes(fLeft, pLeft, binding)
						&& canUnifyTypes(fRight, pRight, binding);
			}
		} else if (patternType instanceof ParametricType) {
			if (expressionType instanceof ParametricType) {

				ParametricType patParametricType = (ParametricType) patternType;
				ParametricType expParametricType = (ParametricType) expressionType;
				if (!patParametricType.getExprExtension().equals(
						expParametricType)) {
					return false;
				}
				Type[] patTypes = patParametricType.getTypeParameters();
				Type[] expTypes = expParametricType.getTypeParameters();
				boolean ok = true;
				for (int i = 0; i < patTypes.length; i++) {
					ok &= canUnifyTypes(expTypes[i], patTypes[i],
							binding);
					if (!ok) {
						return false;
					}
				}
				return true;
			}
		}
		// unification not possible
		return false;
	}
}
