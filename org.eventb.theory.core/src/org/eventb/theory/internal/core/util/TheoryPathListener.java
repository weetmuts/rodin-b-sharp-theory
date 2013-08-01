package org.eventb.theory.internal.core.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.theory.core.ITheoryPathRoot;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

public class TheoryPathListener implements IElementChangedListener {

	private static void addTheoryPathProjects(IRodinElement element,
			Set<IProject> toBuild) {
		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			if (file.getRootElementType().equals(
					ITheoryPathRoot.ELEMENT_TYPE)) {
				toBuild.add(file.getRodinProject().getProject());
			}
		}
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		final IRodinElementDelta[] affectedProjects = delta
				.getAffectedChildren();
		for (IRodinElementDelta project : affectedProjects) {
			final Set<IProject> toBuild = new HashSet<IProject>();
			for (IRodinElementDelta addedChild : project.getAddedChildren()) {
				addTheoryPathProjects(addedChild.getElement(), toBuild);
			}
			for (IRodinElementDelta removedChild : project
					.getRemovedChildren()) {
				addTheoryPathProjects(removedChild.getElement(), toBuild);
			}
			if (toBuild.isEmpty()) {
				return;
			}

			final Job cleanBuildJob = new TheoryPathCleanBuilder(toBuild);
			cleanBuildJob.setRule(RodinCore.getRodinDB()
					.getSchedulingRule());
			cleanBuildJob.schedule();
		}
	}
}