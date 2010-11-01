/**
 * 
 */
package org.eventb.theory.ui.internal.explorer;

import org.eventb.core.IPSStatus;
import org.eventb.theory.ui.explorer.model.TheoryModelController;

import fr.systerel.internal.explorer.model.IModelElement;


/**
 * 
 * The content provider for proof obligations
 * 
 */
@SuppressWarnings("restriction")
public class POContentProvider extends AbstractContentProvider {

	public POContentProvider() {
		super(IPSStatus.ELEMENT_TYPE);
	}

	public Object getParent(Object element) {
		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}

}
