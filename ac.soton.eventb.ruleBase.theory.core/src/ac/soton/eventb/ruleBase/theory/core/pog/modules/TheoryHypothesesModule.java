package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ISCVariable;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.pog.states.TheoryHypothesesManager;

/**
 * 
 * @author maamria
 *
 */
public class TheoryHypothesesModule extends GlobalHypothesesModule{

	public static final IModuleType<TheoryHypothesesModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryHypothesesModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	TheoryHypothesesManager hypothesisManager;
	IPORoot target;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		IRodinFile scTheoryFile = (IRodinFile) element;
		ISCTheoryRoot scTheoryRoot = (ISCTheoryRoot) scTheoryFile.getRoot();
		target = repository.getTarget();
		ISCVariable[] axioms = scTheoryRoot.getSCVariables();
		IPOPredicateSet rootSet = target.getPredicateSet(TheoryHypothesesManager.ABS_HYP_NAME);
		rootSet.create(null, monitor);
		fetchSetsAndVariables(scTheoryRoot, rootSet, monitor);
		
		List<ISCPredicateElement> predicates = new LinkedList<ISCPredicateElement>();
		fetchPredicates(predicates, axioms);
		ISCPredicateElement[] predicateElements = new ISCPredicateElement[predicates.size()];		
		predicates.toArray(predicateElements);
		boolean accuracy = scTheoryRoot.isAccurate();
		hypothesisManager = 
			new TheoryHypothesesManager(scTheoryFile, target, predicateElements, accuracy);
		repository.setState(hypothesisManager);
	}
	
	private void fetchPredicates(
			List<ISCPredicateElement> predicates,
			ISCPredicateElement[] predicateElements) throws RodinDBException {
		
		for(ISCPredicateElement element : predicateElements) {
			predicates.add(element);
		}
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		hypothesisManager.createHypotheses(monitor);
		target = null;
		hypothesisManager = null;
		
		super.endModule(element, repository, monitor);
	}

}
