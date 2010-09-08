package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.ParsedLHSFormula;
import org.eventb.theory.internal.core.sc.states.ParsedRHSFormula;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

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

	private IParsedFormula parsedCond;
	private ParsedRHSFormula parsedRHS;
	private ParsedLHSFormula parsedLHS;

	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		parsedLHS = (ParsedLHSFormula) repository
				.getState(ParsedLHSFormula.STATE_TYPE);
		parsedCond = (IParsedFormula) repository
				.getState(IParsedFormula.STATE_TYPE);
		parsedRHS = (ParsedRHSFormula) repository
				.getState(ParsedRHSFormula.STATE_TYPE);
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

		if (!CoreUtilities.subset(identsLHS, identsRHS)
				|| !CoreUtilities.subset(identsLHS, identsCond)) {
			createProblemMarker(element,
					TheoryGraphProblem.RHSIdentsNotSubsetOfLHSIdents,
					new Object[0]);
			ok = false;
		}
		if (parsedLHS.getLHSFormula() instanceof Expression) {
			if (!(parsedRHS.getRHSFormula() instanceof Expression)) {
				createProblemMarker((IInternalElement) element,
						TheoryAttributes.FORMULA_ATTRIBUTE,
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
							TheoryAttributes.FORMULA_ATTRIBUTE,
							TheoryGraphProblem.RuleTypeMismatchError, lhsType,
							rhsType);
					ok = false;
				}
			}
		} else {
			if (!(parsedRHS.getRHSFormula() instanceof Predicate)) {
				createProblemMarker((IInternalElement) element,
						TheoryAttributes.FORMULA_ATTRIBUTE,
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
