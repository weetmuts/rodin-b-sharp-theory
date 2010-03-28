package ac.soton.eventb.ruleBase.theory.ui.actions;

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

import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

/**
 * @author maamria
 *
 */
public class DeployWizardPageOne extends WizardPage {
	
	private Combo projectCombo;
	private Button btnKeepNameCbox;
	private Text newNameText;
	private Combo theoryCombo;
	
	private String projectName;
	private boolean useDiffName;
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

		projectCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectCombo.setLayoutData(gd);
		projectCombo.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				String value = projectCombo.getItem(projectCombo.getSelectionIndex());
				if(projectName != null && projectName.equals(value) ){
					return;
				}
				// invalid selection... may not be necessary
				if(value==null & value.length()<= 0){
					return;
				}
				projectName = value ;
				String names[]= TheoryUIUtils.getNonEmptySCTheoryNames(projectName);
				// BUG FIX DONE FIXME when there are no non-empty theories.
				if(names != null)
					theoryCombo.setItems(names);
				initialise();
				dialogChanged();
				
			}
		});
		projectCombo.setItems(TheoryUIUtils.getProjectsNames());
		projectCombo.setFocus();
		new Label(container, SWT.NULL);
		
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
				// invalid selection... may not be necessary
				if(value==null & value.length()<= 0){
					return;
				}
				theoryName = value;
				btnKeepNameCbox.setSelection(false);
				btnKeepNameCbox.setEnabled(true);
				newNameText.setText("");
				newNameText.setEnabled(false);
				useDiffName = false;
				dialogChanged();
				
			}
		});
		theoryCombo.setBounds(100, 39, 307, 21);
		theoryCombo.setToolTipText("Select a non-empty theory to deploy");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnKeepNameCbox = new Button(container, SWT.CHECK);
		btnKeepNameCbox.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				useDiffName = btnKeepNameCbox.getSelection();
				if(useDiffName){
					newNameText.setEnabled(true);
					dialogChanged();
				}
				else {
					newNameText.setEnabled(false);
					dialogChanged();
				}
			}
		});
		btnKeepNameCbox.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		btnKeepNameCbox.setText("Use a different name for the deployed theory.");
		btnKeepNameCbox.setGrayed(true);
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		newNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		newNameText.setLayoutData(gd);
		newNameText.addModifyListener(new ModifyListener() {
			
				public void modifyText(ModifyEvent e) {
					dialogChanged();
				}
			}
		);
		initialise();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		if(projectName == null || projectName.equals("")){
			updateStatus("Project must be specified");
			return;
		}
		if(theoryName == null){
			updateStatus("Theory must be specified");
			return;
		}
		if(useDiffName){
			if (newNameText.getText().length() == 0) {
				updateStatus("New theory name must be specified");
				return;
			}
		}
		updateStatus(null);
	}
	
	private void initialise(){
		theoryName = null;
		useDiffName = false;
		newNameText.setEnabled(false);
		newNameText.setText("");
		btnKeepNameCbox.setEnabled(false);
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
		return projectCombo.getText();
	}
	
	public String getNewName(){
		if(useDiffName){
			return newNameText.getText();
		}
		return null;
	}
	
	public String getTheoryName() {
		return theoryName;
	}
	
	public boolean isUseDiffName() {
		return useDiffName;
	}
}
