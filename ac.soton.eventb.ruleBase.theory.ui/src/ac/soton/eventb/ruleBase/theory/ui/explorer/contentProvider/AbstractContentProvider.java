/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.explorer.contentProvider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rodinp.core.IInternalElementType;

import ac.soton.eventb.ruleBase.theory.ui.explorer.model.TheoryModelController;
import fr.systerel.internal.explorer.model.IModelElement;

/**
 * Based on
 * {@link fr.systerel.internal.explorer.navigator.contentProviders.AbstractContentProvider}
 * .
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractContentProvider implements ITreeContentProvider {

	protected static final Object[] NO_OBJECT = new Object[0];

	protected final IInternalElementType<?> type;

	public AbstractContentProvider(IInternalElementType<?> type) {
		this.type = type;
	}

	public void dispose() {
		// ignore

	}

	public Object[] getChildren(Object element) {
		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(type, false);
		}
		return NO_OBJECT;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

}
