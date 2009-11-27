/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.explorer.model;

import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;
import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class ModelTheory extends ModelPOContainer{

	public boolean poNeedsProcessing = true;
	public final TheoryModelElementNode poNode;


	//indicate whether the poRoot or the psRoot should be processed freshly
	public boolean psNeedsProcessing = true;

	public final TheoryModelElementNode rewRuleNode;
	private ITheoryRoot theoryRoot;
	
	public ModelTheory(ITheoryRoot root) {
		theoryRoot = root;
		rewRuleNode = new TheoryModelElementNode(IRewriteRule.ELEMENT_TYPE,
				this);
		poNode = new TheoryModelElementNode(IPSStatus.ELEMENT_TYPE, this);
	}

	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		if (type == IRewriteRule.ELEMENT_TYPE) {
			return new Object[] { rewRuleNode };
		}
		if (type == IPSStatus.ELEMENT_TYPE) {
			return new Object[]{poNode};
		}
		return new Object[0];
	}

	public IRodinElement getInternalElement() {
		return theoryRoot;
	}

	@Override
	public IModelElement getModelParent() {
		return TheoryModelController.getModelElement(theoryRoot.getRodinProject());
	}

	public Object getParent(boolean complex) {
		return theoryRoot.getRodinProject();
	}

	public ITheoryRoot getTheoryRoot() {
		return theoryRoot;
	}

	public void processChildren() {

	}

	
	/**
	 * Processes the PORoot that belongs to this context.
	 * It creates a ModelProofObligation for each sequent
	 * and adds it to this context as well as to the
	 * concerned Theorems and Axioms.
	 */
	public void processPORoot() {
		if (poNeedsProcessing) {
			try {
				//clear old POs
				proofObligations.clear();
				IPORoot root = theoryRoot.getPORoot();
				if (root.exists()) {
					IPOSequent[] sequents = root.getSequents();
					int pos = 1;
					for (IPOSequent sequent : sequents) {
						ModelProofObligation po = new ModelProofObligation(sequent, pos);
						pos++;
						po.setTheory(this);
						proofObligations.put(sequent, po);
					}
				}
			} catch (RodinDBException e) {
				TheoryUIUtils.log(e, "when processing proof obligations of " +theoryRoot);
			}
			poNeedsProcessing = false;
		}
	}
	
	

	/**
	 * Processes the PSRoot that belongs to this Context. Each status is added to
	 * the corresponding Proof Obligation, if that ProofObligation is present.
	 */
	public void processPSRoot(){
		if (psNeedsProcessing) {
			try {
				IPSRoot root = theoryRoot.getPSRoot();
				if (root.exists()) {
					IPSStatus[] stats = root.getStatuses();
					for (IPSStatus status : stats) {
						IPOSequent sequent = status.getPOSequent();
						// check if there is a ProofObligation for this status (there should be one!)
						if (proofObligations.containsKey(sequent)) {
							proofObligations.get(sequent).setIPSStatus(status);
						}
					}
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing proof statuses of " +theoryRoot);
			}
			psNeedsProcessing = false;
		}
	}
	
	@Override
	public String toString() {
		return ("Model Theory: " + theoryRoot.getComponentName());
	}
}
