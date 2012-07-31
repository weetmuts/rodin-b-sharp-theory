/**
 * 
 */
package org.eventb.theory.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
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
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author RenatoSilva
 *
 */
public class DatabaseUtilitiesTheoryPath{
	
	// As in "theory path unchecked language"
	public static final String THEORY_PATH_FILE_EXTENSION = "tul";
	// As in "theory path checked language"
	public static final String SC_THEORY_PATH_FILE_EXTENSION = "tcl";
	
	// The theory path configuration for the SC and POG
	public static final String THEORY_PATH_CONFIGURATION = TheoryPlugin.PLUGIN_ID + ".tul";
	
	/**
	 * Returns the full name of a theory path file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getTheoryPathFullName(String name) {
		return name + "." + THEORY_PATH_FILE_EXTENSION;
	}
	
	/**
	 * Returns the full name of a theory path file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getSCTheoryPathFullName(String name) {
		return name + "." + SC_THEORY_PATH_FILE_EXTENSION;
	}
	
	public static String getFullDescriptionAvailableTheory(IRodinProject rodinProject, IDeployedTheoryRoot deployedTheory){
		return "["+rodinProject.getElementName()+"]."+deployedTheory.getComponentName();
	}
	
	/**
	 * Adds a listener to the workspace that it is triggered when any of the <code>files</code> are modified. 
	 * The listener action is applied to <code>root</code> for <code>resourceChangeKind</code>.
	 * 
	 * @param files
	 * @param root
	 * @param resourceChangeKind
	 */
	public static <T extends IEventBRoot> void addListener(final List<IPath> files, final T root, int resourceChangeKind){
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				Object source = event.getSource();

				if(source instanceof IProject){
					IProject project = (IProject) source;
					if(project.equals(root.getRodinProject().getProject()))
						return;
				}
				
				IResourceDelta delta = event.getDelta();
				IResourceDelta[] affectedProjects = delta.getAffectedChildren();
				for(IResourceDelta affectedProject: affectedProjects){
					if(!affectedProject.getFullPath().equals(root.getRodinProject().getResource().getFullPath())){
						for(IResourceDelta affectedFile: affectedProject.getAffectedChildren()){
							if(files.contains(affectedFile.getFullPath())){
								buildDependencyProject(root);
								break;
							}
						}
					}
				}
			}
		};

		workspace.addResourceChangeListener(listener, resourceChangeKind);
	}
	
	/**
	 * Builds the rodinProject specified according to the kind of build defined
	 * @param root
	 * @param builderKind
	 */
	private static void buildDependencyProject(final IEventBRoot root) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws RodinDBException {
					try {
						root.setAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE,root.hasAttribute(EventBAttributes.COMMENT_ATTRIBUTE) ? root.getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE): "", monitor);
						root.getRodinFile().save(monitor, true);
						root.getRodinProject().getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
					} catch (CoreException e) {
						CoreUtilities.log(e, "when trying to build project " + root.getElementName());
					}
				}
			}, null);
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "when trying to build project " + root.getElementName());
		}
	}

}
