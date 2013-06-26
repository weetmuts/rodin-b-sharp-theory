/**
 * 
 */
package org.eventb.theory.core.sc.modules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCImportTheoryProject;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.basis.SCTheoryDecorator;
import org.eventb.theory.core.maths.extensions.FormulaExtensionsLoader;
import org.eventb.theory.core.maths.extensions.dependencies.DeployedTheoriesGraph;
import org.eventb.theory.core.maths.extensions.dependencies.SCTheoriesGraph;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IImportProjectTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportTheoryProjectModule extends SCProcessorModule {
	
	private final IModuleType<ImportTheoryProjectModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".importTheoryProjectModule"); //$NON-NLS-1$
	
	private ITheoryRoot root;
	private IImportProjectTable projectTable;
	private static final String THEORY_PROJECT_NAME = "THPRJ";
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		root = (ITheoryRoot) file.getRoot();
		projectTable = (IImportProjectTable) repository.getState(IImportProjectTable.STATE_TYPE);
		repository.setState(projectTable);
	}
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		//checking conflict between imported theories and the self theory (importing theory)
		IImportTheoryProject[] theoryProjects = root.getImportTheoryProjects();
		for(IImportTheoryProject theoryProject: theoryProjects){
			IImportTheory[] importedTheories = theoryProject.getImportTheories();
			for (IImportTheory importedTheory: importedTheories){
				IDeployedTheoryRoot deployedTheoryRoot = importedTheory.getImportTheory();
				ISCTheoryRoot theory = (ISCTheoryRoot) deployedTheoryRoot;
				SCTheoryDecorator hierarchy = new SCTheoryDecorator(theory);
				
				ISCTheoryRoot selfTheory = root.getSCTheoryRoot();
				SCTheoryDecorator selfHierarchy = new SCTheoryDecorator(selfTheory);
				if (selfTheory.getRodinFile().exists() && hierarchy.isConflicting(selfHierarchy)){
					createProblemMarker(importedTheory,
							TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
							TheoryGraphProblem.TheoriesConflictError,
							selfTheory.getComponentName(), deployedTheoryRoot.getComponentName());
				}
			}	
		}
		
		projectTable = null;
		root = null;
		super.endModule(element, repository, monitor);
	}
	
	/**
	 * 
	 */
	public ImportTheoryProjectModule() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.ISCProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("restriction")
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		IImportTheoryProject[] theoryProjects = root.getImportTheoryProjects();
		
		if (theoryProjects.length == 0) {
			// commented this out as this should be ok
			//createProblemMarker(root,
					//TheoryGraphProblem.NoTheoryProjectClausesError);
		}
		else {
			monitor.subTask(Messages.bind(Messages.progress_TheoryProjects));
			initFilterModules(repository, monitor);

			int index = 0;
			for(IImportTheoryProject theoryProject: theoryProjects){
				if(!theoryProject.hasTheoryProject()){
					createProblemMarker(theoryProject,
							TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
							TheoryGraphProblem.ImportTheoryProjectMissing);
				}
				else if(filterModules(theoryProject, repository, monitor)){
					ISCImportTheoryProject scTheoryProject = saveSCTheoryProject((ISCTheoryRoot) target, theoryProject, index++, monitor);
					projectTable.addTheoryProject(theoryProject);
					
					//process Theories for this project
					processTheories(theoryProject, scTheoryProject, repository, monitor);
				}
				monitor.worked(1);
			}
			endFilterModules(repository, monitor);
		}
		
		IImportTheory[] importTheories = root.getImportTheories();
		if (importTheories != null)
			commitImports((ISCTheoryRoot) target, monitor);
		
		projectTable.makeImmutable();
		monitor.done();
	}
	
	private void processTheories(IImportTheoryProject theoryProject, IInternalElement target, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException{
		initProcessorModules(theoryProject, repository, monitor);
		processModules(theoryProject, target, repository, monitor);
		endProcessorModules(theoryProject, repository, monitor);
		
		Set<ISCTheoryRoot> importedTheories = new HashSet<ISCTheoryRoot>();
		//root.getSCTheoryRoot().getImportTheories();
		IImportTheory[] importTheories = root.getImportTheories();
		for (IImportTheory importTheory : importTheories) {
			ISCTheoryRoot importRoot = importTheory.getImportTheory();
			importedTheories.add(importRoot);
		}
		patchFormulaFactory(importedTheories, repository);		
	}

	private ISCImportTheoryProject saveSCTheoryProject(ISCTheoryRoot target,
			IImportTheoryProject theoryProject, int index,
			IProgressMonitor monitor) throws RodinDBException {
		ISCImportTheoryProject scTheoryProject = target.getSCImportTheoryProject(THEORY_PROJECT_NAME+"_"+index);
		scTheoryProject.create(null, monitor);
		scTheoryProject.setSCTheoryProject(theoryProject.getTheoryProject(), monitor); 
		scTheoryProject.setSource(theoryProject, monitor);
		
		return scTheoryProject;
	}
	
	/**
	 * Patches the formula factory to be used for the rest of the static checking process.
	 * @param importedTheories the set of imported theories to consider
	 * @param repository the state repository
	 * @throws CoreException
	 */
	protected void patchFormulaFactory(Set<ISCTheoryRoot> importedTheories,
			ISCStateRepository repository) throws CoreException {
		// need to patch up formula factory
		SCTheoriesGraph graph = new SCTheoriesGraph();
		//DeployedTheoriesGraph graph = new DeployedTheoriesGraph();
		graph.setElements(importedTheories);
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();

		for (ISCTheoryRoot root : graph.getElements()) {
			FormulaExtensionsLoader loader = new FormulaExtensionsLoader(root,
					factory);
			Set<IFormulaExtension> exts = loader.load();
			factory = factory.withExtensions(exts);
			typeEnvironment = AstUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		repository.setFormulaFactory(factory);
		repository.setTypeEnvironment(factory.makeTypeEnvironment());
	}
	
	/**
	 * Commits the theory imports to the target root.
	 * 
	 * @param targetRoot
	 *            the SC theory target
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	protected void commitImports(ISCTheoryRoot targetRoot,
			IProgressMonitor monitor) throws CoreException {
		
		IImportTheory[] importTheories = root.getImportTheories();
		for (IImportTheory currentImportTheory : importTheories) {
			ISCImportTheory scImport = targetRoot
					.getImportTheory(currentImportTheory.getElementName());
			scImport.create(null, monitor);
			scImport.setSource(currentImportTheory, monitor);
			scImport.setImportTheory(currentImportTheory.getImportTheory(),
					monitor);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
