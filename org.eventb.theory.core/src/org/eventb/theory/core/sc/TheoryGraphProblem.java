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
			TheoryMetaVarNameConflict(IMarker.SEVERITY_ERROR,
			Messages.scuser_TheoryMetaVarNameConflict), 
			TheoryTypeParameterNameConflictError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_TheoryTypeParameterNameConflict), 
			TheoryTypeParameterNameConflictWarning(
			IMarker.SEVERITY_WARNING,
			Messages.scuser_TheoryTypeParameterNameConflict), 
			OperatorArgumentNameConflictError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorArgumentNameConflict), 
			OperatorHasMoreThan1DefError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorHasMoreThan1DefError), 
			OperatorArgumentNameConflictWarning(
			IMarker.SEVERITY_WARNING,
			Messages.scuser_OperatorArgumentNameConflict), 
			OperatorCannotBeCommutError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorCannotBeCommutError), 
			OperatorCannotBeAssosWarning(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorCannotBeAssosError), 
			OperatorWithSameSynJustBeenAdded(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorWithSameSynJustBeenAdded), 
			OpCannotReferToTheseTypes(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OpCannotReferToTheseTypes), 
			UntypedTheoryTypeParameterError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_UntypedTypeParameterError), 
			UntypedOperatorArgumentError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_UntypedOperatorArgumentError), 
			DatatypeNameAlreadyATypeParError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_DatatypeNameAlreadyATypeParError), 
			TypeArgMissingError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeArgMissingError), 
			TypeArgNotDefinedError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeArgNotDefinedError), 
			TypeArgRedundWarn(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_TypeArgRedundWarn), 
			DatatypeHasNoConsError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_DatatypeHasNoConsError), 
			DatatypeHasNoBaseConsError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_DatatypeHasNoBaseConsError), 
			ConstructorNameAlreadyATypeParError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_ConstructorNameAlreadyATypeParError), 
			DestructorNameAlreadyATypeParError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_DestructorNameAlreadyATypeParError), 
			MissingDestructorNameError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MissingDestructorNameError), 
			MissingDestructorTypeError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MissingDestructorTypeError), 
			MissingConstructorNameError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MissingConstructorNameError), 
			MissingDatatypeNameError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MissingDatatypeNameError), 
			TypeIsNotRefTypeError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeIsNotRefTypeError), 
			IdentIsNotTypeParError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_IdentIsNotTypeParError), 
			IdenIsADatatypeNameError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_IdenIsADatatypeNameError), 
			IdenIsAConsNameError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_IdenIsAConsNameError), 
			IdenIsADesNameError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_IdenIsADesNameError), 
			MissingOpLabelIDError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MissingOpLabelIDError), 
			OperatorIDConflictWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_OperatorIDConflictWarning), 
			OperatorIDConflictError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorIDConflictError), 
			OperatorIDExistsError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorIDExistsError), 
			OperatorSynMissingError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorSynMissingError), 
			OperatorSynExistsError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorSynExistsError), 
			OperatorSynIsATypeParError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorSynIsATypeParError), 
			OperatorFormTypeMissingError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorFormTypeMissingError), 
			OperatorNotationTypeMissingError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorNotationTypeMissingError), 
			OperatorAssocMissingError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorAssocMissingWarning), 
			OperatorCommutMissingError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorCommutMissingWarning), 
			TypeAttrMissingForOpArgError(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_TypeAttrMissingForOpArgError), 
			WDPredUndefError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_WDPredUndefError), 
			MissingFormulaAttrError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MissingFormulaAttrError), 
			OperatorNoDefError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorNoDefError), 
			OperatorDefNotExpError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorDefNotExpError), 
			OperatorDefNotPredError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorDefNotPredError), 
			OperatorInvalidSynError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorInvalidSynError), 
			RulesBlockLabelProblemError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RulesBlockLabelProblemError), 
			RulesBlockLabelProblemWarning(
			IMarker.SEVERITY_WARNING,
			Messages.scuser_RulesBlockLabelProblemWarning), 
			TheoremPredMissingError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TheoremPredMissingError), 
			TheoremLabelProblemError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TheoremLabelProblemError), 
			TheoremLabelProblemWarning(
			IMarker.SEVERITY_WARNING,
			Messages.scuser_TheoremLabelProblemWarning), 
			AutoUndefWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_AutoUndefWarning), 
			RedundantImportWarn(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_RedundantImportWarn),
			ImportTheoryAttrMissing( 
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TheoryInImportMissing),
			ImportTheoryNotExist( 
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_ImportTheoryNotExist), 
			InferenceGivenBTRUEPredWarn(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_InferenceGivenBTRUEPredWarn),
			InferenceInferBTRUEPredErr(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InferenceInferBTRUEPredErr),
			UntypedMetavariableError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_UntypedMetavariableError), 
			InterUndefWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_InterUndefWarning), 
			ToolTipNotSupplied(
			IMarker.SEVERITY_INFO, 
			Messages.scuser_ToolTipNotSupplied), 
			DescNotSupplied(
			IMarker.SEVERITY_INFO, 
			Messages.scuser_DescNotSupplied), 
			CompleteUndefWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_CompleteUndefWarning), 
			LhsAndRhsNotSynClassMatching(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_LhsAndRhsNotSynClassMatching), 
			LHSUndefError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_LHSUndef),
			RHSGivensNotSubsetOfLHSGivens(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_RHSGivensNotSubsetOfLHSGivens),
			CondGivensNotSubsetOfLHSGivens(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_CondGivensNotSubsetOfLHSGivens),
			CondIdentsNotSubsetOfLHSIdents(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_CondIdentsNotSubsetOfLHSIdents), 
			RHSIdentsNotSubsetOfLHSIdents(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_RHSIdentsNotSubsetOfLHSIdents), 
			RHSUndefError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RHSUndef), 
			RuleSideNotTheoryFormula(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RuleSideNotTheoryFormula), 
			RuleTypeMismatchError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RuleTypeMismatch), 
			TheoryLabelConflictError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RewriteRuleLabelConflict), 
			TheoryLabelConflictWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_RewriteRuleLabelConflict),
			InferenceRuleLabelConflictError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InferenceRuleLabelConflict), 
			InferenceRuleLabelConflictWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_InferenceRuleLabelConflict),
			OperatorExpPrefixCannotBeAssos(IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorExpPrefixCannotBeAssos),
			OperatorPredOnlyPrefix(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorPredOnlyPrefix), 
			OperatorExpCannotBePostfix(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorExpCannotBePostfix),
			OperatorPredNeedOneOrMoreArgs(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorPredNeedOneOrMoreArgs), 
			OperatorPredCannotBeAssos(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorPredCannotBeAssos), 
			OperatorExpInfixNeedsAtLeastTwoArgs(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_OperatorExpInfixNeedsAtLeastTwoArgs), 
			RhsLabelConflictError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RhsLabelConflict), 
			RhsLabelConflictWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_RhsLabelConflict), 
			CondUndefError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_CondUndef), 
			CondAttrUndefWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_CondAttrUndef), 
			RuleNoRhsError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RuleWithNoRHSs), 
			RuleNoInfersError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RuleWithNoInfers),
			NoToolTipWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_NoToolTipWarning), 
			NoRuleDescWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_NoRuleDescWarning), 
			LHSIsIdentErr(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_LHSIsIdentErr), 
			RHSPredVarsNOTSubsetOFLHS(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RHSPredVarsNOTSubsetOFLHS), 
			NonTypeParOccurError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_NonTypeParOccurError),
			InferenceRuleNotApplicableError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InferenceRuleNotApplicableError),
			InferenceRuleBackward(
			IMarker.SEVERITY_INFO, 
			Messages.scuser_InferenceRuleBackward),
			InferenceRuleForward(
			IMarker.SEVERITY_INFO, 
			Messages.scuser_InferenceRuleForward),
			InferenceRuleBoth(
			IMarker.SEVERITY_INFO, 
			Messages.scuser_InferenceRuleBoth),
			LHS_IsNotWDStrict(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_LHS_IsNotWDStrict), 
			ImportDepCircularity(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_ImportDepCircularity),
			IndRedundantImportWarn(
			IMarker.SEVERITY_WARNING,
			Messages.scuser_IndRedundantImportWarn),
			ImportConflict(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_ImportConflict), 
			ArgumentNotExistOrNotParametric(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_ArgumentNotExistOrNotParametric), 
			InductiveCaseMissing(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_InductiveCaseMissing), 
			ExprIsNotDatatypeConstr(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_ExprIsNotDatatypeConstr), 
			ConstrAlreadyCovered(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_ConstrAlreadyCovered), 
			ExprNotApproInductiveCase(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_ExprNotApproInductiveCase), 
			ConstrArgumentNotIdentifier(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_ConstrArgumentNotIdentifier), 
			OperatorCannotBePostfix(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_OperatorCannotBePostfix), 
			InductiveArgMissing(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InductiveArgMissing), 
			NoRecCasesError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_NoRecCasesError), 
			InductiveCaseNotAppropriateExp(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InductiveCaseNotAppropriateExp), 
			ConsArgNotIdentInCase(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_ConsArgNotIdentInCase), 
			IdentCannotBeUsedAsConsArg(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_IdentCannotBeUsedAsConsArg), 
			UnableToTypeCase(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_UnableToTypeCase), 
			RecCaseAlreadyCovered(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_RecCaseAlreadyCovered), 
			TypeMissmatchOfRecDef(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeMissmatchOfRecDef), 
			NoCoverageAllRecCase(
			IMarker.SEVERITY_ERROR,
			Messages.scuser_NoCoverageAllRecCase), 
			RecOpTypeNotConsistent(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_RecOpTypeNotConsistent), 
			OpArgExprNotSet(
					IMarker.SEVERITY_ERROR, 
					Messages.scuser_OpArgExprNotSet)
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
