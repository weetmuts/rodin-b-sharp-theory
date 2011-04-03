package org.eventb.theory.rbp.base;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.rbp.internal.base.IDeployedInferenceRule;
import org.eventb.theory.rbp.internal.base.IDeployedRewriteRule;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * <p>An implementation of a rule base manager.</p>
 * <p>Clients should refrain from using this class directly and use <code>IRuleBaseManager</code> instead.</p>
 * @author maamria
 *
 */
public class RuleBaseManager implements IRuleBaseManager{

	private static RuleBaseManager instance;
	private RulesCache cache ;
	private boolean changeOccurred;
	private FormulaFactory globalFactory;
	
	private RuleBaseManager(){
		globalFactory = ProverUtilities.getCurrentFormulaFactory();
		cache = new RulesCache(globalFactory);
		RodinCore.addElementChangedListener(this);
	}

	/**
	 * Returns the default instance of the rule base manager.
	 * @return the singeleton instance
	 */
	public static RuleBaseManager getDefault(){
		if(instance == null)
			instance = new RuleBaseManager();
		return instance;
	}
	
	@Override
	public List<IDeployedRewriteRule> getRewriteRules() {
		// TODO Auto-generated method stub
		return cache.getRewriteRules();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic,
			Class<? extends Formula> clazz) {
		// TODO Auto-generated method stub
		return cache.getRewriteRules(automatic, clazz);
	}

	@Override
	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic) {
		// TODO Auto-generated method stub
		return cache.getRewriteRules(automatic);
	}

	public <E extends Formula<? extends Formula<?>>> IDeployedRewriteRule getInteractiveRule(String ruleName, String theoryName, Class<E> clazz) {
		// TODO Auto-generated method stub
		return cache.getInteractiveRule(ruleName, theoryName, clazz);
	}

	@Override
	public List<IDeployedInferenceRule> getInferenceRules() {
		// TODO Auto-generated method stub
		return cache.getInferenceRules();
	}
	
	@Override
	public List<IDeployedInferenceRule> getInferenceRules(ReasoningType type) {
		// TODO Auto-generated method stub
		return cache.getInferenceRules(type);
	}

	@Override
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic) {
		// TODO Auto-generated method stub
		return cache.getInferenceRules(automatic);
	}
	
	@Override
	public List<IDeployedInferenceRule> getInferenceRules(ReasoningType type,
			boolean automatic) {
		// TODO Auto-generated method stub
		return cache.getInferenceRules(type, automatic);
	}

	@Override
	public IDeployedInferenceRule getInferenceRule(String ruleName,
			String theoryName) {
		// TODO Auto-generated method stub
		return cache.getInferenceRule(ruleName, theoryName);
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		// TODO Auto-generated method stub
		try {
			processDelta(event.getDelta());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if(changeOccurred){
			globalFactory = ProverUtilities.getCurrentFormulaFactory();
			cache = new RulesCache(globalFactory);
			changeOccurred = false;
		}
	}
	
	protected void processDelta(IRodinElementDelta delta) throws CoreException {
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();
		if (element instanceof IRodinDB) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
		if (element instanceof IRodinProject) {
			IRodinProject proj = (IRodinProject) element;
			if (proj.getElementName().equals(
					DatabaseUtilities.THEORIES_PROJECT)) {
				for (IRodinElementDelta d : affected) {
					processDelta(d);
				}
			}
		}
		if (element instanceof IRodinFile) {
			IRodinFile file = (IRodinFile) element;
			if (file.getRoot() instanceof IDeployedTheoryRoot) {
				changeOccurred = true;
			}
		}
	}
	
}
