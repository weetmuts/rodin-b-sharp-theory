/**
 * 
 */
package org.eventb.theory.language.core.sc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.StaticChecker;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.sc.Messages;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author RenatoSilva
 *
 */
public class TheoryPathStaticChecker extends StaticChecker{

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
			IRodinFile source = RodinCore.valueOf(file);
			ITheoryPathRoot root = (ITheoryPathRoot) source.getRoot();
			IRodinFile target = root.getSCTheoryPathRoot().getRodinFile();
			graph.addTarget(target.getResource());
			graph.addToolDependency(source.getResource(), target.getResource(), true);
			
			for(IAvailableTheoryProject theoryProj: root.getAvailableTheoryProjects()){
				List<IPath> files = new ArrayList<IPath>();
				for(IAvailableTheory availableTheory: theoryProj.getTheories()){
					if(availableTheory.hasAvailableTheory()){
						IFile resource = availableTheory.getDeployedTheory().getRodinFile().getResource();
						IPath path = resource.getFullPath();
						files.add(path);
						graph.addToolDependency(
								resource,
								target.getResource(), false);
					}
				}
				IRodinProject theoryProject = theoryProj.getTheoryProject();
				
				if(!theoryProject.equals(root.getRodinProject())){
					DatabaseUtilitiesTheoryPath.addListener(files,root, IResourceChangeEvent.POST_BUILD);
				}
			}
			
		} finally {
			monitor.done();
		}
	}
}
