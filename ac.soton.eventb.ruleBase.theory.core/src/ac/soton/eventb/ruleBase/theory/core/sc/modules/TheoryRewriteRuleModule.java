package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.Messages;
import ac.soton.eventb.ruleBase.theory.core.sc.modules.base.LabeledElementModule;
import ac.soton.eventb.ruleBase.theory.core.sc.states.FilteredLHSs;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IFilteredLHSs;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ILabelSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ILabelSymbolTable;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IParsedLHSFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IRewriteRuleLabelSymbolTable;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IRuleAccuracyInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ITheoryAccuracyInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ITheoryLabelSymbolTable;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ParsedLHSFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.RewriteRuleLabelSymbolTable;
import ac.soton.eventb.ruleBase.theory.core.sc.states.RuleAccuracyInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.symbolTable.TheorySymbolFactory;

/**
 * A processor module for rewrite rules. It sets the accuracy of processed rules.
 * @author maamria
 *
 */
public class TheoryRewriteRuleModule extends LabeledElementModule{

	public static final IModuleType<TheoryRewriteRuleModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryRewriteRuleModule");
	
	private final static int LABEL_SYMTAB_SIZE = 2047;
	private static String REWRITE_RULE_NAME_PREFIX = "rule";
	
	private ITheoryAccuracyInfo accuracyInfo;
	private IFilteredLHSs filteredLHSs;
	private IRewriteRule[] rules;
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		rules = getRuleElements(element);
		accuracyInfo = (ITheoryAccuracyInfo) repository
				.getState(ITheoryAccuracyInfo.STATE_TYPE);
		filteredLHSs = new FilteredLHSs();

	}
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile theoryFile = (IRodinFile) element;
		assert rules.length > 0;
		monitor.subTask(Messages.bind(Messages.progress_TheoryRewriteRules));
		ILabelSymbolInfo[] symbolInfos = fetchRules(theoryFile, repository,
				monitor);
		ISCRewriteRule[] scRules = new ISCRewriteRule[rules.length];
		commitRules((ISCTheoryRoot) target, scRules, symbolInfos,
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
				.getState(ITheoryLabelSymbolTable.STATE_TYPE);
	}
	
	// Utilities
	
	private IRewriteRule[] getRuleElements(IRodinElement element)
			throws CoreException {
		IRodinFile theoryFile = (IRodinFile) element;
		ITheoryRoot theoryRoot = (ITheoryRoot) theoryFile.getRoot();
		return theoryRoot.getRewriteRules();
	}
	
	private ILabelSymbolInfo[] fetchRules(IRodinFile theoryFile,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		String theoryName = theoryFile.getElementName();
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
	
	private void commitRules(ISCTheoryRoot target,
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
	private ISCRewriteRule createSCRule(ISCTheoryRoot target, int index,
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
				Formula<?> lhs = filteredLHSs.getRulesLHSs().get(rule);
				boolean ok = (lhs != null);
				if (ok) {
					if(!infos[i].hasError())
						scRules[i].setLHSFormula(lhs, monitor);
					// upload the states to repository
					// the label table
					IRewriteRuleLabelSymbolTable ruleLabelSymbolTable = new RewriteRuleLabelSymbolTable(
							LABEL_SYMTAB_SIZE);
					repository.setState(ruleLabelSymbolTable);
					// 1- rule accuracy
					IRuleAccuracyInfo ruleAccuracyInfo = new RuleAccuracyInfo();
					repository.setState(ruleAccuracyInfo);
					// 2- the lhs formula
					IParsedLHSFormula parsedLHS = new ParsedLHSFormula();
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
