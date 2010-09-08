package org.eventb.theory.ui.explorer.model;

import org.eventb.core.IPOSequent;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;


/**
 * Represents a Proof Obligation in the Model.
 * 
 */
@SuppressWarnings("restriction")
public class ModelProofObligation extends fr.systerel.internal.explorer.model.ModelProofObligation{
	private boolean broken = false;

	private boolean discharged = false;
	private IPOSequent internal_sequent;
	
	private IPSStatus internal_status;
	private boolean manual = false;
	private int position;
	private boolean reviewed = false;
	private ModelTheory theory;
	public ModelProofObligation(IPOSequent sequent, int position) {
		super(sequent, position);
		this.internal_sequent = sequent;
		this.position = position;
	}

	/**
	 * Compare according to the <code>position</code> of the proof obligations
	 */
	public int compareTo(ModelProofObligation o) {
		return getPosition() - o.getPosition();
	}



	public String getElementName() {
		return internal_sequent.getElementName();
	}

	public IPOSequent getIPOSequent() {
		return internal_sequent;
	}

	public IPSStatus getIPSStatus() {
		return internal_status;
	}

	public String getName() {
		return internal_sequent.getElementName();
	}

	/**
	 * 
	 * @return the position of this proof obligation in relation to other other
	 *         proof obligations. The lower the number, the higher on the list.
	 */
	public int getPosition() {
		return position;
	}

	public ModelTheory getTheory() {
		return theory;
	}

	public boolean isBroken() {
		return broken;
	}

	/**
	 * 
	 * @return <code>true</code> if this PO is discharged <code>false</code>
	 *         otherwise.
	 */
	public boolean isDischarged() {
		return discharged;
	}

	public boolean isManual() {
		return manual;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	/**
	 * Set the status of this proof obligation. Updates stored attributes such
	 * as discharged or reviewed
	 * 
	 * @param status
	 *            The new status of this proof obligation
	 */
	public void setIPSStatus(IPSStatus status) {
		internal_status = status;
		try {
			int confidence = status.getConfidence();
			discharged = (status.getConfidence() > IConfidence.REVIEWED_MAX)
					&& !status.isBroken();
			reviewed = (confidence > IConfidence.PENDING && confidence <= IConfidence.REVIEWED_MAX);
			broken = status.isBroken();
			manual = status.getHasManualProof();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when acessing " +status);
		}
	}

	public void setTheory(ModelTheory thy) {
		theory = thy;
	}

}