package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.deploy.IDeployedTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * A project scope formula provider. 
 * <p>
 * This provider listens on any changes that relate to deployed theories, so that
 * for a given project, it would know if reloading of extensions is necessary.
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
		
		RodinCore.addElementChangedListener(this, IResourceChangeEvent.POST_CHANGE);
		
		extensionsCache = new LinkedHashMap<String, Set<IFormulaExtension>>();
		changedProjects = new LinkedHashMap<String, Boolean>();
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBProject project) {
		String projectName = project.getRodinProject().getElementName();
		if (extensionsCache.containsKey(projectName)) {
			if(!changedProjects.get(projectName).booleanValue()){
				return extensionsCache.get(projectName);
			}
		}
		if (!changedProjects.containsKey(projectName)) {
			changedProjects.put(projectName, newBoolean(true));
		}

		Set<IFormulaExtension> ext = new LinkedHashSet<IFormulaExtension>();
		FormulaExtensionsLoader loader = new FormulaExtensionsLoader(project);
		try {
			ext.addAll(loader.getFormulaExtensions());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		extensionsCache.put(projectName, ext);
		changedProjects.put(projectName, newBoolean(false));
		return ext;
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		// Rodin DB
		IRodinElementDelta[] affected = event.getDelta().getAffectedChildren();
		for (IRodinElementDelta delta : affected) {
			// rodin project
			if (delta.getElement() instanceof IRodinProject) {
				String projectName = ((IRodinProject) delta.getElement()).getElementName();
				IRodinElementDelta[] affectedChildren = delta
						.getAffectedChildren();
				for (IRodinElementDelta thyDelta : affectedChildren) {
					if (thyDelta.getElement() instanceof IRodinFile) {
						IInternalElement root = ((IRodinFile) thyDelta
								.getElement()).getRoot();
						if (root instanceof IDeployedTheoryRoot) {
							changedProjects.put(projectName, newBoolean(true));
							// one change is enough to trigger re-requesting of deployed theories
							break;
						}
					}
				}

			}

		}
	}
	
	protected Boolean newBoolean(boolean bool){
		return new Boolean(bool);
	}

}
