/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.eventb.theory.core.IAvailableTheory;
import org.rodinp.core.IInternalElementType;

import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author RenatoSilva
 *
 */
public class ModelAvailableTheory extends AbstractModelElement<IAvailableTheory>{
	
	@SuppressWarnings("restriction")
	public ModelAvailableTheory(IAvailableTheory element, IModelElement parent) {
		super(element, parent);
	}
	
	public Object getParent(boolean complex) {
		if (parent instanceof ModelAvailableTheoryProject) {
			return ((ModelAvailableTheoryProject) parent).internalElement;
		}
		return parent;
	}

	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
//		if (type != IPSStatus.ELEMENT_TYPE) {
//			if (ExplorerUtils.DEBUG) {
//				System.out.println("Unsupported children type for rule: " +type);
//			}
			return new Object[0];
//		}
//		return getIPSStatuses();
	}

}
