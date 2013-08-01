package org.eventb.theory.internal.core.util;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.theory.core.plugin.TheoryPlugin;

public class TheoryPathCleanBuilder extends Job {

	private final Set<IProject> toCleanBuild;

	public TheoryPathCleanBuilder(Set<IProject> toBuild) {
		super("Theory Path Dependency CLean Build");
		this.toCleanBuild = toBuild;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final SubMonitor sm = SubMonitor.convert(monitor,
				toCleanBuild.size());
		for (final IProject prj : toCleanBuild) {
			try {
				prj.build(
						IncrementalProjectBuilder.CLEAN_BUILD,
						sm.newChild(1));
			} catch (CoreException e) {
				CoreUtilities
						.log(e,
								"when trying to clean/build project "
										+ prj.getName()
										+ " after adding/deleting theory path file ");
			}
		}
		return new Status(IStatus.OK, TheoryPlugin.PLUGIN_ID,
				"Rebuild after theory path modification");
	}
}