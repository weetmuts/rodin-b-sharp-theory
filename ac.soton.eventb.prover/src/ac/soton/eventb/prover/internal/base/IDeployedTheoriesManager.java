package ac.soton.eventb.prover.internal.base;

import java.util.List;


/**
 * <p> Common protocol for a deployed theories manager.</p>
 * <p> A deployment manager works with the specific directory: theories directory.</p>
 * <p> A deployment manager can, upon request, get the theories deployed in the deployment directory.</p>
 * 
 * @author maamria
 */
public interface IDeployedTheoriesManager {
	
	
	/**
	 * <p> This method gets a list of available theories that have been validated.</p>
	 * <p> If a theory is included in the returned list, it is guaranteed to be validated.</p>
	 * @return a list of available theories
	 */
	public List<IDTheoryFile> getTheories();
	
	/**
	 * <p> Returns a single theory that corresponds to the given name in the deployment directory.</p>
	 * <p> Returns <code>null</code> if there was an IO problem or theory file could not be validated. </p>
	 * <p> In most cases, clients should call <code>IDeployedTheoriesManager.getTheories()</code>.</p>
	 * 
	 * @param name of the theory
	 * @return a theory or <code>null</code> if problem occurred
	 */
	public IDTheoryFile getTheory(String name);

}
