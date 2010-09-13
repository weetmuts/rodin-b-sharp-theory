package org.eventb.theory.ui.deploy;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class DeployWizardPageTwo extends WizardPage {

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
	private ArrayList<String> uPOs;
	private String uTitle;
	private Label theoryLabel;
	private Label theoryDirLabel;
	
	protected DeployWizardPageTwo() {
		super("deployWizard");
		setTitle("Deploy theory ");
		setDescription("Deploy theory file to deployment directory.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		{
			Label label = new Label(container, SWT.NULL);
			label.setText("Theory:");
		}
		
		theoryLabel = new Label(container, SWT.BORDER | SWT.SINGLE);
		{
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			theoryLabel.setLayoutData(gd);
		}
		
		new Label(container, SWT.NULL);
		{
			Label label = new Label(container, SWT.NULL);
			label.setText("Deployment Directory:");
		}
		theoryDirLabel = new Label(container, SWT.BORDER | SWT.SINGLE);
		{
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			theoryDirLabel.setLayoutData(gd);
			theoryDirLabel.setText("Home Directory");
		}
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		{
			Label label = new Label(container, SWT.NULL);
			label.setText("Theory Soundness:");
		}
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		
		tree = new Tree(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		{
			trtmDischargedPos = new TreeItem(tree, SWT.NONE);
			trtmDischargedPos.setImage(TheoryUIUtils.getPluginImage("ac.soton.eventb.ruleBase.theory.ui", "icons/discharged.gif"));
			
		}
		{
			trtmReviewedPos = new TreeItem(tree, SWT.NONE);
			trtmReviewedPos.setImage(TheoryUIUtils.getPluginImage("ac.soton.eventb.ruleBase.theory.ui", "icons/reviewed.gif"));
			
		}
		{
			trtmUndischargedPos = new TreeItem(tree, SWT.NONE);
			trtmUndischargedPos.setImage(TheoryUIUtils.getPluginImage("ac.soton.eventb.ruleBase.theory.ui", "icons/pending.gif"));
			
		}
		{
			GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
			gridData.verticalSpan = 3;
			tree.setLayoutData(gridData);
		}
		setControl(container);

	}
	
	public void setVisible(boolean visible){
		if (visible) {
			projectName = ((DeployWizardPageOne) getPreviousPage())
					.getProjectName();
			theoryName = ((DeployWizardPageOne) getPreviousPage())
					.getTheoryName();
			theoryLabel.setText(projectName+"\\"+theoryName);
			IRodinFile file = TheoryUIUtils.getSCTheoryInProject(theoryName, projectName);
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
