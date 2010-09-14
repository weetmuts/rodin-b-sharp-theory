package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashSet;
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

public class TheoryFormulaExtensionProvider implements
		IFormulaExtensionProvider, IElementChangedListener {

	protected final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID
			+ ".theoryExtensionsProvider";

	public TheoryFormulaExtensionProvider() {
		RodinCore.addElementChangedListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBProject project) {
		Set<IFormulaExtension> ext = new LinkedHashSet<IFormulaExtension>();
		FormulaExtensionsLoader loader = new FormulaExtensionsLoader(project);
		try {
			ext.addAll(loader.getFormulaExtensions());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return ext;
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		// Rodin DB
		IRodinElementDelta[] affected = event.getDelta().getAffectedChildren();
		for (IRodinElementDelta delta : affected) {
			// rodin project
			if (delta.getElement() instanceof IRodinProject) {
				IRodinElementDelta[] affectedChildren = delta
						.getAffectedChildren();
				for (IRodinElementDelta thyDelta : affectedChildren) {
					if (thyDelta.getElement() instanceof IRodinFile) {
						IInternalElement root = ((IRodinFile) thyDelta
								.getElement()).getRoot();
						if (root instanceof IDeployedTheoryRoot) {
							System.out.print(true);
						}
					}
				}

			}

		}
	}

}
