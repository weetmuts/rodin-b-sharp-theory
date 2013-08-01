package org.eventb.theory.core.plugin;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.osgi.framework.BundleContext;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;

public class TheoryPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eventb.theory.core";

	// The shared instance
	private static TheoryPlugin plugin;
	
	/**
	 * The constructor
	 */
	public TheoryPlugin() {
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;		
		
		/*
		 * adding a listener for theory path creation/deletion, that triggers a project clean, in order to force the recalculation of the dependencies.
		 * it is needed when theory path is created after a context/machine (using the math extension), so the dependencies of the other component are not recalculated.
		 */
		IElementChangedListener listener = new IElementChangedListener() {

			@Override
			public void elementChanged(ElementChangedEvent event) {
				IRodinElementDelta delta = event.getDelta();
				IRodinElementDelta[] affectedProjects = delta.getAffectedChildren();
				for(IRodinElementDelta project: affectedProjects){
					IRodinElementDelta[] addedChildren =  project.getAddedChildren();
					IRodinElementDelta[] removedChildren =  project.getRemovedChildren();
					try{
						for(IRodinElementDelta addedChild: addedChildren){
							if (addedChild.getElement().getElementName().startsWith("TheoryPath"))
								project.getElement().getRodinProject().getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
						}
						for(IRodinElementDelta removedChild: removedChildren){
							if (removedChild.getElement().getElementName().startsWith("TheoryPath"))
								project.getElement().getRodinProject().getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
							
						}
					} catch (CoreException e) {
						CoreUtilities.log(e, "when trying to clean/build project " + project.getElement().getRodinProject().getProject().getName() + " after adding/deleting theory path file ");
					}
					
				}
			}
			
		};
		
		RodinCore.addElementChangedListener(listener);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TheoryPlugin getDefault() {
		return plugin;
	}
}
