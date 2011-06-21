/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;
import static org.eventb.theory.core.DatabaseUtilities.originatedFromTheory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.maths.extensions.dependencies.SCTheoriesGraph;
import org.eventb.theory.rbp.internal.rulebase.IDeployedInferenceRule;
import org.eventb.theory.rbp.internal.rulebase.IDeployedRewriteRule;
import org.rodinp.core.IRodinProject;

/**
 * 
 * @author maamria
 * 
 */
public class ProjectBaseEntry {

	private IRodinProject project;
	private Map<IDeployedTheoryRoot, TheoryBaseEntry<IDeployedTheoryRoot>> deployedRoots;
	private Map<ISCTheoryRoot, TheoryBaseEntry<ISCTheoryRoot>> scRoots;

	public ProjectBaseEntry(IRodinProject project) {
		this.project = project;
		this.deployedRoots = new LinkedHashMap<IDeployedTheoryRoot, TheoryBaseEntry<IDeployedTheoryRoot>>();
		this.scRoots = new LinkedHashMap<ISCTheoryRoot, TheoryBaseEntry<ISCTheoryRoot>>();
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

	public List<IDeployedRewriteRule> getExpressionRewriteRules(
			boolean automatic, Class<? extends Expression> clazz,
			IEventBRoot root, FormulaFactory factory) {
		List<IDeployedRewriteRule> toReturn = new ArrayList<IDeployedRewriteRule>();
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<ISCTheoryRoot> reqRoots = getRequiredSCRoots(root);
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				toReturn.addAll(scRoots.get(scRoot).getExpressionRewriteRules(
						automatic, clazz, factory));
			}
		}
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getExpressionRewriteRules(automatic, clazz, factory));
			}
		}
		return toReturn;
	}
	
	public List<IDeployedRewriteRule> getPredicateRewriteRules(boolean automatic, 
			Class<? extends Predicate> clazz, IEventBRoot root,FormulaFactory factory){
		List<IDeployedRewriteRule> toReturn = new ArrayList<IDeployedRewriteRule>();
		if (originatedFromTheory(root.getRodinFile(), project)) {
			List<ISCTheoryRoot> reqRoots = getRequiredSCRoots(root);
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				toReturn.addAll(scRoots.get(scRoot).getPredicateRewriteRules(
						automatic, clazz, factory));
			}
		}
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getPredicateRewriteRules(automatic, clazz, factory));
			}
		}
		return toReturn;
	}
	
	public IDeployedRewriteRule getExpressionRewriteRule(String ruleName, String theoryName,
			Class<? extends Expression> clazz, IEventBRoot root, FormulaFactory factory){
		if (originatedFromTheory(root.getRodinFile(), project)){
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(theoryName, project);
			if (!scRoots.containsKey(scRoot)){
				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scRoot);
				scRoots.put(scRoot, entry);
			}
			return scRoots.get(scRoot).getExpressionRewriteRule(ruleName, clazz, factory);
		}
		else {
			IDeployedTheoryRoot depRoot = DatabaseUtilities.getDeployedTheory(theoryName, project);
			if (!deployedRoots.containsKey(depRoot)){
				TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
				deployedRoots.put(depRoot, entry);
			}
			return deployedRoots.get(depRoot).getExpressionRewriteRule(ruleName, clazz, factory);
		}
	}
	
	public IDeployedRewriteRule getPredicateRewriteRule(String ruleName, String theoryName,
			Class<? extends Predicate> clazz, IEventBRoot root, FormulaFactory factory){
		if (originatedFromTheory(root.getRodinFile(), project)){
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(theoryName, project);
			if (!scRoots.containsKey(scRoot)){
				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scRoot);
				scRoots.put(scRoot, entry);
			}
			return scRoots.get(scRoot).getPredicateRewriteRule(ruleName, clazz, factory);
		}
		else {
			IDeployedTheoryRoot depRoot = DatabaseUtilities.getDeployedTheory(theoryName, project);
			if (!deployedRoots.containsKey(depRoot)){
				TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
				deployedRoots.put(depRoot, entry);
			}
			return deployedRoots.get(depRoot).getPredicateRewriteRule(ruleName, clazz, factory);
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
				toReturn.addAll(scRoots.get(scRoot).getInferenceRules(automatic, type, factory));
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

	public boolean managingMathExtensionsProject() {
		return DatabaseUtilities.isMathExtensionsProject(project);
	}
	
	private IDeployedTheoryRoot[] getDeployedRoots (){
		try {
			return DatabaseUtilities.getDeployedTheories(project);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new IDeployedTheoryRoot[0];
	}

	private List<ISCTheoryRoot> getRequiredSCRoots(IEventBRoot root) {
		if (!originatedFromTheory(root.getRodinFile())) {
			return new ArrayList<ISCTheoryRoot>();
		}
		ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(
				root.getComponentName(), project);
		if (scRoot.exists()) {
			SCTheoriesGraph graph = new SCTheoriesGraph();
			graph.setElements(getSCTheoryRoots());
			// Fixed bug: added the SC theory file as a required root
			Set<ISCTheoryRoot> upperSet = graph.getUpperSet(scRoot);
			upperSet.add(scRoot);
			return new ArrayList<ISCTheoryRoot>(upperSet);
		}
		return new ArrayList<ISCTheoryRoot>();
	}

	private ISCTheoryRoot[] getSCTheoryRoots() {
		try {
			return DatabaseUtilities.getSCTheoryRoots(project,
					DatabaseUtilities.getNonTempSCTheoriesFilter());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ISCTheoryRoot[0];
	}

}
