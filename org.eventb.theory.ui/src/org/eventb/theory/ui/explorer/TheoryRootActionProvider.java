package org.eventb.theory.ui.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.YesToAllMessageDialog;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.ITheoryImages;
import org.eventb.theory.internal.ui.TheoryImage;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.deploy.SimpleDeployWizard;
import org.eventb.theory.ui.internal.explorer.ActionProvider;
import org.eventb.theory.ui.internal.explorer.NavigatorActionProvider;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection;

@SuppressWarnings("restriction")
public class TheoryRootActionProvider extends NavigatorActionProvider {

	public static String GROUP_META = "meta";

	public void fillActionBars(IActionBars actionBars) {
		// forward doubleClick to doubleClickAction
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, ActionProvider.getOpenAction(site));
		// forwards pressing the delete key to deleteAction
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), ActionCollection.getDeleteAction(site));
	}

	public void fillContextMenu(IMenuManager menu) {
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, ActionProvider.getOpenAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, buildOpenWithMenu());
		menu.add(new Separator(GROUP_META));
		menu.appendToGroup(GROUP_META, getDeployTheoryAction());
		menu.appendToGroup(GROUP_META, getUndeployTheoryAction());
		menu.appendToGroup(GROUP_META, getDeleteAction(site));
	}

	private Action getDeployTheoryAction() {
		Action action = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					if (sel.getFirstElement() instanceof ITheoryRoot) {
						if (TheoryUIUtils.createDeployTheoryErrorDialog(site.getViewSite().getShell(), (ITheoryRoot) sel.getFirstElement())) {
							SimpleDeployWizard wizard = new SimpleDeployWizard(null, (ITheoryRoot) sel.getFirstElement());
							WizardDialog wd = new WizardDialog(null, wizard);
							wd.setTitle(wizard.getWindowTitle());
							wd.open();
						}

					}
				}
			}
			
			@Override
			public boolean isEnabled() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					if (sel.getFirstElement() instanceof ITheoryRoot) {
						ITheoryRoot root = (ITheoryRoot) sel.getFirstElement();
						if (DatabaseUtilities.isTheoryDeployable(root)){
							return true;
						}
					}
				}
				return false;
			}
		};
		action.setText("Deploy");
		action.setToolTipText("Deploy theory");
		action.setImageDescriptor(TheoryImage.getImageDescriptor(ITheoryImages.IMG_DTHEORY_PATH));
		return action;
	}

	private Action getUndeployTheoryAction() {
		Action action = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					if (sel.getFirstElement() instanceof ITheoryRoot) {
						ITheoryRoot theory = (ITheoryRoot) sel.getFirstElement();
						if (TheoryUIUtils.createUndeployUndeployedTheoryDialog(site.getViewSite().getShell(), theory)
								&& TheoryUIUtils.createConfirmUndeployTheoryDialog(site.getViewSite().getShell(), theory)) {

							DatabaseUtilities.cleanUp(theory.getDeployedTheoryRoot());
						}

					}
				}
			}
			
			@Override
			public boolean isEnabled() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					if (sel.getFirstElement() instanceof ITheoryRoot) {
						ITheoryRoot theory = (ITheoryRoot) sel.getFirstElement();
						if(theory.hasDeployedVersion()){
							return true;
						}
					}
				}
				return false;
			}
		};
		action.setText("Undeploy");
		action.setToolTipText("Undeploy theory and its dependents");
		action.setImageDescriptor(TheoryImage.getImageDescriptor(ITheoryImages.IMG_THEORY_PATH));
		return action;
	}

	private Action getDeleteAction(final ICommonActionExtensionSite site) {
		Action deleteAction = new Action() {
			@Override
			public void run() {
				if (!(site.getStructuredViewer().getSelection().isEmpty())) {

					// Putting the selection into a set which does not contains
					// any pair
					// of parent and child
					Collection<IRodinElement> set = new ArrayList<IRodinElement>();

					IStructuredSelection ssel = (IStructuredSelection) site.getStructuredViewer().getSelection();

					for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
						final Object obj = it.next();
						if (!(obj instanceof IRodinElement)) {
							continue;
						}
						IRodinElement elem = (IRodinElement) obj;
						if (elem.isRoot()) {
							elem = elem.getParent();
						}
						set = UIUtils.addToTreeSet(set, elem);
					}

					int answer = YesToAllMessageDialog.YES;
					for (IRodinElement element : set) {
						if (element instanceof IRodinFile) {
							IRodinFile rodinFile = (IRodinFile) element;
							if (answer != YesToAllMessageDialog.YES_TO_ALL) {
								answer = YesToAllMessageDialog.openYesNoToAllQuestion(site.getViewSite().getShell(), "Confirm File Delete",
										"Are you sure you want to delete file '" + rodinFile.getElementName() + "' in project '"
												+ element.getParent().getElementName() + "' ?");
							}
							if (answer == YesToAllMessageDialog.NO_TO_ALL)
								break;
							// FIXED BUG here when u press the close button (X) it deletes the theory anyway
							if (answer != YesToAllMessageDialog.NO && answer != -1) {
								try {
									TheoryUIUtils.closeOpenedEditor(rodinFile);
									// delete the deployed version
									if(rodinFile.getRootElementType().equals(ITheoryRoot.ELEMENT_TYPE)){
										ITheoryRoot theoryRoot = (ITheoryRoot) rodinFile.getRoot();
										
										IRodinFile deployedFile = rodinFile.getRodinProject().getRodinFile(
												DatabaseUtilities.getDeployedTheoryFullName(theoryRoot.getComponentName()));
										if (deployedFile.exists()){
											deployedFile.delete(true, new NullProgressMonitor());
										}
									}
									rodinFile.delete(true, new NullProgressMonitor());
									
								} catch (PartInitException e) {
									MessageDialog.openError(null, "Error", "Could not delete file");
								} catch (RodinDBException e) {
									MessageDialog.openError(null, "Error", "Could not delete file");
								}
							}
						}
					}

				}
			}
		};
		deleteAction.setText("&Delete");
		deleteAction.setToolTipText("Delete theory");
		deleteAction.setImageDescriptor(EventBImage.getImageDescriptor(IEventBSharedImages.IMG_DELETE_PATH));
		return deleteAction;
	}
}
