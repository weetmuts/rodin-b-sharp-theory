/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.eventb.theory.core.IAvailableTheory;

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
			return ((ModelAvailableTheoryProject) parent).availableTheory_node;
		}
		return parent;
	}

}
