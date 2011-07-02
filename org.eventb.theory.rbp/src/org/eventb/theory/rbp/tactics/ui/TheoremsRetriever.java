/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * A utility class the help retrieve theorems suitable for a given context.
 * @author maamria
 *
 */
public class TheoremsRetriever {

	private IPOContext poContext;
	private BaseManager baseManager;
	private FormulaFactory factory;
	
	private Map<IRodinProject, Map<IExtensionRulesSource, List<IDeployedTheorem>>> theorems;

	public TheoremsRetriever(IPOContext poContext){
		this.poContext = poContext;
		this.factory = poContext.getFormulaFactory();
		baseManager = BaseManager.getDefault();
		getTheorems();
	}
	
	/**
	 * Populates the relevant theorems.
	 */
	protected void getTheorems(){
		this.theorems =  baseManager.getTheorems(poContext, factory);
	}
	
	/**
	 * Returns all rodin projects from which theorems can be retrieved for the context.
	 * @return suitable rodin projects
	 */
	public String[] getRodinProjects(){
		Set<IRodinProject> keySet = theorems.keySet();
		Set<String> set = new LinkedHashSet<String>();
		for (IRodinProject p : keySet) {
			set.add(p.getElementName());
		}
		return set.toArray(new String[set.size()]);
	}
	
	/**
	 * Returns the theories in the given project from which theorems can be retrieved for the context.
	 * @param project the project name
	 * @return suitable theories
	 */
	public String[] getTheories(String project){
		IRodinProject rodinProject =  RodinCore.getRodinDB().getRodinProject(project);
		Map<IExtensionRulesSource, List<IDeployedTheorem>> theories = theorems.get(rodinProject);
		if (theories != null){
			Set<IExtensionRulesSource> keySet = theories.keySet();
			return DatabaseUtilities.getNames(keySet).toArray(new String[keySet.size()]);
		}
		return new String[0];
	}
	
	/**
	 * Returns all deployed theorem specified by the given modifiers.
	 * @param project the project name
	 * @param theory the theory name
	 * @return list of deployed theorems
	 */
	public List<IDeployedTheorem> getDeployedTheorems(String project, String theory){
		IRodinProject rodinProject =  RodinCore.getRodinDB().getRodinProject(project);
		Map<IExtensionRulesSource, List<IDeployedTheorem>> theories = theorems.get(rodinProject);
		for (IExtensionRulesSource source : theories.keySet()){
			if (source.getElementName().equals(theory)){
				return theories.get(source);
			}
		}
		return new ArrayList<IDeployedTheorem>();
	}
	
	/**
	 * Returns the particular theorem defined by the given modifiers.
	 * @param project the project name
	 * @param theory the theory name
	 * @param theorem the theorem name
	 * @return the deployed theorem, or <code>null</code> if not found
	 */
	public IDeployedTheorem getDeployedTheorem(String project, String theory, String theorem){
		for (IDeployedTheorem deployedTheorem : getDeployedTheorems(project, theory)){
			if (deployedTheorem.getName().equals(theorem)){
				return deployedTheorem;
			}
		}
		return null;
	}
	
	/**
	 * Returns the formula factory suitable for the proof obligation context.
	 * @return the formula factory
	 */
	public FormulaFactory getFactory(){
		return factory;
	}
}
