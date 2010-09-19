/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 *
 */
public class ProjectModel {

	IRodinProject project;
	Map<String, Set<IFormulaExtension>> deployedExtensions;
	Map<String, Set<IFormulaExtension>> scTheoriesExtensions;
	
	public ProjectModel(IRodinProject project){
		this.project = project;
		this.deployedExtensions = new LinkedHashMap<String, Set<IFormulaExtension>>();
		this.scTheoriesExtensions = new LinkedHashMap<String, Set<IFormulaExtension>>();
	}
	
	protected boolean rootIsNotGenerated(IEventBRoot root) {
		return (root instanceof IContextRoot 
				|| root instanceof IMachineRoot 
				|| root instanceof ITheoryRoot);
	}
	
}
