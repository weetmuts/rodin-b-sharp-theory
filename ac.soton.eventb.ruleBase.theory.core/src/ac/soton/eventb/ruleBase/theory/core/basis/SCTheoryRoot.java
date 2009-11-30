package ac.soton.eventb.ruleBase.theory.core.basis;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.basis.EventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ICategory;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCSet;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ISCVariable;

public class SCTheoryRoot extends EventBRoot implements ISCTheoryRoot {

	public SCTheoryRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCTheoryRoot> getElementType() {
		return ISCTheoryRoot.ELEMENT_TYPE;
	}

	public ISCRewriteRule getSCRewriteRule(String name) {
		return getInternalElement(ISCRewriteRule.ELEMENT_TYPE, name);
	}

	public ISCRewriteRule[] getSCRewriteRules() throws RodinDBException {
		return getChildrenOfType(ISCRewriteRule.ELEMENT_TYPE);
	}

	public ISCSet getSCSet(String name) {
		return getInternalElement(ISCSet.ELEMENT_TYPE, name);
	}

	public ISCSet[] getSCSets() throws RodinDBException {
		return getChildrenOfType(ISCSet.ELEMENT_TYPE);
	}

	public ISCVariable getSCVariable(String name) {
		return getInternalElement(ISCVariable.ELEMENT_TYPE, name);
	}

	public ISCVariable[] getSCVariables() throws RodinDBException {
		return getChildrenOfType(ISCVariable.ELEMENT_TYPE);
	}

	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {
		ITypeEnvironment typenv = factory.makeTypeEnvironment();
		augmentTypeEnvironment(this, typenv, factory);
		return typenv;
	}

	// Utility method
	private void augmentTypeEnvironment(ISCTheoryRoot thy,
			ITypeEnvironment typenv, FormulaFactory factory)
			throws RodinDBException {

		for (ISCSet set : thy.getSCSets()) {
			typenv.addGivenSet(set.getIdentifierString());
		}
		for (ISCVariable var : thy.getSCVariables()) {
			typenv.addName(var.getIdentifierString(), var.getType(factory));
		}
	}


	public ICategory[] getCategories() throws RodinDBException {
		return getChildrenOfType(ICategory.ELEMENT_TYPE);
	}

	public ICategory getCategory(String catName) {
		return getInternalElement(ICategory.ELEMENT_TYPE, catName);
	}

}
