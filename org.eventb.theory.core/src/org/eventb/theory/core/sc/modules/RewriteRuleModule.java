package org.eventb.theory.core.sc.modules;

import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ParsedFormula;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.RewriteRuleLabelSymbolTable;
import org.eventb.theory.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

/**
 * A processor module for rewrite rules. It sets the accuracy of processed rules.
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class RewriteRuleModule extends RuleModule<IRewriteRule, ISCRewriteRule>{

	public static final IModuleType<RewriteRuleModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".rewriteRuleModule");
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRewriteRule(symbol,
				true, element, component);
	}
	
	protected IRewriteRule[] getRuleElements(IRodinElement element)
			throws CoreException {
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		return rulesBlock.getRewriteRules();
	}
	
	protected ILabelSymbolInfo[] fetchRules(IRewriteRule[] rules, String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean accurate = true;
		ILabelSymbolInfo[] symbolInfos = new ILabelSymbolInfo[rules.length];
		initFilterModules(repository, monitor);
		for (int i = 0; i < rules.length; i++) {
			symbolInfos[i] = fetchLabel(rules[i], theoryName, monitor);
			if (symbolInfos[i] == null){
				accurate = false;
				continue;
			}
			if (!filterModules(rules[i], repository, monitor)) {
				symbolInfos[i].setError();
				accurate = false;
			}
		}
		endFilterModules(repository, monitor);
		if(!accurate)
			accuracyInfo.setNotAccurate();
		return symbolInfos;
	}
	
	protected void processRules(IRewriteRule[] rules, ISCRewriteRule[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			IRewriteRule rule = rules[i];
			if (infos[i] != null && !infos[i].hasError()) {
				Formula<?> lhsForm = checkLeftHandSide(rule, infos[i], repository, monitor);
				boolean ok = (lhsForm != null);
				if (ok) {
					if(scRules[i] != null){
						scRules[i].setSCFormula(lhsForm, monitor);
						// states
						RewriteRuleLabelSymbolTable labelTable = new RewriteRuleLabelSymbolTable(ModulesUtils.LABEL_SYMTAB_SIZE);
						repository.setState(labelTable);
						RuleAccuracyInfo ruleAccuracyInfo = new RuleAccuracyInfo();
						repository.setState(ruleAccuracyInfo);
						ParsedFormula lhsParsedFormula = new ParsedFormula();
						lhsParsedFormula.setFormula(lhsForm);
						repository.setState(lhsParsedFormula);
						// call child processors
						initProcessorModules(rule, repository, null);
						processModules(rule, scRules[i], repository, monitor);
						endProcessorModules(rule, repository, null);
						// accuracy
						scRules[i].setAccuracy(ruleAccuracyInfo.isAccurate(), monitor);
						if(!ruleAccuracyInfo.isAccurate()){
							accuracyInfo.setNotAccurate();
						}
					}
					else {
						ok = false;
					}
				}
				if (!ok) {
					infos[i].setError();
					if(scRules[i] != null)
						scRules[i].setAccuracy(false, monitor);
					accuracyInfo.setNotAccurate();
				}	
			} else{
				accuracyInfo.setNotAccurate();
			}
		}
		monitor.worked(1);
	}

	@Override
	protected String getMessage() {
		return Messages.progress_TheoryRewriteRules;
	}

	@Override
	protected ISCRewriteRule[] createSCRulesArray(int length) {
		return new ISCRewriteRule[length];
	}

	@Override
	protected ISCRewriteRule getSCRule(ILabeledElement scRule) {
		return (ISCRewriteRule) scRule;
	}

	@Override
	protected ILabelSymbolInfo makeLocalRule(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRewriteRule(symbol, true, element, component);
	}
	
	private Formula<?> checkLeftHandSide(IRewriteRule rule, ILabelSymbolInfo symbolInfo,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		// lhs formula is set
		if (!rule.hasFormula()) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSUndefError);
			return null;
		}
		// parse the lhs
		Formula<?> lhsForm = ModulesUtils.parseFormula(rule, repository.getFormulaFactory(), this);
		if (lhsForm == null) {
			return null;
		}
		lhsForm = ModulesUtils.checkFormula(rule, lhsForm, repository.getTypeEnvironment(), this);
		if(lhsForm == null){
			return null;
		}
		// lhs is a free identifier or predicate variable
		if (lhsForm instanceof FreeIdentifier) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSIsIdentErr);
			return null;
		}
		// lhs does not contain structured predicates
		WDStrictChecker checker = new WDStrictChecker();
		lhsForm.accept(checker);
		if (!checker.wdStrict) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHS_IsNotWDStrict);
			return null;
		}
		// final check against type parameters
		if (!CoreUtilities.checkAgainstTypeParameters(rule, lhsForm,
				repository.getTypeEnvironment(), this)) {
			return null;
		}
		return lhsForm;
	}
	
	static final class WDStrictChecker extends DefaultVisitor {

		private boolean wdStrict = true;

		/**
		 * Returns whether the check reached the conclusion that the visited
		 * formula may not be WD strict.
		 * 
		 * @return whether the visited formula is WD strict
		 */
		public boolean isWdStrict() {
			return wdStrict;
		}

		@Override
		public boolean enterEXISTS(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterFORALL(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLAND(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLOR(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLIMP(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLEQV(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}

	}
	
}
