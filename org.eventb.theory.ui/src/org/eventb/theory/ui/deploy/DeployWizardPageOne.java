package org.eventb.theory.ui.deploy;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eclipse.swt.layout.GridLayout;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class DeployWizardPageOne extends AbstractDeployWizardPageOne {

	private Combo theoryCombo;
	private Combo projectCombo;

	public DeployWizardPageOne() {
		super();
		setDescription("Select a theory to deploy. The theory must be non-empty.");
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	protected void dialogChanged() {
		if(projectName == null){
			theoryCombo.setEnabled(false);
			btnRebuildProjects.setSelection(false);
			btnRebuildProjects.setEnabled(false);
			updateStatus("Project must be specified");
			return;
		}
		theoryCombo.setEnabled(true);
		btnRebuildProjects.setSelection(false);
		btnRebuildProjects.setEnabled(false);
		if (theoryName == null) {
			updateStatus("Theory must be specified");
			return;
		}
		btnRebuildProjects.setEnabled(true);
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);

	}

	@Override
	protected void customise(Composite container) {
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText("Project:");

		projectCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		projectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		projectCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String value = projectCombo.getItem(projectCombo
						.getSelectionIndex());
				if (projectName != null && projectName.equals(value)) {
					return;
				}
				projectName = value;
				theoryName = null;
				theoryCombo.setItems(getSCTheories());
				dialogChanged();
				
			}
		});
		projectCombo.setItems(getProjects());
		Label label = new Label(container, SWT.NULL);
		label.setText("Theory:");

		theoryCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		theoryCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		theoryCombo.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String value = theoryCombo.getItem(theoryCombo
						.getSelectionIndex());
				if (theoryName != null && theoryName.equals(value)) {
					return;
				}
				theoryName = value;
				dialogChanged();

			}
		});
		theoryCombo.setToolTipText("Select a non-empty theory to deploy");
		new Label(container, SWT.NONE);

	}

	private String[] getSCTheories() {
		try {
			Collection<String> col = DatabaseUtilities.getNames(DatabaseUtilities
					.getDeployableSCTheories(projectName));
			return col.toArray(new String[col.size()]);
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "error retrieving deployable statically checked theories.");
		}
		return new String[0];
	}
	
	private String[] getProjects(){
		Collection<String> col = new ArrayList<String>();
		try {
			IRodinProject[] projects = RodinCore.getRodinDB().getRodinProjects();
			for (IRodinProject project : projects){
				col.add(project.getElementName());
			}
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "error retrieving available rodin projects.");
		}
		return col.toArray(new String[col.size()]);
	}

}
