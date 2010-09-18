package org.eventb.theory.internal.ui.attr;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ImportAttributeManipulation extends AbstractAttributeManipulation {

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// nothing

	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		return asImportTheory(element).hasImportedTheory();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		return asImportTheory(element).getImportedTheoryName();
	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		// TODO Auto-generated method stub
		asImportTheory(element).setImportedTheory(value, monitor);

	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		asImportTheory(element).removeAttribute(TARGET_ATTRIBUTE, monitor);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		IImportTheory importTheory = asImportTheory(element);
		// get the parent
		ITheoryRoot parent = importTheory.getAncestor(ITheoryRoot.ELEMENT_TYPE);
		Set<String> set = new LinkedHashSet<String>();
		try {
		// get all theories
		set.addAll(TheoryCoreFacade.getNames(TheoryCoreFacade.getTheoryRoots(parent.getRodinProject())));
		set.removeAll(getUsedTheories(parent));
		// remove parent name
		set.remove(parent.getElementName());
		// remove cycle
		removeCycle(set, parent);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return set.toArray(new String[set.size()]);
	}
	
	
	protected void removeCycle(Set<String> set, ITheoryRoot parent) 
	throws CoreException{
		// TODO Auto-generated method stub
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()){
			String next = iterator.next();
			ITheoryRoot theoryRoot = TheoryCoreFacade.getTheory(next, parent.getRodinProject());
			if(TheoryCoreFacade.doesTheoryImportTheory(theoryRoot, parent)){
				iterator.remove();
			}
		}
		
	}

	protected IImportTheory asImportTheory(IRodinElement element){
		assert element instanceof IImportTheory;
		return (IImportTheory) element;
	}
	
	protected Set<String> getUsedTheories(ITheoryRoot parent){
		HashSet<String> used = new HashSet<String>();
		try{
			IImportTheory[] usedUp = parent.getImportTheories();
			for(IImportTheory thy : usedUp){
				if(thy.hasImportedTheory())
					used.add(thy.getImportedTheoryName());
			}
		}
		
		catch (CoreException ex){
			TheoryUIUtils.log(ex, "error getting used theories by "+ parent.getElementName());
			return new HashSet<String>();
		}
		return used;
		
	}
	
}
