package org.eventb.theory.ui.explorer;


import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.theory.ui.explorer.model.ModelTheory;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.eventb.theory.ui.internal.explorer.AbstractContentProvider;

import fr.systerel.explorer.IElementNode;

/**
 * The content provider for CarrierSet elements
 */
public class TypeParameterContentProvider extends AbstractContentProvider {

	public TypeParameterContentProvider() {
		super(ITypeParameter.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {

		// there is no ModelElement for carrier sets.
		if (element instanceof ITypeParameter) {
			ITypeParameter carr = (ITypeParameter) element;
			ITheoryRoot root = (ITheoryRoot) carr.getRoot();
			ModelTheory thy = TheoryModelController.getTheory(root);
			if (thy != null) {
				return thy.typepar_node;
			}
		}
		if (element instanceof IElementNode) {
			return ((IElementNode) element).getParent();
		}
		return null;
	}

}
