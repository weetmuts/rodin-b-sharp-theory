package ac.soton.eventb.ruleBase.theory.ui.editor.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.rodinp.core.IRodinProject;

import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.util.Messages;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

public class TheoryDeployer {

	private String theoryName;
	private IRodinProject project;
	private Shell shell;
	
	public TheoryDeployer(String theoryName, IRodinProject project, Shell shell){
		this.theoryName= theoryName;
		this.project = project;
		this.shell =shell;
	}
	
	public void deploy(){
		ISCTheoryRoot root = (ISCTheoryRoot) TheoryUIUtils.getTheoryInProject(theoryName,
				ISCTheoryRoot.ELEMENT_TYPE, project.getElementName()).getRoot();
		if(TheoryUIUtils.isTheoryEmpty(root)){
			MessageDialog.openInformation(shell, "Deploy Theory", 
					Messages.bind(Messages.theoryUIUtils_deployNothing,
							theoryName));
		}
		else {
			if(MessageDialog.openConfirm(shell, "Deploy Theory", 
					Messages.bind(Messages.theoryUIUtils_deployConfirm,
					theoryName)))
				TheoryUIUtils.deployTheory(theoryName, 
						theoryName, project.getElementName(), 
						shell);
		}
	}
}
