/**
 * 
 */
package org.eventb.theory.core;

import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

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
	public static <T extends IEventBRoot> void addListener(final List<IRodinFile> files, final T root){
		IElementChangedListener listener = new IElementChangedListener() {
			@Override
			public void elementChanged(ElementChangedEvent event) {
				IRodinElementDelta delta = event.getDelta();
				IRodinElementDelta[] affectedProjects = delta.getAffectedChildren();
				for(IRodinElementDelta affectedProject: affectedProjects){
//					if(!affectedProject.getElement().equals(root.getRodinProject())){
						for(IRodinElementDelta affectedFile: affectedProject.getAffectedChildren()){
							if(files.contains(affectedFile.getElement())){
								notifyDependency(root);
								break;
							}
						}
//					}
				}
			}
		};
		
		RodinCore.addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
	}
	
	/**
	 * Builds the rodinProject specified according to the kind of build defined
	 * @param root
	 * @param builderKind
	 */
	private static void notifyDependency(final IEventBRoot root) {
		IJobManager jobManager = Job.getJobManager();
		ISchedulingRule currentRule = jobManager.currentRule();
		
		jobManager.beginRule(currentRule, null);
		
		Job job = new Job(Messages.progress_notifyTheoryPath) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					root.setAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE,root.hasAttribute(EventBAttributes.COMMENT_ATTRIBUTE) ? root.getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE): "", monitor);
					root.getRodinFile().save(monitor, true);
					root.getRodinProject().getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
				} catch (CoreException e) {
					CoreUtilities.log(e, "when trying to build project " + root.getElementName() + " after notifying file "+ root.getComponentName());
					return Status.CANCEL_STATUS;
				}
				
				return Status.OK_STATUS;

			}
		};
		
		job.setUser(false);
		job.schedule(); // start as soon as possible
		
		jobManager.endRule(currentRule);
	}

}
