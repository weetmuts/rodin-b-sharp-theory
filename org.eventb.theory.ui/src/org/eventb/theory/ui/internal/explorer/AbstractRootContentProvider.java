/**
 * 
 */
package org.eventb.theory.ui.internal.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.explorer.model.TheoryModelController;

/**
 * Based on fr.systerel.internal.explorer.navigator.contentProviders.
 * AbstractRootContentProvider.
 * 
 * @author maamria
 * 
 */

public abstract class AbstractRootContentProvider implements
		ITreeContentProvider {

	private static final Object[] NO_OBJECT = new Object[0];

	protected final IInternalElementType<? extends IEventBRoot> rootType;

	public AbstractRootContentProvider(
			IInternalElementType<? extends IEventBRoot> rootType) {
		this.rootType = rootType;
	}

	public void dispose() {
		// ignore
	}

	public Object[] getChildren(Object element) {
		// ensure the existence on the instance
		TheoryModelController.createInstance();
		if (element instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) element);
			if (proj.exists()) {
				TheoryModelController.processProject(proj);
				try {
					return getRootChildren(proj);
				} catch (RodinDBException e) {
					TheoryUIUtils.log(e, "when accessing " + rootType.getName()
							+ " roots of " + proj);
				}
			}
		}
		return NO_OBJECT;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) element);
			if (proj.exists()) {
				try {
					return getRootChildren(proj).length > 0;
				} catch (RodinDBException e) {
					return false;
				}
			}
		}
		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	protected abstract IEventBRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException;

}
