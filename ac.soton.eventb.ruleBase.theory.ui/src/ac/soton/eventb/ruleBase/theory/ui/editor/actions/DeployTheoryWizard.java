package ac.soton.eventb.ruleBase.theory.ui.editor.actions;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

/**
 * Theory deployment wizard.
 * <p>
 * @author maamria
 *
 */
public class DeployTheoryWizard extends Wizard {

	private DeployWizardPage page;
	private String projectName;
	private Shell shell;
	
	private String theoryName;
	
	public DeployTheoryWizard(String tName, String pName, Shell shell) {
		setWindowTitle("Deploy Theory");
		this.theoryName=tName;
		this.projectName =pName;
		this.shell = shell;
	}

	@Override
	public void addPages() {
		page = new DeployWizardPage(theoryName, projectName);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return TheoryUIUtils.deployTheory(theoryName, page.getDiffTheoryName(), projectName, shell);
	}

}
