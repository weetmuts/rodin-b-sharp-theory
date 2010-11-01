package org.eventb.theory.internal.core.pog;

import static org.eventb.theory.core.TheoryCoreFacadeDB.hasDeployedVersion;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.modules.BaseModule;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.pog.states.TheoremsAccumulator;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryPOGModule extends BaseModule {

	public static final IModuleType<TheoryPOGModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryModule"); 
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		IRodinFile file = (IRodinFile) element;
		//ensure sync
		ISCTheoryRoot root = (ISCTheoryRoot) file.getRoot();
		if(hasDeployedVersion(root)){
			return;
		}
		TheoremsAccumulator accumulator = new TheoremsAccumulator();
		repository.setState(accumulator);
		super.initModule(element, repository, monitor);
	}
	
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		IRodinFile file = (IRodinFile) element;
		//ensure sync
		ISCTheoryRoot root = (ISCTheoryRoot) file.getRoot();
		if(!hasDeployedVersion(root)){
			repository.removeState(TheoremsAccumulator.STATE_TYPE);
			super.endModule(element, repository, monitor);
		}
	}
	
	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		IRodinFile file = (IRodinFile) element;
		//ensure sync
		ISCTheoryRoot root = (ISCTheoryRoot) file.getRoot();
		if(!hasDeployedVersion(root)){
			processModules(element, repository, monitor);
		}
		
		
	}
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
