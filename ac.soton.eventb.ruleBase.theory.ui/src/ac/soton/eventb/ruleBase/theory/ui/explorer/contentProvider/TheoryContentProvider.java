package ac.soton.eventb.ruleBase.theory.ui.explorer.contentProvider;

import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

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
