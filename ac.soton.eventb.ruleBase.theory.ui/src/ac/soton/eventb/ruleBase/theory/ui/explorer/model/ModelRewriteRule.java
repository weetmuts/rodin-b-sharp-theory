package ac.soton.eventb.ruleBase.theory.ui.explorer.model;

import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;

@SuppressWarnings("restriction")
public class ModelRewriteRule extends ModelPOContainer{
	
	IRewriteRule rule;
	
	public ModelRewriteRule(IRewriteRule rule, IModelElement parent){
		this.rule = rule;
		this.parent = parent;
	}

	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type != IPSStatus.ELEMENT_TYPE) {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for rule: " +type);
			}
			return new Object[0];
		}
		return getIPSStatuses();
	}

	public IRodinElement getInternalElement() {
		return rule;
	}

	public Object getParent(boolean complex) {
		if(parent instanceof ModelTheory){
			return ((ModelTheory) parent). rewRuleNode;
		}
		return parent;
	}

}
