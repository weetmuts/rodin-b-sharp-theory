package org.eventb.theory.ui.explorer.model;

import java.util.HashMap;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeParameter;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * This class represents a Context in the model
 *
 */
@SuppressWarnings("restriction")
public class ModelTheory extends ModelPOContainer{
	
	/**
	 * The nodes are used by the ContentProviders to present a node in the tree
	 * above elements such as Axioms or Theorems.
	 */
	public final TheoryModelElementNode typepar_node;
	public final TheoryModelElementNode datatype_node;
	public final TheoryModelElementNode po_node;
	public final TheoryModelElementNode op_node;
	public final TheoryModelElementNode pb_node;
	public final TheoryModelElementNode thm_node;
	
	private HashMap<IDatatypeDefinition, ModelDatatype> datatypes = new HashMap<IDatatypeDefinition, ModelDatatype>();
	private HashMap<IProofRulesBlock, ModelRulesBlock> blocks = new HashMap<IProofRulesBlock, ModelRulesBlock>();

	private ITheoryRoot root;

	//indicate whether the poRoot or the psRoot should be processed freshly
	public boolean psNeedsProcessing = true;
	public boolean poNeedsProcessing = true;

	
	/**
	 * Creates a ModelContext from a given IContextRoot
	 * @param root	The ContextRoot that this ModelContext is based on.
	 */
	public ModelTheory(ITheoryRoot root){
		this.root = root;
		typepar_node = new TheoryModelElementNode(ITypeParameter.ELEMENT_TYPE, this);
		datatype_node = new TheoryModelElementNode(IDatatypeDefinition.ELEMENT_TYPE, this);
		po_node = new TheoryModelElementNode(IPSStatus.ELEMENT_TYPE, this);
		op_node = new TheoryModelElementNode(INewOperatorDefinition.ELEMENT_TYPE, this);
		thm_node = new TheoryModelElementNode(ITheorem.ELEMENT_TYPE, this);
		pb_node = new TheoryModelElementNode(IProofRulesBlock.ELEMENT_TYPE, this);
	}


	
	/**
	 * Processes the children of this Context:
	 * Clears existing axioms and theorems.
	 * Adds all axioms and theorems found in the internalContext root.
	 */
	public void processChildren(){
		// TODO children which have proof obligations needs to be processed
		datatypes.clear();
		blocks.clear();
		try {
			for (IDatatypeDefinition dtd : root.getDatatypeDefinitions()) {
				addDatatype(dtd);
			}
			for (IProofRulesBlock block : root.getProofRulesBlocks()){
				addBlock(block);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when accessing datatypes of "+root);
		}
	}
	
	
	public void addDatatype(IDatatypeDefinition def) {
		datatypes.put(def, new ModelDatatype(def, this));
	}
	
	public void addBlock(IProofRulesBlock block) {
		blocks.put(block, new ModelRulesBlock(block, this));
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
				IPORoot root = this.root.getPORoot();
				if (root.exists()) {
					IPOSequent[] sequents = root.getSequents();
					int pos = 1;
					for (IPOSequent sequent : sequents) {
						ModelProofObligation po = new ModelProofObligation(sequent, pos);
						pos++;
						po.setTheory(this);
						proofObligations.put(sequent, po);
			
						IPOSource[] sources = sequent.getSources();
						for (int j = 0; j < sources.length; j++) {
							IRodinElement source = sources[j].getSource();
							//only process sources that belong to this context.
							if (root.isAncestorOf(source)) {
								processSource(source, po);
							}
						}
					}
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing proof obligations of " +root);
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
				IPSRoot root = this.root.getPSRoot();
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
				UIUtils.log(e, "when processing proof statuses of " +root);
			}
			psNeedsProcessing = false;
		}
	}


	/**
	 * @return The Project that contains this Context.
	 */
	@Override
	public IModelElement getModelParent() {
		return TheoryModelController.getProject(root.getRodinProject());
	}
	
	
	/**
	 * process the proof obligations if needed
	 * 
	 * @return the total number of Proof Obligations
	 */
	@Override
	public int getPOcount(){
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		return proofObligations.size();
	}
	
	/**
	 * process the proof obligations if needed
	 * 
	 * @return The number of undischarged Proof Obligations
	 */
	@Override
	public int getUndischargedPOcount() {
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		int result = 0;
		for (ModelProofObligation po : proofObligations.values()) {
			if (!po.isDischarged()) {
				result++;
			}
		}
		return result;
		
	}
	

	@Override
	public IRodinElement getInternalElement() {
		return root;
	}
	
	/**
	 * Processes a source belonging to a given Proof Obligation
	 * 
	 * @param source
	 *            The source to process
	 * @param po
	 *            The proof obligation the source belongs to
	 */
	protected void processSource (IRodinElement source, ModelProofObligation po) {
		// TODO concerns elements with associated POs
		
	}
	
	public IModelElement getModelElement(IRodinElement element) {
		if(element instanceof IDatatypeDefinition)
			return datatypes.get(element);
		else if (element instanceof IProofRulesBlock)
			return blocks.get(element);
		return null;
	}

	/**
	 * In the complex version this gets the first abstract context of this
	 * context. If none exist or in the non-complex version this returns the
	 * containing project.
	 */
	@Override
	public Object getParent(boolean complex) {
		return root.getRodinProject();
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {

		
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		
		if (type == IDatatypeDefinition.ELEMENT_TYPE) {
			return new Object[]{datatype_node};
		}
		if (type == ITypeParameter.ELEMENT_TYPE) {
			return new Object[]{typepar_node};
		}
		if (type == IPSStatus.ELEMENT_TYPE) {
			return new Object[]{po_node};
		}
		if(type == INewOperatorDefinition.ELEMENT_TYPE)
			return new Object[]{op_node};
		if(type == IProofRulesBlock.ELEMENT_TYPE)
			return new Object[]{pb_node};
		if(type == ITheorem.ELEMENT_TYPE)
			return new Object[]{thm_node};
		if (ExplorerUtils.DEBUG) {
			System.out.println("Unsupported children type for theory: " +type);
		}
		return new Object[0];
	}



	/**
	 * @return
	 */
	public IEventBRoot getTheoryRoot() {
		// TODO Auto-generated method stub
		return root;
	}
	

}
