package org.eventb.theory.ui.internal.deploy;

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
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class DeployWizardPageTwo extends WizardPage {

	protected String projectName;
	protected String theoryName;

	protected ArrayList<String> dPOs;
	protected String dTitle;
	protected ArrayList<String> rPOs;
	protected String rTitle;

	protected int totalNumPOs;
	protected Tree tree;

	protected TreeItem trtmDischargedPos;
	protected TreeItem trtmReviewedPos;

	protected TreeItem trtmUndischargedPos;
	protected ArrayList<String> uPOs;
	protected String uTitle;
	protected Label theoryLabel;
	private Label label2;
	protected Label projectLabel;

	protected DeployWizardPageTwo() {
		super("deployWizard");
		setTitle("Deploy theory ");
		setDescription("Deploy theory file to MathExtensions project.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		{
			Label label = new Label(container, SWT.NULL);
			label.setText("Theory:");
		}

		theoryLabel = new Label(container, SWT.BORDER | SWT.SINGLE);
		theoryLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			label2 = new Label(container, SWT.NONE);
			label2.setText("Target Project:");
		}
		{
			projectLabel = new Label(container, SWT.BORDER | SWT.SHADOW_IN);
			projectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));
		}
		{
			Label label = new Label(container, SWT.NULL);
			label.setText("Theory Soundness:");
		}
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);

		tree = new Tree(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.SINGLE);
		{
			trtmDischargedPos = new TreeItem(tree, SWT.NONE);
			trtmDischargedPos.setImage(TheoryUIUtils.getPluginImage(
					TheoryUIPlugIn.PLUGIN_ID, "icons/discharged.gif"));

		}
		{
			trtmReviewedPos = new TreeItem(tree, SWT.NONE);
			trtmReviewedPos.setImage(TheoryUIUtils.getPluginImage(
					TheoryUIPlugIn.PLUGIN_ID, "icons/reviewed.gif"));

		}
		{
			trtmUndischargedPos = new TreeItem(tree, SWT.NONE);
			trtmUndischargedPos.setImage(TheoryUIUtils.getPluginImage(
					TheoryUIPlugIn.PLUGIN_ID, "icons/pending.gif"));

		}
		{
			GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true,
					true);
			gridData.heightHint = 112;
			gridData.verticalSpan = 2;
			tree.setLayoutData(gridData);
		}
		new Label(container, SWT.NULL);
		setControl(container);

	}

	/**
	 * 
	 * @param parent
	 * @param type
	 *            "d" "u" "r"
	 */
	protected void createTheTree(TreeItem parent, String type) {
		ArrayList<String> pos = null;
		pos = (type.equals("d") ? dPOs : (type.equals("u") ? uPOs : (type
				.equals("r") ? rPOs : null)));
		if (pos != null && pos.size() > 0) {
			for (String s : pos) {
				TreeItem item = new TreeItem(parent, SWT.NONE);
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
	protected void populatePOs(IRodinFile file) {

		IPSRoot root = ((ISCTheoryRoot) file.getRoot()).getPSRoot();
		dPOs = new ArrayList<String>();
		rPOs = new ArrayList<String>();
		uPOs = new ArrayList<String>();
		try {
			IPSStatus statuses[] = root.getStatuses();
			for (IPSStatus s : statuses) {
				if (DatabaseUtilities.isDischarged(s)) {
					dPOs.add(s.getElementName());
				} else if (DatabaseUtilities.isReviewed(s)) {
					rPOs.add(s.getElementName());
				} else {
					uPOs.add(s.getElementName());
				}
			}
			totalNumPOs = statuses.length;
		} catch (RodinDBException e) {

		}

	}

	public void setVisible(boolean visible) {
		if (visible) {
			projectName = ((AbstractDeployWizardPageOne) getPreviousPage()).getProjectName();
			theoryName = ((AbstractDeployWizardPageOne) getPreviousPage()).getTheoryName();
			theoryLabel.setText(projectName + "\\" + theoryName);
			projectLabel.setText(projectName);
			IRodinFile file = TheoryUIUtils.getSCTheoryInProject(theoryName,
					projectName);
			if (file != null) {
				populatePOs(file);
				dTitle = "Discharged POs (" + dPOs.size() + "/" + totalNumPOs
						+ ")";
				rTitle = "Reviewed POs (" + rPOs.size() + "/" + totalNumPOs
						+ ")";
				uTitle = "Pending POs (" + uPOs.size() + "/" + totalNumPOs
						+ ")";
				trtmDischargedPos.setText(dTitle);
				createTheTree(trtmDischargedPos, "d");
				trtmReviewedPos.setText(rTitle);
				createTheTree(trtmReviewedPos, "r");
				trtmUndischargedPos.setText(uTitle);
				createTheTree(trtmUndischargedPos, "u");
			}
		} else {
			trtmDischargedPos.setItemCount(0);
			trtmReviewedPos.setItemCount(0);
			trtmUndischargedPos.setItemCount(0);
		}
		super.setVisible(visible);
	}

}
