package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ISCVariable;
import ac.soton.eventb.ruleBase.theory.core.TheoryAttributes;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.pog.states.ITheoryHypothesesManager;

/**
 * <p>This module generates the actual proof obligations associated with rewrite rules.</p>
 * @author maamria
 * 
 */
public class TheoryRewriteRuleModule extends UtilityModule {

	public static final IModuleType<TheoryRewriteRuleModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryRewriteRuleModule");

	protected ITheoryHypothesesManager theoryHypothesesManager;

	protected ITypeEnvironment typeEnvironment;

	// TODO externalise these strings
	private final static String RULE_RHS_WD_SUFFIX = "/WD-S/";
	private final static String RULE_C_WD_SUFFIX = "/WD-C/";
	private final static String RULE_S_SUFFIX = "/S/";
	private final static String RULE_C_SUFFIX = "/C";
	
	private final static String RULE_C_WD_DESC = "Rule Condition WD-preservation";
	private final static String RULE_RHS_WD_DESC = "Rule RHS WD-preservation";
	private final static String RULE_SOUNDNESS_DESC = "Rule Soundness"; 
	private final static String RULE_COMPLETENESS_DESC = "Rule Completeness";
	
	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		theoryHypothesesManager = null;
		super.endModule(element, repository, monitor);
	}
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		theoryHypothesesManager = (ITheoryHypothesesManager) repository
				.getState(ITheoryHypothesesManager.STATE_TYPE);
	}

	@SuppressWarnings("restriction")
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IPORoot target = repository.getTarget();
		IRodinFile scFile = (IRodinFile) element;
		ISCTheoryRoot scTheoryRoot = (ISCTheoryRoot) scFile.getRoot();
		ISCRewriteRule[] scRules = scTheoryRoot.getSCRewriteRules();
		for(ISCRewriteRule rule : scRules){
			if(!rule.hasAttribute(TheoryAttributes.LHS_ATTRIBUTE)){
				continue;
			}
			Formula<?> lhs = rule.getLHSFormula(factory, typeEnvironment);
			//if lhs contains predicate variables, DONOT generate POs
			if(lhs.hasPredicateVariable()){
				continue;
			}
			Predicate lhsWD = lhs.getWDPredicate(factory);
			ArrayList<Predicate> allConditions = new ArrayList<Predicate>();
			ArrayList<Predicate> wdAllConditions = new ArrayList<Predicate>();
			String ruleName = ((ILabeledElement)rule).getLabel();
			IPOGSource[] sources = new IPOGSource[] {
					makeSource(IPOSource.DEFAULT_ROLE, rule.getSource())};
			List<IPOGHint> hints = getTypingHints(scTheoryRoot, lhs.getFreeIdentifiers());
			ISCRewriteRuleRightHandSide[] scRHSs = rule.getSCRuleRHSs();
			for(ISCRewriteRuleRightHandSide rhs : scRHSs){
				String rhsLabel = rhs.getLabel();
				Formula<?> rhsForm = rhs
						.getRHSFormula(factory, typeEnvironment);
				Predicate rhsWD = rhsForm.getWDPredicate(factory);
				Predicate condition = rhs
						.getPredicate(factory, typeEnvironment);
				// since we will make a disjunction, disregard bottom?
				allConditions.add(condition);
				Predicate conditionWD = condition.getWDPredicate(factory);
				wdAllConditions.add(conditionWD);
				Predicate soundnessPredicate = null;
				if (lhs instanceof Expression) {
					soundnessPredicate = factory.makeRelationalPredicate(
							Formula.EQUAL, (Expression) lhs,
							(Expression) rhsForm, null);
				} else {
					soundnessPredicate = factory.makeBinaryPredicate(
							Formula.LEQV, (Predicate) lhs, (Predicate) rhsForm,
							null);
				}
				//-------------------------------------------------------
				// --------------------------WD-Preservation of Condition
				//-------------------------------------------------------
				// lhsWD => conditionWD
				if (!goalIsTrivial(conditionWD)) {
					String poName = ruleName + RULE_C_WD_SUFFIX +rhsLabel;
					List<IPOGPredicate> hyps = new ArrayList<IPOGPredicate>();
					addIfNotTrueAndNotAlreadyIn(hyps, lhsWD, rule);
					IPOGHint hint = getLocalHypothesisSelectionHint(target, poName);
					hints.add(hint);
					createPO(target, poName, 
							natureFactory.getNature(RULE_C_WD_DESC),
							theoryHypothesesManager.getFullHypothesis(), 
							hyps, makePredicate(conditionWD, rule.getSource()), 
							sources, hints.toArray(new IPOGHint[hints.size()]), 
							theoryHypothesesManager.theoryIsAccurate(), monitor);
					hints.remove(hint);
				} else {
					if (DEBUG_TRIVIAL)
						debugTraceTrivial("WD-C");
				}
				//-------------------------------------------------------
				//----------------------------WD-Preservation of RHS
				//-------------------------------------------------------
				// lhsWD & conditionWD & condition => rhsWD
				if (!goalIsTrivial(rhsWD)) {
					String poName = ruleName + RULE_RHS_WD_SUFFIX +rhsLabel;
					List<IPOGPredicate> hyps = new ArrayList<IPOGPredicate>();
					addIfNotTrueAndNotAlreadyIn(hyps, lhsWD, rule);
					addIfNotTrueAndNotAlreadyIn(hyps, conditionWD, rule);
					addIfNotTrueAndNotAlreadyIn(hyps, condition, rule);
					IPOGHint hint = getLocalHypothesisSelectionHint(target, poName);
					hints.add(hint);
					createPO(target, poName, 
							natureFactory.getNature(RULE_RHS_WD_DESC),
							theoryHypothesesManager.getFullHypothesis(), 
							hyps, makePredicate(rhsWD, rule.getSource()), 
							sources, hints.toArray(new IPOGHint[hints.size()]), 
							theoryHypothesesManager.theoryIsAccurate(), monitor);
					hints.remove(hint);
				} else {
					if (DEBUG_TRIVIAL)
						debugTraceTrivial("WD-RHS");
				}
				//-------------------------------------------------------
				//------------------------------Soundness of RHS
				//-------------------------------------------------------
				// lhsWD & conditionWD & condition & rhsWD => lhs = rhs
				if(!goalIsTrivial(soundnessPredicate)){
					String poName = ruleName + RULE_S_SUFFIX +rhsLabel;
					List<IPOGPredicate> hyps = new ArrayList<IPOGPredicate>();
					addIfNotTrueAndNotAlreadyIn(hyps, lhsWD, rule);
					addIfNotTrueAndNotAlreadyIn(hyps, rhsWD, rule);
					addIfNotTrueAndNotAlreadyIn(hyps, conditionWD, rule);
					addIfNotTrueAndNotAlreadyIn(hyps, condition, rule);
					IPOGHint hint = getLocalHypothesisSelectionHint(target, poName);
					hints.add(hint);
					createPO(target, poName, 
							natureFactory.getNature(RULE_SOUNDNESS_DESC),
							theoryHypothesesManager.getFullHypothesis(), 
							hyps, makePredicate(soundnessPredicate, rule.getSource()), 
							sources, hints.toArray(new IPOGHint[hints.size()]), 
							theoryHypothesesManager.theoryIsAccurate(), monitor);
					hints.remove(hint);
				}
				else {
					if (DEBUG_TRIVIAL)
						debugTraceTrivial("S");
				}
				
			}
			//-------------------------------------------------------
			//----------------------------------Completeness of Rule
			//-------------------------------------------------------
			// A conditionWD => V condition
			if(rule.isComplete() && 
					allConditions.size() > 0){
				String poName = ruleName + RULE_C_SUFFIX;
				List<IPOGPredicate> hyps = new ArrayList<IPOGPredicate>();
				for(Predicate wd: wdAllConditions){
					addIfNotTrueAndNotAlreadyIn(hyps, wd, rule);
				}
				IPOGHint hint = getLocalHypothesisSelectionHint(target, poName);
				hints.add(hint);
				Predicate goal = 
					allConditions.size()==1? allConditions.get(0):factory.makeAssociativePredicate(Formula.LOR, allConditions, null);
				createPO(target, poName, 
						natureFactory.getNature(RULE_COMPLETENESS_DESC),
						theoryHypothesesManager.getFullHypothesis(), 
						hyps, makePredicate(goal, rule.getSource()), 
						sources, hints.toArray(new IPOGHint[hints.size()]), 
						theoryHypothesesManager.theoryIsAccurate(), monitor);
				hints.remove(hint);
			}
		}
		
	}
	
	private List<IPOGHint> getTypingHints(ISCTheoryRoot scRoot, 
			FreeIdentifier[] identifierContext)throws CoreException {
		ArrayList<IPOGHint> hints = new ArrayList<IPOGHint>();
		ISCVariable[] vars = scRoot .getSCVariables();
		for(FreeIdentifier fi : identifierContext){
			String identName = fi.getName();
			for(ISCVariable var : vars){
				String varName = var.getElementName();
				if(identName.equals(varName)){
					hints.add(makePredicateSelectionHint(theoryHypothesesManager.getPredicate(var)));
					break;
				}
			}
		}
		return hints;
	}
	
	private void addIfNotTrueAndNotAlreadyIn(List<IPOGPredicate> hyps, Predicate pred, IRodinElement source){
		if(!pred.equals(btrue)){
			IPOGPredicate pogPred = new POGPredicate(pred, source);
			if(!hyps.contains(pogPred)){
				hyps.add(pogPred);
			}
		}
	}
	
	protected IPOGHint getLocalHypothesisSelectionHint(IPORoot target, String sequentName) 
	throws RodinDBException {
		return makeIntervalSelectionHint(
				theoryHypothesesManager.getRootHypothesis(),
				getSequentHypothesis(target, sequentName));
	}
}
