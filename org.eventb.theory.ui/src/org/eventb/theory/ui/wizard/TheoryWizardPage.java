package org.eventb.theory.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eventb.theory.core.DB_TCFacade;
import org.rodinp.core.IRodinFile;

/**
 * <p>The "New" wizard page allows setting the container for the new file
 * as well as the file name. The page will only accept file name without
 * the extension OR with the extension that matches the expected one(tuf).
 * </p>
 * @author maamria
 * 
 */
public class TheoryWizardPage extends WizardPage {
	// Some text areas.
	private Text theoryText;

	public TheoryWizardPage() {
		super("wizardPage");
		setTitle("New Event-B Theory");
		setDescription("This wizard creates a new theory file in MathExtensions project.");
	}

	/**
	 * Creating the components of the dialog.
	 * <p>
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Theory Name:");

		theoryText = new Text(container, SWT.BORDER | SWT.SINGLE);
		theoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		theoryText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		initialize();
		dialogChanged();
		setControl(container);
	}
	
	
	public String getTheoryName() {
		return theoryText.getText();
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		String theoryName = getTheoryName();
		
		if (theoryName.length() == 0) {
			updateStatus("Theory name must be specified");
			return;
		}
		
		IRodinFile theoryFile = DB_TCFacade.getTheory(
				theoryName, 
				DB_TCFacade.getDeploymentProject(null)).getRodinFile();
		
		if (theoryFile.exists()) {
			updateStatus("There is already a theory with this name");
			return;
		}
		updateStatus(null);
	}


	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		theoryText.setText("changeMe");
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}