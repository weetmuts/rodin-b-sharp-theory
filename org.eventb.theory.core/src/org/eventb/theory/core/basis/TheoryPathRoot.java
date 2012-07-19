/**
 * 
 */
package org.eventb.theory.core.basis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.basis.EventBRoot;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryLanguageRoot;
import org.eventb.theory.core.ITheoryLanguageRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author Renato Silva
 *
 */
public class TheoryPathRoot extends EventBRoot implements ITheoryLanguageRoot {

	public TheoryPathRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.InternalElement#getElementType()
	 */
	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IAvailableTheoryProject getAvailableTheoryProject(String name) {
		return getInternalElement(IAvailableTheoryProject.ELEMENT_TYPE, name);
	}
	
	@Override
	public IAvailableTheory[] getAvailableTheories() throws RodinDBException { 
		List<IAvailableTheory> theories = new ArrayList<IAvailableTheory>();
		
		for(IAvailableTheoryProject availableTheoryProject: getAvailableTheoryProjects()){
			theories.addAll(Arrays.asList(availableTheoryProject.getTheories()));
		}
		
		return theories.toArray(new IAvailableTheory[theories.size()]);
	}

	@Override
	public IAvailableTheoryProject[] getAvailableTheoryProjects()
			throws RodinDBException {
		return getChildrenOfType(IAvailableTheoryProject.ELEMENT_TYPE);
	}
	
	@Override
	public IRodinFile getSCTheoryPathFile(String bareName) {
		String fileName = DatabaseUtilitiesTheoryPath.getSCTheoryPathFullName(bareName);
		IRodinFile file = getRodinProject().getRodinFile(fileName);
		return file;
	}

	@Override
	public ISCTheoryLanguageRoot getSCTheoryPathRoot() {
		return getSCTheoryPathRoot(getElementName());
	}
	
	public ISCTheoryLanguageRoot getSCTheoryPathRoot(String bareName) {
		ISCTheoryLanguageRoot root = (ISCTheoryLanguageRoot) getSCTheoryPathFile(bareName).getRoot();
		return root;
	}

	@Override
	public Map<IRodinProject, Set<IDeployedTheoryRoot>> getTheories()
			throws RodinDBException {
		IAvailableTheoryProject[] availableTheoryProjects = getAvailableTheoryProjects();
		Map<IRodinProject, Set<IDeployedTheoryRoot>> theories = new HashMap<IRodinProject, Set<IDeployedTheoryRoot>>(availableTheoryProjects.length);
		for(IAvailableTheoryProject proj: availableTheoryProjects){
			Set<IDeployedTheoryRoot> deployedTheories = new HashSet<IDeployedTheoryRoot>();
			Collections.addAll(deployedTheories, proj.getDeployedTheories());
			theories.put(proj.getTheoryProject(), deployedTheories);
		}
		return theories;
	}

}
