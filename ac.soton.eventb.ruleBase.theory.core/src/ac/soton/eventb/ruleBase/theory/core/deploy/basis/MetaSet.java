package ac.soton.eventb.ruleBase.theory.core.deploy.basis;

import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.deploy.basis.IMetaSet;

/**
 * An implementation of a meta set internal element.
 * @author maamria
 *
 */
public class MetaSet extends EventBElement implements IMetaSet{

	public MetaSet(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
