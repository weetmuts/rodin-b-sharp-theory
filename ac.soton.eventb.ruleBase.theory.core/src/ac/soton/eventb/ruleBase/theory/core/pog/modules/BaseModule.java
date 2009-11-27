package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * @author maamria
 *
 */
public abstract class BaseModule extends POGProcessorModule {

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		endProcessorModules(element, repository, monitor);
		
	}

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		initProcessorModules(element, repository, monitor);
	}

	public void process(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		processModules(element, repository, monitor);
		
	}

}
