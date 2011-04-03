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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.theory.core.basis.TheoryDeployer;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
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
	public static final String THEORY_CONFIGURATION = TheoryPlugin.PLUGIN_ID
			+ ".thy";

	// ////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////// DB
	// ////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a SC theory that exists in the given project. If the theory does
	 * not exist, <code>null</code> is returned.
	 * 
	 * @param fullName
	 *            the full name of the SC theory file inc. extension
	 * @param project
	 *            the rodin project
	 * @return the SC theory
	 * @throws CoreException
	 */
	public static ISCTheoryRoot getExistingTheoryRoot(String fullName,
			String project) throws CoreException {
		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(
				project);
		if (rodinProject.exists()) {
			IRodinFile file = rodinProject.getRodinFile(fullName);
			if (file.exists()) {
				ISCTheoryRoot theoryRoot = (ISCTheoryRoot) file.getRoot();
				if (theoryRoot.exists()) {
					return theoryRoot;
				}
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
	public static ISCTheoryRoot[] getSCTheoryRoots(IRodinProject project,
			TheoriesFilter<ISCTheoryRoot> filter) throws CoreException {
		ISCTheoryRoot[] roots = project
				.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
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
	 * Returns the theory parent of the given element if any.
	 * 
	 * @param element
	 *            the rodin element
	 * @return the parent theory
	 */
	public static ITheoryRoot getTheoryParent(IRodinElement element) {
		return element.getAncestor(ITheoryRoot.ELEMENT_TYPE);
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
	public static <E extends IInternalElement> Set<String> getNames(
			List<E> elements) {
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
					return false;
				}
				l = root.getSCDatatypeDefinitions().length
						+ root.getSCNewOperatorDefinitions().length
						+ root.getProofRulesBlocks().length
						+ root.getTheorems().length;

			}
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "Failed to retrieve details from theory.");
		}
		return l == 0;
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
	 * Returns the fullname of a theory file.
	 * 
	 * @param name
	 *            the name
	 * @return the fullname
	 */
	public static String getTheoryFullName(String name) {
		return name + "." + THEORY_FILE_EXTENSION;
	}

	/**
	 * Returns the fullname of a SC theory file.
	 * 
	 * @param name
	 *            the name
	 * @return the fullname
	 */
	public static String getSCTheoryFullName(String name) {
		return name + "." + SC_THEORY_FILE_EXTENSION;
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
	public static boolean originatedFromTheory(IRodinFile file)
			 {
		IRodinProject project = file.getRodinProject();
		IRodinFile theory = project.getRodinFile(file.getBareName() + "."
				+ THEORY_FILE_EXTENSION);
		if (theory.exists()) {
			return true;
		}
		return false;
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
	public static void rebuild(IRodinProject project, IProgressMonitor monitor)
			throws CoreException {
		if (project == null || !project.exists()) {
			throw new CoreException(new Status(IStatus.ERROR,
					TheoryPlugin.PLUGIN_ID,
					"attempting to rebuild a non-existent project"));
		}
		IProject nativeProj = ((IProject) project.getAdapter(IProject.class));
		if (nativeProj != null) {
			nativeProj.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
		}
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
				for (IRodinProject project : RodinCore.getRodinDB()
						.getRodinProjects()) {
					rebuild(project, monitor);
				}
			}
		}, monitor);

	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////// Deploy
	// ////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a deployer for the given theory.
	 * 
	 * @param theoryName
	 *            the name of the SC theory, must exist
	 * @param project
	 *            the Event-B project, must exist
	 * @param targetProject
	 *            the Event-B target project, must exist
	 * @param force
	 *            whether to force deployment
	 * @return the deployed theory
	 * @throws CoreException
	 */
	public static final ITheoryDeployer getTheoryDeployer(String theoryName,
			String project, boolean force, boolean rebuildProject)
			throws CoreException {
		String fullName = theoryName + "." + SC_THEORY_FILE_EXTENSION;
		ISCTheoryRoot theoryRoot = getExistingTheoryRoot(fullName, project);
		if (theoryRoot == null) {
			return null;
		}
		return new TheoryDeployer(theoryRoot, force, rebuildProject);
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
	public static IDeployedTheoryRoot[] getDeployedTheories(
			IRodinProject project, TheoriesFilter<IDeployedTheoryRoot> filter)
			throws CoreException {
		IDeployedTheoryRoot[] roots = project
				.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
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
	 * are not empty and not already deployed.
	 * 
	 * @return list of deployable SC theories
	 * @throws CoreException
	 */
	public static List<ISCTheoryRoot> getDeployableSCTheories(String projectName)
			throws CoreException {
		IRodinProject project = RodinCore.getRodinDB().getRodinProject(projectName);
		if (!project.exists()){
			return new ArrayList<ISCTheoryRoot>();
		}
		ISCTheoryRoot[] roots = project
				.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		List<ISCTheoryRoot> list = new ArrayList<ISCTheoryRoot>();
		for (ISCTheoryRoot scRoot : roots) {
			if (!getDeployedTheory(scRoot.getComponentName(), project).exists()
					&& !isTheoryEmptyOrNotAccurate(scRoot)) {
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
	public static IDeployedTheoryRoot[] getDeployedTheories(
			IRodinProject project) throws CoreException {

		return getDeployedTheories(project,
				new TheoriesFilter<IDeployedTheoryRoot>() {

					@Override
					public boolean filter(IDeployedTheoryRoot theory) {
						// TODO Auto-generated method stub
						return theory.exists();
					}
				});
	}

	/**
	 * Returns a handle to the deployed theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the deployed theory
	 */
	public static IDeployedTheoryRoot getDeployedTheory(String name,
			IRodinProject project) {
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
	public static boolean doesDeployedTheoryExist(String name,
			IRodinProject project) {
		return getDeployedTheory(name, project).exists();
	}

	/**
	 * Returns the fullname of a deployed theory file.
	 * 
	 * @param name
	 *            the name
	 * @return the fullname
	 */
	public static String getDeployedTheoryFullName(String name) {
		return name + "." + DEPLOYED_THEORY_FILE_EXTENSION;
	}

	/**
	 * Returns whether deployed theory <code>user</code> uses theory
	 * <code>user</code>.
	 * 
	 * @param user
	 * @param used
	 * @return whether use relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryUseTheory(IDeployedTheoryRoot user,
			IDeployedTheoryRoot used) throws CoreException {
		if (user == null || !user.exists()) {
			return false;
		}
		String usedName = used.getComponentName();
		List<IDeployedTheoryRoot> theories = getUsedTheories(user);
		for (IDeployedTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(usedName)
					|| doesTheoryUseTheory(theory, used)) {
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
	public static boolean doesTheoryImportTheory(ITheoryRoot importer,
			ISCTheoryRoot importee) throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String importeeName = importee.getComponentName();
		List<ISCTheoryRoot> theories = getImportedTheories(importer);
		for (ISCTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(importeeName)
					|| doesTheoryImportTheory(theory, importee)) {
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
	public static boolean doesTheoryImportTheory(ISCTheoryRoot importer,
			ISCTheoryRoot importee) throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String importeeName = importee.getComponentName();
		List<ISCTheoryRoot> theories = getImportedTheories(importer);
		for (ISCTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(importeeName)
					|| doesTheoryImportTheory(theory, importee)) {
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
	public static List<ISCTheoryRoot> getImportedTheories(ISCTheoryRoot importer)
			throws CoreException {
		if (importer == null || !importer.exists()) {
			return null;
		}
		ISCImportTheory[] importedTheories = importer.getImportTheories();
		List<ISCTheoryRoot> result = new ArrayList<ISCTheoryRoot>();
		for (ISCImportTheory use : importedTheories) {
			if(use.hasImportTheory())
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
	public static List<ISCTheoryRoot> getImportedTheories(ITheoryRoot importer)
			throws CoreException {
		if (importer == null || !importer.exists()) {
			return null;
		}
		IImportTheory[] importedTheories = importer.getImportTheories();
		List<ISCTheoryRoot> result = new ArrayList<ISCTheoryRoot>();
		for (IImportTheory use : importedTheories) {
			if(use.hasImportTheory())
				result.add(use.getImportTheory());
		}
		return result;
	}

	/**
	 * Returns the deployed theories used by the given theory.
	 * 
	 * @param user
	 *            the deployed theory
	 * @return the list of used theories
	 * @throws CoreException
	 */
	public static List<IDeployedTheoryRoot> getUsedTheories(IDeployedTheoryRoot user)
			throws CoreException {
		if (user == null || !user.exists()) {
			return null;
		}
		IUseTheory[] usedTheories = user.getUsedTheories();
		List<IDeployedTheoryRoot> result = new ArrayList<IDeployedTheoryRoot>();
		for (IUseTheory use : usedTheories) {
			if(use.hasUseTheory())
				result.add(use.getUsedTheory());
		}
		return result;
	}
	
	/**
	 * Returns the list of SC theories that can be imported by the given unchecked theory.
	 * 
	 * <p> This method essentially avoids redundancy and circularity.
	 * @param theory the unchecked theory
	 * @return the list of potential imports
	 * @throws CoreException
	 */
	public static ISCTheoryRoot[] getPotentialTheoryImports(ITheoryRoot theory)
	throws CoreException{
		IRodinProject project = theory.getRodinProject();
		ISCTheoryRoot[] allSCRoots = getSCTheoryRoots(project, new TheoriesFilter<ISCTheoryRoot>() {

			@Override
			public boolean filter(ISCTheoryRoot theory) {
				// TODO Auto-generated method stub
				return theory.exists();
			}
		});
		List<ISCTheoryRoot> potentialImports = new ArrayList<ISCTheoryRoot>();
		ISCTheoryRoot theorySC = theory.getSCTheoryRoot();
		for (ISCTheoryRoot root : allSCRoots){
			if(
					// not import oneself
					!root.getComponentName().equals(theory.getComponentName()) &&
					// no circularity
					!doesTheoryImportTheory(root, theorySC)
					){
				potentialImports.add(root);
			}
		}
		
		return potentialImports.toArray(new ISCTheoryRoot[potentialImports.size()]);
	}
	
	/**
	 * A utility to return the names of a collection of internal elements.
	 * @param elements the internal elements
	 * @return the names of the elements
	 */
	public static String[] getInternalElementNames(IInternalElement... elements ){
		if (elements == null){
			// return an empty array of names TODO check this for safety
			return new String[0];
		}
		String[] names = new String[elements.length];
		int i = 0;
		for (IInternalElement element : elements){
			names[i++] = element.getElementName();
		}
		return names;
	}

	/**
	 * Ensures that the deployment project exists.
	 * 
	 * @throws CoreException
	 */
	public static void ensureDeploymentProjectExists() throws CoreException {

		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(THEORIES_PROJECT));

		if (resource != null) {
			if (!resource.isAccessible()) {
				resource.getProject().open(null);
			}
			return;
		}
		final IRodinProject rodinProject = RodinCore.getRodinDB()
				.getRodinProject(THEORIES_PROJECT);
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
					description
							.setNatureIds(new String[] { RodinCore.NATURE_ID });
					project.setDescription(description, null);
				}

			}, RodinCore.getRodinDB().getSchedulingRule(), null);

		} catch (CoreException e) {
			CoreUtilities.log(e, "Failed to create deployed theories project.");
		}

	}

	/**
	 * Returns whether a deployed version of the given theory exists.
	 * 
	 * @param root
	 *            the SC theory root
	 * @return whether a deployed version exists
	 */
	public static boolean hasDeployedVersion(ISCTheoryRoot root) {
		if (root == null) {
			return false;
		}
		IDeployedTheoryRoot dep = root.getDeployedTheoryRoot();
		return dep.exists();
	}

	/**
	 * Returns the formula factory with the currently deployed extensions.
	 * 
	 * @return formula factory
	 */
	public static FormulaFactory getCurrentFormulaFactory() {
		// TODO change this
		return FormulaFactory.getDefault();
	}

	/**
	 * Marks the given deployed theory as outdated.
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
						IDeployedTheoryRoot[] depRoots = getDeployedTheories(root
								.getRodinProject());
						root.getRodinFile().delete(true, monitor);
						for (IDeployedTheoryRoot dep : depRoots) {
							if (dep.getComponentName().equals(
									root.getComponentName())) {
								continue;
							}
							if (doesTheoryUseTheory(dep, root)) {
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
		final IRodinProject project = RodinCore.getRodinDB().getRodinProject(
				THEORIES_PROJECT);
		try {
			ensureDeploymentProjectExists();

		} catch (CoreException e) {
			CoreUtilities.log(e, "Failed to create deployed theories project.");
			return null;
		}
		return project;
	}

	/**
	 * 
	 * A simple protocol for formula extensions sources filter.
	 * 
	 * @author maamria
	 * 
	 * @param <T>
	 *            the type of the source
	 */
	public static interface TheoriesFilter<T extends IFormulaExtensionsSource> {

		/**
		 * Returns whether the given theory satisfies the criteria of this
		 * filter.
		 * 
		 * @param theory
		 *            the theory
		 * @return whether <code>theory</code> satisfies the criteria of this
		 *         filter
		 */
		public boolean filter(T theory);

	}

	/**
	 * Returns whether the given proof status is of discharged status.
	 * 
	 * @param status
	 *            proof status
	 * @return whether status PO has been discharged
	 * @throws RodinDBException
	 */
	public static boolean isDischarged(IPSStatus status)
			throws RodinDBException {
		return (status.getConfidence() > IConfidence.REVIEWED_MAX)
				&& (!status.isBroken());
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
		return (status.getConfidence() > IConfidence.PENDING)
				&& (status.getConfidence() <= IConfidence.REVIEWED_MAX);
	}
	
	/**
	 * Returns whether the given element is within a project level scoped theory/component.
	 * @param element the internal element
	 * @return whether the given element is project level scoped
	 */
	public static boolean projectLevelScoped(IInternalElement element){
		IRodinProject project = element.getRodinProject();
		if(project.getElementName().equals(THEORIES_PROJECT)){
			return false;
		}
		return true;
	}
	
	/**
	 * Returns whether the given project is the <code>MathExtensions</code> project.
	 * @param project the Rodin project, must not be <code>null</code>
	 * @return whether <code>project</code> is the <code>MathExtensions</code> project
	 */
	public static boolean isMathExtensionsProject(IRodinProject project){
		return THEORIES_PROJECT.equals(project.getElementName());
	}
	
	/**
	 * Returns all the theories imported by the given theory directly and indirectly.
	 * @param root the SC theory root
	 * @return all imported theories
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> importClosure(ISCTheoryRoot root) throws CoreException{
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		List<ISCTheoryRoot> imported = getImportedTheories(root);
		set.addAll(imported);
		for (ISCTheoryRoot scRoot : imported){
			set.addAll(importClosure(scRoot));
		}
		return set;
	}
	
	/**
	 * Returns all theory roots that exist and are not temporary.
	 * @param project the rodin project
	 * @return all non-temp sc theory roots
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> getSCTheoryRoots(IRodinProject project) throws CoreException{
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		ISCTheoryRoot[] roots = getSCTheoryRoots(project, new TheoriesFilter<ISCTheoryRoot>() {

			@Override
			public boolean filter(ISCTheoryRoot theory) {
				// TODO Auto-generated method stub
				return theory.exists() && !theory.getRodinFile().getElementName().endsWith(SC_THEORY_FILE_EXTENSION+"_tmp");
			}
		});
		set.addAll(Arrays.asList(roots));
		return set;
	}
}