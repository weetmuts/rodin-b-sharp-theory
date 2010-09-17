package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IInternalTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.maths.extensions.InternalTheoryTransformer;
import org.eventb.theory.internal.core.util.CoreUtilities;
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
@SuppressWarnings("restriction")
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
		IEventBProject project = root.getEventBProject();
		String projectName = project.getRodinProject().getElementName();
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		IFormulaExtension cond = Cond.getCond();
		extensions.add(cond);
		FormulaFactory factory = FormulaFactory.getInstance(extensions);
		if (!rootIsNotGenerated(root)) {
			try {
				IInternalTheory internalTheories[] = root
						.getChildrenOfType(IInternalTheory.ELEMENT_TYPE);
				if (internalTheories.length > 0) {
					for (IInternalTheory internalTheory : internalTheories) {
						InternalTheoryTransformer transformer = new InternalTheoryTransformer();
						Set<IFormulaExtension> internalExtensions = transformer
								.transform(internalTheory, factory,
										factory.makeTypeEnvironment());
						extensions.addAll(internalExtensions);
						factory = FormulaFactory.getInstance(extensions);
					}
					// TODO compare with what we have? No need
					extensions.remove(cond);
					return extensions;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		List<String> execludedTheories = new ArrayList<String>();
		if (root instanceof ITheoryRoot) {
			execludedTheories.add(root.getElementName());
		}
		FormulaExtensionsLoader loader = new FormulaExtensionsLoader(project,
				execludedTheories);
		try {
			extensions.addAll(loader.getFormulaExtensions());
			extensions.remove(cond);
			extensionsCache.put(projectName, extensions);
			changedProjects.put(projectName, newBoolean(false));
			if (root instanceof ITheoryRoot) {
				Set<IFormulaExtension> neededExtensions = new LinkedHashSet<IFormulaExtension>();
				neededExtensions.addAll(extensions);
				neededExtensions.removeAll(loader.getExecludedExtensions());
				return neededExtensions;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return extensionsCache.get(projectName);
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
							changedProjects.put(projectName, newBoolean(true));
							// one change is enough to trigger re-requesting of
							// deployed extensions
							break;
						}
					}
				}

			}

		}
	}

	protected Boolean newBoolean(boolean bool) {
		return new Boolean(bool);
	}

	protected boolean rootIsNotGenerated(IEventBRoot root) {
		return (root instanceof IContextRoot || root instanceof IMachineRoot || root instanceof ITheoryRoot);
	}

	@Override
	public void setFormulaFactory(IEventBRoot root, FormulaFactory ff) {
		try {
			ISCTheoryRoot scTheoryRoot = CoreUtilities.correspondsToSCTheory(root);
			if (!rootIsNotGenerated(root)) {

				IDeployedTheoryRoot[] deployedRoots = root
						.getRodinProject()
						.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
				for (IDeployedTheoryRoot deployedRoot : deployedRoots) {

					if (scTheoryRoot != null
							&& deployedRoot.getElementName().equals(
									scTheoryRoot.getElementName())) {
						continue;
					}
					CoreUtilities
							.copyMathematicalExtensions(root, deployedRoot, true);
				}
				// copy it from the SC file instead
				if (scTheoryRoot != null)
					CoreUtilities
							.copyMathematicalExtensions(root, scTheoryRoot, true);

			}
		} catch (CoreException ex) {

		}
	}
}
