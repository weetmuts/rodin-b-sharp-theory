package org.eventb.theory.core.sc;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.rodinp.core.IRodinProblem;

import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 * 
 */
public enum TheoryGraphProblem implements IRodinProblem {
		TheoryTypeParameterNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoryTypeParameterNameConflict), 
		TheoryTypeParameterNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_TheoryTypeParameterNameConflict),
		OperatorArgumentNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorArgumentNameConflict), 
		OperatorHasMoreThan1DirectDefError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorHasMoreThan1DirectDefError), 
		OperatorArgumentNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OperatorArgumentNameConflict),
		OperatorCannotBeCommutWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OperatorCannotBeCommutWarning),
		OperatorCannotBeAssosWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OperatorCannotBeAssosWarning),
		OpCannotReferToTheseTypes(IMarker.SEVERITY_ERROR, Messages.scuser_OpCannotReferToTheseTypes),
		UntypedTheoryTypeParameterError(IMarker.SEVERITY_ERROR,Messages.scuser_UntypedTypeParameterError), 
		UntypedOperatorArgumentError(IMarker.SEVERITY_ERROR,Messages.scuser_UntypedOperatorArgumentError), 
		DatatypeNameAlreadyATypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_DatatypeNameAlreadyATypeParError),
		TypeArgMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_TypeArgMissingError),
		TypeArgNotDefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_TypeArgNotDefinedError),
		TypeArgRedundWarn(IMarker.SEVERITY_WARNING, Messages.scuser_TypeArgRedundWarn),
		DatatypeHasNoConsError(IMarker.SEVERITY_ERROR, Messages.scuser_DatatypeHasNoConsError),
		DatatypeHasNoBaseConsError(IMarker.SEVERITY_ERROR, Messages.scuser_DatatypeHasNoBaseConsError),
		ConstructorNameAlreadyATypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_ConstructorNameAlreadyATypeParError),
		DestructorNameAlreadyATypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_DestructorNameAlreadyATypeParError),
		MissingDestructorNameError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingDestructorNameError),
		MissingDestructorTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingDestructorTypeError),
		MissingConstructorNameError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingConstructorNameError),
		MissingDatatypeNameError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingDatatypeNameError),
		TypeIsNotRefTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_TypeIsNotRefTypeError),
		IdentIsNotTypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_IdentIsNotTypeParError),
		IdenIsADatatypeNameError(IMarker.SEVERITY_ERROR, Messages.scuser_IdenIsADatatypeNameError),
		IdenIsAConsNameError(IMarker.SEVERITY_ERROR, Messages.scuser_IdenIsAConsNameError),
		IdenIsADesNameError(IMarker.SEVERITY_ERROR, Messages.scuser_IdenIsADesNameError),
		MissingOpLabelIDError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingOpLabelIDError),
		OperatorIDConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OperatorIDConflictWarning), 
		OperatorIDConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorIDConflictError), 
		OperatorIDExistsError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorIDExistsError), 
		OperatorSynMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorSynMissingError), 
		OperatorSynExistsError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorSynExistsError), 
		OperatorSynIsATypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorSynIsATypeParError), 
		OperatorFormTypeMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorFormTypeMissingError),
		OperatorNotationTypeMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorNotationTypeMissingError),
		OperatorAssocMissingWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OperatorAssocMissingWarning),
		OperatorCommutMissingWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OperatorCommutMissingWarning),
		TypeAttrMissingForOpArgError(IMarker.SEVERITY_ERROR, Messages.scuser_TypeAttrMissingForOpArgError),
		WDPredUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_WDPredUndefError), 
		MissingFormulaAttrError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingFormulaAttrError),
		OperatorNoDirectDefError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorNoDirectDefError),
		OperatorDefNotExpError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorDefNotExpError),
		OperatorDefNotPredError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorDefNotPredError),
			AutoUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AutoUndefWarning), 
			InterUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InterUndefWarning),
			ToolTipNotSupplied(IMarker.SEVERITY_INFO, Messages.scuser_ToolTipNotSupplied),
			DescNotSupplied(IMarker.SEVERITY_INFO, Messages.scuser_DescNotSupplied),
			CompleteUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CompleteUndefWarning),
			LhsAndRhsNotSynClassMatching(IMarker.SEVERITY_ERROR,Messages.scuser_LhsAndRhsNotSynClassMatching),
			LHSUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_LHSUndef), 
			RHSIdentsNotSubsetOfLHSIdents(IMarker.SEVERITY_ERROR,Messages.scuser_RHSIdentsNotSubsetOfLHSIdents), 
			RHSUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_RHSUndef),
			RuleSideNotTheoryFormula(IMarker.SEVERITY_ERROR, Messages.scuser_RuleSideNotTheoryFormula), 
			RuleTypeMismatchError(IMarker.SEVERITY_ERROR, Messages.scuser_RuleTypeMismatch), 
			TheoryLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_RewriteRuleLabelConflict), 
			TheoryLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_RewriteRuleLabelConflict), 
			
			RhsLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_RhsLabelConflict),
			RhsLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_RhsLabelConflict),
			CondUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_CondUndef),
			CondAttrUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CondAttrUndef),
			RuleNoRhsError(IMarker.SEVERITY_ERROR, Messages.scuser_RuleWithNoRHSs),
			NoToolTipWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoToolTipWarning),
			NoRuleDescWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoRuleDescWarning), 
			LHSIsIdentErr(IMarker.SEVERITY_ERROR, Messages.scuser_LHSIsIdentErr),
			RHSPredVarsNOTSubsetOFLHS(IMarker.SEVERITY_ERROR, Messages.scuser_RHSPredVarsNOTSubsetOFLHS), 
			
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

	
	public String getErrorCode() {
		return errorCode;
	}

	
	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

	
	public int getSeverity() {
		return severity;
	}

	public static TheoryGraphProblem valueOfErrorCode(String errorCode) {
		String instName = errorCode.substring(errorCode.lastIndexOf('.') + 1);
		return valueOf(instName);
	}
}
