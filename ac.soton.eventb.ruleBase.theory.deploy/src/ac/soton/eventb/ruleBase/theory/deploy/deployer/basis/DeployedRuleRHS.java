package ac.soton.eventb.ruleBase.theory.deploy.deployer.basis;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.RHS_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.deploy.deployer.IDeployedRuleRHS;

/**
 * An implementation of a deployed rhs of a rewrite rule internal element.
 * @author maamria
 *
 */
public class DeployedRuleRHS extends EventBElement implements IDeployedRuleRHS{

	public DeployedRuleRHS(String name, IRodinElement parent) {
		super(name, parent);
	}

	
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	
	public String getRHSString() throws RodinDBException {
		return getAttributeValue(RHS_ATTRIBUTE);
	}

	
	public boolean hasRHSString() throws RodinDBException {
		return hasAttribute(RHS_ATTRIBUTE);
	}

	
	public void setRHSString(String form, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(RHS_ATTRIBUTE, form, pm);
		
	}

}
