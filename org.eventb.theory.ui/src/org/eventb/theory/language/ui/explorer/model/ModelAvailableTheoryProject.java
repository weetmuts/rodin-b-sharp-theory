/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.eventb.theory.core.IAvailableTheoryProject;

import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author renatosilva
 *
 */
public class ModelAvailableTheoryProject extends AbstractModelElement<IAvailableTheoryProject> {

	public Object availableTheory_node;

	@SuppressWarnings("restriction")
	public ModelAvailableTheoryProject(IAvailableTheoryProject avt,
			IModelElement modelTheoryPath) {
		super(avt, modelTheoryPath);
	}

	public Object getParent(boolean complex) {
		if (parent instanceof ModelTheoryPath) {
			return ((ModelTheoryPath) parent).availableTheoryProject_node;
		}
		return parent;
	}

}
