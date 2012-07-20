/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import java.util.HashMap;

import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.ITheoryLanguageRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;

import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author RenatoSilva
 *
 */
public class TheoryPathModelProject implements IModelElement {
	
	private IRodinProject internalProject;
	private HashMap<ITheoryLanguageRoot, ModelTheoryPath> theories = new HashMap<ITheoryLanguageRoot, ModelTheoryPath>();
	
	//indicates whether to projects needs to be processed freshly (process Machines etc.)
	public boolean needsProcessing =  true;

	public TheoryPathModelProject(IRodinProject project) {
		internalProject = project;
	}
	
	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getModelParent()
	 */
	@Override
	public IModelElement getModelParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getInternalElement()
	 */
	@Override
	public IRodinElement getInternalElement() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getParent(boolean)
	 */
	@Override
	public Object getParent(boolean complex) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getChildren(org.rodinp.core.IInternalElementType, boolean)
	 */
	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ModelTheoryPath getTheoryPath(ITheoryLanguageRoot theory) {
		return theories.get(theory);
	}
	
	public void processTheoryPath(ITheoryLanguageRoot theory) {
		ModelTheoryPath thy;
		if (!theories.containsKey(theory)) {
			thy = new ModelTheoryPath(theory);
			theories.put(theory, thy);
		} else {
			thy = theories.get(theory);
		}
		thy.poNeedsProcessing = true;
		thy.psNeedsProcessing = true;
		thy.processChildren();
	}
	
	
	public void removeTheoryPath(ITheoryLanguageRoot theoryPathRoot) {
		ModelTheoryPath theory = theories.get(theoryPathRoot);
		if (theory != null) {
			theories.remove(theoryPathRoot);
		}
	}

	public IModelElement getModelElement(IRodinElement element) {
		
		if (element instanceof ITheoryLanguageRoot) {
			return getTheoryPath((ITheoryLanguageRoot) element);
		}
		IEventBRoot parent= element.getAncestor(ITheoryRoot.ELEMENT_TYPE);
		ModelTheoryPath theory = theories.get(parent);
		if (theory != null) {
			return theory.getModelElement(element);
		}
		return null;
	}

}
