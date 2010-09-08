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
import org.eventb.theory.core.IAutomaticElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.rodinp.core.IRodinElement;

/**
 * A filter module for the automatic attribute of a rewrite rule.
 * <p>It performs the following checks:</p>
 * <ul>
 * 	<li> if the attribute is not present, it issues a warning and sets it to false (default).
 * </ul>
 * @author maamria
 *
 */
public class TheoryRewriteRuleAutoModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleAutoModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryRewriteRuleAutoModule");
	
	private ILabelSymbolTable labelSymbolTable;
	
	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = getLabelSymbolTable(repository);
	}
	
	
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IAutomaticElement autoElement = (IAutomaticElement) element;
		boolean isAuto = false;
		// warning auto status needs to be defined
		if (!autoElement.hasAutomatic()) {
			createProblemMarker(autoElement,
					TheoryAttributes.AUTOMATIC_ATTRIBUTE,
					TheoryGraphProblem.AutoUndefWarning);
			// default is manual
			isAuto = false;
		}
		else {
			isAuto = autoElement.isAutomatic();
		}
		
		final String label = ((ILabeledElement) element).getLabel();
		checkAndSetSymbolInfo(label, isAuto);
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

	private void checkAndSetSymbolInfo(String label, boolean isAuto) {
		final ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(label);
		if (symbolInfo == null) {
			throw new IllegalStateException("No defined symbol for: " + label);
		}
		symbolInfo.setAttributeValue(TheoryAttributes.AUTOMATIC_ATTRIBUTE,
				isAuto);

	}
	
	private TheoryLabelSymbolTable getLabelSymbolTable(
			ISCStateRepository repository) throws CoreException {
		return (TheoryLabelSymbolTable) repository
				.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}
	
}
