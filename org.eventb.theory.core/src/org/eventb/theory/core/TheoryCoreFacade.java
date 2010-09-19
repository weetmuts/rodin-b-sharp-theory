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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
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
 * Accessibility class for some fields and methods for other plug-ins.
 * 
 * @author maamria
 * 
 */
public class TheoryCoreFacade {

	// As in "theory unchecked file"
	public static final String THEORY_FILE_EXTENSION = "tuf";
	// As in "theory checked file"
	public static final String SC_THEORY_FILE_EXTENSION = "tcf";
	// As in "deployed theory file"
	public static final String DEPLOYED_THEORY_FILE_EXTENSION = "dtf";
	// As in "B project dependencies file"
	public static final String BPDF_FILE_EXTENSION = "bpdf";

	// The theory configuration for the SC and POG
	public static final String THEORY_CONFIGURATION = TheoryPlugin.PLUGIN_ID
			+ ".thy";

	public static final String POSTFIX = Notation.POSTFIX.toString();

	public static final String INFIX = Notation.INFIX.toString();

	public static final String PREFIX = Notation.PREFIX.toString();

	public static final String[] POSSIBLE_NOTATION_TYPES = new String[] {
			PREFIX, INFIX, POSTFIX };

	/**
	 * Converts a string (eg. "POSTFIX") to the corresponding notation.
	 * 
	 * @param type
	 *            in string format
	 * @return the corresponding notation
	 */
	public static Notation getNotation(String type) {
		if (type.equalsIgnoreCase(POSTFIX)) {
			return Notation.POSTFIX;
		} else if (type.equalsIgnoreCase(INFIX)) {
			return Notation.INFIX;
		} else {
			return Notation.PREFIX;
		}
	}

	/**
	 * Returns a deployer for the given theory.
	 * 
	 * @param theoryName
	 *            the name of the SC theory
	 * @param project
	 *            the Event-B project
	 * @param force
	 *            whether to force deployment
	 * @return the deployed theory
	 * @throws CoreException
	 */
	public static final ITheoryDeployer getTheoryDeployer(String theoryName, String project, boolean force)
			throws CoreException {
		String fullName = theoryName + "." + SC_THEORY_FILE_EXTENSION;
		ISCTheoryRoot theoryRoot = CoreUtilities.getTheoryRoot(fullName,
				project);
		if (theoryRoot == null) {
			return null;
		}
		return new TheoryDeployer(theoryRoot, force);
	}

	/**
	 * Returns the SC theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @return SC theories
	 * @throws CoreException
	 */
	public static ISCTheoryRoot[] getSCTheoryRoots(IRodinProject project)
			throws CoreException {
		ISCTheoryRoot roots[] = project
				.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		return roots;
	}

	/**
	 * Returns the theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @return theories
	 * @throws CoreException
	 */
	public static ITheoryRoot[] getTheoryRoots(IRodinProject project)
			throws CoreException {
		ITheoryRoot roots[] = project
				.getRootElementsOfType(ITheoryRoot.ELEMENT_TYPE);
		return roots;
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
	 * Returns a set representation of the array of the given elements.
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param elements
	 *            the actual elements
	 * @return the set
	 */
	public static <E extends IInternalElement> Set<E> getSet(E[] elements) {
		Set<E> set = new LinkedHashSet<E>();
		set.addAll(Arrays.asList(elements));
		return set;
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
	 * Returns the deployed theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @return deployed theories
	 * @throws CoreException
	 */
	public static IDeployedTheoryRoot[] getDeployedTheories(
			IRodinProject project) throws CoreException {
		IDeployedTheoryRoot[] roots = project
				.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
		return roots;
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
	 * Returns a new core exception.
	 * 
	 * @param message
	 *            the message
	 * @return the core exception
	 */
	public static CoreException newCoreException(String message) {
		return new CoreException(new Status(IStatus.ERROR,
				TheoryPlugin.PLUGIN_ID, message));
	}

	/**
	 * Returns a new rodin DB exception.
	 * 
	 * @param message
	 *            the message
	 * @return the rodin DB exception
	 */
	public static RodinDBException newDBException(String message) {
		return new RodinDBException(newCoreException(message));
	}

	/**
	 * Returns the list of imported theories by this theory.
	 * 
	 * @param root
	 *            the theory
	 * @return the list of imported theories
	 * @throws CoreException
	 */
	public static List<String> getImportedTheories(ITheoryRoot root)
			throws CoreException {
		List<String> result = new ArrayList<String>();
		IImportTheory imports[] = root.getImportTheories();
		for (IImportTheory im : imports) {
			if (im.hasImportedTheory()) {
				result.add(im.getImportedTheoryName());
			}
		}
		return result;
	}

	/**
	 * Returns whether theory <code>importer</code> has imported theory
	 * <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether import relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(ITheoryRoot importer,
			ITheoryRoot importee) throws CoreException {
		IRodinProject project = importer.getRodinProject();
		String importeeName = importee.getComponentName();
		List<String> theories = getImportedTheories(importer);
		for (String theory : theories) {
			ITheoryRoot importedTheory = TheoryCoreFacade.getTheory(
					theory, project);
			if (theory.equals(importeeName)
					|| doesTheoryImportTheory(importedTheory, importee)) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Returns all direct and indirect importing theories.
	 * 
	 * @param root
	 *            the theory
	 * @return all importers
	 * @throws CoreException
	 */
	public static List<ITheoryRoot> getImportingTheories(ITheoryRoot root)
			throws CoreException {
		List<ITheoryRoot> result = new ArrayList<ITheoryRoot>();
		ITheoryRoot[] roots = getTheoryRoots(root.getRodinProject());
		for (ITheoryRoot thy : roots) {
			if (doesTheoryImportTheory(thy, root)) {
				result.add(thy);
			}
		}
		return result;
	}
	
	/**
	 * Returns the project with the given name if it exists.
	 * @param name the project name
	 * @return the project or <code>null</code>
	 */
	public static IRodinProject getRodinProject(String name){
		IRodinProject project = RodinCore.getRodinDB().getRodinProject(name);
		if(project.exists())
			return project;
		else 
			return null;
	}
	
	public static boolean originatedFromTheory(IRodinFile file)
	throws CoreException{
		IRodinProject project = file.getRodinProject();
		IRodinFile theory = project.getRodinFile(file.getBareName()+"."+THEORY_FILE_EXTENSION);
		if(theory.exists()){
			return true;
		}
		return false;
	}
}
