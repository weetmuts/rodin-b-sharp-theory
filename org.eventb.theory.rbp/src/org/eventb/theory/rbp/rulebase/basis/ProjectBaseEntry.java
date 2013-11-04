/*******************************************************************************
 * Copyright (c) 2011, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;
import static org.eventb.theory.core.DatabaseUtilities.originatedFromTheory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.extensions.WorkspaceExtensionsManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.IProjectBaseEntry;
import org.eventb.theory.rbp.rulebase.ITheoryBaseEntry;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.IRodinProject;

/**
 * 
 * @author maamria, asiehsalehi
 * @since 1.0
 * 
 */
public class ProjectBaseEntry implements IProjectBaseEntry{

	private IRodinProject project;
	private Map<IDeployedTheoryRoot, ITheoryBaseEntry<IDeployedTheoryRoot>> deployedRoots;
	private Map<ISCTheoryRoot, ITheoryBaseEntry<ISCTheoryRoot>> scRoots;

	public ProjectBaseEntry(IRodinProject project) {
		this.project = project;
		this.deployedRoots = new LinkedHashMap<IDeployedTheoryRoot, ITheoryBaseEntry<IDeployedTheoryRoot>>();
		this.scRoots = new LinkedHashMap<ISCTheoryRoot, ITheoryBaseEntry<ISCTheoryRoot>>();
	}
	
