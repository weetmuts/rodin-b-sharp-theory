package org.eventb.theory.ui.explorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.ITheoryImages;
import org.eventb.theory.internal.ui.TheoryImage;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.deploy.SimpleDeployWizard;
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
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				ActionCollection.getDeleteAction(site));
	}

	public void fillContextMenu(IMenuManager menu) {
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN,
				ActionProvider.getOpenAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH,
				buildOpenWithMenu());
		menu.add(new Separator(GROUP_META));
		menu.appendToGroup(GROUP_META, getDeployTheoryAction());
		menu.appendToGroup(GROUP_META, ActionCollection.getDeleteAction(site));
	}

	private Action getDeployTheoryAction() {
		Action action = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site
						.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					if (sel.getFirstElement() instanceof ITheoryRoot) {
						if (TheoryUIUtils.createDeployEmptyTheoryDialog(site.getViewSite().getShell(),
								(ITheoryRoot)sel.getFirstElement())) {

							SimpleDeployWizard wizard = new SimpleDeployWizard(
									null, (ITheoryRoot) sel.getFirstElement());
							WizardDialog wd = new WizardDialog(null, wizard);
							wd.setTitle(wizard.getWindowTitle());
							wd.open();
						}

					}
				}
			}
		};
		action.setText("&Deploy");
		action.setImageDescriptor(TheoryImage
				.getImageDescriptor(ITheoryImages.IMG_DEPLOY_PATH));
		return action;
	}

}
