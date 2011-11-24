/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.basis.TheoryDeployer;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.DeployElementRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Accessibility class for some fields and methods for other plug-ins. Includes
 * mostly Rodin database related facilities.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class DatabaseUtilities {

	// Global constants
	public static final String THEORIES_PROJECT = "MathExtensions";
	// As in "theory unchecked file"
	public static final String THEORY_FILE_EXTENSION = "tuf";
	// As in "theory checked file"
	public static final String SC_THEORY_FILE_EXTENSION = "tcf";
	// As in "deployed theory file"
	public static final String DEPLOYED_THEORY_FILE_EXTENSION = "dtf";

	// The theory configuration for the SC and POG
	public static final String THEORY_CONFIGURATION = TheoryPlugin.PLUGIN_ID + ".thy";

	public static final String[] POSSIBLE_APPLICABILITY_TYPES = new String[] { 
		"automatic", 
		"interactive",
		"both"};
	
	/**
	 * Returns the {@link RuleApplicability} object corresponding to the given string.
	 * @param str the string
	 * @return the rule applicability object
	 */
	public static RuleApplicability getRuleApplicability(String str){
		if (str.equalsIgnoreCase(POSSIBLE_APPLICABILITY_TYPES[0])) {
			return RuleApplicability.AUTOMATIC;
		} else if (str.equalsIgnoreCase(POSSIBLE_APPLICABILITY_TYPES[1])) {
			return RuleApplicability.INTERACTIVE;
		}
		else if (str.equalsIgnoreCase(POSSIBLE_APPLICABILITY_TYPES[2])){
			return RuleApplicability.AUTOMATIC_AND_INTERACTIVE;
		}
		return RuleApplicability.INTERACTIVE; 
	}
	
	/**
	 * Returns a user-friendly string representation of the rule applicability object.
	 * @param app the rule applicability
	 * @return the string view
	 */
	public static String getString(RuleApplicability app){
		switch(app){
		case AUTOMATIC : return POSSIBLE_APPLICABILITY_TYPES[0];
		case INTERACTIVE : return POSSIBLE_APPLICABILITY_TYPES[1];
		case AUTOMATIC_AND_INTERACTIVE : return POSSIBLE_APPLICABILITY_TYPES[2];
		}
		return POSSIBLE_APPLICABILITY_TYPES[1];
	}
	
	/**
	 * Returns the {@link RuleApplicability} object corresponding to the boolean modifiers for automated and interactive application.
	 * @param isAutomatic whether the rule is automatic
	 * @param isInteractive whether the rule is interactive
	 * @return the rule applicability object
	 */
	public static RuleApplicability getRuleApplicability(boolean isAutomatic, boolean isInteractive){
		if (isAutomatic && isInteractive)
			return RuleApplicability.AUTOMATIC_AND_INTERACTIVE;
		if (isAutomatic)
			return RuleApplicability.AUTOMATIC;
		else
			return RuleApplicability.INTERACTIVE;
	}
	
	/**
	 * Returns whether the given applicability enables automatic application.
	 * @param app the rule applicability
	 * @return whether the given applicability enables automatic application
	 */
	public static boolean isAutomatic(RuleApplicability app){
		return app.equals(RuleApplicability.AUTOMATIC) ||
			app.equals(RuleApplicability.AUTOMATIC_AND_INTERACTIVE);
	}
	
	/**
	 * Returns whether the given applicability enables interactive application.
	 * @param app the rule applicability
	 * @return whether the given applicability enables interactive application
	 */
	public static boolean isInteractive(RuleApplicability app){
		return app.equals(RuleApplicability.INTERACTIVE) ||
			app.equals(RuleApplicability.AUTOMATIC_AND_INTERACTIVE);
	}
	
	/**
	 * Returns a SC theory that exists in the given project. If the theory does
	 * not exist, <code>null</code> is returned.
	 * 
	 * @param fullName
	 *            the name of the SC theory
	 * @param project
	 *            the rodin project
	 * @return the SC theory
	 * @throws CoreException
	 */
	public static ISCTheoryRoot getExistingTheoryRoot(String fullName, String project) throws CoreException {
		IRodinProject rodinProject = getRodinProject(project);
		if (rodinProject != null) {
			ISCTheoryRoot theoryRoot = getSCTheory(fullName, rodinProject);
			if (theoryRoot.exists()){
				return theoryRoot;
			}
		}
		return null;
	}

	/**
	 * Returns the SC theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @param filter
	 *            theories filter
	 * @return SC theories
	 * @throws CoreException
	 */
	public static ISCTheoryRoot[] getSCTheoryRoots(IRodinProject project, ITheoryFilter<ISCTheoryRoot> filter) throws CoreException {
		ISCTheoryRoot[] roots = project.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		List<ISCTheoryRoot> okRoots = new ArrayList<ISCTheoryRoot>();
		for (ISCTheoryRoot root : roots) {
			if (filter.filter(root)) {
				okRoots.add(root);
			}
		}
		return okRoots.toArray(new ISCTheoryRoot[okRoots.size()]);
	}

	/**
	 * Returns the SC theory parent of the given element if any.
	 * 
	 * @param element
	 *            the rodin element
	 * @return the parent theory
	 */
	public static ISCTheoryRoot getSCTheoryParent(IRodinElement element) {
		return element.getAncestor(ISCTheoryRoot.ELEMENT_TYPE);
	}

	/**
	 * Returns a set of string representations of the array of the given
	 * elements.
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param elements
	 *            the actual elements
	 * @return the set
	 */
	public static <E extends IInternalElement> Set<String> getNames(E[] elements) {
		Set<String> set = new LinkedHashSet<String>();
		for (E e : elements) {
			set.add(e.getElementName());
		}
		return set;
	}

	/**
	 * Returns a set of string representations of the list of the given
	 * elements.
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param elements
	 *            the actual elements
	 * @return the set
	 */
	public static <E extends IInternalElement> Set<String> getNames(Collection<E> elements) {
		Set<String> set = new LinkedHashSet<String>();
		for (E e : elements) {
			set.add(e.getElementName());
		}
		return set;
	}

	/**
	 * Returns whether the theory contains no definitions or rules, or whether
	 * the theory is not accurate.
	 * 
	 * @param root
	 *            the SC theory root
	 * @return whether the theory is empty or not accurate
	 */
	public static boolean isTheoryEmptyOrNotAccurate(ISCTheoryRoot root) {
		int l = 0;
		try {
			if (root.exists()) {
				if (!root.isAccurate()) {
					return true;
				}
				l = root.getSCDatatypeDefinitions().length + root.getSCNewOperatorDefinitions().length + root.getProofRulesBlocks().length + root.getTheorems().length;
				// need to check for contributed children
				DeployElementRegistry registry = DeployElementRegistry.getDeployedElementsRegistry();
				List<IInternalElementType<IInternalElement>> rootDeployedElements = registry.getRootDeployedElements();
				for (IInternalElementType<IInternalElement> type : rootDeployedElements){
					l += root.getChildrenOfType(type).length;
				}
			}
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "Failed to retrieve details from theory.");
		}
		return l == 0;
	}
	
	/**
	 * Returns whether a theory is deployable i.e., that it is not empty and is accurate.
	 * @param theoryRoot the theory
	 * @return whether a theory is deployable
	 */
	public static boolean isTheoryDeployable(ITheoryRoot theoryRoot){
		ISCTheoryRoot scRoot = theoryRoot.getSCTheoryRoot();
		return isTheoryDeployable(scRoot);
	}
	
	/**
	 * Returns whether a theory is deployable i.e., that it is not empty and is accurate.
	 * @param scRoot the theory
	 * @return whether a theory is deployable
	 */
	public static boolean isTheoryDeployable(ISCTheoryRoot scRoot){
		return scRoot.exists() && !isTheoryEmptyOrNotAccurate(scRoot);
	}

	/**
	 * Returns a handle to the SC theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the SC theory
	 */
	public static ISCTheoryRoot getSCTheory(String name, IRodinProject project) {
		IRodinFile file = project.getRodinFile(getSCTheoryFullName(name));
		return (ISCTheoryRoot) file.getRoot();
	}

	/**
	 * Returns a handle to the theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the theory
	 */
	public static ITheoryRoot getTheory(String name, IRodinProject project) {
		IRodinFile file = project.getRodinFile(getTheoryFullName(name));
		return (ITheoryRoot) file.getRoot();
	}

	/**
	 * Returns the full name of a theory file.
	 * 
	 * @param bareName
	 *            the name
	 * @return the full name
	 */
	public static String getTheoryFullName(String bareName) {
		return bareName + "." + THEORY_FILE_EXTENSION;
	}

	/**
	 * Returns the full name of a SC theory file.
	 * 
	 * @param bareName
	 *            the name
	 * @return the full name
	 */
	public static String getSCTheoryFullName(String bareName) {
		return bareName + "." + SC_THEORY_FILE_EXTENSION;
	}

	/**
	 * Returns the project with the given name if it exists.
	 * 
	 * @param name
	 *            the project name
	 * @return the project or <code>null</code> if the project does not exist
	 */
	public static IRodinProject getRodinProject(String name) {
		IRodinProject project = RodinCore.getRodinDB().getRodinProject(name);
		if (project.exists())
			return project;
		else
			return null;
	}

	/**
	 * Returns whether the given rodin file originated from a theory file.
	 * 
	 * @param file
	 *            the rodin file
	 * @return whether the file originated from a theory
	 * @throws CoreException
	 */
	public static boolean originatedFromTheory(IRodinFile file) {
		IRodinProject project = file.getRodinProject();
		ITheoryRoot theoryRoot = getTheory(file.getBareName(), project);
		if (theoryRoot.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the file originated from a theory within the given rodin project.
	 * @param file the rodin file
	 * @param project the rodin project
	 * @return whether the file originated from a theory within the given rodin project
	 */
	public static boolean originatedFromTheory(IRodinFile file, IRodinProject project) {
		if (file.getRodinProject().equals(project)) {
			return originatedFromTheory(file);
		} else {
			return false;
		}
	}

	/**
	 * Rebuilds the given rodin project.
	 * 
	 * @param project
	 *            the rodin project
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	public static void rebuild(final IRodinProject project, IProgressMonitor monitor) throws CoreException {
		if (project == null || !project.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, TheoryPlugin.PLUGIN_ID, "attempting to rebuild a non-existent project"));
		}
		RodinCore.run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IProject nativeProj = ((IProject) project.getAdapter(IProject.class));
				if (nativeProj != null) {
					nativeProj.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
				}
			}
		}, monitor);
		
	}

	/**
	 * Rebuilds the entire workspace.
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	public static void rebuild(IProgressMonitor monitor) throws CoreException {
		RodinCore.run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				for (IRodinProject project : RodinCore.getRodinDB().getRodinProjects()) {
					IProject nativeProj = ((IProject) project.getAdapter(IProject.class));
					if (nativeProj != null) {
						nativeProj.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
					}
				}
			}
		}, monitor);

	}
	
	/**
	 * Returns a deployer for the given theory.
	 * 
	 * @param theoryName
	 *            the name of the SC theory, must exist
	 * @param project
	 *            the Event-B project, must exist
	 * @param targetProject
	 *            the Event-B target project, must exist
	 * @return the deployed theory
	 * @throws CoreException
	 */
	public static final ITheoryDeployer getTheoryDeployer(String theoryName, String project, boolean rebuildProject) throws CoreException {
		ISCTheoryRoot theoryRoot = getExistingTheoryRoot(theoryName, project);
		if (theoryRoot == null) {
			return null;
		}
		return new TheoryDeployer(theoryRoot, rebuildProject);
	}

	/**
	 * Returns the deployed theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @param filter
	 *            theories filter
	 * @return deployed theories
	 * @throws CoreException
	 */
	public static IDeployedTheoryRoot[] getDeployedTheories(IRodinProject project, ITheoryFilter<IDeployedTheoryRoot> filter) throws CoreException {
		IDeployedTheoryRoot[] roots = project.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
		List<IDeployedTheoryRoot> okRoots = new ArrayList<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot root : roots) {
			if (filter.filter(root)) {
				okRoots.add(root);
			}
		}
		return okRoots.toArray(new IDeployedTheoryRoot[okRoots.size()]);
	}

	/**
	 * Returns the list of SC theories that can be deployed i.e., theories that
	 * are accurate and not empty.
	 * 
	 * @return list of deployable SC theories
	 * @throws CoreException
	 */
	public static List<ISCTheoryRoot> getDeployableSCTheories(String projectName) throws CoreException {
		IRodinProject project = RodinCore.getRodinDB().getRodinProject(projectName);
		return getDeployableSCTheories(project);
	}
	
	/**
	 * Returns the list of SC theories that can be deployed i.e., theories that
	 * are accurate and not empty.
	 * 
	 * @return list of deployable SC theories
	 * @throws CoreException
	 */
	public static List<ISCTheoryRoot> getDeployableSCTheories(IRodinProject project) throws CoreException {
		if (!project.exists()) {
			return new ArrayList<ISCTheoryRoot>();
		}
		ISCTheoryRoot[] roots = project.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		List<ISCTheoryRoot> list = new ArrayList<ISCTheoryRoot>();
		for (ISCTheoryRoot scRoot : roots) {
			if (isTheoryDeployable(scRoot)) {
				list.add(scRoot);
			}
		}
		return list;
	}

	/**
	 * Returns the deployed theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @return deployed theories
	 * @throws CoreException
	 */
	public static IDeployedTheoryRoot[] getDeployedTheories(IRodinProject project) throws CoreException {
		return getDeployedTheories(project, getExistingDeployedTheoriesFilter());
	}

	/**
	 * Returns a handle to the deployed theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the deployed theory
	 */
	public static IDeployedTheoryRoot getDeployedTheory(String name, IRodinProject project) {
		IRodinFile file = project.getRodinFile(getDeployedTheoryFullName(name));
		return (IDeployedTheoryRoot) file.getRoot();
	}

	/**
	 * Returns whether a deployed theory with the given name exists in the given
	 * project.
	 * 
	 * @param name
	 *            of the deployed theory
	 * @param project
	 *            the Rodin project
	 * @return whether the deployed theory exists
	 */
	public static boolean doesDeployedTheoryExist(String name, IRodinProject project) {
		return getDeployedTheory(name, project).exists();
	}

	/**
	 * Returns the full name of a deployed theory file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getDeployedTheoryFullName(String name) {
		return name + "." + DEPLOYED_THEORY_FILE_EXTENSION;
	}

	/**
	 * Returns whether deployed theory <code>importer</code> imports deployed theory
	 * <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether use relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(IDeployedTheoryRoot importer, IDeployedTheoryRoot importee) throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String usedName = importee.getComponentName();
		List<IDeployedTheoryRoot> theories = getImportedTheories(importer);
		for (IDeployedTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(usedName) || doesTheoryImportTheory(theory, importee)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether theory <code>importer</code> imports SC theory
	 * <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether import relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(ITheoryRoot importer, ISCTheoryRoot importee) throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String importeeName = importee.getComponentName();
		List<ISCTheoryRoot> theories = getImportedTheories(importer);
		for (ISCTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(importeeName) || doesTheoryImportTheory(theory, importee)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether SC theory <code>importer</code> imports SC theory
	 * <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether import relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(ISCTheoryRoot importer, ISCTheoryRoot importee) throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String importeeName = importee.getComponentName();
		List<ISCTheoryRoot> theories = getImportedTheories(importer);
		for (ISCTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(importeeName) || doesTheoryImportTheory(theory, importee)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the SC theories imported by the given SC theory.
	 * 
	 * @param importer
	 *            the SC theory
	 * @return the list of imported SC theories
	 * @throws CoreException
	 */
	public static List<ISCTheoryRoot> getImportedTheories(ISCTheoryRoot importer) throws CoreException {
		if (importer == null || !importer.exists()) {
			return null;
		}
		ISCImportTheory[] importedTheories = importer.getImportTheories();
		List<ISCTheoryRoot> result = new ArrayList<ISCTheoryRoot>();
		for (ISCImportTheory use : importedTheories) {
			if (use.hasImportTheory())
				result.add(use.getImportTheory());
		}
		return result;
	}

	/**
	 * Returns the SC theories imported by the given theory.
	 * 
	 * @param importer
	 *            the theory
	 * @return the list of imported SC theories
	 * @throws CoreException
	 */
	public static List<ISCTheoryRoot> getImportedTheories(ITheoryRoot importer) throws CoreException {
		if (importer == null || !importer.exists()) {
			return null;
		}
		IImportTheory[] importedTheories = importer.getImportTheories();
		List<ISCTheoryRoot> result = new ArrayList<ISCTheoryRoot>();
		for (IImportTheory use : importedTheories) {
			if (use.hasImportTheory())
				result.add(use.getImportTheory());
		}
		return result;
	}

	/**
	 * Returns the deployed theories imported by the given theory.
	 * 
	 * @param theory
	 *            the deployed theory
	 * @return the list of used theories
	 * @throws CoreException
	 */
	public static List<IDeployedTheoryRoot> getImportedTheories(IDeployedTheoryRoot theory) throws CoreException {
		if (theory == null || !theory.exists()) {
			return null;
		}
		IUseTheory[] usedTheories = theory.getUsedTheories();
		List<IDeployedTheoryRoot> result = new ArrayList<IDeployedTheoryRoot>();
		for (IUseTheory use : usedTheories) {
			if (use.hasUseTheory())
				result.add(use.getUsedTheory());
		}
		return result;
	}

	/**
	 * Returns the list of SC theories that can be imported by the given
	 * unchecked theory.
	 * 
	 * <p>
	 * This method essentially avoids redundancy and circularity.
	 * 
	 * @param theory
	 *            the unchecked theory
	 * @return the list of potential imports
	 * @throws CoreException
	 */
	public static ISCTheoryRoot[] getPotentialTheoryImports(ITheoryRoot theory) throws CoreException {
		IRodinProject project = theory.getRodinProject();
		ISCTheoryRoot[] allSCRoots = getSCTheoryRoots(project, getExistingSCTheoriesFilter());
		List<ISCTheoryRoot> potentialImports = new ArrayList<ISCTheoryRoot>();
		ISCTheoryRoot theorySC = theory.getSCTheoryRoot();
		for (ISCTheoryRoot root : allSCRoots) {
			// not import oneself and no circularity
			if (!root.getComponentName().equals(theory.getComponentName()) &&
					!doesTheoryImportTheory(root, theorySC)) {
				potentialImports.add(root);
			}
		}
		return potentialImports.toArray(new ISCTheoryRoot[potentialImports.size()]);
	}

	/**
	 * Ensures that the deployment project exists.
	 * 
	 * @throws CoreException
	 */
	public static void ensureDeploymentProjectExists() throws CoreException {

		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(THEORIES_PROJECT));

		if (resource != null) {
			if (!resource.isAccessible()) {
				resource.getProject().open(null);
			}
			return;
		}
		final IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(THEORIES_PROJECT);
		if (rodinProject.exists()) {
			return;
		}
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor pMonitor) throws CoreException {
					IProject project = rodinProject.getProject();
					if (!project.exists())
						project.create(null);
					project.open(null);
					IProjectDescription description = project.getDescription();
					description.setNatureIds(new String[] { RodinCore.NATURE_ID });
					project.setDescription(description, null);
				}

			}, RodinCore.getRodinDB().getSchedulingRule(), null);

		} catch (CoreException e) {
			CoreUtilities.log(e, "Failed to create deployed theories project.");
		}

	}

	/**
	 * Removes the deployed theory and any deployed theories that depend on it.
	 * 
	 * @param root
	 *            the deployed theory
	 */
	public static void cleanUp(final IDeployedTheoryRoot root) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					if (root.exists()) {
						IDeployedTheoryRoot[] depRoots = getDeployedTheories(root.getRodinProject());
						root.getRodinFile().delete(true, monitor);
						for (IDeployedTheoryRoot dep : depRoots) {
							if (dep.getComponentName().equals(root.getComponentName())) {
								continue;
							}
							if (doesTheoryImportTheory(dep, root)) {
								dep.getRodinFile().delete(true, monitor);
							}
						}
						rebuild(root.getRodinProject(), monitor);
					}

				}
			}, null);
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "Failed to clean up deployed theory.");
		}

	}

	/**
	 * Returns the deployment project.
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @return the deployment project
	 * @throws CoreException
	 */
	public static IRodinProject getDeploymentProject(IProgressMonitor monitor) {
		final IRodinProject project = RodinCore.getRodinDB().getRodinProject(THEORIES_PROJECT);
		try {
			ensureDeploymentProjectExists();

		} catch (CoreException e) {
			CoreUtilities.log(e, "Failed to access/create deployed theories project.");
			return null;
		}
		return project;
	}

	/**
	 * Returns whether the given proof status is of discharged status.
	 * 
	 * @param status
	 *            proof status
	 * @return whether status PO has been discharged
	 * @throws RodinDBException
	 */
	public static boolean isDischarged(IPSStatus status) throws RodinDBException {
		return (status.getConfidence() > IConfidence.REVIEWED_MAX) && (!status.isBroken());
	}

	/**
	 * Returns whether the given proof status is of reviewed status.
	 * 
	 * @param status
	 *            proof status
	 * @return whether status PO has been reviewed
	 * @throws RodinDBException
	 */
	public static boolean isReviewed(IPSStatus status) throws RodinDBException {
		return (status.getConfidence() > IConfidence.PENDING) && (status.getConfidence() <= IConfidence.REVIEWED_MAX);
	}

	/**
	 * Returns whether the given element is within a project level scoped
	 * theory/component.
	 * 
	 * @param element
	 *            the internal element
	 * @return whether the given element is project level scoped
	 */
	public static boolean projectLevelScoped(IInternalElement element) {
		IRodinProject project = element.getRodinProject();
		if (isMathExtensionsProject(project)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns whether the given project is the <code>MathExtensions</code>
	 * project.
	 * 
	 * @param project
	 *            the Rodin project, must not be <code>null</code>
	 * @return whether <code>project</code> is the <code>MathExtensions</code>
	 *         project
	 */
	public static boolean isMathExtensionsProject(IRodinProject project) {
		return THEORIES_PROJECT.equals(project.getElementName());
	}

	/**
	 * Returns all the theories imported by the given theory directly and
	 * indirectly.
	 * 
	 * @param root
	 *            the SC theory root
	 * @return all imported theories
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> importClosure(ISCTheoryRoot root) throws CoreException {
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		List<ISCTheoryRoot> imported = getImportedTheories(root);
		set.addAll(imported);
		for (ISCTheoryRoot scRoot : imported) {
			set.addAll(importClosure(scRoot));
		}
		return set;
	}

	/**
	 * Returns all theory roots that exist and are not temporary.
	 * 
	 * @param project
	 *            the rodin project
	 * @return all non-temporary SC theory roots
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> getSCTheoryRoots(IRodinProject project) throws CoreException {
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		ISCTheoryRoot[] roots = getSCTheoryRoots(project, getNonTempSCTheoriesFilter());
		set.addAll(Arrays.asList(roots));
		return set;
	}
	
	/**
	 * Returns a filter that accepts existing SC theory file with the ordinary file extension.
	 * @return a theory filter
	 */
	public static ITheoryFilter<ISCTheoryRoot> getNonTempSCTheoriesFilter(){
		
		return new ITheoryFilter<ISCTheoryRoot>() {
			@Override
			public boolean filter(ISCTheoryRoot theory) {
				return theory.exists() && !theory.getRodinFile().getElementName().endsWith(SC_THEORY_FILE_EXTENSION + "_tmp");
			}
		};
	}
	
	/**
	 * Returns a filter that accepts existing SC theories.
	 * @return a theory filter
	 */
	public static ITheoryFilter<ISCTheoryRoot> getExistingSCTheoriesFilter(){
		
		return new ITheoryFilter<ISCTheoryRoot>() {
			@Override
			public boolean filter(ISCTheoryRoot theory) {
				return theory.exists();
			}
		};
	}
	
	/**
	 * Returns a filter that accepts existing deployed theories.
	 * @return a theory filter
	 */
	public static ITheoryFilter<IDeployedTheoryRoot> getExistingDeployedTheoriesFilter(){
		
		return new ITheoryFilter<IDeployedTheoryRoot>() {
			@Override
			public boolean filter(IDeployedTheoryRoot theory) {
				return theory.exists();
			}
		};
	}
}
