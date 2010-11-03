package org.eventb.theory.rbp.internal.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.DB_TCFacade;
import org.rodinp.core.RodinDBException;

/**
 * An implementation of of a deployment manager.
 * 
 * @author maamria
 *
 */
public class DeployedTheoriesManager implements IDeployedTheoriesManager{
	
	protected FormulaFactory factory;

	public DeployedTheoriesManager(FormulaFactory factory){
		this.factory = factory;
	}
	
	
	public List<IDeployedTheoryFile> getTheories(){
		List<IDeployedTheoryFile> list = new ArrayList<IDeployedTheoryFile>();
		IDeployedTheoryRoot[] roots;
		try {
			roots = DB_TCFacade.getDeploymentProject(null).getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
			for (IDeployedTheoryRoot root : roots){
				if(root.exists())
					list.add(new DeployedTheoryFile(root.getComponentName(), factory));
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return Collections.unmodifiableList(list);
	}
	
	
	protected IDeployedTheoryFile getTheory(String name, FormulaFactory factory) {
		return new DeployedTheoryFile(name, factory);
	}
}
