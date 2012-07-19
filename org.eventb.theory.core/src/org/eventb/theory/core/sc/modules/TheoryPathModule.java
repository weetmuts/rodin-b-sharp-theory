/**
 * 
 */
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ISCTheoryLanguageRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.TheoryPathAccuracyInfo;
import org.eventb.theory.core.sc.states.TheoryPathProjectTable;
import org.eventb.theory.core.sc.states.TheoryPathTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author renatosilva
 *
 */
public class TheoryPathModule extends SCProcessorModule {
	
	private final IModuleType<TheoryPathModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryPathModule");

	private final static int SYMTAB_SIZE = 2047;

	private TheoryPathAccuracyInfo accuracyInfo;
	private ISCTheoryLanguageRoot theoryLanguageRoot;
	private IRodinElement source;

	/**
	 * 
	 */
	public TheoryPathModule() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.ISCProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryLanguageRoot = (ISCTheoryLanguageRoot) target;
		source = element;
		processModules(element, target, repository, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		accuracyInfo = new TheoryPathAccuracyInfo();
		final TheoryPathProjectTable projectSymbolTable = new TheoryPathProjectTable(SYMTAB_SIZE);
		final TheoryPathTable theorySymbolTable = new TheoryPathTable(SYMTAB_SIZE);
		repository.setState(projectSymbolTable);
		repository.setState(theorySymbolTable);
		repository.setState(accuracyInfo);
		initProcessorModules(element, repository, monitor);
	};
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		theoryLanguageRoot.setAccuracy(accuracyInfo.isAccurate(), monitor);
		theoryLanguageRoot.setSource(source, monitor);
		super.endModule(element, repository, monitor);
		endProcessorModules(element, repository, monitor);
	}

}
