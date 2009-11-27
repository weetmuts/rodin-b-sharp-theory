package ac.soton.eventb.ruleBase.theory.ui.wizard;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IEventBProject;
import org.eventb.internal.ui.RodinProjectSelectionDialog;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.ui.plugin.TheoryUIPlugIn;
import ac.soton.eventb.ruleBase.theory.ui.prefs.facade.PrefsRepresentative;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

/**
 * <p>The "New" wizard page allows setting the container for the new file
 * as well as the file name. The page will only accept file name without
 * the extension OR with the extension that matches the expected one(but).
 * </p>
 * <p>As well as offering the necessary capability to specify theory name 
 * and its enclosing project, it also provides the capability to specify
 *  the categories to assign to the newly created theory file.
 *  </p>
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryWizardPage extends WizardPage {
	private List list;
	// Some text areas.
	private Text projectText;
	private ISelection selection;
	private Text theoryText;

	public TheoryWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("New Event-B Theory");
		setDescription("This wizard creates a new theory file with .but extension.");
		this.selection = selection;
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
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Project:");

		projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
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
		label.setText("&Theory Name:");

		theoryText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		theoryText.setLayoutData(gd);
		theoryText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblcategory = new Label(container, SWT.SHADOW_IN);
		{
			GridData gridData = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
			gridData.heightHint = 21;
			lblcategory.setLayoutData(gridData);
		}
		lblcategory.setText("&Categories: ");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		list = new List(container, SWT.BORDER | SWT.MULTI);
		{
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gridData.heightHint = 130;
			gridData.widthHint = 244;
			list.setLayoutData(gridData);
			populateCategories(list);
		}
		new Label(container, SWT.NONE);
	}

	public String[] getCategories (){
		return list.getSelection();
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
		return theoryText.getText();
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		final String projectName = getProjectName();
		String theoryName = getTheoryName();
		if (projectName.length() == 0) {
			updateStatus("Project must be specified");
			return;
		}

		IRodinProject rodinProject = RodinCore.getRodinDB()
				.getRodinProject(projectName);
		if (!rodinProject.exists()) {
			updateStatus("Project name must be valid");
			return;
		}

		if (rodinProject.isReadOnly()) {
			updateStatus("Project must be writable");
			return;
		}

		if (theoryName.length() == 0) {
			updateStatus("Theory name must be specified");
			return;
		}
		final IEventBProject evbProject = (IEventBProject) rodinProject
				.getAdapter(IEventBProject.class);
		final IRodinFile machineFile = evbProject.getMachineFile(theoryName);
		final IRodinFile contextFile = evbProject.getContextFile(theoryName);
		final IRodinFile theoryFile = getTheoryFile(rodinProject, theoryName);
		if (machineFile == null || contextFile == null) {
			updateStatus("Theory name must be valid");
			return;
		}
		if (machineFile.exists()) {
			updateStatus("There is already a machine with this name");
			return;
		}
		if (contextFile.exists()) {
			updateStatus("There is already a context with this name");
			return;
		}
		if (theoryFile.exists()) {
			updateStatus("There is already a theory with this name");
			return;
		}
		updateStatus(null);
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
			}
		}
	}

	private IRodinFile getTheoryFile(IRodinProject project, String theoryName) {
		return project
				.getRodinFile(TheoryUIUtils.getTheoryFileName(theoryName));
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		IRodinProject project = null;
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object element = ssel.getFirstElement();
			IRodinElement curr;
			if (element instanceof IRodinElement) {
				curr = (IRodinElement) element;
			} else
				curr = null;
			while (!(curr instanceof IRodinProject || curr == null)) {
				curr = curr.getParent();
			}
			project = (IRodinProject) curr;
		}
		if (project != null) {
			projectText.setText(project.getElementName());
			theoryText.setFocus();
			theoryText.selectAll();
		} else {
			projectText.setFocus();
		}
		theoryText.setText("changeMe");
	}

	private void populateCategories(List list){
		list.setItems(PrefsRepresentative.getCategories());
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}