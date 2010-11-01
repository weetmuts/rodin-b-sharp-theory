/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static org.eventb.theory.internal.core.util.MathExtensionsUtilities.COND;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryCoreFacadeDB;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.eventb.theory.internal.core.maths.extensions.graph.WorkspaceDependenciesGraph;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * @author maamria
 * 
 */
public abstract class FormulaExtensionsWorkspaceManager implements
		IFormulaExtensionsWorkspaceManager {

	protected final Set<IFormulaExtension> EMPTY_EXT = new LinkedHashSet<IFormulaExtension>();

	protected WorkspaceDependenciesGraph graph;

	protected Map<DeployedEntry, Set<IFormulaExtension>> deployedExtensionsMap;
	protected Set<IFormulaExtension> allDeployedExtensions;

	protected Set<DeployedEntry> deployedEntries;
	protected Set<DeployedEntry> changedEntries;

	protected FormulaExtensionsWorkspaceManager() {

		this.deployedExtensionsMap = Collections
				.synchronizedMap(new LinkedHashMap<DeployedEntry, Set<IFormulaExtension>>());
		this.allDeployedExtensions = Collections
				.synchronizedSet(new LinkedHashSet<IFormulaExtension>());
		this.deployedEntries = new LinkedHashSet<FormulaExtensionsWorkspaceManager.DeployedEntry>();

		graph = new WorkspaceDependenciesGraph();

		try {
			populateDeployedEntries();
			this.changedEntries = new LinkedHashSet<FormulaExtensionsWorkspaceManager.DeployedEntry>(
					deployedEntries);
			populateDeployedExtensions();
			populateAllDeployedExtensions();
		} catch (CoreException e) {
			reset();
			CoreUtilities.log(e, "Failed to load deployed extensions.");
		}
	}

	@Override
	public Set<IFormulaExtension> getDeployedExtensions() throws CoreException {
		populateDeployedExtensions();
		populateAllDeployedExtensions();
		return new LinkedHashSet<IFormulaExtension>(allDeployedExtensions);
	}

	@Override
	public Set<IFormulaExtension> getDirtyExtensions(IEventBRoot root,
			FormulaFactory factory) throws CoreException {
		ISCTheoryRoot scRoot = TheoryCoreFacadeDB.getSCTheory(
				root.getComponentName(), root.getRodinProject());
		if (!scRoot.exists()) {
			return EMPTY_EXT;
		}
		TheoryTransformer transformer = new TheoryTransformer();
		return transformer.transform(scRoot, factory,
				factory.makeTypeEnvironment());
	}

	@Override
	public Set<IFormulaExtension> getNeededExtensions(ISCTheoryRoot root)
			throws CoreException {
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		FormulaFactory factory = MathExtensionsUtilities.getFactoryWithCond();
		if (!graph.isErroneous()) {
			for (IDeployedTheoryRoot dep : graph.getNeededTheories(root)) {
				TheoryTransformer transformer = new TheoryTransformer();
				Set<IFormulaExtension> exts = transformer.transform(dep,
						factory, factory.makeTypeEnvironment());
				factory = factory.withExtensions(exts);
			}
			extensions.addAll(factory.getExtensions());
			extensions.remove(COND);
		}
		return extensions;
	}

	@Override
	public Set<IFormulaExtension> getNeededExtensions(ITheoryRoot root)
			throws CoreException {
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		FormulaFactory factory = MathExtensionsUtilities.getFactoryWithCond();
		if (!graph.isErroneous()) {
			for (IDeployedTheoryRoot dep : graph.getNeededTheories(root)) {
				TheoryTransformer transformer = new TheoryTransformer();
				Set<IFormulaExtension> exts = transformer.transform(dep,
						factory, factory.makeTypeEnvironment());
				factory = factory.withExtensions(exts);
			}
			extensions.addAll(factory.getExtensions());
			extensions.remove(COND);
		}
		return extensions;
	}

	protected boolean reloadIfNecessary(DeployedEntry entry)
			throws CoreException {
		if (changedEntries.contains(entry)) {
			reloadDeployedEntry(entry.deployedRoot);
			changedEntries.remove(entry);
			return true;
		}
		return changedEntries.size() > 0;

	}

	protected void reset() {
		this.deployedEntries.clear();
		this.deployedExtensionsMap.clear();
		this.allDeployedExtensions.clear();
		this.graph.setDeployedRoots(new IDeployedTheoryRoot[0]);

	}

	protected abstract void populateDeployedEntries() throws CoreException;

	protected abstract void reloadDeployedEntry(IDeployedTheoryRoot deployedRoot)
			throws CoreException;

	protected abstract void populateAllDeployedExtensions();

	protected abstract void populateDeployedExtensions() throws CoreException;

	protected static class DeployedEntry {
		IDeployedTheoryRoot deployedRoot;
		String name;

		public DeployedEntry(IDeployedTheoryRoot deployedRoot) {
			this.deployedRoot = deployedRoot;
			this.name = deployedRoot.getComponentName();
		}

		public boolean equals(Object o) {
			if (o instanceof DeployedEntry) {
				return name.equals(((DeployedEntry) o).name);
			}
			return false;
		}

		public int hashCode() {
			return name.hashCode();
		}

	}

}
