package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.IInteractiveElement;
import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.TheoryAttributes;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.TheoryGraphProblem;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ILabelSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ILabelSymbolTable;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ITheoryLabelSymbolTable;

/**
 * A filter module for the interactive attribute of a rewrite rule as well as the
 * other two related attributes (tool tip and description).
 * <p>It performs the following checks:</p>
 * <ul>
 * 	<li> if the interactive attribute is missing, it issues a warning and sets it to true (default).
 *  <li> if the rule is supposed to be interactive, then:
 *  	<ul>
 *  		<li> if the tool tip attribute is missing, it issues information and sets it to a default value.
 *  		<li> if the description attribute is missing, it issues information and sets it to a default value.
 *  	</ul>
 * </ul>
 * @author maamria
 *
 */
public class TheoryRewriteRuleInterModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleInterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".theoryRewriteRuleInterModule");

	private ILabelSymbolTable labelSymbolTable;

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = getLabelSymbolTable(repository);
	}

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRule rule = (IRewriteRule) element;
		String toolTip = rule.getParent().getElementName()+"."+rule.getLabel();
		String desc = rule.getParent().getElementName()+"."+rule.getLabel();
		IInteractiveElement interElement = (IInteractiveElement) element;
		boolean isInter = false;
		// warning inter status needs to be defined
		if (!interElement.hasInteractive()) {
			createProblemMarker(interElement,
					TheoryAttributes.INTERACTIVE_ATTRIBUTE,
					TheoryGraphProblem.InterUndefWarning);
			// default is interactive
			isInter = true;
		} else {
			isInter = interElement.isInteractive();
		}
		if(isInter){
			// check the tool tip
			if(!rule.hasToolTip() || (rule.hasToolTip() && rule.getToolTip().equals(""))){
				createProblemMarker(rule, TheoryAttributes.TOOL_TIP_ATTRIBUTE, 
						TheoryGraphProblem.ToolTipNotSupplied, rule.getLabel());
			}
			else{
				toolTip = rule.getToolTip();
			}
			// check desc
			if(!rule.hasDescription() || (rule.hasDescription() && rule.getDescription().equals(""))){
				createProblemMarker(rule, 
						TheoryAttributes.DESC_ATTRIBUTE , 
						TheoryGraphProblem.DescNotSupplied, rule.getLabel());
			}
			else {
				desc = rule.getDescription();
			}
		}

		final String label = ((ILabeledElement) element).getLabel();
		checkAndSetSymbolInfo(label, isInter, toolTip, desc);
		return true;
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private void checkAndSetSymbolInfo(String label, boolean isInter, String toolTip , String desc) {
		final ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(label);
		if (symbolInfo == null) {
			throw new IllegalStateException("No defined symbol for: " + label);
		}
		symbolInfo.setAttributeValue(TheoryAttributes.INTERACTIVE_ATTRIBUTE,
				isInter);
		symbolInfo.setAttributeValue(TheoryAttributes.TOOL_TIP_ATTRIBUTE,
				toolTip);
		symbolInfo.setAttributeValue(TheoryAttributes.DESC_ATTRIBUTE,
				desc);

	}

	private ITheoryLabelSymbolTable getLabelSymbolTable(
			ISCStateRepository repository) throws CoreException {
		return (ITheoryLabelSymbolTable) repository
				.getState(ITheoryLabelSymbolTable.STATE_TYPE);
	}

}
