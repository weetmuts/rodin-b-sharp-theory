package org.eventb.theory.core.basis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.TheoryHierarchyHelper;

/**
 * A decorator for deployed theories.
 * 
 * <p> Objects of this class provide additional functionality on top of a 
 * deployed theory object.
 * @author im06r
 *
 */
public class DeployedTheoryDecorator {
	
	private IDeployedTheoryRoot deployedRoot;

	public DeployedTheoryDecorator(IDeployedTheoryRoot deployedRoot) {
		this.deployedRoot = deployedRoot;
	}
	
	/**
	 * Returns the syntactic contributions of the deployed theory.
	 * 
	 * @return the syntactic contributions
	 * @throws CoreException
	 */
	public Set<String> getContributions() throws CoreException {
		Set<String> set = new TreeSet<String>();
		ISCDatatypeDefinition[] datatypeDefinitions = deployedRoot.getSCDatatypeDefinitions();
		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			set.add(definition.getIdentifierString());
			ISCDatatypeConstructor[] constructors = definition.getConstructors();
			for (ISCDatatypeConstructor constructor : constructors) {
				set.add(constructor.getIdentifierString());
				ISCConstructorArgument arguments[] = constructor.getConstructorArguments();
				for (ISCConstructorArgument argument : arguments) {
					set.add(argument.getIdentifierString());
				}
			}
		}
		ISCNewOperatorDefinition[] operatorDefinitions = deployedRoot.getSCNewOperatorDefinitions();
		for (ISCNewOperatorDefinition definition : operatorDefinitions) {
			set.add(definition.getLabel());
		}
		return set;
	}
	
	/**
	 * Returns the set of statically checked theories that import (directly or indirectly) this theory.
	 * @return the set of dependent theories
	 * @throws CoreException
	 */
	public Set<IDeployedTheoryRoot> getDependentTheories() throws CoreException {
		return TheoryHierarchyHelper.importingClosure(deployedRoot);
	}

	/**
	 * Returns the set of deployed imported (directly or indirectly) theories by this theory.
	 * @return the set of imported theories
	 * @throws CoreException
	 */
	public Set<IDeployedTheoryRoot> getRequiredTheories() throws CoreException {
		return TheoryHierarchyHelper.importingClosure(deployedRoot);
	}

	/**
	 * Returns the map of syntactic contributions of a hierarchy of theories depending on <code>theoryIsLeaf</code>:
	 * <ul>
	 * 	<li> if <code>theoryIsList</code> is <code>true</code>, the map constitutes of the contributions of 
	 * theories that the theory depends on;
	 * 	<li> if <code>theoryIsList</code> is <code>false</code>, the map constitutes of the contributions of 
	 * theories that depends on this theory.
	 * </ul>
	 * @param theoryIsLeaf whether to consider the theory as a leaf or root of the hierarchy
	 * @return map of syntactic contributions
	 * @throws CoreException
	 */
	public Map<IDeployedTheoryRoot, Set<String>> getHierarchyContributions(boolean theoryIsLeaf)
			throws CoreException {
		Map<IDeployedTheoryRoot, Set<String>> contribs = new LinkedHashMap<IDeployedTheoryRoot, Set<String>>();
		if (theoryIsLeaf){
			Set<IDeployedTheoryRoot> requiredTheories = getRequiredTheories();
			for(IDeployedTheoryRoot req : requiredTheories){
				contribs.put(req, new DeployedTheoryDecorator(req).getContributions());
			}
		}
		else {
			Set<IDeployedTheoryRoot> dependentTheories = getDependentTheories();
			for(IDeployedTheoryRoot req : dependentTheories){
				contribs.put(req, new DeployedTheoryDecorator(req).getContributions());
			}
		}
		return contribs;
	}

}
