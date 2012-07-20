/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.IAvailableTheory;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author RenatoSilva
 *
 */
public class TheoryPathModelElementNode implements IModelElement, IElementNode {
	
	public TheoryPathModelElementNode(IInternalElementType<?> type, ModelTheoryPath parent) {
		this.type = type;
		this.parent = parent;
		if (parent instanceof ModelTheoryPath) {
			this.parentRoot = ((ModelTheoryPath) parent).getTheoryPathRoot();
		}
	}
	
	private IInternalElementType<?> type;
	private ModelTheoryPath parent;
	private IEventBRoot parentRoot;	

	@Override
	public ModelTheoryPath getModelParent() {
		return parent;
	}

	@Override
	public IInternalElementType<?> getChildrenType() {
		return type;
	}

	@Override
	public IEventBRoot getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRodinElement getInternalElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(boolean complex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(IInternalElementType<?> element_type, boolean complex) {
		if (type != element_type) {
			return new Object[0];
		} 
		else {
			if (type == IAvailableTheory.ELEMENT_TYPE) {
////				return parent.getIPSStatuses();
////			} else {
////				try {
////					return parentRoot.getChildrenOfType(type);
////				} catch (RodinDBException e) {
////					UIUtils.log(e, "when accessing children of type " +type +" of " +parentRoot);
////				}
			}
		}
		return new Object[0];
	}

}
