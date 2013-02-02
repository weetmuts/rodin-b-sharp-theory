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
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
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
 * 
 * @author maamria
 * 
 */
public class BaseManager implements IElementChangedListener {

	private static BaseManager manager;

	private Map<IRodinProject, IProjectBaseEntry> projectEntries;

	private BaseManager() {
		projectEntries = new LinkedHashMap<IRodinProject, IProjectBaseEntry>();
		RodinCore.addElementChangedListener(this);
	}

	public Map<IRodinProject, Map<IExtensionRulesSource, List<IDeployedTheorem>>> getTheorems(IPOContext context,
			FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		Map<IRodinProject, Map<IExtensionRulesSource, List<IDeployedTheorem>>> map = new LinkedHashMap<IRodinProject, Map<IExtensionRulesSource, List<IDeployedTheorem>>>();
		// add theory path stuff
		try {
			ISCTheoryPathRoot[] paths = rodinProject.getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
			if (paths.length == 1) {
				for (ISCAvailableTheoryProject availProj : paths[0].getSCAvailableTheoryProjects()) {
					check(availProj.getSCAvailableTheoryProject());
					IProjectBaseEntry projBaseEntry = projectEntries.get(availProj.getSCAvailableTheoryProject());
					if (projBaseEntry != null) {
						Map<IExtensionRulesSource, List<IDeployedTheorem>> thms = new LinkedHashMap<IExtensionRulesSource, List<IDeployedTheorem>>();
						for (ISCAvailableTheory availThy : availProj.getSCAvailableTheories()) {
							IDeployedTheoryRoot scDeployedTheoryRoot = availThy.getSCDeployedTheoryRoot();
							ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry = projBaseEntry
									.getTheoryBaseEntry(scDeployedTheoryRoot);
							thms.put(scDeployedTheoryRoot, theoryBaseEntry.getDeployedTheorems(factory));
						}
						map.put(availProj.getSCAvailableTheoryProject(), thms);
					}
				}

			}
		} catch (CoreException e) {
			ProverUtilities.log(e, "Error while processing theory path for project " + rodinProject);
		}
		map.put(rodinProject, projectEntries.get(rodinProject).getTheorems(context, factory));
		return map;
	}

	/**
	 * Returns the list of definitional rewrite rules for the given formula
	 * class under the given context.
	 * 
	 * @param clazz
	 *            the formula runtime class
	 * @param context
	 *            the proof obligation context
	 * @return the list of rewrite rules
	 */
	public List<IDeployedRewriteRule> getDefinitionalRules(Class<?> clazz, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		List<IDeployedRewriteRule> rules = new ArrayList<IDeployedRewriteRule>();
		// add theory path stuff
		try {
			ISCTheoryPathRoot[] paths = rodinProject.getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
			if (paths.length == 1) {
				for (ISCAvailableTheoryProject availProj : paths[0].getSCAvailableTheoryProjects()) {
					check(availProj.getSCAvailableTheoryProject());
					IProjectBaseEntry projBaseEntry = projectEntries.get(availProj.getSCAvailableTheoryProject());
					if (projBaseEntry != null) {
						for (ISCAvailableTheory availThy : availProj.getSCAvailableTheories()) {
							IDeployedTheoryRoot scDeployedTheoryRoot = availThy.getSCDeployedTheoryRoot();
							ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry = projBaseEntry
									.getTheoryBaseEntry(scDeployedTheoryRoot);
							rules.addAll(theoryBaseEntry.getDefinitionalRules(context.getFormulaFactory()));
						}
					}
				}

			}
		} catch (CoreException e) {
			ProverUtilities.log(e, "Error while processing theory path for project " + rodinProject);
		}
		rules.addAll(projectEntries.get(rodinProject).getDefinitionalRules(clazz, parentRoot, context.getFormulaFactory()));

		return rules;
	}

	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic, Class<?> clazz, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		List<IDeployedRewriteRule> rules = new ArrayList<IDeployedRewriteRule>();
		// add theory path stuff
		try {
			ISCTheoryPathRoot[] paths = rodinProject.getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
			if (paths.length == 1) {
				for (ISCAvailableTheoryProject availProj : paths[0].getSCAvailableTheoryProjects()) {
					check(availProj.getSCAvailableTheoryProject());
					IProjectBaseEntry projBaseEntry = projectEntries.get(availProj.getSCAvailableTheoryProject());
					if (projBaseEntry != null) {
						for (ISCAvailableTheory availThy : availProj.getSCAvailableTheories()) {
							IDeployedTheoryRoot scDeployedTheoryRoot = availThy.getSCDeployedTheoryRoot();
							ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry = projBaseEntry
									.getTheoryBaseEntry(scDeployedTheoryRoot);
							rules.addAll(theoryBaseEntry.getRewriteRules(automatic, clazz, context.getFormulaFactory()));
						}
					}
				}

			}
		} catch (CoreException e) {
			ProverUtilities.log(e, "Error while processing theory path for project " + rodinProject);
		}
		rules.addAll(projectEntries.get(rodinProject).getRewriteRules(automatic, clazz, parentRoot,
				context.getFormulaFactory()));

