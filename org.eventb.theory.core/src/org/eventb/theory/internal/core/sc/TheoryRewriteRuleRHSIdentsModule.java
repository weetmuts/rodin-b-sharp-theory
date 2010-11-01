package org.eventb.theory.internal.core.sc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.ParsedLHSFormula;
import org.eventb.theory.internal.core.sc.states.ParsedRHSFormula;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * A filter module for the free identifiers occurring in the right hand sides of
 * rewrite rules.
 * 
 * <p>
 * Assuming VAR(form) stands for the free variables occuring in form, this
 * module performs the following checks:
 * </p>
 * <ul>
 * <li>if VAR(rhs) notSubsetOf VAR(lhs) or VAR(c) notSubsetOf VAR(lhs), it
 * issues an error.
 * <li>if lhs and rhs formulas are of different syntactic class, it issues an
 * error.
 * <li>if (parsed) lhs is an expression, then if type(lhs) notEqual type(rhs),
 * it issues an error.
 * </ul>
 * 
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
		super.initModule(repository, monitor);
		parsedLHS = (ParsedLHSFormula) repository
				.getState(ParsedLHSFormula.STATE_TYPE);
		parsedCond = (IParsedFormula) repository
				.getState(IParsedFormula.STATE_TYPE);
		parsedRHS = (ParsedRHSFormula) repository
				.getState(ParsedRHSFormula.STATE_TYPE);
	}

	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRuleRightHandSide rewriteRuleRightHandSide = (IRewriteRuleRightHandSide) element;
		String label = rewriteRuleRightHandSide.getLabel();
		// condition idents subset
		Formula<?> lhsFormula = parsedLHS.getLHSFormula();
		Formula<?> rhsFormula = parsedRHS.getRHSFormula();
		Formula<?> rhsCond = parsedCond.getFormula();

		FreeIdentifier[] identsLHS = lhsFormula.getFreeIdentifiers();
		Set<GivenType> givensLHS = lhsFormula.getGivenTypes();
		List<String> lhsIdentStrs = convertIdentsToListOfStrings(identsLHS);
		List<String> lhsGivensStrs = convertTypesToListOfStrings(givensLHS);

		FreeIdentifier[] identsCond = rhsCond.getFreeIdentifiers();
		Set<GivenType> givensCond = rhsCond.getGivenTypes();
		List<String> condIdentStrs = convertIdentsToListOfStrings(identsCond);
		List<String> condGivensStrs = convertTypesToListOfStrings(givensCond);

		FreeIdentifier[] identsRHS = rhsFormula.getFreeIdentifiers();
		Set<GivenType> givensRHS = rhsFormula.getGivenTypes();
		List<String> rhsIdentStrs = convertIdentsToListOfStrings(identsRHS);
		List<String> rhsGivensStrs = convertTypesToListOfStrings(givensRHS);

		for (String type : rhsGivensStrs) {
			if (!lhsGivensStrs.contains(type)) {
				createProblemMarker(rewriteRuleRightHandSide, 
						TheoryAttributes.FORMULA_ATTRIBUTE, 
						TheoryGraphProblem.RHSGivensNotSubsetOfLHSGivens, label);
				return false;
			}
		}
		for (String type : condGivensStrs) {
			if (!lhsGivensStrs.contains(type)) {
				createProblemMarker(rewriteRuleRightHandSide, 
						EventBAttributes.PREDICATE_ATTRIBUTE, 
						TheoryGraphProblem.CondGivensNotSubsetOfLHSGivens, label);
				return false;
			}
		}

		for (String ident : rhsIdentStrs) {
			if(!lhsIdentStrs.contains(ident) && !lhsGivensStrs.contains(ident)){
				createProblemMarker(rewriteRuleRightHandSide, 
						TheoryAttributes.FORMULA_ATTRIBUTE, 
						TheoryGraphProblem.RHSIdentsNotSubsetOfLHSIdents, label);
				return false;
			}
		}
		for (String ident : condIdentStrs) {
			if(!lhsIdentStrs.contains(ident) && !lhsGivensStrs.contains(ident)){
				createProblemMarker(rewriteRuleRightHandSide, 
						EventBAttributes.PREDICATE_ATTRIBUTE, 
						TheoryGraphProblem.CondIdentsNotSubsetOfLHSIdents, label);
				return false;
			}
		}
		if (lhsFormula instanceof Expression) {
			if (!(rhsFormula instanceof Expression)) {
				createProblemMarker((IInternalElement) element,
						TheoryAttributes.FORMULA_ATTRIBUTE,
						TheoryGraphProblem.LhsAndRhsNotSynClassMatching,
						"Expression", "Predicate");
				return false;
			} else {
				Type lhsType = ((Expression) lhsFormula).getType();
				Type rhsType = ((Expression) rhsFormula).getType();
				if (!lhsType.equals(rhsType)) {
					createProblemMarker((IInternalElement) element,
							TheoryAttributes.FORMULA_ATTRIBUTE,
							TheoryGraphProblem.RuleTypeMismatchError, lhsType,
							rhsType);
					return false;
				}
			}
		} else {
			if (!(rhsFormula instanceof Predicate)) {
				createProblemMarker((IInternalElement) element,
						TheoryAttributes.FORMULA_ATTRIBUTE,
						TheoryGraphProblem.LhsAndRhsNotSynClassMatching,
						"Predicate", "Expression");
				return false;
			}
		}
		return true;
	}

	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		parsedCond = null;
		parsedRHS = null;
		parsedLHS = null;
		super.endModule(repository, monitor);
	}

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private List<String> convertIdentsToListOfStrings(FreeIdentifier[] idents) {
		List<String> result = new ArrayList<String>();
		for (FreeIdentifier ident : idents) {
			result.add(ident.getName());
		}
		return result;
	}

	private List<String> convertTypesToListOfStrings(Set<GivenType> types) {
		List<String> result = new ArrayList<String>();
		for (GivenType type : types) {
			result.add(type.getName());
		}
		return result;
	}
}
