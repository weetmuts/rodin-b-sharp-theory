package ac.soton.eventb.ruleBase.theory.ui.explorer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.internal.core.pm.ProofManager;
import org.eventb.internal.core.pom.AutoProver;
import org.eventb.internal.core.pom.RecalculateAutoStatus;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.editor.images.ITheoryImages;
import ac.soton.eventb.ruleBase.theory.ui.editor.images.TheoryImage;
import ac.soton.eventb.ruleBase.theory.ui.explorer.model.TheoryModelController;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;
import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;

/**
 * A collection of actions for the navigator
 *
 */
@SuppressWarnings({ "restriction"})
public class ActionProvider {
	
	/**
	 * Provides an open action for various elements in Rodin (Machines,
	 * Contexts, Invariants, ProofObligations...)
	 * 
	 * @param site
	 * @return An open action
	 */
	public static Action getOpenAction(final ICommonActionExtensionSite site) {
		Action doubleClickAction = new Action("Open") {
			@Override
			public void run() {
				ISelection selection = site.getStructuredViewer()
						.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (!(obj instanceof IRodinProject)) {
					TheoryUIUtils.linkToEventBEditor(obj);
				}
			}
		};
		return doubleClickAction;

	}
	
	
	public static RecalculateAutoStatusAction getRecalculateAutoStatusAction(final ICommonActionExtensionSite site) {
		return new RecalculateAutoStatusAction(site.getStructuredViewer());
	}
	
	public static RetryAutoProversAction getRetryAutoProversAction(final ICommonActionExtensionSite site) {
		return new RetryAutoProversAction(site.getStructuredViewer());
	}
	
}

/**
 * This is mostly copied from 
 * org.eventb.internal.ui.obligationexplorer.actions.ObligationsRecalcuateAutoStatus
 *
 */
@SuppressWarnings("restriction")
class RecalculateAutoStatusAction extends Action {

	private StructuredViewer viewer;

    public RecalculateAutoStatusAction(StructuredViewer viewer) {
		this.viewer = viewer;
		setText("Re&calculate Auto Status");
		setToolTipText("Rerun the Auto Prover on all selected proof obligations to recalculate the auto proven status");
		setImageDescriptor(TheoryImage
				.getImageDescriptor(ITheoryImages.IMG_DISCHARGED_PATH));
	}

