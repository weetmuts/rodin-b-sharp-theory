package ac.soton.eventb.ruleBase.theory.deploy.deployer.basis;

import static ac.soton.eventb.ruleBase.theory.deploy.deployer.DeployAttributes.SOUND_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.TheoryElement;
import ac.soton.eventb.ruleBase.theory.deploy.deployer.IDeployedRewriteRule;
import ac.soton.eventb.ruleBase.theory.deploy.deployer.IDeployedRuleRHS;

/**
 * An implementation for deployed rewrite rule internal element.
 * @author maamria
 *
 */
public class DeployedRewriteRule extends TheoryElement implements IDeployedRewriteRule{

	public DeployedRewriteRule(String name, IRodinElement parent) {
		super(name, parent);
	}

	
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	
	public IDeployedRuleRHS getRHS(String rhsName){
		return getInternalElement(IDeployedRuleRHS.ELEMENT_TYPE, rhsName);
	}

	
	public IDeployedRuleRHS[] getRHSs() throws RodinDBException {
		return getChildrenOfType(IDeployedRuleRHS.ELEMENT_TYPE);
	}

	
	public boolean hasSound() throws RodinDBException {
		return hasAttribute(SOUND_ATTRIBUTE);
	}

	
	public boolean isSound() throws RodinDBException {
		return getAttributeValue(SOUND_ATTRIBUTE);
	}

	
	public void setSound(boolean isSound, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(SOUND_ATTRIBUTE, isSound, monitor);
		
	}

}
