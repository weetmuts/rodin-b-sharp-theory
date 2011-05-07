package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
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
		ILabelSymbolInfo[] symbolInfos = new ILabelSymbolInfo[rules.length];
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
	
	protected void processRules(IRewriteRule[] rules, ISCRewriteRule[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			if (infos[i] != null && !infos[i].hasError()) {
				boolean ok = true;
				if (ok) {
					
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
	protected ISCRewriteRule[] createSCRulesArray(int length) {
		// TODO Auto-generated method stub
		return new ISCRewriteRule[length];
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
