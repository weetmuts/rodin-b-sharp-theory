package org.eventb.theory.ui.deploy;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eventb.internal.ui.RodinProjectSelectionDialog;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class DeployWizardPageOne extends WizardPage {
	
	private Text projectText;
	private Combo theoryCombo;
	
	private String projectName;
	private String theoryName;
	
	public DeployWizardPageOne() {
		super("dWizardPage1");
		setTitle("Select theory");
		setDescription("Select a theory to deploy. The theory must be non-empty.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		Label label = new Label(container, SWT.NULL);
		label.setText("Project:");
		
		projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(projectText.getText().equals(projectName)){
					return;
				}
				projectName = projectText.getText();
				theoryName = null;
				theoryCombo.removeAll();
				String[] names =TheoryUIUtils.getNonEmptySCTheoryNames(projectName);
				if(names!=null)
					theoryCombo.setItems(names);
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("Theory:");

		theoryCombo = new Combo(container, SWT.READ_ONLY|SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		theoryCombo.setLayoutData(gd);
		theoryCombo.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				String value = theoryCombo.getItem(theoryCombo
						.getSelectionIndex());
				if(theoryName != null && theoryName.equals(value)){
					return;
				}
				theoryName = value;
				dialogChanged();
				
			}
		});
		theoryCombo.setBounds(100, 39, 307, 21);
		theoryCombo.setToolTipText("Select a non-empty theory to deploy");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		initialise();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Uses the RODIN project selection dialog to choose the new value for the
	 * project field.
	 */
	void handleBrowse() {
		final String projectName = getProjectName();
		IRodinProject rodinProject;
		if (projectName.equals(""))
			rodinProject = null;
		else
			rodinProject = TheoryUIPlugIn.getRodinDatabase().getRodinProject(
					projectName);

		RodinProjectSelectionDialog dialog = new RodinProjectSelectionDialog(
				getShell(), rodinProject, false, "Project Selection",
				"Select a RODIN project");
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				projectText.setText(((IRodinProject) result[0])
						.getElementName());
				dialogChanged();
			}
		}
	}
	
	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		if(projectName == null || projectName.equals("")){
			updateStatus("Project must be specified");
			return;
		}
		else if (TheoryCoreFacade.getRodinProject(projectName)== null){
			updateStatus("Specified project does not exist");
			theoryCombo.removeAll();
			return;
		}
		if(theoryName == null){
			updateStatus("Theory must be specified");
			return;
		}
		
		updateStatus(null);
	}
	
	private void initialise(){
		theoryName = null;
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
		
	}
	
	/**
	 * Get the name of the project.
	 * <p>
	 * 
	 * @return The name of the project
	 */
	public String getProjectName() {
		return projectText.getText();
	}
	
	
	
	public String getTheoryName() {
		return theoryName;
	}

}
