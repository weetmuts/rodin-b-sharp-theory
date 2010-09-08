package org.eventb.theory.internal.core.sc;

import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.FilteredLHSs;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
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
	private IIdentifierSymbolTable identifierSymbolTable;
	
	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
		filteredLHSs = (FilteredLHSs) repository.getState(FilteredLHSs.STATE_TYPE);
		typeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
	}
	
	
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRule rule = (IRewriteRule) element;
		if (!rule.hasFormula()) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSUndefError);
			return false;
		}
		Formula<?> lhsForm = CoreUtilities.parseAndCheckPatternFormula(
				rule, factory, typeEnvironment, this);
		
		if(lhsForm == null){
			return false;
		}
		if(lhsForm instanceof FreeIdentifier){
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSIsIdentErr);
			return false;
		}
		FreeIdentifier[] freeIdentifiers = lhsForm.getFreeIdentifiers();
		for (FreeIdentifier freeIdentifier : freeIdentifiers) {
			IIdentifierSymbolInfo symbolInfo = getSymbolInfo(rule,
					freeIdentifier, null);
			if (symbolInfo == null) {
				return false;
			}
		}
		filteredLHSs.addLHS(rule.getLabel(), lhsForm);
		return true;
	}

	
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		filteredLHSs = null;
		typeEnvironment = null;
		identifierSymbolTable = null;
		super.endModule(repository, monitor);
	}
	
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			FreeIdentifier freeIdentifier, IProgressMonitor monitor)
			throws CoreException {
		IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
				.getSymbolInfo(freeIdentifier.getName());
		if (symbolInfo == null) {
			createProblemMarker(element, FORMULA_ATTRIBUTE, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.UndeclaredFreeIdentifierError, freeIdentifier
							.getName());
		} else if (symbolInfo.hasError()) {
			createProblemMarker(element, FORMULA_ATTRIBUTE, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.FreeIdentifierFaultyDeclError, freeIdentifier
							.getName());
			symbolInfo = null;
		}
		return symbolInfo;
	}
}
