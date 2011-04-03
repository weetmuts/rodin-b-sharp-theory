/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.internal.deploy;

import org.eclipse.swt.widgets.Shell;
import org.eventb.theory.core.ITheoryRoot;

/**
 * @author maamria
 *
 */
public class SimpleDeployWizard extends AbstractDeployWizard{
	
	ITheoryRoot root;
	
	public SimpleDeployWizard(Shell shell, ITheoryRoot root) {
		super(shell);
		this.root = root;
	}

	@Override
	protected AbstractDeployWizardPageOne getPageOne() {
		// TODO Auto-generated method stub
		return new SimpleDeployWizardPageOne(root.getComponentName(), root.getRodinProject().getElementName());
	}

}