	@Override
	public void run() {
		// Rerun the auto prover on selected elements.
		// The enablement condition guarantees that only machineFiles and
		// contextFiles are selected.
		ISelection sel = viewer.getSelection();
		assert (sel instanceof IStructuredSelection);
		IStructuredSelection ssel = (IStructuredSelection) sel;
		
		final Object [] objects = ssel.toArray(); 
				
		// Run the auto prover on all remaining POs
		IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				for (Object obj : objects) {
					if (obj instanceof IRodinProject || obj instanceof IProject) {
						IRodinProject rodinPrj;
						if (obj instanceof IProject ){
							rodinPrj = RodinCore.valueOf((IProject) obj);
						} else{
							rodinPrj = (IRodinProject) obj;
						}
						if (rodinPrj.exists()) {
							final IPSRoot[] psRoots;
							try {
								psRoots = rodinPrj
										.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
							} catch (RodinDBException e) {
								EventBUIExceptionHandler
										.handleGetChildrenException(e,
												UserAwareness.IGNORE);
								continue;
							}
							for (IPSRoot root : psRoots) {
								treatRoot(root, monitor);
							}
						}
					}
					if (obj instanceof IEventBRoot) {
						IEventBRoot root = (IEventBRoot) obj;
						if (root instanceof ITheoryRoot) {
							treatRoot(root, monitor);
						}
					}
					
					if (obj instanceof IPSStatus) {
						
						IPSStatus status = (IPSStatus)obj;
						IRodinFile psFile = status.getOpenable();
						IPSRoot psRoot = (IPSRoot) psFile.getRoot();
						IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
						IPSStatus[] statuses = new IPSStatus[]{status};
						try {
							// AutoProver.run(prFile, psFile, statuses, monitor);
							RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
							continue;
						} catch (RodinDBException e) {
							EventBUIExceptionHandler.handleRodinException(
									e, UserAwareness.IGNORE);
							continue;
						}

					}
					
					if (obj instanceof IElementNode) {
						treateNode(monitor, (IElementNode) obj);
						continue;
					}
					
					//invariants, events, theorems, axioms
					if (obj instanceof IRodinElement) {
						IModelElement element = TheoryModelController.getModelElement(obj);
						treatElement(monitor, element);
						continue;
						
					}
				}
			}

			
		};
		
		runWithProgress(op);
	}


	
	void treatElement(IProgressMonitor monitor,
			IModelElement element) {
		if (element != null) {
			ArrayList<Object> stats = new ArrayList<Object>();
			stats.addAll(Arrays.asList(element.getChildren(IPSStatus.ELEMENT_TYPE, false)));
			ArrayList<IPSStatus> result = new ArrayList<IPSStatus>();
			IPSStatus status = null;
			for (Object stat : stats) {
				if (stat instanceof IPSStatus) {
					result.add((IPSStatus) stat);
					status = (IPSStatus) stat;
				}
			}
			// at least one status found.
			if (status != null) {
				IRodinFile psFile = status.getRodinFile();
				IPSRoot psRoot = (IPSRoot) status.getRoot();
				IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
				try {
					RecalculateAutoStatus.run(prFile, psFile, result.toArray(new IPSStatus[result.size()]), monitor);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleRodinException(
							e, UserAwareness.IGNORE);
				}
			}
		}
	}
	
	void treateNode(IProgressMonitor monitor, IElementNode node) {
		if (node.getChildrenType() == IPSStatus.ELEMENT_TYPE) {
			IEventBRoot root = node.getParent();
			treatRoot(root, monitor);						
		} else {
			try {
				Object[] children=node.getParent().getChildrenOfType(node.getChildrenType());
				ArrayList<Object> stats = new ArrayList<Object>();
				
				for (Object child : children) {
					IModelElement element = TheoryModelController.getModelElement(child);
					if (element != null) {
						stats.addAll(Arrays.asList(element.getChildren(IPSStatus.ELEMENT_TYPE, false)));
					}
				}
				ArrayList<IPSStatus> result = new ArrayList<IPSStatus>();
				for (Object stat : stats) {
					if (stat instanceof IPSStatus) {
						result.add((IPSStatus) stat);
					}
				}
				IRodinFile psFile = node.getParent().getPSRoot().getRodinFile();
				IPSRoot psRoot = (IPSRoot) psFile.getRoot();
				IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
				RecalculateAutoStatus.run(prFile, psFile, result.toArray(new IPSStatus[result.size()]), monitor);
				
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleRodinException(
						e, UserAwareness.IGNORE);
			}
		}
	}
	
	void treatRoot(IEventBRoot root, IProgressMonitor monitor) {
		IPSRoot psRoot = root.getPSRoot();
		IRodinFile psFile = psRoot.getRodinFile();
		IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
		IPSStatus[] statuses;
		try {
			statuses = psRoot.getStatuses();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler
					.handleGetChildrenException(e,
							UserAwareness.IGNORE);
			return;
		}
		try {
			// AutoProver.run(prFile, psFile, statuses,
			// monitor);
			RecalculateAutoStatus.run(prFile, psFile,
					statuses, monitor);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(
					e, UserAwareness.IGNORE);
		}
		
	}


	private void runWithProgress(IRunnableWithProgress op) {
		final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}
    
	
}


/**
 * This is mostly copied from
 * org.eventb.internal.ui.obligationexplorer.actions.ObligationsAutoProver
 *
 */
@SuppressWarnings("restriction")
class RetryAutoProversAction extends Action {
	
	private StructuredViewer viewer;

