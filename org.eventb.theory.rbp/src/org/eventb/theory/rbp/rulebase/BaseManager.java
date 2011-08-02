/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;
import org.eventb.theory.rbp.rulebase.basis.ProjectBaseEntry;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * An implementation of the rule base manager.
 * @author maamria
 * 
 */
public class BaseManager implements IElementChangedListener {

	private static BaseManager manager;

	private Map<IRodinProject, IProjectBaseEntry> projectEntries;
	private ProjectBaseEntry mathExtensionsProjectEntry;

	private BaseManager() {
		projectEntries = new LinkedHashMap<IRodinProject, IProjectBaseEntry>();
		RodinCore.addElementChangedListener(this);
	}
	
	public Map<IRodinProject,Map<IExtensionRulesSource, List<IDeployedTheorem>>> getTheorems(IPOContext context, FormulaFactory factory){
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		Map<IRodinProject,Map<IExtensionRulesSource, List<IDeployedTheorem>>> map = new
			LinkedHashMap<IRodinProject, Map<IExtensionRulesSource,List<IDeployedTheorem>>>();
		map.put(DatabaseUtilities.getDeploymentProject(new NullProgressMonitor()), 
				mathExtensionsProjectEntry.getTheorems(context, factory));
		if (!context.inMathExtensions()){
			map.put(rodinProject, projectEntries.get(rodinProject).getTheorems(context, factory));
		}
		return map;
	}
	
	/**
	 * Returns the list of definitional rewrite rules for the given formula class under the given context.
	 * @param clazz the formula runtime class
	 * @param context the proof obligation context
	 * @return the list of rewrite rules
	 */
	public List<IDeployedRewriteRule> getDefinitionalRules(Class<?> clazz, IPOContext context){
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		List<IDeployedRewriteRule> rules = new ArrayList<IDeployedRewriteRule>();
		rules.addAll(mathExtensionsProjectEntry.getDefinitionalRules(clazz, parentRoot, context.getFormulaFactory()));
		if (!context.inMathExtensions()) {
			rules.addAll(projectEntries.get(parentRoot.getRodinProject()).getDefinitionalRules(clazz, parentRoot, context.getFormulaFactory()));
		}
		return rules;
	}

	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic, Class<?> clazz, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		List<IDeployedRewriteRule> rules = new ArrayList<IDeployedRewriteRule>();
		rules.addAll(mathExtensionsProjectEntry.getRewriteRules(automatic, clazz, parentRoot, context.getFormulaFactory()));
		if (!context.inMathExtensions()) {
			rules.addAll(projectEntries.get(parentRoot.getRodinProject()).getRewriteRules(automatic, clazz, parentRoot, context.getFormulaFactory()));
		}
		return rules;
	}

	/**
	 * Returns the deployed expression rewrite rule with the given parameters.
	 * @param ruleName the name of the rule
	 * @param theoryName the name of the parent theory
	 * @param clazz the runtime class of the formula in its lhs
	 * @param context the obligation context
	 * @return the deployed rule, or <code>null</code> if not found
	 */
	public IDeployedRewriteRule getRewriteRule(String ruleName, String theoryName, Class<?> clazz, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		IDeployedRewriteRule rule = mathExtensionsProjectEntry.getRewriteRule(theoryName, ruleName, clazz, parentRoot, context.getFormulaFactory());
		if (rule == null) {
			return projectEntries.get(parentRoot.getRodinProject()).getRewriteRule(theoryName, ruleName, clazz, parentRoot, context.getFormulaFactory());
		}
		return rule;
	}

	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic, ReasoningType type, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		List<IDeployedInferenceRule> rules = new ArrayList<IDeployedInferenceRule>();
		rules.addAll(mathExtensionsProjectEntry.getInferenceRules(automatic, type, parentRoot, context.getFormulaFactory()));
		if (!context.inMathExtensions()) {
			rules.addAll(projectEntries.get(parentRoot.getRodinProject()).getInferenceRules(automatic, type, parentRoot, context.getFormulaFactory()));
		}
		return rules;
	}

	/**
	 * Returns the deployed inference rule that matches the given details.
	 * @param theoryName the theory name
	 * @param ruleName the rule name
	 * @param context the obligation context
	 * @return the deployed inference rule, or <code>null</code> if not found
	 */
	public IDeployedInferenceRule getInferenceRule(String theoryName, String ruleName, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());

		IDeployedInferenceRule rule = mathExtensionsProjectEntry.getInferenceRule(theoryName, ruleName, parentRoot, context.getFormulaFactory());
		if (rule == null) {
			return projectEntries.get(parentRoot.getRodinProject()).getInferenceRule(theoryName, ruleName, parentRoot, context.getFormulaFactory());
		}
		return rule;
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the singleton instance
	 */
	public static BaseManager getDefault() {
		if (manager == null)
			manager = new BaseManager();
		return manager;
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		try {
			processDelta(event.getDelta());
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while processing change in db affecting the rule base manager");
		}
	}
	
	private void processDelta(IRodinElementDelta delta) throws CoreException {
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();
		if (element instanceof IRodinDB) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
		if (element instanceof IRodinProject) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}

		}
		if (element instanceof IRodinFile) {
			IRodinFile file = (IRodinFile) element;
			IRodinProject rodinProject = file.getRodinProject();
			check(rodinProject);
			if (file.getRoot() instanceof IDeployedTheoryRoot) {
				if (DatabaseUtilities.isMathExtensionsProject(rodinProject)) {
					mathExtensionsProjectEntry.setHasChanged((IDeployedTheoryRoot) file.getRoot());
				} else {
					projectEntries.get(rodinProject).setHasChanged((IDeployedTheoryRoot) file.getRoot());
				}
			}
			if (file.getRoot() instanceof ISCTheoryRoot) {
				if (DatabaseUtilities.isMathExtensionsProject(rodinProject)) {
					mathExtensionsProjectEntry.setHasChanged((ISCTheoryRoot) file.getRoot());
				} else {
					projectEntries.get(rodinProject).setHasChanged((ISCTheoryRoot) file.getRoot());
				}
			}
		}
	}

	private void check(IRodinProject project) {
		if (mathExtensionsProjectEntry == null) {
			mathExtensionsProjectEntry = new ProjectBaseEntry(DatabaseUtilities.getDeploymentProject(new NullProgressMonitor()));
		}
		if (!DatabaseUtilities.isMathExtensionsProject(project)) {
			if (!projectEntries.containsKey(project)) {
				ProjectBaseEntry entry = new ProjectBaseEntry(project);
				projectEntries.put(project, entry);
			}
		}
	}
}
