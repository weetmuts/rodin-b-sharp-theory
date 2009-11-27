package ac.soton.eventb.ruleBase.theory.deploy.deployer.basis;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.TheoryElement;
import ac.soton.eventb.ruleBase.theory.deploy.deployer.IDeployedRewriteRule;
import ac.soton.eventb.ruleBase.theory.deploy.deployer.IDeployedTheoryRoot;
import ac.soton.eventb.ruleBase.theory.deploy.deployer.IMetaSet;
import ac.soton.eventb.ruleBase.theory.deploy.deployer.IMetaVariable;

/**
 * An implementation of a deployed theory file root element.
 * @author maamria
 *
 */
public class DeployedTheoryRoot extends TheoryElement implements IDeployedTheoryRoot{

	public DeployedTheoryRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IMetaSet getMetaSet(String name){
		return getInternalElement(IMetaSet.ELEMENT_TYPE, name);
	}

	@Override
	public IMetaSet[] getMetaSets() throws RodinDBException {
		return getChildrenOfType(IMetaSet.ELEMENT_TYPE);
	}


	@Override
	public IMetaVariable getMetaVariable(String name){
		return getInternalElement(IMetaVariable.ELEMENT_TYPE, name);
	}

	@Override
	public IMetaVariable[] getMetaVariables() throws RodinDBException {
		return getChildrenOfType(IMetaVariable.ELEMENT_TYPE);
	}

	@Override
	public IDeployedRewriteRule getRewriteRule(String ruleName){
		return getInternalElement(IDeployedRewriteRule.ELEMENT_TYPE, ruleName);
	}

	@Override
	public IDeployedRewriteRule[] getRewriteRules() throws RodinDBException {
		return getChildrenOfType(IDeployedRewriteRule.ELEMENT_TYPE);
	}
	
}
