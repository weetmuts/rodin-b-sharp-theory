/**
 * 
 */
package org.eventb.theory.language.core.sc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.StaticChecker;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
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
						graph.addUserDependency(
								source.getResource(), 
								resource,
								target.getResource(), false);
						
					}
				}
				IRodinProject theoryProject = theoryProj.getTheoryProject();
				
				if(!theoryProject.equals(root.getRodinProject()))
					addListener(theoryProject, files, root);
			}
			
		} finally {
			monitor.done();
		}
	}
	
	
	private void addListener(final IRodinProject rodinProject, final List<IPath> files, final ITheoryPathRoot root){
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {
		      public void resourceChanged(IResourceChangeEvent event) {
		    	  
//		    	  if(event.getType() == IResourceChangeEvent.POST_CHANGE){
		    		  IResourceDelta delta = event.getDelta();
		    		  IResourceDelta[] affectedChildren = delta.getAffectedChildren();
		    		  for(IResourceDelta resourceDelta: affectedChildren){
		    			  if(resourceDelta.getFullPath().equals(rodinProject.getResource().getFullPath())){
		    				  IResourceDelta[] affectedChildren2 = affectedChildren[0].getAffectedChildren();
		    				  for(IResourceDelta file: affectedChildren2){
		    					  if(files.contains(file.getFullPath())){
		    						  System.out.println("Something changed on file:" + file.getResource().getName() + " for theoryPath "+ "[" + rodinProject.getElementName() +"]" + root.getComponentName());
		    						  setBuildTheoryRoot(root);
		    						  break;
		    					  }
		    				  }
		    			  }
		    		  }
		    		  
//		    	  }
		      }
		   };
		   
		workspace. addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}
	
	private final void setBuildTheoryRoot(final ITheoryPathRoot root) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws RodinDBException {
					try {
						root.getRodinProject().getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, null);
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "when trying to build theoryPath " + root.getComponentName());
		}
	}
	
	
}
