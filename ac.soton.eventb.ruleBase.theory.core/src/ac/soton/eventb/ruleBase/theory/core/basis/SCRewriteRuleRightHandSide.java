package ac.soton.eventb.ruleBase.theory.core.basis;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.basis.SCPredicateElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.TheoryAttributes;
import ac.soton.eventb.ruleBase.theory.core.utils.Messages;
import ac.soton.eventb.ruleBase.theory.core.utils.TheoryUtils;

public class SCRewriteRuleRightHandSide extends SCPredicateElement implements ISCRewriteRuleRightHandSide{

	public SCRewriteRuleRightHandSide(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	public Formula<?> getRHSFormula(FormulaFactory factory,
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

	public String getRHSString() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.RHS_ATTRIBUTE);
	}
	

	
	public void setRHSFormula(Formula<?> form, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.RHS_ATTRIBUTE, form.toStringWithTypes(), monitor);
		
	}
	
	// Utility methods
	private Formula<?> getFormula(FormulaFactory factory)
			throws RodinDBException {
		String contents = null;
		boolean isExpression = true;
		contents = getRHSString();
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
