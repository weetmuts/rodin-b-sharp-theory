/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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
public class WorkspaceManager implements IElementChangedListener{

	final Set<IFormulaExtension> EMPTY_SET = Collections
				.unmodifiableSet(new LinkedHashSet<IFormulaExtension>());
	
	private ProjectManager globalProjectManager;
	private Map<IRodinProject, ProjectManager> projectManagers;
	
	protected FormulaFactory basicFactory;
	protected FormulaFactory seedFactory ;
	
	public WorkspaceManager(){
		RodinCore.addElementChangedListener(this);
		globalProjectManager = new ProjectManager(DB_TCFacade.getDeploymentProject(null));
		projectManagers = new HashMap<IRodinProject, ProjectManager>();
		
		basicFactory = FormulaFactory.getInstance(MathExtensionsUtilities.singletonExtension(MathExtensionsUtilities.COND));
		seedFactory = FormulaFactory.getInstance(basicFactory.getExtensions());
		populate();
	}
	
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root){
		IRodinProject project = root.getRodinProject();
		ProjectManager manager = 
			project.getElementName().equals(DB_TCFacade.THEORIES_PROJECT) ? globalProjectManager :projectManagers.get(project);
		if(root instanceof ITheoryRoot && !manager.managingMathExtensionsProject()){
			return globalProjectManager.getAllDeployedExtensions();
		}
		else if (root instanceof ITheoryRoot && manager.managingMathExtensionsProject()){
			return EMPTY_SET;
		}
		if (root instanceof ISCTheoryRoot){
			return manager.getNeededTheories((ISCTheoryRoot) root);
		}
		return null;
	}
	
	private void populate(){
		try {
			globalProjectManager.populate(basicFactory);
			seedFactory = seedFactory.withExtensions(globalProjectManager.getAllDeployedExtensions());
			for (IRodinProject project : RodinCore.getRodinDB().getRodinProjects()){
				if(project.isOpen()){
					ProjectManager manager = new ProjectManager(project);
					manager.populate(seedFactory);
					projectManagers.put(project, manager);
				}
			}
			
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error while populating project managers for extensions");
		}
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
			}
			if (globalProjectManager.hasSCChanged()){
				globalProjectManager.reloadDirtyExtensions(basicFactory);
			}
			for (ProjectManager manager : projectManagers.values()){
				if(manager.hasDeployedChanged()){
					manager.reloadDeployedExtensions(seedFactory);
				}
				if(manager.hasSCChanged()){
					manager.reloadDirtyExtensions(seedFactory);
				}
			}
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error while processing changes in the database");
		}
	}

	
}
