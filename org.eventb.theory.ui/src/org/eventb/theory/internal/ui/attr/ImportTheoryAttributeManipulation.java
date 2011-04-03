/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryElement;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * 
 */
public class ImportTheoryAttributeManipulation extends
		AbstractAttributeManipulation {

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// no default

	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		return asImportTheoryElement(element).hasImportTheory();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		return asImportTheoryElement(element).getImportTheory()
				.getComponentName();
	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		IRodinProject proj = element.getRodinProject();
		ISCTheoryRoot root = DatabaseUtilities.getSCTheory(value, proj);
		if (root != null && root.exists())
			asImportTheoryElement(element).setImportTheory(root, monitor);

	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asImportTheoryElement(element).removeAttribute(
				TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, monitor);

	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		final IImportTheoryElement theoryElement = asImportTheoryElement(element);
		final Set<String> results = new HashSet<String>();
		try {
			final Set<String> theoryNames = getTheoryNames(theoryElement);
			final Set<String> usedTheoryNames = getUsedTheoryNames(theoryElement);
			final String elementValue = getElementValue(theoryElement);

			// result = contextRoot \ (usedContextNames \ { elementValue })
			// then remove values that would introduce a cycle
			final Set<String> valueToRemove = new HashSet<String>();
			valueToRemove.addAll(usedTheoryNames);
			valueToRemove.remove(elementValue);

			results.addAll(theoryNames);
			results.removeAll(valueToRemove);
			removeCycle(theoryElement, results);
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "Error while populating potential imports");
		}
		return results.toArray(new String[results.size()]);

	}

	protected IImportTheoryElement asImportTheoryElement(IRodinElement element) {
		return (IImportTheoryElement) element;
	}

	private Set<String> getTheoryNames(IInternalElement element)
			throws CoreException {
		final IRodinProject rodinProject = element.getRodinProject();
		ISCTheoryRoot[] scTheoryRoots;
		final HashSet<String> result = new HashSet<String>();
		scTheoryRoots = rodinProject
				.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		for (ISCTheoryRoot root : scTheoryRoots) {
			result.add(root.getComponentName());
		}
		return result;
	}

	private String getElementValue(IImportTheoryElement element)
			throws CoreException {
		if (element.exists() && hasValue(element, null))
			return getValue(element, null);
		else
			return "";

	}

	public Set<String> getUsedTheoryNames(IImportTheoryElement element)
			throws CoreException {
		Set<String> usedNames = new HashSet<String>();
		ITheoryRoot root = (ITheoryRoot) element.getRoot();
		// First add myself
		usedNames.add(root.getElementName());
		for (IImportTheoryElement clause : root.getImportTheories()) {

			if (hasValue(clause, null))
				usedNames.add(getValue(clause, null));

		}
		return usedNames;
	}

	protected void removeCycle(IImportTheoryElement element,
			Set<String> theories) throws CoreException {
		final ITheoryRoot root = (ITheoryRoot) element.getRoot();
		final IRodinProject prj = root.getRodinProject();
		final Iterator<String> iter = theories.iterator();
		while (iter.hasNext()) {
			final String name = iter.next();
			final ITheoryRoot theory = DatabaseUtilities.getTheory(name, prj);
			if (isImportedBy(root, theory)) {
				iter.remove();
			}
		}
	}

	private boolean isImportedBy(ITheoryRoot abstractRoot, ITheoryRoot root)
			throws CoreException {
		for (ITheoryRoot thy : getImportedTheories(root)) {
			if (thy.equals(abstractRoot) || isImportedBy(abstractRoot, thy))
				return true;
		}
		return false;
	}

	private ITheoryRoot[] getImportedTheories(ITheoryRoot root)
			throws CoreException {
		List<ITheoryRoot> list = new ArrayList<ITheoryRoot>();
		IImportTheory[] imported = root.getImportTheories();
		IRodinProject project = root.getRodinProject();
		for (IImportTheory imp : imported) {
			if (imp.hasImportTheory()) {
				list.add(DatabaseUtilities.getTheory(imp.getImportTheory()
						.getComponentName(), project));
			}
		}
		return list.toArray(new ITheoryRoot[list.size()]);
	}

}
