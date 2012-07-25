/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - tests for theories
 *******************************************************************************/
package org.eventb.theory.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.DatabaseUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class EventBTheoryFileTest extends TestCase {
	
	private static final String BARE_NAME = "foo";

	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	IRodinProject rodinProject = RodinCore.valueOf(root.getProject("P"));

	private void assertFileName(String name, IRodinFile file) {
		assertEquals("Invalid file name", name, file.getElementName());
	}

	private void assertRootFileName(String extension, IEventBRoot root) {
		final String fileName = root.getElementName() + "." + extension;
		assertFileName(fileName, root.getRodinFile());
	}

	/**
	 * Check all file conversions from the given file.
	 * 
	 * @param file
	 *            an event-B file
	 */
	private void checkFileConversions(IRodinFile file) {
		IEventBRoot root = (IEventBRoot)file.getRoot();
		final String bareName = file.getBareName();
		assertEquals(bareName, root.getComponentName());
		assertRootFileName("bpo", root.getPORoot());
		assertRootFileName("bpr", root.getPRRoot());
		assertRootFileName("bps", root.getPSRoot());
	}

	/**
	 * Ensures that an unchecked theory can be created from an event-B project.
	 */
	public void testTheoryFile() throws Exception {
		IRodinFile file = DatabaseUtilities.getTheory(BARE_NAME, rodinProject).getRodinFile();
		assertFileName(BARE_NAME + ".tuf", file);
		checkFileConversions(file);
	}


	/**
	 * Ensures that a checked theory can be created from an event-B project.
	 */
	public void testSCTheoryFile() throws Exception {
		IRodinFile file = DatabaseUtilities.getSCTheory(BARE_NAME, rodinProject).getRodinFile();
		assertFileName(BARE_NAME + ".tcf", file);
		checkFileConversions(file);
	}
	
	/**
	 * Ensures that a deployed theory can be created from an event-B project.
	 */
	public void testDeployedTheoryFile() throws Exception {
		IRodinFile file = DatabaseUtilities.getDeployedTheory(BARE_NAME, rodinProject).getRodinFile();
		assertFileName(BARE_NAME + ".dtf", file);
		checkFileConversions(file);
	}

	private void assertSimilar(IRodinFile input, IRodinFile expected, IRodinFile actual) {
		if (expected.getRootElementType() == input.getRootElementType()) {
			assertSame(expected, actual);
		} else {
			assertEquals(expected, actual);
		}
	}
	
	public void testFileAdaptation() throws Exception {
		final IRodinFile tuf = DatabaseUtilities.getTheory(BARE_NAME, rodinProject).getRodinFile();
		final IRodinFile tcf = DatabaseUtilities.getSCTheory(BARE_NAME, rodinProject).getRodinFile();
		final IRodinFile dtf = DatabaseUtilities.getDeployedTheory(BARE_NAME, rodinProject).getRodinFile();
		final IRodinFile[] files = new IRodinFile[] { tuf, tcf, dtf};

		for (IRodinFile file : files) {
			final IFile res = file.getResource();
			assertEquals(file, EventBPlugin.asEventBFile(res));
			assertSimilar(file, file, EventBPlugin.asEventBFile(file));
		}
	}

}
