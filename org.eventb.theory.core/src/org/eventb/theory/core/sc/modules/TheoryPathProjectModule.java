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
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.ITheoryPathProjectTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class TheoryPathProjectModule extends SCProcessorModule {
	
	private final IModuleType<TheoryPathProjectModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".availableTheoryProjectModule"); //$NON-NLS-1$
	
	private ITheoryPathRoot root;
	private ITheoryPathProjectTable projectTable;
	private static final String THEORY_PROJECT_NAME = "THPRJ";
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		root = (ITheoryPathRoot) file.getRoot();
		projectTable = (ITheoryPathProjectTable) repository.getState(ITheoryPathProjectTable.STATE_TYPE);
		repository.setState(projectTable);
	}
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		projectTable = null;
		root = null;
		super.endModule(element, repository, monitor);
	}
	
//	/**
//	 * Processes the theory projects.
//	 * 
//	 * @param theoryProjects
//	 *            the theoryProjects
//	 * @param targetRoot
//	 *            the SC theorypath target
//	 * @param repository
//	 *            the state repository
//	 * @param monitor
//	 *            the progress monitor
//	 * @throws CoreException
//	 */
//	protected void processTheoryProjects(IAvailableTheoryProject theoryProject,
//			ISCTheoryLanguageRoot targetRoot, ISCStateRepository repository,
//			IProgressMonitor monitor) throws CoreException {
//		
//		
//		
//		
//		
//		
//		boolean isAccurate = true;
//		// variable used to check against direct and indirect redundancy
//		Set<ISCTheoryRoot> importedTheories = new HashSet<ISCTheoryRoot>();
//		
//		
//		initFilterModules(repository, monitor);
//		if(filterModules(includeClause, repository, monitor)){
//			monitor.worked(1);
//		}
//		
//		endFilterModules(repository, monitor);
//		
//		monitor.worked(1);
//		
//		
////		for (IImportTheory importTheory : importTheories) {
////			// missing attribute
////			if (!importTheory.hasImportTheory()) {
////				createProblemMarker(importTheory,TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,TheoryGraphProblem.ImportTheoryMissing);
////				isAccurate = false;
////				continue;
////			}
////			ISCTheoryRoot importRoot = importTheory.getImportTheory();
////			// target does not exist
////			if (!importRoot.exists()) {
////				createProblemMarker(importTheory,TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,TheoryGraphProblem.ImportTheoryNotExist, importRoot.getComponentName());
////				isAccurate = false;
////				continue;
////			}
////			// direct redundancy
////			if (importedTheories.contains(importRoot)) {
////				createProblemMarker(importTheory,TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,TheoryGraphProblem.RedundantImportWarning,importRoot.getComponentName());
////				isAccurate = false;
////				continue;
////			}
////			// add to the sets
////			importedTheories.add(importRoot);
////			importTheoriesDirectives.add(importTheory);
////		}
////		// clear to use differently
////		importedTheories.clear();
////		// filter imports
////		isAccurate &= filterImports(importedTheories);
////		patchFormulaFactory(importedTheories, repository);
////		if (!isAccurate){
////			accuracyInfo.setNotAccurate();
////		}
//	}
	
	/**
	 * 
	 */
	public TheoryPathProjectModule() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.ISCProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		IAvailableTheoryProject[] theoryProjects = root.getAvailableTheoryProjects();
		
		if (theoryProjects.length == 0) {
			createProblemMarker(root,
					TheoryGraphProblem.NoTheoryProjectClausesError);
		}
		else {
			monitor.subTask(Messages.bind(Messages.progress_TheoryProjects));
			initFilterModules(repository, monitor);

			int index = 0;
			for(IAvailableTheoryProject theoryProject: theoryProjects){
				if(!theoryProject.hasTheoryProject()){
					createProblemMarker(theoryProject,
							TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
							TheoryGraphProblem.NoTheoryProjectClausesError);
				}
				else if(filterModules(theoryProject, repository, monitor)){
					ISCAvailableTheoryProject scTheoryProject = saveSCTheoryProject((ISCTheoryPathRoot) target, theoryProject, index++, monitor);
					projectTable.addTheoryProject(theoryProject);
					
					//process Theories for this project
					processTheories(theoryProject, scTheoryProject, repository, monitor);
				}
				monitor.worked(1);
			}
		}
		
//		theoryTable.makeImmutable();
		projectTable.makeImmutable();
		monitor.done();
	}
	
	private void processTheories(IAvailableTheoryProject theoryProject, IInternalElement target, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException{
		initProcessorModules(theoryProject, repository, monitor);
		processModules(theoryProject, target, repository, monitor);
		endProcessorModules(theoryProject, repository, monitor);
	}

	private ISCAvailableTheoryProject saveSCTheoryProject(ISCTheoryPathRoot target,
			IAvailableTheoryProject theoryProject, int index,
			IProgressMonitor monitor) throws RodinDBException {
		ISCAvailableTheoryProject scTheoryProject = target.getSCAvailableTheoryProject(THEORY_PROJECT_NAME+"_"+index);
		scTheoryProject.create(null, monitor);
		scTheoryProject.setSCTheoryProject(theoryProject, monitor); 
		scTheoryProject.setSource(theoryProject, monitor);
		
		return scTheoryProject;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
