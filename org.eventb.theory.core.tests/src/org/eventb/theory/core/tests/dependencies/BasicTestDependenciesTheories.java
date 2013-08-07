/**
 *
 */
package org.eventb.theory.core.tests.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author renatosilva
 *
 */
public abstract class BasicTestDependenciesTheories extends BasicTheorySCTestWithThyConfig{

	/**
	 * Check that the order of the setRoots match with the order that is described by scTheoryRoots
	 *
	 * @param setRoots
	 * 			the actual set of roots
	 * @param scTheoryRoots
	 * 			the expected set of roots
	 */
	protected void correctOrder(Set<ISCTheoryRoot> setRoots,
			ISCTheoryRoot... scTheoryRoots ) {

		Iterator<ISCTheoryRoot> iterator = setRoots.iterator();
		int i=0;
		while(iterator.hasNext()){
			ISCTheoryRoot currentItem = iterator.next();
			assertEquals("The theory graph is not correctly sorted.", scTheoryRoots[i++].getComponentName(), currentItem.getComponentName());
		}
	}

	/**
	 * Check that the order of the setRoots match with the order that is described by scTheoryRoots
	 *
	 * @param setRoots
	 * 			the actual set of roots
	 * @param scTheoryRoots
	 * 			the expected set of roots
	 */
	protected void correctPartialOrder(Set<ISCTheoryRoot> setRoots,
			ISCTheoryRoot... scTheoryRoots ) {

		Iterator<ISCTheoryRoot> iterator = setRoots.iterator();
		int i=0;
		while(iterator.hasNext()){
			ISCTheoryRoot currentItem = iterator.next();
			if(i<scTheoryRoots.length && currentItem.getComponentName().equals(scTheoryRoots[i].getComponentName())){
				i++;
			}
		}

		if(i<scTheoryRoots.length){
			StringBuilder expectedSB = new StringBuilder();
			StringBuilder actualSB = new StringBuilder();
			for(ISCTheoryRoot scTheoryRoot: scTheoryRoots){
				expectedSB.append(scTheoryRoot.getComponentName()+";");
			}
			for(ISCTheoryRoot scTheoryRoot: setRoots){
				actualSB.append(scTheoryRoot.getComponentName()+";");
			}
			fail("The theory graph is not correctly sorted. Order should follow: "+expectedSB+" but is: " + actualSB);
		}
	}

	protected IMarker[] runBuilderProblems(IRodinProject rp) throws CoreException {
		final IProject project = rp.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs= project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER,
				true,
				IResource.DEPTH_INFINITE
		);

		return buildPbs;
	}
}
