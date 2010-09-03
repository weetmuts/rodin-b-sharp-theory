package org.eventb.theory.ui.explorer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.theory.ui.internal.explorer.ActionProvider;
import org.eventb.theory.ui.internal.explorer.NavigatorActionProvider;

import fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection;


@SuppressWarnings("restriction")
public class TheoryRootActionProvider extends NavigatorActionProvider {
	
	public static String GROUP_META = "meta";
	
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
		menu.add(new Separator(GROUP_META));
		menu.appendToGroup(GROUP_META, ActionCollection
				.getDeleteAction(site));
	}

	
	
}

