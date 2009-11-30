package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.LHS_ATTRIBUTE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.TheoryGraphProblem;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IFilteredLHSs;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolTable;
import ac.soton.eventb.ruleBase.theory.core.utils.TheoryUtils;

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
	private IFilteredLHSs filteredLHSs;
	private ITypeEnvironment typeEnvironment;
	private IIdentifierSymbolTable identifierSymbolTable;
	
	
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = FormulaFactory.getDefault();
		filteredLHSs = (IFilteredLHSs) repository.getState(IFilteredLHSs.STATE_TYPE);
		typeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
	}
	
	
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRule rule = (IRewriteRule) element;
		if (!rule.hasLHSString()) {
			createProblemMarker(rule, LHS_ATTRIBUTE,
					TheoryGraphProblem.LHSUndefError);
			return false;
		}
		String formulaStr = rule.getAttributeValue(LHS_ATTRIBUTE);
		Formula<?> lhsForm = TheoryUtils.parseFormula(rule, 
				formulaStr, factory, typeEnvironment, 
				LHS_ATTRIBUTE, repository, this);
		if(lhsForm == null){
			return false;
		}
		if(lhsForm instanceof FreeIdentifier){
			createProblemMarker(rule, LHS_ATTRIBUTE,
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
		filteredLHSs.addLHS(rule, lhsForm);
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
			createProblemMarker(element, LHS_ATTRIBUTE, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.UndeclaredFreeIdentifierError, freeIdentifier
							.getName());
		} else if (symbolInfo.hasError()) {
			createProblemMarker(element, LHS_ATTRIBUTE, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.FreeIdentifierFaultyDeclError, freeIdentifier
							.getName());
			symbolInfo = null;
		}
		return symbolInfo;
	}
}
