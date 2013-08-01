package org.eventb.theory.core;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.basis.TheoryDeployer;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * A helper class to manipulate theory hierarchies.
 * 
 * @author im06r
 * 
 */
public class TheoryHierarchyHelper {
	
	/**
	 * Returns the import relationship closure of the given SC theory. TODO
	 * ensure no cycles
	 * 
	 * @param scRoot
	 *            the SC theory
	 * @return the import closure
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> importClosure(ISCTheoryRoot scRoot) throws CoreException {
		Set<ISCTheoryRoot> closure = new LinkedHashSet<ISCTheoryRoot>();
		Set<ISCTheoryRoot> imported = getImportedTheories(scRoot);
		closure.addAll(imported);
		for (ISCTheoryRoot otherRoot : imported) {
			closure.addAll(importClosure(otherRoot));
		}
		return closure;
	}

	/**
	 * Returns the SC theories imported by the given SC theory.
	 * 
	 * @param importer
	 *            the SC theory
	 * @return the list of imported SC theories
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> getImportedTheories(ISCTheoryRoot importer) throws CoreException {
		Set<ISCTheoryRoot> result = new LinkedHashSet<ISCTheoryRoot>();
		if (importer == null || !importer.exists()) {
			return result;
		}
		
		ISCImportTheory[] importedTheories = importer.getImportTheories();
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
	public static Set<ISCTheoryRoot> getImportedTheories(ITheoryRoot importer) throws CoreException {
		if (importer == null || !importer.exists()) {
			return null;
		}
		IImportTheory[] importedTheories = importer.getImportTheories();
		Set<ISCTheoryRoot> result = new LinkedHashSet<ISCTheoryRoot>();
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
	public static Set<IDeployedTheoryRoot> getImportedTheories(IDeployedTheoryRoot theory) throws CoreException {
		if (theory == null || !theory.exists()) {
			return null;
		}
		IUseTheory[] usedTheories = theory.getUsedTheories();
		Set<IDeployedTheoryRoot> result = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IUseTheory use : usedTheories) {
			if (use.hasUseTheory())
				result.add(use.getUsedTheory());
		}
		return result;
	}

	/**
	 * Returns the importing relationship closure of the given deployed theory
	 * i.e., all theories importing the given theory.
	 * 
	 * @param root
	 *            the deployed theory
	 * @return the importing closure
	 * @throws CoreException
	 */
	public static Set<IDeployedTheoryRoot> importingClosure(IDeployedTheoryRoot root) throws CoreException {
		Set<IDeployedTheoryRoot> set = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot otherRoot : DatabaseUtilities.getDeployedTheories(root.getRodinProject())) {
			if (doesTheoryImportTheory(otherRoot, root)) {
				set.add(otherRoot);
			}
		}
		return set;
	}

	/**
	 * Returns the importing relationship closure of the given SC theory i.e.,
	 * all theories importing the given theory.
	 * 
	 * @param root
	 *            the SC theory
	 * @return the importing closure
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> importingClosure(ISCTheoryRoot root) throws CoreException {
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		ISCTheoryRoot[] scTheoryRoots = DatabaseUtilities.getSCTheoryRoots(root.getRodinProject(),
				DatabaseUtilities.getNonTempSCTheoriesFilter());
		for (ISCTheoryRoot otherRoot : scTheoryRoots) {
			if (doesTheoryImportTheory(otherRoot, root)) {
				set.add(otherRoot);
			}
		}
		return set;
	}

	/**
	 * Returns whether deployed theory <code>importer</code> imports deployed
	 * theory <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether use relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(IDeployedTheoryRoot importer, IDeployedTheoryRoot importee)
			throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String usedName = importee.getComponentName();
		Set<IDeployedTheoryRoot> theories = getImportedTheories(importer);
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
		Set<ISCTheoryRoot> theories = getImportedTheories(importer);
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
		Set<ISCTheoryRoot> theories = getImportedTheories(importer);
		for (ISCTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(importeeName) || doesTheoryImportTheory(theory, importee)) {
				return true;
			}
		}
		return false;
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
		ISCTheoryRoot[] allSCRoots = DatabaseUtilities.getSCTheoryRoots(project,
				DatabaseUtilities.getExistingSCTheoriesFilter());
		Set<ISCTheoryRoot> potentialImports = new LinkedHashSet<ISCTheoryRoot>();
		ISCTheoryRoot theorySC = theory.getSCTheoryRoot();
		for (ISCTheoryRoot root : allSCRoots) {
			// not import oneself and no circularity
			if (!root.getComponentName().equals(theory.getComponentName()) && !doesTheoryImportTheory(root, theorySC)) {
				potentialImports.add(root);
			}
		}
		return potentialImports.toArray(new ISCTheoryRoot[potentialImports.size()]);
	}

	/**
	 * Returns the set of SC theories to deploy based on the given array of
	 * theories.
	 * 
	 * @param roots
	 *            the initial roots
	 * @return the set of SC theories to deploy, this is the initial roots plus
	 *         any required dependencies
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> getAllTheoriesToDeploy(ISCTheoryRoot... roots) throws CoreException {
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		for (ISCTheoryRoot root : roots) {
			Set<ISCTheoryRoot> importedTheories = importClosure(root);
			for (ISCTheoryRoot importedRoot : importedTheories) {
				IDeployedTheoryRoot deployedTheoryRoot = importedRoot.getDeployedTheoryRoot();
				// FIXED BUG check if the outdated attribute exists
				if (!deployedTheoryRoot.exists() || (deployedTheoryRoot.exists() && 
						deployedTheoryRoot.hasOutdatedAttribute() && deployedTheoryRoot.isOutdated())) {
					set.add(importedRoot);
				}
			}
			set.add(root);
		}
		return set;
	}

	/**
	 * Returns the set of theories to undeploy based on the given array.
	 * 
	 * @param roots
	 *            the initial array of theories to undeploy
	 * @return the complete set of theories to undeploy
	 */
	public static Set<IDeployedTheoryRoot> getAllTheoriesToUndeploy(IDeployedTheoryRoot... roots) throws CoreException {
		Set<IDeployedTheoryRoot> set = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot root : roots) {
			set.addAll(importingClosure(root));
			set.add(root);
		}
		return set;
	}

	/**
	 * Returns a comparator of SC theories based on the import relationship that
	 * exists between theories.
	 * 
	 * <p>
	 * A theory A is greater than theory B iff A imports B.
	 * 
	 * @return a theories dependency comparator
	 */
	public static Comparator<ISCTheoryRoot> getSCTheoryDependencyComparator() {
		return new Comparator<ISCTheoryRoot>() {

			@Override
			public int compare(ISCTheoryRoot root1, ISCTheoryRoot root2) {
				try {
					if (doesTheoryImportTheory(root1, root2)) {
						return 1;
					} else if (doesTheoryImportTheory(root2, root1)) {
						return -1;
					} else if (root1.getComponentName().equals(root2.getComponentName())) {
						return 0;
					}
				} catch (CoreException e) {
					CoreUtilities
							.log(e,
									"Error comparing theories " + root1.getComponentName() + " and "
											+ root2.getComponentName());
				}
				// check what is safer to return
				return 1;
			}
		};
	}

	/**
	 * Returns a comparator of deployed theories based on the import (or use)
	 * relationship that exists between theories.
	 * 
	 * <p>
	 * A theory A is greater than theory B iff A uses B.
	 * 
	 * @return a theories dependency comparator
	 */
	public static Comparator<IDeployedTheoryRoot> getDeployedTheoryDependencyComparator() {
		return new Comparator<IDeployedTheoryRoot>() {
			@Override
			public int compare(IDeployedTheoryRoot root1, IDeployedTheoryRoot root2) {
				try {
					if (doesTheoryImportTheory(root1, root2)) {
						return 1;
					} else if (doesTheoryImportTheory(root2, root1)) {
						return -1;
					} else if (root1.getComponentName().equals(root2.getComponentName())) {
						return 0;
					}
				} catch (CoreException e) {
					CoreUtilities
							.log(e,
									"Error comparing theories " + root1.getComponentName() + " and "
											+ root2.getComponentName());
				}
				// check what is safer to return
				return 1;
			}
		};
	}
	
	public static ITheoryDeployer getDeployer(IRodinProject project, Set<ISCTheoryRoot> theories){
		return new TheoryDeployer(project, theories);
	}
	
	public static IWorkspaceRunnable getUndeployer(final IRodinProject project, final Set<IDeployedTheoryRoot> theories){
		return new IWorkspaceRunnable() {
			
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("undeploying theories", 3*theories.size());
				for (IDeployedTheoryRoot root : theories){
					if (root.exists()) {
						root.getRodinFile().delete(true, monitor);
						/* populate (from Rodin2.8):
						 * for each Theory path which importing this theory,
						 * the checked theory path (.tcl) file is deleted; this automatically triggers a build in the project 
						 */
						monitor.subTask("populating");
						for (IRodinProject project : RodinCore.getRodinDB().getRodinProjects()){
							monitor.worked(2);
							ITheoryPathRoot[] theoryPath = project.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
							if (theoryPath.length != 0 && theoryPath[0].getRodinFile().exists()) {
						
								for (IAvailableTheory availThy : theoryPath[0].getAvailableTheories()){
									if (availThy.getDeployedTheory().equals(root) &&  (theoryPath[0].getSCTheoryPathRoot().getRodinFile().exists())) {
										theoryPath[0].getSCTheoryPathRoot().getRodinFile().delete(true, monitor);
									}		
								}
							}
						}
					}
					monitor.worked(3);
				}	
			}
		};
	}
}
