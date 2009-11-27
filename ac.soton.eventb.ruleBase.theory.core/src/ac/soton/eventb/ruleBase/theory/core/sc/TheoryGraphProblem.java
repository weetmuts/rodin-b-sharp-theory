package ac.soton.eventb.ruleBase.theory.core.sc;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.rodinp.core.IRodinProblem;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 * 
 */
public enum TheoryGraphProblem implements IRodinProblem {
			AutoUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AutoUndefWarning), 
			InterUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InterUndefWarning),
			ToolTipNotSupplied(IMarker.SEVERITY_INFO, Messages.scuser_ToolTipNotSupplied),
			DescNotSupplied(IMarker.SEVERITY_INFO, Messages.scuser_DescNotSupplied),
			CompleteUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CompleteUndefWarning),
			AxiomLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomLabelConflict), 
			AxiomLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AxiomLabelConflict), 
			LhsAndRhsNotSynClassMatching(IMarker.SEVERITY_ERROR,Messages.scuser_LhsAndRhsNotSynClassMatching),
			LHSUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_LHSUndef), 
			RHSIdentsNotSubsetOfLHSIdents(IMarker.SEVERITY_ERROR,Messages.scuser_RHSIdentsNotSubsetOfLHSIdents), 
			RHSUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_RHSUndef),
			RuleSideNotTheoryFormula(IMarker.SEVERITY_ERROR, Messages.scuser_RuleSideNotTheoryFormula), 
			RuleTypeMismatchError(IMarker.SEVERITY_ERROR, Messages.scuser_RuleTypeMismatch), 
			TheoryLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_RewriteRuleLabelConflict), 
			TheoryLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_RewriteRuleLabelConflict), 
			TheorySetNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_TheorySetNameConflict), 
			TheorySetNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_TheorySetNameConflict), 
			TheoryVariableNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableNameConflict), 
			TheoryVariableNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_VariableNameConflict), 
			UntypedTheorySetError(IMarker.SEVERITY_ERROR,Messages.scuser_UntypedTheorySetError), 
			UntypedTheoryVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedVariableError),
			RhsLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_RhsLabelConflict),
			RhsLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_RhsLabelConflict),
			CondUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_CondUndef),
			UncondRuleManyNoneRhsWarning(IMarker.SEVERITY_WARNING, Messages.scuser_UncondManyNoneRhs),
			UncondRuleWithNonTrueCond(IMarker.SEVERITY_WARNING, Messages.scuser_UncondRuleWithNonTrueCond),
			CondAttrUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CondAttrUndef),
			RuleNoRhsError(IMarker.SEVERITY_ERROR, Messages.scuser_RuleWithNoRHSs),
			PredicateNotTypingPredError(IMarker.SEVERITY_ERROR, Messages.scuser_PredicateNotTypingPredError),
			CategoryNotPredefined(IMarker.SEVERITY_ERROR, Messages.scuser_CategoryNotPredefines),
			DuplicateCategoryWarning(IMarker.SEVERITY_WARNING, Messages.scuser_DuplicateCategory),
			NoToolTipWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoToolTipWarning),
			NoRuleDescWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoRuleDescWarning), 
			LHSIsIdentErr(IMarker.SEVERITY_ERROR, Messages.scuser_LHSIsIdentErr)
			;

	private int arity;

	private final String errorCode;

	private final String message;

	private final int severity;

	private TheoryGraphProblem(int severity, String message) {
		this.severity = severity;
		this.message = message;
		this.errorCode = TheoryPlugin.PLUGIN_ID + "." + name();
		arity = -1;
	}

	/**
	 * Returns the number of parameters needed by the message of this problem,
	 * i.e. the length of the object array to be passed to
	 * <code>getLocalizedMessage()</code>.
	 * 
	 * @return the number of parameters needed by the message of this problem
	 */
	public int getArity() {
		if (arity == -1) {
			MessageFormat mf = new MessageFormat(message);
			arity = mf.getFormatsByArgumentIndex().length;
		}
		return arity;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

	@Override
	public int getSeverity() {
		return severity;
	}

	public static TheoryGraphProblem valueOfErrorCode(String errorCode) {
		String instName = errorCode.substring(errorCode.lastIndexOf('.') + 1);
		return valueOf(instName);
	}
}
