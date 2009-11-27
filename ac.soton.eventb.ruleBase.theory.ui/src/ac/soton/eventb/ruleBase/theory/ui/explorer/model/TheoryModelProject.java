/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.explorer.model;

import java.util.HashMap;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryModelProject extends ModelProject {

	private IRodinProject internalProject;
	private HashMap<ITheoryRoot, ModelTheory> theories = new HashMap<ITheoryRoot, ModelTheory>();

	public TheoryModelProject(IRodinProject project) {
		super(project);
		internalProject = project;
	}

	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		
		try {
			if (type == ITheoryRoot.ELEMENT_TYPE) {
				return internalProject.getRootElementsOfType(type);
			}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting machines or contexts of " +internalProject);
			}
		
		if (ExplorerUtils.DEBUG) {
			System.out.println("Did not find children of type: "+type +"for project " +internalProject);
		}
		return new Object[0];
	}

	/**
	 * @return The number of manually discharged Proof Obligations of this project
	 */
	public int getManuallyDischargedPOcount() {
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getManuallyDischargedPOcount();
		}
		return result;
	}

	@Override
	public IModelElement getModelElement(IRodinElement element) {

		if (element instanceof ITheoryRoot) {
			return getTheory((ITheoryRoot) element);
		}
		return null;

	}

	/**
	 * @return the total number of Proof Obligations of this project
	 */
	public int getPOcount(){
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getPOcount();
		}
		return result;
	}

	public ModelProofObligation getProofObligation(IPSStatus status){
		IEventBRoot root = (IEventBRoot) status.getRoot();
		if (root instanceof IPSRoot) {
			
			ModelTheory theory = theories.get(root.getRoot());
			if (theory != null) {
				return theory.getProofObligation(status);
			}
		}
		return null;
	}
	
	/**
	 * @return The number of manually discharged Proof Obligations of this project
	 */
	public int getReviewedPOcount() {
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getReviewedPOcount();
		}
		return result;
		
	}
	
	public HashMap<ITheoryRoot, ModelTheory> getTheories() {
		return theories;
	}

	public ModelTheory getTheory(ITheoryRoot theory) {
		return theories.get(theory);
	}
	
	/**
	 * @return The number of undischarged Proof Obligations of this project
	 */
	public int getUndischargedPOcount() {
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getUndischargedPOcount();
		}
		return result;
		
	}
	
	public void processTheory(ITheoryRoot theory) {
		ModelTheory thy;
		if (!theories.containsKey(theory)) {
			thy = new ModelTheory(theory);
			theories.put(theory, thy);
		} else {
			thy = theories.get(theory);
		}
		thy.processChildren();
		thy.poNeedsProcessing = true;
		thy.psNeedsProcessing = true;
	}
	
	public void removeTheory(ITheoryRoot theoryRoot) {
		ModelTheory theory = theories.get(theoryRoot);
		if (theory != null) {
			theories.remove(theoryRoot);
		}
	}
}
