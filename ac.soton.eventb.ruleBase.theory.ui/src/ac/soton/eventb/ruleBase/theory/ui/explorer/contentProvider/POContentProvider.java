/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.explorer.contentProvider;

import org.eventb.core.IPSStatus;

import ac.soton.eventb.ruleBase.theory.ui.explorer.model.TheoryModelController;
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

	// proof obligations can have multiple parents. return none at all.
	public Object getParent(Object element) {
		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}

}
