package ac.soton.eventb.ruleBase.theory.ui.explorer.actionProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.editor.actions.TheoryDeployer;
import ac.soton.eventb.ruleBase.theory.ui.editor.images.ITheoryImages;
import ac.soton.eventb.ruleBase.theory.ui.editor.images.TheoryImage;
import ac.soton.eventb.ruleBase.theory.ui.explorer.ActionProvider;
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
		menu.appendToGroup(GROUP_META, getDeployTheoryAction());
		menu.appendToGroup(GROUP_META, ActionCollection
				.getDeleteAction(site));
	}

	private Action getDeployTheoryAction(){
		Action action = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					if(sel.getFirstElement() instanceof ITheoryRoot){
						ITheoryRoot root = (ITheoryRoot) sel.getFirstElement();
						TheoryDeployer deployer = new TheoryDeployer(root.getElementName(), 
								root.getRodinProject(), site.getViewSite().getShell());
						deployer.deploy();
						
					}
				}
			}
		};
		action.setText("&Deploy");
		action.setImageDescriptor(TheoryImage.getImageDescriptor(ITheoryImages.IMG_THEORY_PATH));
		return action;
	}
	
}

