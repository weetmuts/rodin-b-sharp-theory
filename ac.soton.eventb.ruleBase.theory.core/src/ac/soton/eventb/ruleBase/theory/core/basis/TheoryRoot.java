package ac.soton.eventb.ruleBase.theory.core.basis;

import org.eventb.core.basis.EventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ICategory;
import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ISet;
import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.IVariable;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

public class TheoryRoot extends EventBRoot implements ITheoryRoot {

	public TheoryRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	
	public IInternalElementType<ITheoryRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	public IRewriteRule getRewriteRule(String ruleName) {
		return getInternalElement(IRewriteRule.ELEMENT_TYPE, ruleName);
	}

	public IRewriteRule[] getRewriteRules() throws RodinDBException {
		return getChildrenOfType(IRewriteRule.ELEMENT_TYPE);
	}

	public IRodinFile getSCTheoryFile(String bareName) {
		String fileName = TheoryPlugin.getSCTheoryFileName(bareName);
		IRodinFile file = getRodinProject().getRodinFile(fileName);
		return file;
	}

	public ISCTheoryRoot getSCTheoryRoot() {
		return getSCTheoryRoot(getElementName());
	}

	public ISCTheoryRoot getSCTheoryRoot(String bareName) {
		ISCTheoryRoot root = (ISCTheoryRoot) getSCTheoryFile(bareName)
				.getRoot();
		return root;
	}

	public ISet getSet(String setName) {
		return getInternalElement(ISet.ELEMENT_TYPE, setName);
	}

	public ISet[] getSets() throws RodinDBException {
		return getChildrenOfType(ISet.ELEMENT_TYPE);
	}

	public IVariable getVariable(String varName) {
		return getInternalElement(IVariable.ELEMENT_TYPE, varName);
	}

	public IVariable[] getVariables() throws RodinDBException {
		return getChildrenOfType(IVariable.ELEMENT_TYPE);
	}

	
	public ICategory[] getCategories() throws RodinDBException {
		return getChildrenOfType(ICategory.ELEMENT_TYPE);
	}
	
	
	public ICategory getCategory(String catName) {
		return getInternalElement(ICategory.ELEMENT_TYPE, catName);
	}
}
