package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;

public class DeployWizardPageTwo extends WizardPage {
	
	private TableViewer theoriesTableViewer;

	/**
	 * Create the wizard.
	 */
	public DeployWizardPageTwo() {
		super("deployWizardPage");
		setTitle(Messages.wizard_deployTitle);
		setDescription(Messages.wizard_deployDescription);
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		label.setText("The following theories will be deployed:");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		theoriesTableViewer = new TableViewer(container, SWT.BORDER);
		Table table = theoriesTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setup();
		setControl(container);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible){
			DeployWizardPageOne pageOne = (DeployWizardPageOne) getPreviousPage();
			theoriesTableViewer.setInput(pageOne.getSelectedTheories());
		}
		super.setVisible(visible);
	}
	
	private void setup(){
		theoriesTableViewer.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			@Override
			public void dispose() {}
			
			@Override
			public Object[] getElements(Object inputElement) {
				@SuppressWarnings("unchecked")
				Set<ISCTheoryRoot> input = (Set<ISCTheoryRoot>) inputElement;
				return input.toArray();
			}
		});
		theoriesTableViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				ISCTheoryRoot root = (ISCTheoryRoot) element;
				return TheoryUIUtils.getTheoryImage(root);
			}

			@Override
			public String getText(Object element) {
				ISCTheoryRoot root = (ISCTheoryRoot) element;
				return root.getElementName();
			}
		});
	}

}
