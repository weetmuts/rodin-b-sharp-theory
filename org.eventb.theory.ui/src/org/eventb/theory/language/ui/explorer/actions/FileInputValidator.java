/**
 * 
 */
package org.eventb.theory.language.ui.explorer.actions;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class FileInputValidator implements IInputValidator {
	
	private IRodinProject prj;

	public FileInputValidator(IRodinProject prj) {
		this.prj = prj;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
	 */
	@Override
	public String isValid(String newText) {
		try {
			for(IRodinElement rodinElement :prj.getChildren()){
				if(rodinElement instanceof IRodinFile && (((IRodinFile)rodinElement).getBareName().equalsIgnoreCase(newText))){
					return Messages.wizardRenameFileExists;
				}
			}
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "Exception occurred while retrieving children of project "+ prj.getElementName());
		}
		
		return null;
	}

}
