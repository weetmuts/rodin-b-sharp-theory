package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ParsedFormula;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.ParsedLHSFormula;
import org.eventb.theory.internal.core.sc.states.ParsedRHSFormula;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

@SuppressWarnings("restriction")
public class TheoryRewriteRuleRHSPredVarsModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleRHSPredVarsModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".theoryRewriteRuleRHSFormulasPredVarsModule");
	
	
	private ParsedFormula parsedCond;
	private ParsedRHSFormula parsedRHS;
	private ParsedLHSFormula parsedLHS;

	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		parsedLHS = (ParsedLHSFormula) repository
				.getState(ParsedLHSFormula.STATE_TYPE);
		parsedCond = (ParsedFormula) repository
				.getState(IParsedFormula.STATE_TYPE);
		parsedRHS = (ParsedRHSFormula) repository
				.getState(ParsedRHSFormula.STATE_TYPE);
	}
	
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		boolean ok = true;
		
		PredicateVariable[] predsLHS = parsedLHS.getLHSFormula().getPredicateVariables();
		PredicateVariable[] predsCond = parsedCond.getFormula().getPredicateVariables();
		PredicateVariable[] predsRHS = parsedRHS.getRHSFormula().getPredicateVariables();
		
		if(!CoreUtilities.subset(predsLHS, predsCond) ||
				!CoreUtilities.subset(predsLHS, predsRHS)){
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
