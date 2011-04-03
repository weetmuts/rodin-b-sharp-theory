/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.internal.deploy;

import org.eclipse.swt.widgets.Composite;

/**
 * @author maamria
 *
 */
public class SimpleDeployWizardPageOne extends AbstractDeployWizardPageOne{
	
	protected SimpleDeployWizardPageOne(String theoryName, String projectName) {
		super();
		this.theoryName = theoryName;
		this.projectName = projectName;
		setDescription("Deploy theory file.");
	}

	@Override
	protected void customise(Composite container) {
		// nothing to do
	}
	
	
}
