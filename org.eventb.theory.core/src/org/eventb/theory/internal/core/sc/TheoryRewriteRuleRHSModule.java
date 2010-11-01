package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ParsedFormula;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.ParsedRHSFormula;
import org.eventb.theory.internal.core.sc.states.RewriteRuleLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * A processor module for right hand sides of rewrite rules.
 * <p>It performs the following checks:</p>
 * <ul>
 * 	<li>if the rhs or condition are undefined, it issues an error.
 * 	<li>if the rhs or condition does not parse, it issues errors.
 * 	<li>if rhs or condition contain non-predefined identifiers, it issues errors.
 * </ul>
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryRewriteRuleRHSModule extends LabeledElementModule {

	public static final IModuleType<TheoryRewriteRuleRHSModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".theoryRewriteRuleRHSModule");

	private static String REWRITE_RULE_RHS_NAME_PREFIX = "rhs";

	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	private RuleAccuracyInfo accuracyInfo;

	private Formula<?> rhsFormulas[];
	private Predicate conditions[];
	private ILabelSymbolInfo[] symbolInfos;

	private IRewriteRuleRightHandSide ruleRhsSides[];

	private ParsedRHSFormula parsedRHS;
	private ParsedFormula parsedCondition;

	private IIdentifierSymbolTable identifierSymbolTable;

	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		accuracyInfo = (RuleAccuracyInfo) repository
				.getState(RuleAccuracyInfo.STATE_TYPE);
		ruleRhsSides = getRuleRhsElements(element);
		rhsFormulas = new Formula<?>[ruleRhsSides.length];
		conditions = new Predicate[ruleRhsSides.length];
		symbolInfos = new ILabelSymbolInfo[ruleRhsSides.length];
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
	}

	
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		createStates(repository);
		checkAndType(element.getParent().getElementName(), repository, monitor);
		createSCRuleRhs(target, REWRITE_RULE_RHS_NAME_PREFIX,
				CoreUtilities.SC_STARTING_INDEX, monitor);
		removeStates(repository);
	}

	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		ruleRhsSides = null;
		conditions = null;
		rhsFormulas = null;
		symbolInfos = null;
		accuracyInfo = null;
		typeEnvironment = null;
		factory = null;
		identifierSymbolTable = null;
		super.endModule(element, repository, monitor);
	}

	
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRHS(symbol, true,
				element, component);
	}

	
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository
				.getState(RewriteRuleLabelSymbolTable.STATE_TYPE);
	}

	// utilities

	private void createStates(ISCStateRepository repository)
			throws CoreException {
		parsedRHS = new ParsedRHSFormula();
		repository.setState(parsedRHS);
		parsedCondition = new ParsedFormula();
		repository.setState(parsedCondition);
	}

	private void removeStates(ISCStateRepository repository)
			throws CoreException {
		repository.removeState(IParsedFormula.STATE_TYPE);
		repository.removeState(ParsedRHSFormula.STATE_TYPE);
	}

	private IRewriteRuleRightHandSide[] getRuleRhsElements(IRodinElement element)
			throws CoreException {
		IRewriteRule rule = (IRewriteRule) element;
		return rule.getRuleRHSs();
	}

	// ///////////////////////////////////////////////////////////////
	// / The method for checking and typing rhs and cond. ///
	// / ///
	// ///////////////////////////////////////////////////////////////
	private void checkAndType(String component, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		initFilterModules(repository, null);
		for (int i = 0; i < ruleRhsSides.length; i++) {
			IRewriteRuleRightHandSide ruleRhsElement = ruleRhsSides[i];
			ILabelSymbolInfo symbolInfo = fetchLabel(ruleRhsElement, component,
					null);
			Formula<?> rhsSide = parseFormula(ruleRhsElement, factory,
					repository, true);
			rhsFormulas[i] = rhsSide;
			Predicate condition = (Predicate) parseFormula(ruleRhsElement,
					factory, repository, false);
			conditions[i] = condition;

			boolean ok = (rhsSide != null && condition != null);
			if (ok) {
				parsedCondition.setFormula(condition);
				parsedRHS.setRHSFormula(rhsSide);
				ok = symbolInfo != null;
				if (ok && !filterModules(ruleRhsElement, repository, null)) {
					ok = false;
				}
			}
			if (!ok) {
				if (symbolInfo != null)
					symbolInfo.setError();
				rhsFormulas[i] = null;
				conditions[i] = null;
				if (accuracyInfo != null) {
					accuracyInfo.setNotAccurate();
				}
			}
			symbolInfos[i] = symbolInfo;
			setImmutable(symbolInfo);
			monitor.worked(1);
		}
		endFilterModules(repository, null);
	}

	private Formula<?> parseFormula(IRewriteRuleRightHandSide rhsSide,
			FormulaFactory factory, ISCStateRepository repository, boolean isRHS)
			throws CoreException {
		IAttributeType.String attributeType = isRHS ? TheoryAttributes.FORMULA_ATTRIBUTE
				: EventBAttributes.PREDICATE_ATTRIBUTE;
		String string = null;
		Formula<?> finalForm = null;
		IParseResult expResult = null;
		IParseResult predResult = null;
		boolean isExpression = true;
		if (isRHS) {
			if (!rhsSide.hasFormula()) {
				createProblemMarker(rhsSide, attributeType,
						TheoryGraphProblem.RHSUndefError);
				return null;
			}
			string = rhsSide.getFormula();

			expResult = factory.parseExpressionPattern(string, LanguageVersion.V2,
					rhsSide);
			if (expResult.hasProblem()) {
				isExpression = false;
				predResult = factory.parsePredicatePattern(string, LanguageVersion.V2,
						rhsSide);
				if (CoreUtilities.issueASTProblemMarkers(rhsSide, attributeType,
						predResult, this)) {
					// TODO only error messages of predicate are issued.
					return null;
				}
			}
		} else {
			if (!rhsSide.hasPredicateString()) {
				createProblemMarker(rhsSide, attributeType,
						TheoryGraphProblem.CondUndefError);
				return null;
			} else {
				string = rhsSide.getPredicateString();
				predResult = factory.parsePredicatePattern(string, LanguageVersion.V2,
						rhsSide);
				if (CoreUtilities.issueASTProblemMarkers(rhsSide, attributeType,
						predResult, this)) {
					return null;
				}
			}
		}
		if (isRHS) {
			if (isExpression) {
				finalForm = expResult.getParsedExpression();
			} else {
				finalForm = predResult.getParsedPredicate();
			}
		} else {
			finalForm = predResult.getParsedPredicate();
		}
		// FreeIdents module emulator
		if (!accept(rhsSide, repository, finalForm, attributeType, null)) {
			return null;
		}
		ITypeCheckResult tcResult = finalForm.typeCheck(typeEnvironment);
		if (CoreUtilities.issueASTProblemMarkers(rhsSide, attributeType,
				tcResult, this)) {
			return null;
		}
		return finalForm;
	}

	public boolean accept(IRodinElement element, ISCStateRepository repository,
			Formula<?> form, IAttributeType.String type,
			IProgressMonitor monitor) throws CoreException {
		boolean ok = true;
		IInternalElement internalElement = (IInternalElement) element;
		FreeIdentifier[] condIdents = form.getFreeIdentifiers();
		for (FreeIdentifier freeIdentifier : condIdents) {
			IIdentifierSymbolInfo symbolInfo = getSymbolInfo(internalElement,
					type, freeIdentifier, monitor);
			if (symbolInfo == null) {
				ok = false;
			}
		}

		return ok;
	}

	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			IAttributeType.String type, FreeIdentifier freeIdentifier,
			IProgressMonitor monitor) throws CoreException {
		IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
				.getSymbolInfo(freeIdentifier.getName());
		if (symbolInfo == null) {
			createProblemMarker(element, type, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.UndeclaredFreeIdentifierError, freeIdentifier
							.getName());
		} else if (symbolInfo.hasError()) {
			createProblemMarker(element, type, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.FreeIdentifierFaultyDeclError, freeIdentifier
							.getName());
			symbolInfo = null;
		}
		return symbolInfo;
	}

	protected void setImmutable(ILabelSymbolInfo symbolInfo) {
		if (symbolInfo != null)
			symbolInfo.makeImmutable();
	}

	private final int createSCRuleRhs(IInternalElement target,
			String namePrefix, int index, IProgressMonitor monitor)
			throws CoreException {
		int k = index;
		for (int i = 0; i < rhsFormulas.length; i++) {
			if (rhsFormulas[i] == null || conditions[i] == null)
				continue;
			ISCRewriteRuleRightHandSide rhs = (ISCRewriteRuleRightHandSide) symbolInfos[i]
					.createSCElement(target, namePrefix + k++, monitor);
			rhs.setPredicate(conditions[i], monitor);
			rhs.setSCFormula(rhsFormulas[i], monitor);
		}
		return k;
	}

}