		return rules;
	}

	/**
	 * Returns the deployed expression rewrite rule with the given parameters.
	 * 
	 * @param ruleName
	 *            the name of the rule
	 * @param theoryName
	 *            the name of the parent theory
	 * @param clazz
	 *            the runtime class of the formula in its lhs
	 * @param context
	 *            the obligation context
	 * @return the deployed rule, or <code>null</code> if not found
	 */
	public IDeployedRewriteRule getRewriteRule(String projectName, String ruleName, String theoryName, Class<?> clazz, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
		check(rodinProject);
		return projectEntries.get(rodinProject).getRewriteRule(theoryName, ruleName, clazz, parentRoot,
				context.getFormulaFactory());

	}

	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic, ReasoningType type, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		List<IDeployedInferenceRule> rules = new ArrayList<IDeployedInferenceRule>();
		// add theory path stuff
		try {
			ISCTheoryPathRoot[] paths = rodinProject.getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
			if (paths.length == 1) {
				for (ISCAvailableTheoryProject availProj : paths[0].getSCAvailableTheoryProjects()) {
					check(availProj.getSCAvailableTheoryProject());
					IProjectBaseEntry projBaseEntry = projectEntries.get(availProj.getSCAvailableTheoryProject());
					if (projBaseEntry != null) {
						for (ISCAvailableTheory availThy : availProj.getSCAvailableTheories()) {
							IDeployedTheoryRoot scDeployedTheoryRoot = availThy.getSCDeployedTheoryRoot();
							ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry = projBaseEntry
									.getTheoryBaseEntry(scDeployedTheoryRoot);
							rules.addAll(theoryBaseEntry.getInferenceRules(automatic, type, context.getFormulaFactory()));
						}
					}
				}

			}
		} catch (CoreException e) {
			ProverUtilities.log(e, "Error while processing theory path for project " + rodinProject);
		}

		rules.addAll(projectEntries.get(rodinProject).getInferenceRules(automatic, type, parentRoot,
				context.getFormulaFactory()));

		return rules;
	}

	/**
	 * Returns the deployed inference rule that matches the given details.
	 * 
	 * @param theoryName
	 *            the theory name
	 * @param ruleName
	 *            the rule name
	 * @param context
	 *            the obligation context
	 * @return the deployed inference rule, or <code>null</code> if not found
	 */
	public IDeployedInferenceRule getInferenceRule(String projectName, String theoryName, String ruleName, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
		check(rodinProject);
		return projectEntries.get(rodinProject).getInferenceRule(theoryName, ruleName, parentRoot,
				context.getFormulaFactory());

	}

	/**
	 * Returns the singleton instance.
	 * 
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
				projectEntries.get(rodinProject).setHasChanged((IDeployedTheoryRoot) file.getRoot());
			}
			if (file.getRoot() instanceof ISCTheoryRoot) {
				projectEntries.get(rodinProject).setHasChanged((ISCTheoryRoot) file.getRoot());

			}
		}
	}

	private void check(IRodinProject project) {
		if (!projectEntries.containsKey(project)) {
			ProjectBaseEntry entry = new ProjectBaseEntry(project);
			projectEntries.put(project, entry);
		}
	}
}
