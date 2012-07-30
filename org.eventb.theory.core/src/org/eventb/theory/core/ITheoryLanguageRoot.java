/**
 * 
 */
package org.eventb.theory.core;

import java.util.Map;
import java.util.Set;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * TheoryLanguageRoot (or TheoryPath) contains the theories that are available for each rodin project. 
 * Children are of type {@link IAvailableTheory}. 
 * 
 * 
 * @author Renato Silva
 *
 */
public interface ITheoryLanguageRoot extends IEventBRoot, ICommentedElement,
		IConfigurationElement {
	
	IInternalElementType<ITheoryLanguageRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".theoryLanguageRoot");
	
//	/**
//	 * Returns the available theory with the given name
//	 * @param name the name 
//	 * @return the import theory
//	 */
//	public IAvailableTheory getAvailableTheory(String name);
	
	public Map<IRodinProject,Set<IDeployedTheoryRoot>> getTheories() throws RodinDBException;
	
	/**
	 * Returns all available theories children of this element.
	 * @return all available theories
	 * @throws RodinDBException
	 */
	public IAvailableTheory[] getAvailableTheories() throws RodinDBException;
	
	/**
	 * Returns the available theory project with the given name
	 * @param name the name 
	 * @return the import theory
	 */
	public IAvailableTheoryProject getAvailableTheoryProject(String name); 
	
	/**
	 * Returns all available theory projects of this element.
	 * @return all available theory projects
	 * @throws RodinDBException
	 */
	public IAvailableTheoryProject[] getAvailableTheoryProjects() throws RodinDBException;
	
	/**
	 * <p>Returns the SC theorypath root corresponding to this element.</p>
	 * <p>This is handle-only method.</p>
	 * @return the SC theoryPath root
	 */
	public ISCTheoryLanguageRoot getSCTheoryPathRoot();
	
	/**
	 * Returns the SC theorypath root corresponding to bareName
	 * @param bareName
	 * @return the SC theorypath root
	 */
	public ISCTheoryLanguageRoot getSCTheoryPathRoot(String bareName);

	/**
	 * Retuens the SC theorypath file corresponding to bareName
	 * 
	 * @param bareName
	 * @return the SC theorypath file
	 */
	IRodinFile getSCTheoryPathFile(String bareName);
	
	



}
