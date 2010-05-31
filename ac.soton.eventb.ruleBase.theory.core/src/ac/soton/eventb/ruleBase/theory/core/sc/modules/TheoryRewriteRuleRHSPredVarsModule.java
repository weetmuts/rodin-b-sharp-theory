package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.TheoryGraphProblem;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IParsedFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IParsedLHSFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IParsedRHSFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ParsedFormula;
import ac.soton.eventb.ruleBase.theory.core.utils.TheoryUtils;

public class TheoryRewriteRuleRHSPredVarsModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleRHSPredVarsModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".theoryRewriteRuleRHSFormulasPredVarsModule");
	
	private ParsedFormula parsedCond;
	private IParsedRHSFormula parsedRHS;
	private IParsedLHSFormula parsedLHS;

	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		parsedLHS = (IParsedLHSFormula) repository
				.getState(IParsedLHSFormula.STATE_TYPE);
		parsedCond = (ParsedFormula) repository
				.getState(IParsedFormula.STATE_TYPE);
		parsedRHS = (IParsedRHSFormula) repository
				.getState(IParsedRHSFormula.STATE_TYPE);
	}
	
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		boolean ok = true;
		
		PredicateVariable[] predsLHS = parsedLHS.getLHSFormula().getPredicateVariables();
		PredicateVariable[] predsCond = parsedCond.getFormula().getPredicateVariables();
		PredicateVariable[] predsRHS = parsedRHS.getRHSFormula().getPredicateVariables();
		
		if(!TheoryUtils.subset(predsLHS, predsCond) ||
				!TheoryUtils.subset(predsLHS, predsRHS)){
			createProblemMarker(element,
					TheoryGraphProblem.RHSPredVarsNOTSubsetOFLHS,
					new Object[0]);
			ok = false;
		}
		return ok;
	}
	
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		parsedCond = null;
		parsedRHS = null;
		parsedLHS = null;
	}

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
