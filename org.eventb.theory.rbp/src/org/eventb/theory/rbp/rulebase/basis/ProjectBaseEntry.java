/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;
import static org.eventb.theory.core.DatabaseUtilities.originatedFromTheory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.extensions.dependencies.SCTheoriesGraph;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.IProjectBaseEntry;
import org.eventb.theory.rbp.rulebase.ITheoryBaseEntry;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.IRodinProject;

/**
 * 
 * @author maamria
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
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<ISCTheoryRoot> reqRoots = getRequiredSCRoots(root);
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
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getRewriteRules(automatic, clazz, factory));
			}
		}
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
		if (originatedFromTheory(root.getRodinFile(), project)){
			List<ISCTheoryRoot> reqRoots = getRequiredSCRoots(root);
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
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getInferenceRules(automatic, type, factory));
			}
		}
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
	public Map<IExtensionRulesSource, List<IDeployedTheorem>> getTheorems(IPOContext poContext, FormulaFactory factory) {
		IEventBRoot root = poContext.getParentRoot();
		String componentName = root.getComponentName();
		Map<IExtensionRulesSource, List<IDeployedTheorem>> map = new LinkedHashMap<IExtensionRulesSource, List<IDeployedTheorem>>();
		if (originatedFromTheory(root.getRodinFile(), project)){
			int order = poContext.getOrder();
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(componentName, project);
			List<ISCTheoryRoot> requiredRoots = getRequiredSCRoots(scRoot);
			for (ISCTheoryRoot scTheoryRoot : requiredRoots){
				if (!scRoots.containsKey(scTheoryRoot)){
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scTheoryRoot);
					scRoots.put(scTheoryRoot, entry);
				} 
				if(order != -1 && root.getComponentName().equals(scTheoryRoot.getComponentName())){
					map.put(scTheoryRoot, scRoots.get(scTheoryRoot).getDeployedTheorems(order, factory));
				}
				else {
					map.put(scTheoryRoot, scRoots.get(scTheoryRoot).getDeployedTheorems(factory));
				}
			}
		}
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedRoot : deployedTheoryRoots){
				if (!deployedRoots.containsKey(deployedRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedRoot);
					deployedRoots.put(deployedRoot, entry);
				}
				map.put(deployedRoot, deployedRoots.get(deployedRoot).getDeployedTheorems(factory));
			}
		}
		return map;
	}

	@Override
	public List<IDeployedRewriteRule> getDefinitionalRules(IEventBRoot root, FormulaFactory factory) {
		List<IDeployedRewriteRule> toReturn = new ArrayList<IDeployedRewriteRule>();
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<ISCTheoryRoot> reqRoots = getRequiredSCRoots(root);
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
			List<ISCTheoryRoot> reqRoots = getRequiredSCRoots(root);
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

	public boolean managingMathExtensionsProject() {
		return DatabaseUtilities.isMathExtensionsProject(project);
	}
	
	private IDeployedTheoryRoot[] getDeployedRoots (){
		try {
			return DatabaseUtilities.getDeployedTheories(project);
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while getting deployed roots of "+ project.getElementName());
		}
		return new IDeployedTheoryRoot[0];
	}

	private List<ISCTheoryRoot> getRequiredSCRoots(IEventBRoot root) {
		if (!originatedFromTheory(root.getRodinFile())) {
			return new ArrayList<ISCTheoryRoot>();
		}
		ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(root.getComponentName(), project);
		if (scRoot.exists()) {
			SCTheoriesGraph graph = new SCTheoriesGraph();
			graph.setElements(getSCTheoryRoots());
			// TODO fix this may introduce cyclic dep
			// Fixed bug: added the SC theory file as a required root
			Set<ISCTheoryRoot> upperSet = graph.getUpperSet(scRoot);
			upperSet.add(scRoot);
			return new ArrayList<ISCTheoryRoot>(upperSet);
		}
		return new ArrayList<ISCTheoryRoot>();
	}

	private ISCTheoryRoot[] getSCTheoryRoots() {
		try {
			return DatabaseUtilities.getSCTheoryRoots(project, DatabaseUtilities.getNonTempSCTheoriesFilter());
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while getting sc theory roots of "+ project.getElementName());
		}
		return new ISCTheoryRoot[0];
	}
}
