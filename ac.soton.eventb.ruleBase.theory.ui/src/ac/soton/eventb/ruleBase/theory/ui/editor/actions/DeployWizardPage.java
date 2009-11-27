package ac.soton.eventb.ruleBase.theory.ui.editor.actions;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
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


import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.perspective.ResourceManager;
import ac.soton.eventb.ruleBase.theory.ui.perspective.SWTResourceManager;
import ac.soton.eventb.ruleBase.theory.ui.prefs.facade.PrefsRepresentative;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

/**
 * Deployment wizard page that displays the chosen theory for deployment as well as simple stats regarding its soundness.
 * It also provides the capability to chose a different deployment name for the theory.
 * <p>
 * @author maamria
 *
 */
public class DeployWizardPage extends WizardPage {

	private Button btnUseDiffName;
	private Text deployText;
	private String diffTheoryName;
	private ArrayList<String> dPOs;
	
	private String dTitle;

	private Text newTNameText;
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
	private boolean useDiffName = false;
	private String uTitle;
	
	
	/**
	 * Create the wizard.
	 */
	public DeployWizardPage(String theoryName, String projectName) {
		super("deployWizard");
		setTitle("Deploy theory ");
		setDescription("Deploy theory file to deployment directory");
		this.theoryName = theoryName;
		this.projectName = projectName;
		populatePOs();
		dTitle = "Discharged POs (" + dPOs.size() + "/" + totalNumPOs + ")";
		rTitle = "Reviewed POs (" + rPOs.size() + "/" + totalNumPOs + ")";
		uTitle = "Pending POs (" + uPOs.size() + "/" + totalNumPOs + ")";
		dialogChanged();
		
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
			lblTheoryName.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			lblTheoryName.setBounds(10, 30, 99, 13);
			lblTheoryName.setText("Theory: ");
		}
		{
			txtTheory = new Text(container, SWT.NONE);
			txtTheory.setBounds(125, 30, 279, 19);
		}
		{
			Label lblTheorySoundness = new Label(container, SWT.NONE);
			lblTheorySoundness.setBounds(10, 142, 99, 13);
			lblTheorySoundness.setText("Theory Soundness:");
		}
		{
			tree = new Tree(container, SWT.BORDER);
			tree.setBounds(125, 80, 279, 141);
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
			lblTheoriesDirectory.setBounds(10, 55, 99, 13);
			lblTheoriesDirectory.setText("Theories Directory");
		}
		{
			deployText = new Text(container, SWT.NONE);
			deployText.setBounds(125, 55, 279, 19);
			deployText.setText(PrefsRepresentative.getTheoriesDirectory());
			deployText.setEditable(false);
		}
		
		txtTheory.setText(projectName+"\\"+theoryName);
		txtTheory.setEditable(false);
		trtmDischargedPos.setText(dTitle);
		createTheTree(trtmDischargedPos, "d");
		trtmReviewedPos.setText(rTitle);
		createTheTree(trtmReviewedPos, "r");
		trtmUndischargedPos.setText(uTitle);
		createTheTree(trtmUndischargedPos, "u");
		{
			btnUseDiffName = new Button(container, SWT.CHECK);
			btnUseDiffName.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					useDiffName = btnUseDiffName.getSelection();
					if(useDiffName){
						newTNameText.setEnabled(true);
					}
					else{
						newTNameText.setText("");
						newTNameText.setEnabled(false);
						
					}
					dialogChanged();
				}
			});
			btnUseDiffName.setGrayed(true);
			btnUseDiffName.setBounds(10, 254, 244, 16);
			btnUseDiffName.setSelection(false);
			btnUseDiffName.setText("Use a different name for the deployed theory.");
		}
		{
			newTNameText = new Text(container, SWT.BORDER);
			newTNameText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					diffTheoryName = newTNameText.getText();
					dialogChanged();
				}
			});
			newTNameText.setBounds(265, 253, 139, 19);
			newTNameText.setEnabled(false);
		}
	}

	public String getDiffTheoryName() {
		if(useDiffName)
			return diffTheoryName;
		else 
			return theoryName;
	}

	public boolean isUseDiffName() {
		return useDiffName;
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		if(useDiffName){
			if(newTNameText.getText()== null || newTNameText.getText().equals("")){
				updateStatus("New theory name must be specified");
				return;
			}
		}
		updateStatus(null);
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
	private void populatePOs() {
		IRodinFile file = TheoryUIUtils.getTheoryInProject(theoryName, ISCTheoryRoot.ELEMENT_TYPE, projectName);
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
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
		
	}
}
