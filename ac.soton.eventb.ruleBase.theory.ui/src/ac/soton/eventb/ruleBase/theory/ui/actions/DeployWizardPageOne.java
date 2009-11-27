package ac.soton.eventb.ruleBase.theory.ui.actions;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.editor.actions.DeployWizardPage;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

/**
 * @see DeployWizardPage
 * @author maamria
 *
 */
public class DeployWizardPageOne extends WizardPage {
	
	private Button btnKeepThe;
	private Label lblnewName;
	private Text newNameText;
	private Combo projCombo;
	private String projectName;
	
	private Combo theoryCombo;
	private String theoryName;
	private boolean useDiffName;
	
	/**
	 * Create the wizard.
	 */
	public DeployWizardPageOne() {
		super("dWizardPage1");
		setTitle("Select theory");
		setDescription("Select theory to deploy");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		{
			Label lblTheoryName = new Label(container, SWT.NONE);
			lblTheoryName.setBounds(10, 42, 84, 13);
			lblTheoryName.setText("&Theory Name: ");
		}
		{
			theoryCombo = new Combo(container, SWT.READ_ONLY);
			theoryCombo.addSelectionListener(new SelectionAdapter() {
				@Override
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
					btnKeepThe.setSelection(false);
					btnKeepThe.setEnabled(true);
					lblnewName.setEnabled(false);
					newNameText.setText("");
					newNameText.setEnabled(false);
					useDiffName = false;
					dialogChanged();
					
				}
			});
			theoryCombo.setBounds(100, 39, 307, 21);
		}
		{
			Label lblproject = new Label(container, SWT.NONE);
			lblproject.setBounds(10, 10, 84, 13);
			lblproject.setText("&Project Name: ");
		}
		{
			projCombo = new Combo(container, SWT.READ_ONLY);
			projCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String value = projCombo.getItem(projCombo.getSelectionIndex());
					if(projectName != null && projectName.equals(value) ){
						return;
					}
					// invalid selection... may not be necessary
					if(value==null & value.length()<= 0){
						return;
					}
					projectName = value ;
					String names[]= TheoryUIUtils.getRootNames(projectName, ISCTheoryRoot.ELEMENT_TYPE);
					theoryCombo.setItems(names);
					initialise();
					dialogChanged();
					
				}
			});
			projCombo.setBounds(100, 7, 307, 21);
			projCombo.setItems(TheoryUIUtils.getProjectsNames());
			projCombo.setFocus();
		}
		{
			btnKeepThe = new Button(container, SWT.CHECK);
			btnKeepThe.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					useDiffName = btnKeepThe.getSelection();
					if(useDiffName){
						newNameText.setEnabled(true);
						lblnewName.setEnabled(true);
						dialogChanged();
					}
					else {
						newNameText.setEnabled(false);
						lblnewName.setEnabled(false);
						dialogChanged();
					}
				}
			});
			btnKeepThe.setGrayed(true);
			btnKeepThe.setBounds(10, 113, 283, 16);
			btnKeepThe.setText("Use a different name for the deployed theory.");
		}
		{
			lblnewName = new Label(container, SWT.NONE);
			lblnewName.setBounds(49, 140, 45, 13);
			lblnewName.setText("&Name: ");
		}
		{
			newNameText = new Text(container, SWT.BORDER);
			newNameText.setBounds(100, 137, 307, 19);
			newNameText.addModifyListener(new ModifyListener(){
				@Override
				public void modifyText(ModifyEvent e) {
					dialogChanged();
				}
				
			});
		}
		initialise();
		dialogChanged();
	}
	
	public String getNewName(){
		if(useDiffName){
			return newNameText.getText();
		}
		return null;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public String getTheoryName() {
		return theoryName;
	}
	
	public boolean isUseDiffName() {
		return useDiffName;
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
		btnKeepThe.setEnabled(false);
		lblnewName.setEnabled(false);
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
		
	}
}
