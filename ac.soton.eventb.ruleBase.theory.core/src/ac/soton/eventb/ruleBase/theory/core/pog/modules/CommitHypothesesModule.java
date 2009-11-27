package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * @author maamria
 *
 */
public abstract class CommitHypothesesModule extends POGProcessorModule {

	IHypothesisManager hypothesisManager;

	@SuppressWarnings("restriction")
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		hypothesisManager.makeImmutable();

	}

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		hypothesisManager = getHypothesisManager(repository);
	}
	
	protected abstract IHypothesisManager getHypothesisManager(
			IPOGStateRepository repository) throws CoreException;

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		hypothesisManager = null;
		super.endModule(element, repository, monitor);
	}
	
}
