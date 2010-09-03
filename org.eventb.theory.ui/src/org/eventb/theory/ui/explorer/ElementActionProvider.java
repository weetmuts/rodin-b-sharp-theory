package org.eventb.theory.ui.explorer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.theory.ui.internal.explorer.ActionProvider;
import org.eventb.theory.ui.internal.explorer.NavigatorActionProvider;

public class ElementActionProvider extends NavigatorActionProvider {

	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		// forward doubleClick to doubleClickAction
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
				ActionProvider.getOpenAction(site));
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, ActionProvider
				.getOpenAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH,
				buildOpenWithMenu());
	}

}
