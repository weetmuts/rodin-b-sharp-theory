/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.deploy;

import org.eclipse.swt.widgets.Shell;
import org.eventb.theory.core.ITheoryRoot;

/**
 * @author maamria
 *
 */
public class SimpleDeployWizard extends AbstractDeployWizard{

	DeployWizardPageDefault page;
	ITheoryRoot root;
	
	public SimpleDeployWizard(Shell shell, ITheoryRoot root) {
		super(shell);
		this.root = root;
	}

	@Override
	public void addPages() {
		setWindowTitle("Deploy Theory");
		page = new DeployWizardPageDefault(root.getRodinProject().getElementName(), root.getComponentName());
		addPage(page);

	}
	
	@Override
	protected String getProjectName() {
		// TODO Auto-generated method stub
		return page.getProjectName();
	}

	@Override
	protected String getTheoryName() {
		// TODO Auto-generated method stub
		return page.getTheoryName();
	}

	@Override
	protected boolean forceDeployment() {
		// TODO Auto-generated method stub
		return page.forceDeployment();
	}

	@Override
	protected boolean deployDependencies() {
		// TODO Auto-generated method stub
		return page.deployDependencies();
	}

}
