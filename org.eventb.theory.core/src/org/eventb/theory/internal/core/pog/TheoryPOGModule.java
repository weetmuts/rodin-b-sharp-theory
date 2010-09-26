package org.eventb.theory.internal.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.modules.BaseModule;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
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
		IRodinFile scTheoryFile = (IRodinFile) element;
		FormulaFactory factory = ((ISCTheoryRoot)scTheoryFile.getRoot()).getFormulaFactory();
		repository.setFormulaFactory(factory);
		repository.setTypeEnvironment(MathExtensionsUtilities.getTypeEnvironmentForFactory(repository.getTypeEnvironment(), factory));
		super.initModule(element, repository, monitor);
	}
	
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
	}
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
