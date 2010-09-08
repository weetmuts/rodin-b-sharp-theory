package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.sc.states.FilteredLHSs;
import org.eventb.theory.internal.core.sc.states.ParsedLHSFormula;
import org.eventb.theory.internal.core.sc.states.RewriteRuleLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * A processor module for rewrite rules. It sets the accuracy of processed rules.
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryRewriteRuleModule extends LabeledElementModule{

	public static final IModuleType<TheoryRewriteRuleModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryRewriteRuleModule");
	
	private final static int LABEL_SYMTAB_SIZE = 2047;
	private static String REWRITE_RULE_NAME_PREFIX = "rule";
	
	private TheoryAccuracyInfo accuracyInfo;
	private FilteredLHSs filteredLHSs;
	private IRewriteRule[] rules;
	

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		rules = getRuleElements(element);
		accuracyInfo = (TheoryAccuracyInfo) repository
				.getState(TheoryAccuracyInfo.STATE_TYPE);
		filteredLHSs = new FilteredLHSs();

	}
	

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IProofRulesBlock block = (IProofRulesBlock) element;
		monitor.subTask(Messages.bind(Messages.progress_TheoryRewriteRules));
		ILabelSymbolInfo[] symbolInfos = fetchRules(
				block.getParent().getElementName(), repository,
				monitor);
		ISCRewriteRule[] scRules = new ISCRewriteRule[rules.length];
		commitRules((ISCProofRulesBlock) target, scRules, symbolInfos,
				monitor);
		processRules(scRules, repository, symbolInfos, monitor);

	}
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		accuracyInfo = null;
		rules = null;
		filteredLHSs = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRewriteRule(symbol,
				true, element, component);
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository
				.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}
	
	// Utilities
	
	private IRewriteRule[] getRuleElements(IRodinElement element)
			throws CoreException {
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		return rulesBlock.getRewriteRules();
	}
	
	private ILabelSymbolInfo[] fetchRules(String theoryName,
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
	
	private void commitRules(ISCProofRulesBlock target,
			ISCRewriteRule[] scRules, ILabelSymbolInfo[] symbolInfos,
			IProgressMonitor monitor) throws CoreException {
		int index = TheoryPlugin.SC_STARTING_INDEX;
		for (int i = 0; i < rules.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				scRules[i] = createSCRule(target, index++, symbolInfos[i],
						 monitor);
			}
		}
	}
	
	// create an empty sc element
	private ISCRewriteRule createSCRule(ISCProofRulesBlock target, int index,
			ILabelSymbolInfo symbolInfo, 
			IProgressMonitor monitor) throws CoreException {
		ILabeledElement scRule = symbolInfo.createSCElement(target,
				REWRITE_RULE_NAME_PREFIX + index, monitor);
		return (ISCRewriteRule) scRule;
	}
	
	private void processRules(ISCRewriteRule[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			if (infos[i] != null && !infos[i].hasError()) {
				IRewriteRule rule = rules [i];
				Formula<?> lhs = filteredLHSs.getRulesLHSs().get(rule.getLabel());
				boolean ok = (lhs != null);
				if (ok) {
					if(!infos[i].hasError())
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
					if (infos[i] != null)
						infos[i].setError();
					lhs = null;
					if(scRules[i] != null)
						scRules[i].setAccuracy(false, monitor);
					if (accuracyInfo != null) {
						accuracyInfo.setNotAccurate();
					}
				}	
			}
		}
		monitor.worked(1);
	}
	
}
