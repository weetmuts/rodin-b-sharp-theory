package org.eventb.theory.ui.explorer;

import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.internal.explorer.AbstractRootContentProvider;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class TheoryContentProvider extends AbstractRootContentProvider {

	public TheoryContentProvider() {
		super(ITheoryRoot.ELEMENT_TYPE);
	}

	public Object getParent(Object element) {
		if (element instanceof ITheoryRoot) {
			return ((ITheoryRoot) element).getParent().getParent();
		}
		return null;
	}

	@Override
	protected IEventBRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException {
		return (ITheoryRoot[]) getTheoryChildren(project);
	}

	private IRodinElement[] getTheoryChildren(IRodinProject proj) {
		try {
			return proj.getRootElementsOfType(ITheoryRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "Error while retrieving "
					+ ITheoryRoot.ELEMENT_TYPE + " from " + proj);
			e.printStackTrace();
			return null;
		}
	}

}
