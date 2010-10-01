/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static org.eventb.theory.internal.core.util.MathExtensionsUtilities.COND;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * @author maamria
 * 
 */
public class ProjectManager extends FormulaExtensionsProjectManager {

	public ProjectManager(IRodinProject project) throws CoreException {
		super(project);
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		try {
			processDelta(delta);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected void populateDeployedEntries() throws CoreException {
		deployedEntries.clear();
		IDeployedTheoryRoot[] roots = TheoryCoreFacade
				.getDeployedTheories(project);
		for (IDeployedTheoryRoot root : roots) {
			deployedEntries.add(new DeployedEntry(root));
		}
	}

	protected void populateAllDeployedExtensions() {
		synchronized (deployedExtensionsMap) {
			updateExtensionsMap();
			allDeployedExtensions.clear();
			for (Set<IFormulaExtension> exts : deployedExtensionsMap.values()) {
				allDeployedExtensions.addAll(exts);
			}
		}
	}

	protected void updateExtensionsMap(){
		Iterator<Entry<DeployedEntry, Set<IFormulaExtension>>> iter = deployedExtensionsMap.entrySet().iterator();
		while (iter.hasNext()){
			if(!deployedEntries.contains(iter.next().getKey())){
				iter.remove();
			}
		}
		
	}
	
	protected void reloadDeployedEntry(IDeployedTheoryRoot deployedRoot)
			throws CoreException {
		DeployedEntry entry = new DeployedEntry(deployedRoot);
		setGraph(new ISCTheoryRoot[0]);
		Set<IDeployedTheoryRoot> needed = projectGraph
				.getNeededTheories(deployedRoot);
		Set<IFormulaExtension> neededExts = new LinkedHashSet<IFormulaExtension>();
		for (IDeployedTheoryRoot root : needed) {
			neededExts.addAll(deployedExtensionsMap
					.get(new DeployedEntry(root)));
		}
		FormulaFactory factory = MathExtensionsUtilities.getFactoryWithCond();
		factory = factory.withExtensions(neededExts);
		TheoryTransformer transformer = new TheoryTransformer();
		Set<IFormulaExtension> ext = transformer.transform(deployedRoot,
				factory, factory.makeTypeEnvironment());
		deployedExtensionsMap.put(entry, ext);
	}

	protected void populateDeployedExtensions() throws CoreException {
		FormulaFactory factory = MathExtensionsUtilities.getFactoryWithCond();
		for (IDeployedTheoryRoot deployedRoot : projectGraph.getDeployedRoots()) {
			TheoryTransformer transformer = new TheoryTransformer();
			Set<IFormulaExtension> exts = transformer.transform(deployedRoot,
					factory, factory.makeTypeEnvironment());
			factory = factory.withExtensions(exts);
			exts.remove(COND);
			deployedExtensionsMap.put(new DeployedEntry(deployedRoot), exts);
		}

	}

	protected void processDelta(IRodinElementDelta delta) 
	throws CoreException{
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();
		if (element instanceof IRodinDB) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
		if (element instanceof IRodinProject) {
			IRodinProject proj = (IRodinProject) element;
			if (proj.getElementName().equals(project.getElementName())) {
				for (IRodinElementDelta d : affected) {
					processDelta(d);
				}
			}
		}
		if(element instanceof IRodinFile){
			IRodinFile file = (IRodinFile) element;
			if(file.getRoot() instanceof IDeployedTheoryRoot &&
					delta.getKind() == IRodinElementDelta.REMOVED){
				IDeployedTheoryRoot root = (IDeployedTheoryRoot) file.getRoot();
				DeployedEntry key = new DeployedEntry(root ); 
				changedEntries.add(key);
				populateDeployedEntries();
				return;
			}
			for (IRodinElementDelta d : affected) {
				if(file.getRoot() instanceof IDeployedTheoryRoot)
					processDelta(d);
			}
		}
		if (element instanceof IDeployedTheoryRoot) {
			IDeployedTheoryRoot root = (IDeployedTheoryRoot)element;
			if (delta.getKind() == IRodinElementDelta.CHANGED) {
				DeployedEntry key = new DeployedEntry(root); 
				changedEntries.add(key);
				populateDeployedEntries();
			}
		}
	}

}
