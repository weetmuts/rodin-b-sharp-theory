package org.eventb.theory.internal.core.sc;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.FilteredLHSs;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

/**
 * A filter module for the lhs attribute of a rewrite rule.
 * <p>It performs the following checks:</p>
 * <ul>
 * 	<li> if the lhs attribute is missing, it issues an error.
 * 	<li> if the lhs attribute cannot be parsed, it issues errors.
 * 	<li> if the lhs contains any not pre-defined free identifiers, it issues errors.
 * </ul>
 * @author maamria
 *
 */
public class TheoryRewriteRuleLHSModule extends SCFilterModule {

	public static final IModuleType<TheoryRewriteRuleLHSModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID
				+ ".theoryRewriteRuleLHSModule");
	
	private FormulaFactory factory;
	private FilteredLHSs filteredLHSs;
	private ITypeEnvironment typeEnvironment;
	
	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
		filteredLHSs = (FilteredLHSs) repository.getState(FilteredLHSs.STATE_TYPE);
		typeEnvironment = repository.getTypeEnvironment();
	}
	
	
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRule rule = (IRewriteRule) element;
		if (!rule.hasFormula()) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSUndefError);
			return false;
		}
		Formula<?> lhsForm = parseAndCheckPatternFormula(
				rule, factory, typeEnvironment);
		
		if(lhsForm == null){
			return false;
		}
		if(lhsForm instanceof FreeIdentifier){
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSIsIdentErr);
			return false;
		}
		WDStrictChecker checker = new WDStrictChecker();
		lhsForm.accept(checker);
		if (!checker.wdStrict){
			createProblemMarker(rule, FORMULA_ATTRIBUTE, 
					TheoryGraphProblem.LHS_IsNotWDStrict);
			return false;
		}
		if(!CoreUtilities.checkAgainstTypeParameters(rule, lhsForm, typeEnvironment, this)){
			return false;
		}
		filteredLHSs.addLHS(rule.getLabel(), lhsForm);
		return true;
	}

	
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		filteredLHSs = null;
		typeEnvironment = null;
		super.endModule(repository, monitor);
	}
	
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}


	/**
	 * Parses and type checks the formula occurring as an attribute to the given
	 * element. The formula may contain predicate variables.
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factor
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed formula
	 * @throws CoreException
	 */
	protected Formula<?> parseAndCheckPatternFormula(
			IFormulaElement element, FormulaFactory ff,
			ITypeEnvironment typeEnvironment)
			throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		IParseResult result = ff.parsePredicatePattern(form, V2, null);
		if (result.hasProblem()) {
			result = ff.parseExpressionPattern(form, V2, null);
			if (CoreUtilities.issueASTProblemMarkers(element, attributeType, result, this)) {
				return null;
			} else {
				formula = result.getParsedExpression();
			}
		} else {
			formula = result.getParsedPredicate();
		}
	
		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				createProblemMarker(element, attributeType,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if (CoreUtilities.issueASTProblemMarkers(element, attributeType, tcResult, this)) {
			return null;
		}
		return formula;
	}
	
	public static class WDStrictChecker extends DefaultVisitor{
		
		boolean wdStrict = true;
		
		public WDStrictChecker(){
			
		}
		
		@Override
		public boolean enterEXISTS(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterFORALL(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLAND(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLOR(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLIMP(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLEQV(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}
		
	}
}
