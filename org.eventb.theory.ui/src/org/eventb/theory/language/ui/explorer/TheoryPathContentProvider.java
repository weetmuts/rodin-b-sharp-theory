/**
 * 
 */
package org.eventb.theory.language.ui.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.ITheoryLanguageRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.language.ui.explorer.model.TheoryPathModelController;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Renato Silva
 *
 */
public class TheoryPathContentProvider implements ITreeContentProvider {
	
	private static final Object[] NO_OBJECT = new Object[0];
	
	protected final IInternalElementType<? extends IEventBRoot> rootType;
	
	public TheoryPathContentProvider() {
		rootType = ITheoryLanguageRoot.ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		// ensure the existence on the instance
		TheoryPathModelController.createInstance();
		if (parentElement instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) parentElement);
			if (proj.exists()) {
				TheoryPathModelController.processProject(proj);
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof ITheoryLanguageRoot) {
			return ((ITheoryLanguageRoot) element).getParent().getParent();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
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
	
	protected IEventBRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException {
		return (ITheoryLanguageRoot[]) getTheoryLanguageChildren(project);
	}
	
	private IRodinElement[] getTheoryLanguageChildren(IRodinProject proj) {
		try {
			return proj.getRootElementsOfType(ITheoryLanguageRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "Error while retrieving "
					+ ITheoryRoot.ELEMENT_TYPE + " from " + proj);
			e.printStackTrace();
			return null;
		}
	}

}
