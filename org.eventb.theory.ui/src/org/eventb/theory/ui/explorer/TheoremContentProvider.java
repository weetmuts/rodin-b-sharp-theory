package org.eventb.theory.ui.explorer;

import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.ui.explorer.model.ModelTheory;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.eventb.theory.ui.internal.explorer.AbstractContentProvider;

import fr.systerel.explorer.IElementNode;

public class TheoremContentProvider extends AbstractContentProvider {

	/**
	 * @param type
	 */
	public TheoremContentProvider() {
		super(ITheorem.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {

		if (element instanceof ITheorem) {
			ITheorem carr = (ITheorem) element;
			ITheoryRoot root = (ITheoryRoot) carr.getRoot();
			ModelTheory thy = TheoryModelController.getTheory(root);
			if (thy != null) {
				return thy.thm_node;
			}
		}
		if (element instanceof IElementNode) {
			return ((IElementNode) element).getParent();
		}
		return null;
	}

}
