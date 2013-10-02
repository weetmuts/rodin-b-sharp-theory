/*******************************************************************************
 * Copyright (c) 2010, 2013 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static org.eventb.theory.core.DatabaseUtilities.getNonTempSCTheoryPaths;

import java.util.Collection;
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
	
	private static final Set<IFormulaExtension> COND_EXTS = Collections
			.singleton(AstUtilities.COND);
	
	private static final WorkspaceExtensionsManager INSTANCE = new WorkspaceExtensionsManager();
	
	private Map<IRodinProject, ProjectManager> projectManagers;
	
	protected FormulaFactory basicFactory;
	protected FormulaFactory seedFactory ;
	
	private WorkspaceExtensionsManager() {
		RodinCore.addElementChangedListener(this);
		
		projectManagers = new HashMap<IRodinProject, ProjectManager>();
		basicFactory = FormulaFactory.getInstance(COND_EXTS);
		seedFactory = FormulaFactory.getInstance(basicFactory.getExtensions());
	}

	public static WorkspaceExtensionsManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Returns the manager for the given project, creating it if needed.
	 * <p>
	 * IMPORTANT: use that method everywhere to access project managers.
	 * </p>
	 * 
	 * @param proj
	 *            the managed project
	 * @return the manager for the given project.
	 * @throws CoreException if a problem occurs while populating the manager 
	 */
	private synchronized ProjectManager fetchManager(IRodinProject proj)
			throws CoreException {
		ProjectManager manager = projectManagers.get(proj);
		if (manager == null) {
			manager = new ProjectManager(proj);
			manager.populate(seedFactory);
			projectManagers.put(proj, manager);
		}
		return manager;
	}
	
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root){
		
		Set<IFormulaExtension> setOfExtensions= new LinkedHashSet<IFormulaExtension>();
		// add cond extension
		setOfExtensions.addAll(COND_EXTS);
		IRodinProject project = root.getRodinProject();
		
		try{
			ISCTheoryPathRoot[] paths = getNonTempSCTheoryPaths(project);
			// theories cannot depend on theory path, so the presence of a
			// theory path should not change the input language of a theory
			if (paths.length == 1 && !(root instanceof ITheoryRoot) && !(root instanceof ISCTheoryRoot)){
				for (ISCAvailableTheoryProject availProj: paths[0].getSCAvailableTheoryProjects()){
					final IRodinProject rodinProj = availProj.getSCAvailableTheoryProject();
					final ProjectManager projectManager = fetchManager(rodinProj);
					for (ISCAvailableTheory availThy : availProj.getSCAvailableTheories()){
						IDeployedTheoryRoot deployedTheoryRoot = availThy.getSCDeployedTheoryRoot();
						//when availThy is undeployed then deployedTheoryRoot = null
						if (deployedTheoryRoot != null) {
							setOfExtensions.addAll(projectManager.getNeededTheories(deployedTheoryRoot));
						}
					}
				}
					
			}
			// else ignore paths
		} catch(CoreException e){
			CoreUtilities.log(e, "Error while processing theory path for project "+project);
		}

		// case unchecked Theory
		if (root instanceof ITheoryRoot){
			return setOfExtensions;
		}
		
		// case SC Theory: basic set + from needed theories + from given theory
		if (root instanceof ISCTheoryRoot){
			try {
				final ProjectManager manager = fetchManager(project);
				setOfExtensions.addAll(manager.getNeededTheories((ISCTheoryRoot) root));
				return setOfExtensions;
			} catch (CoreException e) {
				CoreUtilities.log(e,
						"Error while processing SC theory " + root.getPath());
			}
		}
		
		// case theory dependent roots (not ITheoryRoot) : they get the SC theory root extensions
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile())){
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(root.getComponentName(), project);
			if (scRoot.exists()){
				return getFormulaExtensions(scRoot);
			}
		}
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
			//FIXME handle deleted projects
			IRodinProject proj = (IRodinProject) element;
			ProjectManager manager = fetchManager(proj);
			manager.processDelta(delta);
		}
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		final Collection<ProjectManager> managers; 
		synchronized(this) {
			managers = projectManagers.values();
		}
		try {
			processDelta(delta);
			for (ProjectManager manager : managers){
				if(manager.hasDeployedChanged()){
					manager.reloadDeployedExtensions(seedFactory);
					manager.setDeployedChanged(false);
				}
			}
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error while processing changes in the database");
		}
	}
	
	/**
	 * Informs that the given SC theory has changed and reloads its extensions.
	 * 
	 * @param scTheory
	 *            the changed SC theory root
	 */
	public void scTheoryChanged(ISCTheoryRoot scTheory) {
		try {
			final ProjectManager manager = fetchManager(scTheory.getRodinProject());
			manager.reloadDirtyExtensions(seedFactory);
		} catch (CoreException e) {
			CoreUtilities.log(e,
					"Error while processing changes in SC theory " + scTheory.getPath());
		}
	}
	
}
