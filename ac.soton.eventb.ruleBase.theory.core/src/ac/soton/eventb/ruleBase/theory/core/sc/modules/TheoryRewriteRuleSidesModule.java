package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.IRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.TheoryGraphProblem;

/**
 * A filter module for rewrite rule right hand sides. It issues an error
 * if the rule does not have any right hand side.
 * @author maamria
 *
 */
public class TheoryRewriteRuleSidesModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleSidesModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryRewriteRuleSidesModule");
	
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRule rule = (IRewriteRule) element;
		IRewriteRuleRightHandSide[] ruleHandSides = rule.getRuleRHSs();
		if(ruleHandSides.length < 1){
			createProblemMarker(element, TheoryGraphProblem.RuleNoRhsError, rule.getLabel());
			return false;
		}
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
