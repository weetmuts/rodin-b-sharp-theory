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
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.rbp.internal.rulebase.IDeployedInferenceRule;
import org.eventb.theory.rbp.internal.rulebase.IDeployedRewriteRule;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * 
 * @author maamria
 * 
 */
public class BaseManager implements IElementChangedListener {

	private static BaseManager manager;

	private Map<IRodinProject, ProjectBaseEntry> projectEntries;
	private ProjectBaseEntry mathExtensionsProjectEntry;

	private BaseManager() {
		projectEntries = new LinkedHashMap<IRodinProject, ProjectBaseEntry>();
		RodinCore.addElementChangedListener(this);
	}

	public List<IDeployedRewriteRule> getExpressionRewriteRules(
			boolean automatic, Class<? extends Expression> clazz,
			IPOContext context, FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		List<IDeployedRewriteRule> rules = new ArrayList<IDeployedRewriteRule>();
		rules.addAll(mathExtensionsProjectEntry.getExpressionRewriteRules(
				automatic, clazz, parentRoot, factory));
		if (!context.inMathExtensions()) {
			rules.addAll(projectEntries.get(parentRoot.getRodinProject())
					.getExpressionRewriteRules(automatic, clazz, parentRoot,
							factory));
		}
		return rules;
	}

	public List<IDeployedRewriteRule> getPredicateRewriteRules(
			boolean automatic, Class<? extends Predicate> clazz,
			IPOContext context, FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		List<IDeployedRewriteRule> rules = new ArrayList<IDeployedRewriteRule>();
		rules.addAll(mathExtensionsProjectEntry.getPredicateRewriteRules(
				automatic, clazz, parentRoot, factory));
		if (!context.inMathExtensions()) {
			rules.addAll(projectEntries.get(parentRoot.getRodinProject())
					.getPredicateRewriteRules(automatic, clazz, parentRoot,
							factory));
		}
		return rules;
	}

	public IDeployedRewriteRule getExpressionRewriteRule(String ruleName,
			String theoryName, Class<? extends Expression> clazz,
			IPOContext context, FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		IDeployedRewriteRule rule = mathExtensionsProjectEntry
				.getExpressionRewriteRule(ruleName, theoryName, clazz,
						parentRoot, factory);
		if (rule == null) {
			return projectEntries.get(parentRoot.getRodinProject())
					.getExpressionRewriteRule(ruleName, theoryName, clazz,
							parentRoot, factory);
		}
		return rule;
	}

	public IDeployedRewriteRule getPredicateRewriteRule(String ruleName,
			String theoryName, Class<? extends Predicate> clazz,
			IPOContext context, FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		IDeployedRewriteRule rule = mathExtensionsProjectEntry
				.getPredicateRewriteRule(ruleName, theoryName, clazz,
						parentRoot, factory);
		if (rule == null) {
			return projectEntries.get(parentRoot.getRodinProject())
					.getPredicateRewriteRule(ruleName, theoryName, clazz,
							parentRoot, factory);
		}
		return rule;
	}

	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic,
			ReasoningType type, IPOContext context, FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());
		List<IDeployedInferenceRule> rules = new ArrayList<IDeployedInferenceRule>();
		rules.addAll(mathExtensionsProjectEntry.getInferenceRules(automatic,
				type, parentRoot, factory));
		if (!context.inMathExtensions()) {
			rules.addAll(projectEntries.get(parentRoot.getRodinProject())
					.getInferenceRules(automatic, type, parentRoot, factory));
		}
		return rules;
	}

	public IDeployedInferenceRule getInferenceRule(String theoryName,
			String ruleName, IPOContext context, FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		check(parentRoot.getRodinProject());

		IDeployedInferenceRule rule = mathExtensionsProjectEntry
				.getInferenceRule(theoryName, ruleName, parentRoot, factory);
		if (rule == null) {
			return projectEntries
					.get(parentRoot.getRodinProject())
					.getInferenceRule(theoryName, ruleName, parentRoot, factory);
		}
		return rule;
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		// TODO Auto-generated method stub
		try {
			processDelta(event.getDelta());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected void processDelta(IRodinElementDelta delta) throws CoreException {
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
					mathExtensionsProjectEntry
							.setHasChanged((IDeployedTheoryRoot) file.getRoot());
				} else {
					projectEntries.get(rodinProject).setHasChanged(
							(IDeployedTheoryRoot) file.getRoot());
				}
			}
			if (file.getRoot() instanceof ISCTheoryRoot) {
				if (DatabaseUtilities.isMathExtensionsProject(rodinProject)) {
					mathExtensionsProjectEntry
							.setHasChanged((ISCTheoryRoot) file.getRoot());
				} else {
					projectEntries.get(rodinProject).setHasChanged(
							(ISCTheoryRoot) file.getRoot());
				}
			}
		}
	}

	protected void check(IRodinProject project) {
		if (mathExtensionsProjectEntry == null) {
			mathExtensionsProjectEntry = new ProjectBaseEntry(
					DatabaseUtilities.getDeploymentProject(null));
		}
		if (!DatabaseUtilities.isMathExtensionsProject(project)) {
			if (!projectEntries.containsKey(project)) {
				ProjectBaseEntry entry = new ProjectBaseEntry(project);
				projectEntries.put(project, entry);
			}
		}
	}

	public static BaseManager getDefault() {
		if (manager == null)
			manager = new BaseManager();
		return manager;
	}
}
