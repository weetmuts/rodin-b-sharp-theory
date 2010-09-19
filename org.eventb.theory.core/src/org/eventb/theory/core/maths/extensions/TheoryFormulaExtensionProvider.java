package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * A project scope formula provider.
 * <p>
 * This provider listens on any changes that relate to deployed theories, so
 * that for a given project, it would know if reloading of extensions is
 * necessary.
 * 
 * @author maamria
 * 
 */
public class TheoryFormulaExtensionProvider implements
		IFormulaExtensionProvider, IElementChangedListener {

	protected final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID
			+ ".theoryExtensionsProvider";

	/**
	 * Cache for project information.
	 */
	protected final Map<String, Set<IFormulaExtension>> extensionsCache;
	protected final Map<String, Boolean> changedProjects;

	public TheoryFormulaExtensionProvider() {

		RodinCore.addElementChangedListener(this,
				IResourceChangeEvent.POST_CHANGE);

		extensionsCache = new LinkedHashMap<String, Set<IFormulaExtension>>();
		changedProjects = new LinkedHashMap<String, Boolean>();
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		try {
			if(TheoryCoreFacade.originatedFromTheory(root.getRodinFile())){
				return new LinkedHashSet<IFormulaExtension>();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		IEventBProject project = root.getEventBProject();
		try {
			IProjectDependenciesManager manager = new 
				ProjectDependenciesManager(project.getRodinProject());
			return manager.getFormulaExtensions(root);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		return new LinkedHashSet<IFormulaExtension>();
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		// Rodin DB
		IRodinElementDelta[] affectedDeltas = event.getDelta()
				.getAffectedChildren();
		for (IRodinElementDelta delta : affectedDeltas) {
			IRodinElement element = delta.getElement();
			if (element instanceof IRodinProject) {
				String projectName = ((IRodinProject) element).getElementName();
				IRodinElementDelta[] affectedChildrenDeltas = delta
						.getAffectedChildren();
				for (IRodinElementDelta thyDelta : affectedChildrenDeltas) {
					IRodinElement fileElement = thyDelta.getElement();
					if (fileElement instanceof IRodinFile) {
						IInternalElement root = ((IRodinFile) fileElement)
								.getRoot();
						if (root instanceof IDeployedTheoryRoot) {
							changedProjects.put(projectName, true);
							// one change is enough to trigger re-requesting of
							// deployed extensions
							break;
						}
					}
				}

			}

		}
	}

	

	@Override
	public void setFormulaFactory(IEventBRoot root, FormulaFactory ff) {
		IProjectDependenciesManager manager;
		try {
			if(TheoryCoreFacade.originatedFromTheory(root.getRodinFile())){
				return;
			}
			manager = new ProjectDependenciesManager(root.getRodinProject());
			manager.setFormulaFactory(root);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
