package ac.soton.eventb.ruleBase.theory.ui.explorer.actionProvider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import ac.soton.eventb.ruleBase.theory.ui.explorer.ActionProvider;
import fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection;


@SuppressWarnings("restriction")
public class RootActionProvider extends NavigatorActionProvider {
	
	public void fillActionBars(IActionBars actionBars) {
        // forward doubleClick to doubleClickAction
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
              ActionProvider.getOpenAction(site));
        // forwards pressing the delete key to deleteAction
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), ActionCollection.getDeleteAction(site));
    }
	
	public void fillContextMenu(IMenuManager menu) {
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, ActionProvider
				.getOpenAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH,
				buildOpenWithMenu());
		menu.add(new Separator(GROUP_MODELLING));
		menu.appendToGroup(GROUP_MODELLING, ActionCollection
				.getDeleteAction(site));
		menu.appendToGroup(GROUP_MODELLING, ActionProvider
				.getRetryAutoProversAction(site));
		menu.appendToGroup(GROUP_MODELLING, ActionProvider
				.getRecalculateAutoStatusAction(site));
	}

}

