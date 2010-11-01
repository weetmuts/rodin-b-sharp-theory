package org.eventb.theory.ui.explorer;

import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.eventb.theory.ui.internal.explorer.AbstractContentProvider;

import fr.systerel.internal.explorer.model.IModelElement;

@SuppressWarnings("restriction")
public class DatatypeConstructorContentProvider extends AbstractContentProvider {

	/**
	 * @param type
	 */
	public DatatypeConstructorContentProvider() {
		super(IDatatypeConstructor.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {

		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}

}
