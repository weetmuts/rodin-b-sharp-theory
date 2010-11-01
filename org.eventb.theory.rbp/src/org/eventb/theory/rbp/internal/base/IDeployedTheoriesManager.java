package org.eventb.theory.rbp.internal.base;

import java.util.List;



/**
 * <p> Common protocol for a deployed theories manager.</p>
 * <p> A deployment manager works with the specific directory: theories project.</p>
 * <p> A deployment manager can, upon request, get the theories deployed in the deployment project.</p>
 * 
 * @author maamria
 */
public interface IDeployedTheoriesManager {
	
	
	/**
	 * <p> This method gets a list of available theories that have been validated.</p>
	 * <p> If a theory is included in the returned list, it is guaranteed to be validated.</p>
	 * @return a list of available theories
	 */
	public List<IDeployedTheoryFile> getTheories();
	
}
