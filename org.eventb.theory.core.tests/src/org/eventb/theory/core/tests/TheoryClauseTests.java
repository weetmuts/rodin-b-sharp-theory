package org.eventb.theory.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.rodinp.core.IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.IUseTheory;
import org.junit.Test;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * A test class for theory relationship directives (import and use).
 * @author maamria
 *
 */
public class TheoryClauseTests extends BuilderTest {

	protected static void assertError(int expectedCode, IRodinElement element,
			IWorkspaceRunnable runnable) throws CoreException {
		try {
			runnable.run(null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertError(expectedCode, element, e);
		}
	}

	protected static void assertError(int expectedCode, IRodinElement element,
			RodinDBException exception) {

		final IRodinDBStatus status = exception.getRodinDBStatus();
		assertEquals("Status should be an error", IRodinDBStatus.ERROR, status
				.getSeverity());
		assertEquals("Unexpected status code", expectedCode, status.getCode());
		IRodinElement[] elements = status.getElements();
		if (element == null) {
			assertEquals("Status should have no related element", 0,
					elements.length);
		} else {
			assertEquals("Status should be related to the given element", 1,
					elements.length);
			assertEquals("Status should be related to the given element",
					element, elements[0]);
		}
	}
	
	@Test
	public void testImportTheoryAbsent() throws Exception {
		final ITheoryRoot thy = createTheory("foo");
		final IImportTheory clause = thy.createChild(
				IImportTheory.ELEMENT_TYPE, null, null);
		assertFalse(clause.hasImportTheory());
		assertError(ATTRIBUTE_DOES_NOT_EXIST, clause, new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				clause.getImportTheory();
			}
		});
	}
	
	@Test
	public void testImportTheory() throws Exception {
		final ITheoryRoot thy = createTheory("foo");
		final IImportTheory clause = thy.createChild(
				IImportTheory.ELEMENT_TYPE, null, null);
		final IDeployedTheoryRoot target = DatabaseUtilities.getDeployedTheory("bar", rodinProject);
		clause.setImportTheory(target, null);
		assertEquals(target, clause.getImportTheory());
	}
	
	@Test
	public void testUseTheoryAbsent() throws Exception{
		final IDeployedTheoryRoot thy = createDeployedTheory("foo");
		final IUseTheory clause = thy.createChild(
				IUseTheory.ELEMENT_TYPE, null, null);
		assertFalse(clause.hasUseTheory());
		assertError(ATTRIBUTE_DOES_NOT_EXIST, clause, new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				clause.getUsedTheory();
			}
		});
	}
	
	@Test
	public void testUseTheory() throws Exception {
		final IDeployedTheoryRoot thy = createDeployedTheory("foo");
		final IUseTheory clause = thy.createChild(
				IUseTheory.ELEMENT_TYPE, null, null);
		final IDeployedTheoryRoot target = DatabaseUtilities.getDeployedTheory("bar", rodinProject);
		clause.setUsedTheory(target, null);
		assertEquals(target, clause.getUsedTheory());
	}
	
}
