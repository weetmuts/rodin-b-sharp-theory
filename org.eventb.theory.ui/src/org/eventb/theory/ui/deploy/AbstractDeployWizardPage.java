/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.deploy;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.eclipse.swt.events.SelectionAdapter;

/**
 * @author maamria
 *
 */
public abstract class AbstractDeployWizardPage extends WizardPage{

	protected ArrayList<String> dPOs;
	protected String dTitle;

	protected String projectName;
	protected boolean force = false;
	protected boolean deployDepend = false;

	protected ArrayList<String> rPOs;
	protected String rTitle;
	
	protected String theoryName;
	protected int totalNumPOs;
	protected Tree tree;
	
	protected TreeItem trtmDischargedPos;
	protected TreeItem trtmReviewedPos;
	
	protected TreeItem trtmUndischargedPos;
	protected ArrayList<String> uPOs;
	protected String uTitle;
	protected Label theoryLabel;
	private Button dependButton;
	
	protected AbstractDeployWizardPage() {
		super("deployWizard");
		setTitle("Deploy theory ");
		setDescription("Deploy theory file.");
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
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		{
			Label label = new Label(container, SWT.NULL);
			label.setText("Theory Soundness:");
		}
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		
		tree = new Tree(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		{
			trtmDischargedPos = new TreeItem(tree, SWT.NONE);
			trtmDischargedPos.setImage(TheoryUIUtils.getPluginImage(TheoryUIPlugIn.PLUGIN_ID, "icons/discharged.gif"));
			
		}
		{
			trtmReviewedPos = new TreeItem(tree, SWT.NONE);
			trtmReviewedPos.setImage(TheoryUIUtils.getPluginImage(TheoryUIPlugIn.PLUGIN_ID, "icons/reviewed.gif"));
			
		}
		{
			trtmUndischargedPos = new TreeItem(tree, SWT.NONE);
			trtmUndischargedPos.setImage(TheoryUIUtils.getPluginImage(TheoryUIPlugIn.PLUGIN_ID, "icons/pending.gif"));
			
		}
		{
			GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
			gridData.heightHint = 112;
			gridData.verticalSpan = 2;
			tree.setLayoutData(gridData);
		}
		new Label(container, SWT.NULL);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		{
			dependButton = new Button(container, SWT.CHECK);
			dependButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(dependButton.getSelection()){
						deployDepend = true;
					}
				}
			});
			dependButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			dependButton.setText("Deploy dependencies if necessary.");
			dependButton.setSelection(false);
		}
		{
			final Button forceButton = new Button(container, SWT.CHECK);
			forceButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			forceButton.setText("Override deployed theory if necessary.");
			forceButton.setSelection(false);
			forceButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(forceButton.getSelection()){
						force = true;
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		setControl(container);

	}

	/**
	 * 
	 * @param parent
	 * @param type "d" "u" "r"
	 */
	protected void createTheTree(TreeItem parent, String type){
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
	 * @return the force
	 */
	public boolean forceDeployment() {
		return force;
	}
	
	public boolean deployDependencies() {
		return deployDepend;
	}
	
	public String getProjectName(){
		return projectName;
	}
	
	public String getTheoryName(){
		return theoryName;
	}
	
	/**
	 * 
	 * @param parent
	 * @throws RodinDBException 
	 */
	protected void populatePOs(IRodinFile file) {
		
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
