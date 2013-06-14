package org.eventb.theory.core.maths.extensions;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * 
 * @author maamria
 *
 */
public class WorkspaceExtensionsManager implements IElementChangedListener{
	
	private final Set<IFormulaExtension> COND_EXTS;
	
	private Map<IRodinProject, ProjectManager> projectManagers;
	
	protected FormulaFactory basicFactory;
	protected FormulaFactory seedFactory ;
	
	public WorkspaceExtensionsManager() {
		RodinCore.addElementChangedListener(this);
		COND_EXTS = Collections.singleton(AstUtilities.COND);
		projectManagers = new HashMap<IRodinProject, ProjectManager>();
		basicFactory = FormulaFactory.getInstance(COND_EXTS);
		seedFactory = FormulaFactory.getInstance(basicFactory.getExtensions());
		populate();
	}

	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root){
		
		Set<IFormulaExtension> setOfExtensions= new LinkedHashSet<IFormulaExtension>();
		// add cond extension
		setOfExtensions.addAll(COND_EXTS);
		IRodinProject project = root.getRodinProject();
		
		try{
			ISCTheoryPathRoot[] paths = project.getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
			if (paths.length == 1){
				for (ISCAvailableTheoryProject availProj: paths[0].getSCAvailableTheoryProjects()){
					IRodinProject rodinProj = availProj.getSCAvailableTheoryProject();
					ProjectManager projectManager = projectManagers.get(rodinProj);
					if(projectManager != null){
						for (ISCAvailableTheory availThy : availProj.getSCAvailableTheories()){
							IDeployedTheoryRoot deployedTheoryRoot = availThy.getSCDeployedTheoryRoot();
							setOfExtensions.addAll(projectManager.getNeededTheories(deployedTheoryRoot));
							//add imported theories math extension
							for (IDeployedTheoryRoot importedThy : TheoryHierarchyHelper.getImportedTheories(deployedTheoryRoot)){
								setOfExtensions.addAll(projectManager.getNeededTheories(importedThy));
							}
						}
					}
				}
					
			}
			// else ignore paths
		} catch(CoreException e){
			CoreUtilities.log(e, "Error while processing theory path for project "+project);
		}

		//removed because a deployed local theory is not accessible by the local context/machine any more, 
		//it is just accessible when imported in a theory path (above if case).
/*		// case Theory
		if (root instanceof ITheoryRoot){
			return setOfExtensions;
		}
		
		// case SC Theory not in MathExtensions : basic set plus deployed in math extensions plus needed theories
		if (root instanceof ISCTheoryRoot){
			ProjectManager manager = projectManagers.get(project);
			if (manager != null)
				setOfExtensions.addAll(manager.getNeededTheories((ISCTheoryRoot) root));
			return setOfExtensions;
		}
		
		// case Model : add all deployed 
		if (!DatabaseUtilities.originatedFromTheory(root.getRodinFile())){
			ProjectManager manager = projectManagers.get(project);
			if (manager != null){
				setOfExtensions.addAll(manager.getAllDeployedExtensions());
			}
			return setOfExtensions;
		}
		
		// case theory dependent roots (not ITheoryRoot) : they get the SC theory root extensions
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile())){
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(root.getComponentName(), project);
			if (scRoot.exists()){
				return getFormulaExtensions(scRoot);
			}
		}*/
		return setOfExtensions;
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
			ProjectManager manager = projectManagers.get(proj);
			if (manager != null){
				manager.processDelta(delta);
			}
			else{
				projectManagers.put(proj, new ProjectManager(proj));
			}
		}
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		try {
			processDelta(delta);
			for (ProjectManager manager : projectManagers.values()){
				if(manager.hasDeployedChanged()){
					manager.reloadDeployedExtensions(seedFactory);
					manager.setDeployedChanged(false);
				}
				if(manager.hasSCChanged()){
					manager.reloadDirtyExtensions(seedFactory);
					manager.setSCChanged(false);
				}
			}
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error while processing changes in the database");
		}
	}
	
	private void populate(){
		try {
			for (IRodinProject project : RodinCore.getRodinDB().getRodinProjects()){
				ProjectManager manager = new ProjectManager(project);
				manager.populate(seedFactory);
				projectManagers.put(project, manager);
			}
			
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error while populating project managers for extensions");
		}
	}
	
}