    public RetryAutoProversAction(StructuredViewer viewer) {
		this.viewer = viewer;
		setText("&Retry Auto Provers");
		setToolTipText(" Retry the Automatic Provers on the selected proof obligations");
		setImageDescriptor(TheoryImage
				.getImageDescriptor(ITheoryImages.IMG_DISCHARGED_PATH));
	}
	
	
	@Override
	public void run() {
		// Rerun the auto prover on selected elements.
		// The enablement condition guarantees that only machineFiles and
		// contextFiles are selected.
		ISelection sel = viewer.getSelection();
		assert (sel instanceof IStructuredSelection);
		IStructuredSelection ssel = (IStructuredSelection) sel;
		
		final Object [] objects = ssel.toArray(); 
				
		// Run the auto prover on all remaining POs
		IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				final IProofManager pm = getProofManager();
				for (Object obj : objects) {
					if (obj instanceof IRodinProject || obj instanceof IProject) {
						IRodinProject rodinPrj;
						if (obj instanceof IProject ){
							rodinPrj = RodinCore.valueOf((IProject) obj);
						} else{
							rodinPrj = (IRodinProject) obj;
						}
						if (rodinPrj.exists()) {
							final IPSRoot[] psRoots;
							try {
								psRoots = rodinPrj
										.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
							} catch (RodinDBException e) {
								EventBUIExceptionHandler
										.handleGetChildrenException(e,
												UserAwareness.IGNORE);
								continue;
							}
							for (IPSRoot root: psRoots) {
								treatRoot(pm, root, monitor);
							}
						}
						continue;
					}
					if (obj instanceof IEventBRoot) {
						treatRoot(pm, (IEventBRoot) obj, monitor);
						continue;
					}
					
					if (obj instanceof IPSStatus) {
						
						IPSStatus status = (IPSStatus)obj;
						IRodinFile psFile = status.getOpenable();
						IPSRoot psRoot = (IPSRoot) psFile.getRoot();
						final IProofComponent pc = pm.getProofComponent(psRoot);
						IPSStatus[] statuses = new IPSStatus[]{status};
						try {
							AutoProver.run(pc, statuses, monitor);
							// RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
						} catch (RodinDBException e) {
							EventBUIExceptionHandler.handleRodinException(
									e, UserAwareness.IGNORE);
						}
						continue;

					}
					if (obj instanceof IElementNode) {
						treateNode(monitor, pm, (IElementNode) obj);
						continue;
					}
					
					//invariants, events, theorems, axioms
					if (obj instanceof IRodinElement) {
						IModelElement element = TheoryModelController.getModelElement(obj);
						treatElement(monitor, element, pm);
						continue;
						
					}
					
				}
			}
			
		};
		
		runWithProgress(op);
	}

	void treatElement(IProgressMonitor monitor,
			IModelElement element, IProofManager pm) {
		if (element != null) {
			ArrayList<Object> stats = new ArrayList<Object>();
			stats.addAll(Arrays.asList(element.getChildren(IPSStatus.ELEMENT_TYPE, false)));
			ArrayList<IPSStatus> result = new ArrayList<IPSStatus>();
			IPSStatus status = null;
			for (Object stat : stats) {
				if (stat instanceof IPSStatus) {
					result.add((IPSStatus) stat);
					status = (IPSStatus) stat;
				}
			}
			// at least one status found.
			if (status != null) {
				IRodinFile psFile = status.getRodinFile();
				IPSRoot psRoot = (IPSRoot) psFile.getRoot();
				try {
					final IProofComponent pc = pm.getProofComponent(psRoot);
					AutoProver.run(pc, result.toArray(new IPSStatus[result.size()]), monitor);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleRodinException(
							e, UserAwareness.IGNORE);
				}
			}
		}
	}
	
	void treateNode(IProgressMonitor monitor, final IProofManager pm,
			IElementNode node) {
		if (node.getChildrenType() == IPSStatus.ELEMENT_TYPE) {
			IEventBRoot root = node.getParent();
			treatRoot(pm, root, monitor);
		} else {
			try {
				Object[] children=node.getParent().getChildrenOfType(node.getChildrenType());
				ArrayList<Object> stats = new ArrayList<Object>();
				
				for (Object child : children) {
					IModelElement element = TheoryModelController.getModelElement(child);
					if (element != null) {
						stats.addAll(Arrays.asList(element.getChildren(IPSStatus.ELEMENT_TYPE, false)));
					}
				}
				ArrayList<IPSStatus> result = new ArrayList<IPSStatus>();
				for (Object stat : stats) {
					if (stat instanceof IPSStatus) {
						result.add((IPSStatus) stat);
					}
				}
				IPSRoot psRoot = node.getParent().getPSRoot();
				final IProofComponent pc = pm.getProofComponent(psRoot);
				AutoProver.run(pc, result.toArray(new IPSStatus[result.size()]), monitor);
				
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleRodinException(
						e, UserAwareness.IGNORE);
			}
			
		}
	}
	
	void treatRoot(IProofManager pm, IEventBRoot root, IProgressMonitor monitor){
		final IProofComponent pc = pm.getProofComponent(root);
		IPSStatus[] statuses;
		try {
			statuses = pc.getPSRoot().getStatuses();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler
					.handleGetChildrenException(e,
							UserAwareness.IGNORE);
			return;
		}
		try {
			AutoProver.run(pc, statuses, monitor);
			// RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(
					e, UserAwareness.IGNORE);
		}
		
	}

	private void runWithProgress(IRunnableWithProgress op) {
		final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}
	
	/**
	 * Returns the proof manager of this plug-in.
	 * 
	 * @return the proof manager
	 */
	public static IProofManager getProofManager() {
		return ProofManager.getDefault();
	}
	
}
