/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.deploy;

import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 *
 */
public class DeployWizardPageDefault extends AbstractDeployWizardPage {
	
	protected DeployWizardPageDefault(String projectName, String theoryName) {
		super();
		this.projectName = projectName;
		this.theoryName = theoryName;
		setTitle("Deploy theory ");
		setDescription("Deploy theory file.");
	}

	
	public void setVisible(boolean visible){
		if (visible) {
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