	public void setHasChanged(ISCTheoryRoot scRoot){
		if (!scRoots.containsKey(scRoot)) {
			TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
					scRoot);
			scRoots.put(scRoot, entry);
		}
		scRoots.get(scRoot).setHasChanged(true);
	}
	
	public void setHasChanged(IDeployedTheoryRoot depRoot){
		if(!deployedRoots.containsKey(depRoot)){
			TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
			deployedRoots.put(depRoot, entry);
		}
		deployedRoots.get(depRoot).setHasChanged(true);
	}

	@Override
	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic, Class<?> clazz, IEventBRoot root, FormulaFactory factory) {
		List<IDeployedRewriteRule> toReturn = new ArrayList<IDeployedRewriteRule>();
		// case when POContext is a theory
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			String componentName = root.getComponentName();
			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
			reqRoots.add(SCRoot.getDeployedTheoryRoot());
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				// this is to avoid circularity of rules (rule used to prove itself)
				if (!root.getComponentName().equals(scRoot.getComponentName())){
					toReturn.addAll(scRoots.get(scRoot).getRewriteRules(automatic, clazz, factory));
				}
				else {
					// supply definitional rules for interactive prover only
					if (!automatic)
						toReturn.addAll(scRoots.get(scRoot).getDefinitionalRules(clazz, factory));
				}
			}
		}
		// case when POContext is a context/machine; so no need to add the local theories; local theories need to be imported in the theorypath
		/*else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getRewriteRules(automatic, clazz, factory));
			}
		}*/
		return toReturn;
	}

	@Override
	public IDeployedRewriteRule getRewriteRule(String theoryName, String ruleName, Class<?> clazz, IEventBRoot root, FormulaFactory factory) {
		if (originatedFromTheory(root.getRodinFile(), project)){
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(theoryName, project);
			if (!scRoots.containsKey(scRoot)){
				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scRoot);
				scRoots.put(scRoot, entry);
			}
			return scRoots.get(scRoot).getRewriteRule(ruleName, clazz, factory);
		}
		else {
			IDeployedTheoryRoot depRoot = DatabaseUtilities.getDeployedTheory(theoryName, project);
			if (!deployedRoots.containsKey(depRoot)){
				TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
				deployedRoots.put(depRoot, entry);
			}
			return deployedRoots.get(depRoot).getRewriteRule(ruleName, clazz, factory);
		}
	}
	
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic, ReasoningType type, 
			IEventBRoot root,FormulaFactory factory){
		List<IDeployedInferenceRule> toReturn = new ArrayList<IDeployedInferenceRule>();
		// case when POContext is a theory
		if (originatedFromTheory(root.getRodinFile(), project)){
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			String componentName = root.getComponentName();
			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
			reqRoots.add(SCRoot.getDeployedTheoryRoot());
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				if (!root.getComponentName().equals(scRoot.getComponentName())){
					toReturn.addAll(scRoots.get(scRoot).getInferenceRules(automatic, type, factory));
				}
			}
		}
		// case when POContext is a context/machine; so no need to add the local theories; local theories need to be imported in the theorypath
		/*else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getInferenceRules(automatic, type, factory));
			}
		}*/
		return toReturn;
	}
	
	public IDeployedInferenceRule getInferenceRule(String theoryName, String ruleName, 
			IEventBRoot root, FormulaFactory factory){
		if (originatedFromTheory(root.getRodinFile(), project)){
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(theoryName, project);
			if (!scRoots.containsKey(scRoot)){
				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scRoot);
				scRoots.put(scRoot, entry);
			}
			return scRoots.get(scRoot).getInferenceRule(ruleName, factory);
		}
		else {
			IDeployedTheoryRoot depRoot = DatabaseUtilities.getDeployedTheory(theoryName, project);
			if (!deployedRoots.containsKey(depRoot)){
				TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
				deployedRoots.put(depRoot, entry);
			}
			return deployedRoots.get(depRoot).getInferenceRule(ruleName, factory);
		}
	}
	

	@Override
	public Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> getTheorems(IPOContext poContext, FormulaFactory factory) {
		IEventBRoot root = poContext.getParentRoot();
		String componentName = root.getComponentName();
		Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> map = new LinkedHashMap<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>>();
		if (originatedFromTheory(root.getRodinFile(), project)){
			int order = poContext.getOrder();
			boolean axm = poContext.isAxiom();
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(componentName, project);
			List<IDeployedTheoryRoot> requiredRoots = getRequiredTheories(scRoot);
			//add the current theory
			requiredRoots.add(scRoot.getDeployedTheoryRoot());
			for (ISCTheoryRoot scTheoryRoot : requiredRoots){
				if (!scRoots.containsKey(scTheoryRoot)){
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scTheoryRoot);
					scRoots.put(scTheoryRoot, entry);
				} 
				if(order != -1 && root.getComponentName().equals(scTheoryRoot.getComponentName())){
					final IRodinProject thyProject = scTheoryRoot.getRodinProject();	
					if (!map.containsKey(thyProject)) {
						map.put(thyProject,
								new LinkedHashMap<IExtensionRulesSource, List<ISCTheorem>>());
					}
					final Map<IExtensionRulesSource, List<ISCTheorem>> thms = map.get(thyProject);
					thms.put(scTheoryRoot, scRoots.get(scTheoryRoot).getSCTheorems(axm, order, factory));
				}
				else {
					final IRodinProject thyProject = scTheoryRoot.getRodinProject();
					if (!map.containsKey(thyProject)) {
						map.put(thyProject,
								new LinkedHashMap<IExtensionRulesSource, List<ISCTheorem>>());
					}
					final Map<IExtensionRulesSource, List<ISCTheorem>> thms = map.get(thyProject);
					thms.put(scTheoryRoot, scRoots.get(scTheoryRoot).getSCTheorems(factory));
				}
			}
		}
		// case when POContext is a context/machine; so no need to add the local theories; local theories need to be imported in the theorypath
		/*else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedRoot : deployedTheoryRoots){
				if (!deployedRoots.containsKey(deployedRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedRoot);
					deployedRoots.put(deployedRoot, entry);
				}
				map.put(deployedRoot, deployedRoots.get(deployedRoot).getDeployedTheorems(factory));
			}
		}*/
		return map;
	}

	@Override
	public List<IDeployedRewriteRule> getDefinitionalRules(IEventBRoot root, FormulaFactory factory) {
		List<IDeployedRewriteRule> toReturn = new ArrayList<IDeployedRewriteRule>();
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			String componentName = root.getComponentName();
			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
			reqRoots.add(SCRoot.getDeployedTheoryRoot());
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				toReturn.addAll(scRoots.get(scRoot).getDefinitionalRules(factory));
				
			}
		}
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getDefinitionalRules(factory));
			}
		}
		return toReturn;
	}

	@Override
	public List<IDeployedRewriteRule> getDefinitionalRules(Class<?> clazz, IEventBRoot root, FormulaFactory factory) {
		List<IDeployedRewriteRule> toReturn = new ArrayList<IDeployedRewriteRule>();
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			String componentName = root.getComponentName();
			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
			reqRoots.add(SCRoot.getDeployedTheoryRoot());
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				toReturn.addAll(scRoots.get(scRoot).getDefinitionalRules(clazz, factory));
				
			}
		}
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getDefinitionalRules(clazz, factory));
			}
		}
		return toReturn;
	}
	
	private IDeployedTheoryRoot[] getDeployedRoots (){
		try {
			return DatabaseUtilities.getDeployedTheories(project);
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while getting deployed roots of "+ project.getElementName());
		}
		return new IDeployedTheoryRoot[0];
	}

	private List<IDeployedTheoryRoot> getRequiredTheories(IEventBRoot root) {
		if (!originatedFromTheory(root.getRodinFile())) {
			return Collections.emptyList();
		}
		final ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(root.getComponentName(), project);
		if (!scRoot.exists()) {
			return Collections.emptyList();
		}
		try {
			return WorkspaceExtensionsManager.getInstance().getTheoryImportClosure(scRoot);
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while getting import closure of "
					+ root.getPath());
			return Collections.emptyList();
		}
	}

	@Override
	public ITheoryBaseEntry<ISCTheoryRoot> getTheoryBaseEntry(ISCTheoryRoot root) {
		if (!root.getRodinProject().equals(project)){
			return null;
		}
		ITheoryBaseEntry<ISCTheoryRoot> entry = scRoots.get(root);
		if (entry == null){
			entry = new TheoryBaseEntry<ISCTheoryRoot>(root);
		}
		scRoots.put(root, entry);
		return entry;
	}

	@Override
	public ITheoryBaseEntry<IDeployedTheoryRoot> getTheoryBaseEntry(IDeployedTheoryRoot root) {
		if (!root.getRodinProject().equals(project)){
			return null;
		}
		ITheoryBaseEntry<IDeployedTheoryRoot> entry = deployedRoots.get(root);
		if (entry == null){
			entry = new TheoryBaseEntry<IDeployedTheoryRoot>(root);
		}
		deployedRoots.put(root, entry);
		return entry;
	}
}
