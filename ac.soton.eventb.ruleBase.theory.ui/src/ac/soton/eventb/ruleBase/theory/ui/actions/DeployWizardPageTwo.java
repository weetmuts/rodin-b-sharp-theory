package ac.soton.eventb.ruleBase.theory.ui.actions;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.ProverLib;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.prover.prefs.PrefsRepresentative;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.editor.actions.DeployWizardPage;
import ac.soton.eventb.ruleBase.theory.ui.perspective.ResourceManager;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

/**
 * @see DeployWizardPage
 * @author maamria
 *
 */
public class DeployWizardPageTwo extends WizardPage {
	private Text deployText;
	private ArrayList<String> dPOs;
	private String dTitle;

	private String projectName;
	private ArrayList<String> rPOs;
	private String rTitle;
	
	private String theoryName;
	private int totalNumPOs;
	private Tree tree;
	
	private TreeItem trtmDischargedPos;
	private TreeItem trtmReviewedPos;
	
	private TreeItem trtmUndischargedPos;
	private Text txtTheory;
	private ArrayList<String> uPOs;
	private String uTitle;
	
	
	/**
	 * Create the wizard.
	 */
	public DeployWizardPageTwo() {
		super("deployWizard");
		setTitle("Deploy theory ");
		setDescription("Deploy theory file to deployment directory");
	}
	

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		{
			Label lblTheoryName = new Label(container, SWT.NONE);
			lblTheoryName.setBounds(10, 30, 99, 13);
			lblTheoryName.setText("Theory: ");
		}
		{
			txtTheory = new Text(container, SWT.NONE);
			txtTheory.setBounds(125, 30, 279, 19);
		}
		{
			Label lblTheorySoundness = new Label(container, SWT.NONE);
			lblTheorySoundness.setBounds(10, 183, 99, 13);
			lblTheorySoundness.setText("Theory Soundness:");
		}
		{
			tree = new Tree(container, SWT.BORDER);
			tree.setBounds(125, 88, 279, 202);
			{
				trtmDischargedPos = new TreeItem(tree, SWT.NONE);
				trtmDischargedPos.setImage(ResourceManager.getPluginImage("ac.soton.eventb.ruleBase.theory.ui", "icons/discharged.gif"));
				
			}
			{
				trtmReviewedPos = new TreeItem(tree, SWT.NONE);
				trtmReviewedPos.setImage(ResourceManager.getPluginImage("ac.soton.eventb.ruleBase.theory.ui", "icons/reviewed.gif"));
				
			}
			{
				trtmUndischargedPos = new TreeItem(tree, SWT.NONE);
				trtmUndischargedPos.setImage(ResourceManager.getPluginImage("ac.soton.eventb.ruleBase.theory.ui", "icons/pending.gif"));
				
			}
		}
		{
			Label lblTheoriesDirectory = new Label(container, SWT.NONE);
			lblTheoriesDirectory.setBounds(10, 60, 99, 13);
			lblTheoriesDirectory.setText("Theories Directory");
		}
		{
			deployText = new Text(container, SWT.NONE);
			deployText.setBounds(125, 60, 279, 19);
			deployText.setText(PrefsRepresentative.getTheoriesDirectory());
			deployText.setEditable(false);
		}
	}
	
	public void setVisible(boolean visible){
		if (visible) {
			projectName = ((DeployWizardPageOne) getPreviousPage())
					.getProjectName();
			theoryName = ((DeployWizardPageOne) getPreviousPage())
					.getTheoryName();
			txtTheory.setText(projectName+"\\"+theoryName);
			txtTheory.setEditable(false);
			IRodinFile file = TheoryUIUtils.getTheoryInProject(theoryName, ISCTheoryRoot.ELEMENT_TYPE, projectName);
			populatePOs(file);
			dTitle = "Discharged POs (" + dPOs.size() + "/" + totalNumPOs + ")";
			rTitle = "Reviewed POs (" + rPOs.size() + "/" + totalNumPOs + ")";
			uTitle = "Pending POs (" + uPOs.size() + "/" + totalNumPOs + ")";
			trtmDischargedPos.setText(dTitle);
			createTheTree(trtmDischargedPos, "d");
			trtmReviewedPos.setText(rTitle);
			createTheTree(trtmReviewedPos, "r");
			trtmUndischargedPos.setText(uTitle);
			createTheTree(trtmUndischargedPos, "u");
		}
		else{
			trtmDischargedPos.setItemCount(0);
			trtmReviewedPos.setItemCount(0);
			trtmUndischargedPos.setItemCount(0);
		}
		super.setVisible(visible);
		
	}

	/**
	 * 
	 * @param parent
	 * @param type "d" "u" "r"
	 */
	private void createTheTree(TreeItem parent, String type){
		ArrayList<String> pos = null;
		pos = (type.equals("d")? dPOs : (type.equals("u")? uPOs : (type.equals("r")? rPOs: null)));
		if(pos != null && pos.size() > 0){
			for(String s : pos){
				TreeItem item = new TreeItem(parent,SWT.NONE);
				item.setText(s);
				item.setImage(parent.getImage());
			}
		}
	}
	
	/**
	 * 
	 * @param parent
	 * @throws RodinDBException 
	 */
	private void populatePOs(IRodinFile file) {
		
		IPSRoot root = ((ISCTheoryRoot) file.getRoot()).getPSRoot();
		dPOs =  new ArrayList<String>();
		rPOs =  new ArrayList<String>();
		uPOs =  new ArrayList<String>();
		try {
			IPSStatus statuses[] = root.getStatuses();
			for (IPSStatus s : statuses) {
				if (ProverLib.isDischarged(s.getConfidence())) {
					dPOs.add(s.getElementName());
				} else if (ProverLib.isPending(s.getConfidence())) {
					uPOs.add(s.getElementName());
				} else if (ProverLib.isReviewed(s.getConfidence())) {
					rPOs.add(s.getElementName());
				} else if(!ProverLib.isValid(s.getConfidence())){
					uPOs.add(s.getElementName());
				}
			}
			totalNumPOs = statuses.length;
		} catch (RodinDBException e) {

		}
		
	}
}
