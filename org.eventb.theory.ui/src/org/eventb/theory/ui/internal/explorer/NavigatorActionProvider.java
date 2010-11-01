package org.eventb.theory.ui.internal.explorer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IInternalElement;

/**
 * An abstract Action Provider. 
 * Clients may overwrite <code>fillActionBars(IActionBars actionBars)</code>
 * and <code>fillContextMenu(IMenuManager menu)</code> from superclass.
 *
 */
public abstract class NavigatorActionProvider extends CommonActionProvider {

	
	public static String GROUP_MODELLING = "modelling";
	
    protected ICommonActionExtensionSite site;
    
    StructuredViewer viewer;

    @Override
    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);
        site = aSite;
		viewer = aSite.getStructuredViewer();
	}

    /**
     * Builds an Open With menu.
     * 
     * @return the built menu
     */
	public MenuManager buildOpenWithMenu() {
		MenuManager menu = new MenuManager("Open With",
				ICommonMenuConstants.GROUP_OPEN_WITH);
		ISelection selection = site.getStructuredViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		menu.add(new OpenWithMenu(TheoryUIPlugIn.getActivePage(),
				((IInternalElement) obj).getRodinFile().getResource()));
		return menu;
	}

}

