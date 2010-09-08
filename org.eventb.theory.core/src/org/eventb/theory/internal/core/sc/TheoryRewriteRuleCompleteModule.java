package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ICompleteElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.rodinp.core.IRodinElement;

/**
 * A filter module for the complete attribute of a rewrite rule.
 * <p>It performs the following checks:</p>
 * <ul>
 * 	<li> if the attribute is not present, it issues a warning and sets it to false (default).
 * </ul>
 * @author maamria
 *
 */
public class TheoryRewriteRuleCompleteModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleCompleteModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".theoryRewriteRuleCompleteModule");

	private ILabelSymbolTable labelSymbolTable;

	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = getLabelSymbolTable(repository);
	}

	
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		ICompleteElement compElement = (ICompleteElement) element;
		boolean isComp = false;
		if (!compElement.hasComplete()) {
			createProblemMarker(compElement,
					TheoryAttributes.COMPLETE_ATTRIBUTE,
					TheoryGraphProblem.CompleteUndefWarning);
			// default is incomplete
			isComp = false;
		} else {
			isComp = compElement.isComplete();
		}

		final String label = ((ILabeledElement) element).getLabel();
		checkAndSetSymbolInfo(label, isComp);
		return true;
	}

	
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}

	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private void checkAndSetSymbolInfo(String label, boolean isComp) {
		final ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(label);
		if (symbolInfo == null) {
			throw new IllegalStateException("No defined symbol for: " + label);
		}
		symbolInfo.setAttributeValue(TheoryAttributes.COMPLETE_ATTRIBUTE,
				isComp);

	}

	private TheoryLabelSymbolTable getLabelSymbolTable(
			ISCStateRepository repository) throws CoreException {
		return (TheoryLabelSymbolTable) repository
				.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}

}
