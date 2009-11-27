package ac.soton.eventb.ruleBase.theory.ui.explorer.contentProvider;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.ui.explorer.model.TheoryModelController;
import fr.systerel.internal.explorer.model.IModelElement;

@SuppressWarnings("restriction")
public class RewriteRuleContentProvider extends AbstractContentProvider {

	public RewriteRuleContentProvider() {
		super(IRewriteRule.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {
		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}
}
