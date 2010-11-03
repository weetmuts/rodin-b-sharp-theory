package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.sc.states.FilteredLHSs;
import org.eventb.theory.internal.core.sc.states.ParsedLHSFormula;
import org.eventb.theory.internal.core.sc.states.RewriteRuleLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IRodinElement;

/**
 * A processor module for rewrite rules. It sets the accuracy of processed rules.
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryRewriteRuleModule extends TheoryRuleModule<IRewriteRule, ISCRewriteRule>{

	public static final IModuleType<TheoryRewriteRuleModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryRewriteRuleModule");

	
	private FilteredLHSs filteredLHSs;
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		filteredLHSs = new FilteredLHSs();

	}
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		filteredLHSs = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRewriteRule(symbol,
				true, element, component);
	}
	
	// Utilities
	
	protected IRewriteRule[] getRuleElements(IRodinElement element)
			throws CoreException {
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		return rulesBlock.getRewriteRules();
	}
	
	protected ILabelSymbolInfo[] fetchRules(String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		ILabelSymbolInfo[] symbolInfos = new ILabelSymbolInfo[rules.length];
		// set filtered lhs state
		repository.setState(filteredLHSs);
		initFilterModules(repository, monitor);
		for (int i = 0; i < rules.length; i++) {
			symbolInfos[i] = fetchLabel(rules[i], theoryName, monitor);
			if (symbolInfos[i] == null)
				continue;
			if (!filterModules(rules[i], repository, monitor)) {
				symbolInfos[i].setError();
			}
		}
		endFilterModules(repository, monitor);
		return symbolInfos;
	}
	
	
	
	
	protected void processRules(ISCRewriteRule[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			if (infos[i] != null && !infos[i].hasError()) {
				IRewriteRule rule = rules [i];
				Formula<?> lhs = filteredLHSs.getRulesLHSs().get(rule.getLabel());
				boolean ok = (lhs != null);
				if (ok) {
					if(scRules[i] != null)
						scRules[i].setSCFormula(lhs, monitor);
					// upload the states to repository
					// the label table
					RewriteRuleLabelSymbolTable ruleLabelSymbolTable = new RewriteRuleLabelSymbolTable(
							LABEL_SYMTAB_SIZE);
					repository.setState(ruleLabelSymbolTable);
					// 1- rule accuracy
					RuleAccuracyInfo ruleAccuracyInfo = new RuleAccuracyInfo();
					repository.setState(ruleAccuracyInfo);
					// 2- the lhs formula
					ParsedLHSFormula parsedLHS = new ParsedLHSFormula();
					parsedLHS.setLHSFormula(lhs);
					repository.setState(parsedLHS);
					// call the children processor module
					initProcessorModules(rule, repository, null);
					processModules(rule, scRules[i], repository, monitor);
					endProcessorModules(rule, repository, null);
					if(scRules[i] != null)
						scRules[i].setAccuracy(ruleAccuracyInfo.isAccurate(), monitor);
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
		// TODO Auto-generated method stub
		return Messages.progress_TheoryRewriteRules;
	}

	@Override
	protected ISCRewriteRule[] createSCRulesArray() {
		// TODO Auto-generated method stub
		return new ISCRewriteRule[rules.length];
	}

	@Override
	protected ISCRewriteRule cast(ILabeledElement scRule) {
		// TODO Auto-generated method stub
		return (ISCRewriteRule) scRule;
	}

	@Override
	protected ILabelSymbolInfo makeLocalRule(String symbol,
			ILabeledElement element, String component) throws CoreException {
		// TODO Auto-generated method stub
		return TheorySymbolFactory.getInstance().makeLocalRewriteRule(symbol, true, element, component);
	}
	
}
