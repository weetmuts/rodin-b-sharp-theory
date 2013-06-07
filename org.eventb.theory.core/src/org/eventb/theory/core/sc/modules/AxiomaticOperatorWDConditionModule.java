package org.eventb.theory.core.sc.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class AxiomaticOperatorWDConditionModule extends SCProcessorModule{

	private final IModuleType<AxiomaticOperatorWDConditionModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".axiomaticOperatorWDConditionModule");

	private OperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IAxiomaticOperatorDefinition newOpDef = (IAxiomaticOperatorDefinition) element;
		IOperatorWDCondition[] wdConds = newOpDef.getOperatorWDConditions();
		// check for error
		if (!operatorInformation.hasError() && wdConds != null && wdConds.length > 0) {
			Predicate wdPred = processWdConditions(wdConds, repository, monitor);
			if (wdPred != null && !wdPred.equals(AstUtilities.BTRUE)) {
				if (target != null) {
					Predicate wdPredWD = wdPred.getWDPredicate(repository.getFormulaFactory());
					wdPred = AstUtilities.conjunctPredicates(new Predicate[] { wdPredWD, wdPred }, repository.getFormulaFactory());
					operatorInformation.addWDCondition(wdPred);
				} else {
					operatorInformation.setHasError();
				}
			}
		}
	}

	private Predicate processWdConditions(IOperatorWDCondition[] wds, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		List<Predicate> wdPredicates = new ArrayList<Predicate>();
		for(IOperatorWDCondition wd : wds){
			if(!wd.hasPredicateString() || wd.getPredicateString().equals("")){
				createProblemMarker(wd, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.WDPredMissingError);
				operatorInformation.setHasError();
				continue;
			}
			Predicate pred = CoreUtilities.parseAndCheckPredicate(wd, repository.getFormulaFactory(), 
					repository.getTypeEnvironment(), this);
			if(pred == null || !checkAgainstReferencedIdentifiers(pred, wd)){
				operatorInformation.setHasError();
				continue;
			}
			else {
				if(!pred.equals(AstUtilities.BTRUE))
					wdPredicates.add(pred);
			}
		}
		return AstUtilities.conjunctPredicates(wdPredicates, repository.getFormulaFactory());
	}

	private boolean checkAgainstReferencedIdentifiers(Predicate wdPredicate, IOperatorWDCondition wdConditionElement)
			throws CoreException {
		FreeIdentifier[] idents = wdPredicate.getFreeIdentifiers();
		List<String> notAllowed = new ArrayList<String>();
		for (FreeIdentifier ident : idents) {
			if (!operatorInformation.isAllowedIdentifier(ident)) {
				notAllowed.add(ident.getName());
			}
		}
		if (notAllowed.size() != 0) {
			createProblemMarker(wdConditionElement, EventBAttributes.PREDICATE_ATTRIBUTE,
					TheoryGraphProblem.OpCannotReferToTheseIdents, GeneralUtilities.toString(notAllowed));
			return false;
		}
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

	
}