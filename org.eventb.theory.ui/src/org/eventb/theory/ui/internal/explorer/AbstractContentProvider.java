/**
 * 
 */
package org.eventb.theory.ui.internal.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.rodinp.core.IInternalElementType;
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
	
	@Override
	public Object[] getChildren(Object element) {
		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(type, false);
		}
		return NO_OBJECT;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// ignore
	
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

}
