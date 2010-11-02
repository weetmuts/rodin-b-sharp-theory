package org.eventb.theory.ui.internal.deploy;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eventb.theory.core.DB_TCFacade;

/**
 * @author maamria
 * 
 */
public class DeployWizardPageOne extends AbstractDeployWizardPageOne {

	private Combo theoryCombo;

	public DeployWizardPageOne() {
		super();
		setDescription("Select a theory to deploy. The theory must be non-empty.");
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	protected void dialogChanged() {
		if (theoryName == null) {
			btnRebuildProjects.setEnabled(false);
			updateStatus("Theory must be specified");
			return;
		}
		super.dialogChanged();
	}

	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);

	}

	@Override
	protected void customise(Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText("Theory:");

		theoryCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		theoryCombo.setLayoutData(gd);
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
		theoryCombo.setBounds(100, 39, 307, 21);
		theoryCombo.setItems(getSCTheories());
		theoryCombo.setToolTipText("Select a non-empty theory to deploy");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
	}
	
	private String[] getSCTheories(){
		try {
			Collection<String> col = DB_TCFacade.getNames(DB_TCFacade.getDeployableSCTheories());
			return col.toArray(new String[col.size()]);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[0];
	}
	
}
