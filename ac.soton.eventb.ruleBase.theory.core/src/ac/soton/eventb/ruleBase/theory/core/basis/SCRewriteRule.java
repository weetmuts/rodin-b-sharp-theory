package ac.soton.eventb.ruleBase.theory.core.basis;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.TheoryAttributes;
import ac.soton.eventb.ruleBase.theory.core.TheoryElement;
import ac.soton.eventb.ruleBase.theory.core.utils.Messages;
import ac.soton.eventb.ruleBase.theory.core.utils.TheoryUtils;

/**
 * @author maamria
 * 
 */
public class SCRewriteRule extends TheoryElement implements ISCRewriteRule{

	public SCRewriteRule(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCRewriteRule> getElementType() {
		return ISCRewriteRule.ELEMENT_TYPE;
	}

	public ISCRewriteRuleRightHandSide getSCRuleRHS(String name) {
		return getInternalElement(ISCRewriteRuleRightHandSide.ELEMENT_TYPE, name);
	}

	public ISCRewriteRuleRightHandSide[] getSCRuleRHSs() throws RodinDBException {
		return getChildrenOfType(ISCRewriteRuleRightHandSide.ELEMENT_TYPE);
	}

	public Formula<?> getLHSFormula(FormulaFactory factory,
			ITypeEnvironment typenv) throws RodinDBException {
		Formula<?> result = getFormula(factory);
		ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (!tcResult.isSuccess()) {
			throw TheoryUtils.newRodinDBException(
					Messages.database_SCFormulaTCFailure, this);
		}
		assert result.isTypeChecked();
		return result;
	}


	public void setLHSFormula(Formula<?> form, IProgressMonitor monitor)
	throws RodinDBException {
		setAttributeValue(TheoryAttributes.LHS_ATTRIBUTE, form.toStringWithTypes(), monitor);
	}
	
	// Utility methods
	private Formula<?> getFormula(FormulaFactory factory)
			throws RodinDBException {
		String contents = null;
		boolean isExpression = true;
		contents = getLHSString();
		IParseResult parserResult = factory.parseExpressionPattern(contents, V2, null);
		if (parserResult.getProblems().size() != 0) {
			isExpression = false;
			parserResult = factory.parsePredicatePattern(contents, V2, null);
			// If neither expression nor predicate
			if (parserResult.getProblems().size() != 0) {
				throw TheoryUtils.newRodinDBException(
						Messages.database_SCFormulaParseFailure, this);
			}
		}
		Formula<?> result;
		if (isExpression) {
			result = parserResult.getParsedExpression();
		} else {
			result = parserResult.getParsedPredicate();
		}

		return result;
	}

	
	
}
