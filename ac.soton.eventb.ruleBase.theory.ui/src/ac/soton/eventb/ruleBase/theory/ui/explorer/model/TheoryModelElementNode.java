/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.explorer.model;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import fr.systerel.internal.explorer.model.ModelElementNode;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryModelElementNode extends ModelElementNode {

	private static String PO_TYPE = "Proof Obligations";
	private static String REWRITE_RULE_TYPE = "Rewrite Rules";
	private ModelPOContainer parent;
	private IEventBRoot parentRoot;
	private IInternalElementType<?> type;

	public TheoryModelElementNode(IInternalElementType<?> type,
			ModelPOContainer model) {
		super(type, model);
		this.type = type;
		parentRoot = ((ModelTheory) model).getTheoryRoot();
		parent = model;
		parentRoot.getRodinProject().getRodinFile(parentRoot.getComponentName());
	}

	public Object[] getChildren(IInternalElementType<?> element_type,
			boolean complex) {
		if (type != element_type) {
			return new Object[0];
		} else {
			if (type == IPSStatus.ELEMENT_TYPE) {
				return parent.getIPSStatuses();
			} else {
				try {
					return parentRoot.getChildrenOfType(type);
				} catch (RodinDBException e) {
					UIUtils.log(e, "when accessing children of type " +type +" of " +parentRoot);
				}
			}
			
		}
		return new Object[0];

	}

	public IInternalElementType<?> getChildrenType() {
		return type;
	}

	public IRodinElement getInternalElement() {
		return parentRoot.getRoot();
	}

	public String getLabel() {
		if (type.equals(IRewriteRule.ELEMENT_TYPE)) {
			return REWRITE_RULE_TYPE;
		}
		if(type.equals(IPSStatus.ELEMENT_TYPE)){
			return PO_TYPE;
		}
		return null;

	}

	public ModelPOContainer getModelParent() {
		return (ModelPOContainer)parent;
	}

	public IEventBRoot getParent() {
		return parentRoot;
	}

	public Object getParent(boolean complex) {
		return parentRoot;
	}

}
