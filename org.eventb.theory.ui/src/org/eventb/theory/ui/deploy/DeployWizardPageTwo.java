package org.eventb.theory.ui.deploy;

import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 *
 */
public class DeployWizardPageTwo extends AbstractDeployWizardPage {

	
	protected DeployWizardPageTwo() {
		super();
		setTitle("Deploy theory ");
		setDescription("Deploy theory file.");
	}
	
	public void setVisible(boolean visible){
		if (visible) {
			projectName = ((DeployWizardPageOne) getPreviousPage())
					.getProjectName();
			theoryName = ((DeployWizardPageOne) getPreviousPage())
					.getTheoryName();
			theoryLabel.setText(projectName+"\\"+theoryName);
			IRodinFile file = TheoryUIUtils.getSCTheoryInProject(theoryName, projectName);
			populatePOs(file);
			dTitle = "Discharged POs (" + dPOs.size() + "/" + totalNumPOs + ")";
			rTitle = "Reviewed POs (" + rPOs.size() + "/" + totalNumPOs + ")";
			uTitle = "Pending POs (" + uPOs.size() + "/" + totalNumPOs + ")";
			trtmDischargedPos.setText(dTitle);
			createTheTree(trtmDischargedPos, "d");
			trtmReviewedPos.setText(rTitle);
			createTheTree(trtmReviewedPos, "r");
			trtmUndischargedPos.setText(uTitle);
			createTheTree(trtmUndischargedPos, "u");
		}
		else{
			trtmDischargedPos.setItemCount(0);
			trtmReviewedPos.setItemCount(0);
			trtmUndischargedPos.setItemCount(0);
		}
		super.setVisible(visible);
		
	}

}
