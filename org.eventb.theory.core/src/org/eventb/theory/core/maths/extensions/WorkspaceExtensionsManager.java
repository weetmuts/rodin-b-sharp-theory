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
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
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
	
	final Set<IFormulaExtension> EMPTY_SET = Collections
			.unmodifiableSet(new LinkedHashSet<IFormulaExtension>());
	
	private ProjectManager globalProjectManager;
	private Map<IRodinProject, ProjectManager> projectManagers;
	
	protected FormulaFactory basicFactory;
	protected FormulaFactory seedFactory ;
	
	public WorkspaceExtensionsManager() {
		RodinCore.addElementChangedListener(this);
		projectManagers = new HashMap<IRodinProject, ProjectManager>();
		globalProjectManager = new ProjectManager(DB_TCFacade.getDeploymentProject(null));
		basicFactory = FormulaFactory.getInstance(MathExtensionsUtilities.singletonExtension(MathExtensionsUtilities.COND));
		seedFactory = FormulaFactory.getInstance(basicFactory.getExtensions());
		populate();
	}

	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root){
		IRodinProject project = root.getRodinProject();
		boolean isMathExtensionsProject = project.getElementName().equals(DB_TCFacade.THEORIES_PROJECT);
		// case Theory in MathExtensions
		if (isMathExtensionsProject && root instanceof ITheoryRoot){
			return EMPTY_SET;
		}
		// case Theory not in MathExtensions
		if (!isMathExtensionsProject && root instanceof ITheoryRoot){
			return globalProjectManager.getAllDeployedExtensions();
		}
		
		// case SC Theory in MathExtensions
		if (isMathExtensionsProject && root instanceof ISCTheoryRoot){
			return globalProjectManager.getNeededTheories((ISCTheoryRoot) root);
		}
		// case SC Theory not in MathExtensions
		if (!isMathExtensionsProject && root instanceof ISCTheoryRoot){
			ProjectManager manager = projectManagers.get(project);
			Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
			extensions.addAll(globalProjectManager.getAllDeployedExtensions());
			if (manager != null)
				extensions.addAll(manager.getNeededTheories((ISCTheoryRoot) root));
			return extensions;
		}
		
		// case Model 
		if (!DB_TCFacade.originatedFromTheory(root.getRodinFile())){
			ProjectManager manager = projectManagers.get(project);
			Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
			extensions.addAll(globalProjectManager.getAllDeployedExtensions());
			if (manager != null){
				extensions.addAll(manager.getAllDeployedExtensions());
			}
			return extensions;
		}
		// case theory dependent roots
		if (DB_TCFacade.originatedFromTheory(root.getRodinFile())){
			ISCTheoryRoot scRoot = DB_TCFacade.getSCTheory(root.getComponentName(), project);
			if (scRoot.exists()){
				return getFormulaExtensions(scRoot);
			}
		}
		return EMPTY_SET;
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
			ProjectManager manager = 
				proj.getElementName().equals(DB_TCFacade.THEORIES_PROJECT) ? globalProjectManager :projectManagers.get(proj);
			if (manager != null){
				manager.processDelta(delta);
			}
		}
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		try {
			processDelta(delta);
			if (globalProjectManager.hasDeployedChanged()){
				globalProjectManager.reloadDeployedExtensions(basicFactory);
				seedFactory = basicFactory.withExtensions(globalProjectManager.getAllDeployedExtensions());
				globalProjectManager.setDeployedChanged(false);
			}
			if (globalProjectManager.hasSCChanged()){
				globalProjectManager.reloadDirtyExtensions(basicFactory);
				globalProjectManager.setSCChanged(false);
			}
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
			globalProjectManager.populate(basicFactory);
			seedFactory = seedFactory.withExtensions(globalProjectManager.getAllDeployedExtensions());
			for (IRodinProject project : RodinCore.getRodinDB().getRodinProjects()){
				if(!project.getElementName().equals(DB_TCFacade.THEORIES_PROJECT) && project.isOpen()){
					ProjectManager manager = new ProjectManager(project);
					manager.populate(seedFactory);
					projectManagers.put(project, manager);
				}
			}
			
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error while populating project managers for extensions");
		}
	}
	
}
