package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.TheoryAttributes;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.TheoryGraphProblem;
import ac.soton.eventb.ruleBase.theory.core.sc.modules.TheoryRewriteRuleRHSIdentsModule;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IParsedFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IParsedLHSFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IParsedRHSFormula;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ParsedFormula;
import ac.soton.eventb.ruleBase.theory.core.utils.TheoryUtils;

/**
 * A filter module for the free identifiers occurring in the right hand sides of rewrite rules.
 * 
 * <p>Assuming VAR(form) stands for the free variables occuring in form,
 * this module performs the following checks:</p>
 * <ul>
 * 	<li> if VAR(rhs) notSubsetOf VAR(lhs) or VAR(c) notSubsetOf VAR(lhs), it issues an error.
 *  <li> if lhs and rhs formulas are of different syntactic class, it issues an error.
 *  <li> if (parsed) lhs is an expression, then if type(lhs) notEqual type(rhs), it issues an error.
 * </ul>
 * @author maamria
 *
 */
public class TheoryRewriteRuleRHSIdentsModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleRHSIdentsModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".theoryRewriteRuleRHSFormulasIdentModule");

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
		// condition idents subset
		FreeIdentifier[] identsLHS = parsedLHS.getLHSFormula()
				.getFreeIdentifiers();
		FreeIdentifier[] identsCond = parsedCond.getFormula()
				.getFreeIdentifiers();
		FreeIdentifier[] identsRHS = parsedRHS.getRHSFormula()
				.getFreeIdentifiers();

		if (!TheoryUtils.subset(identsLHS, identsRHS)
				|| !TheoryUtils.subset(identsLHS, identsCond)) {
			createProblemMarker(element,
					TheoryGraphProblem.RHSIdentsNotSubsetOfLHSIdents,
					new Object[0]);
			ok = false;
		}
		if (parsedLHS.getLHSFormula() instanceof Expression) {
			if (!(parsedRHS.getRHSFormula() instanceof Expression)) {
				createProblemMarker((IInternalElement) element,
						TheoryAttributes.RHS_ATTRIBUTE,
						TheoryGraphProblem.LhsAndRhsNotSynClassMatching,
						"Expression", "Predicate");
				ok = false;
			} else {
				Type lhsType = ((Expression) parsedLHS.getLHSFormula())
						.getType();
				Type rhsType = ((Expression) parsedRHS.getRHSFormula())
						.getType();
				if (!lhsType.equals(rhsType)) {
					createProblemMarker((IInternalElement) element,
							TheoryAttributes.RHS_ATTRIBUTE,
							TheoryGraphProblem.RuleTypeMismatchError, lhsType,
							rhsType);
					ok = false;
				}
			}
		} else {
			if (!(parsedRHS.getRHSFormula() instanceof Predicate)) {
				createProblemMarker((IInternalElement) element,
						TheoryAttributes.RHS_ATTRIBUTE,
						TheoryGraphProblem.LhsAndRhsNotSynClassMatching,
						"Predicate", "Expression");
				ok = false;
			}
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
