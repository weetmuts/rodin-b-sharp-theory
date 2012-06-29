package org.eventb.theory.core.basis;

import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public class AxiomaticDefinitionsBlock extends TheoryElement implements IAxiomaticDefinitionsBlock {

	public AxiomaticDefinitionsBlock(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IAxiomaticOperatorDefinition getAxiomaticOperatorDefinition(String name) {
		return getInternalElement(IAxiomaticOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public IAxiomaticOperatorDefinition[] getAxiomaticOperatorDefinitions() throws RodinDBException {
		return getChildrenOfType(IAxiomaticOperatorDefinition.ELEMENT_TYPE);
	}

	@Override
	public IAxiomaticDefinitionAxiom getAxiomaticDefinitionAxiom(String name) {
		return getInternalElement(IAxiomaticDefinitionAxiom.ELEMENT_TYPE, name);
	}

	@Override
	public IAxiomaticDefinitionAxiom[] getAxiomaticDefinitionAxioms() throws RodinDBException {
		return getChildrenOfType(IAxiomaticDefinitionAxiom.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
