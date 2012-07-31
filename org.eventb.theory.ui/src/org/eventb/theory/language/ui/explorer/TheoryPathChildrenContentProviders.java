/**
 * 
 */
package org.eventb.theory.language.ui.explorer;

import java.text.Collator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.language.ui.explorer.model.TheoryPathModelController;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.rodinp.core.IInternalElementType;

import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author RenatoSilva
 *
 */
public class TheoryPathChildrenContentProviders {

	public static class AntiSorter extends ViewerSorter {

		public AntiSorter() {
		}

		public AntiSorter(Collator collator) {
			super(collator);
		}

		/**
		 * No need for sorting.
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return -1;
		}

	}

	/**
	 * 
	 */
	public TheoryPathChildrenContentProviders() {
		// TODO Auto-generated constructor stub
	}

	public static class AvailableTheoryContentProvider extends AbstractContentProvider {

		/**
		 * @param type
		 */
		public AvailableTheoryContentProvider() {
			super(IAvailableTheory.ELEMENT_TYPE);
		}
	}
	
	public static class AvailableTheoryProjectContentProvider extends AbstractContentProvider {
		
		/**
		 * @param type
		 */
		public AvailableTheoryProjectContentProvider() {
			super(IAvailableTheoryProject.ELEMENT_TYPE);
		}
	}
}

	@SuppressWarnings("restriction")
	abstract class AbstractContentProvider implements ITreeContentProvider {

	protected static final Object[] NO_OBJECT = new Object[0];

	protected final IInternalElementType<?> type;

	public AbstractContentProvider(IInternalElementType<?> type) {
		this.type = type;
	}

	@Override
	public Object[] getChildren(Object element) {
		IModelElement model = TheoryPathModelController.getModelElement(element);
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

	/**
	 * Override this to change the parent behaviour.
	 */
	@Override
	public Object getParent(Object element) {

		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
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
