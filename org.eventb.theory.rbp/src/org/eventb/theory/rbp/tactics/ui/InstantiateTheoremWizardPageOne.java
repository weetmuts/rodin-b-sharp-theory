package org.eventb.theory.rbp.tactics.ui;

import java.util.Collections;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.rbp.internal.rulebase.DeployedTheorem;
import org.eventb.theory.rbp.internal.rulebase.IDeployedTheorem;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * 
 * @author maamria
 *
 */
public class InstantiateTheoremWizardPageOne extends WizardPage {
	
	private static final String[] COL_NAMES = new String[]{"Name", "Theorem"};
	private Combo theoryCombo;
	private Combo projectCombo;
	private Table theoremsTable;

	private IPOContext poContext;
	private FormulaFactory factory;
	
	public InstantiateTheoremWizardPageOne(IPOContext poContext, FormulaFactory factory) {
		super("Instantiate Theorem Page One");
		setTitle("Theorem Instantiation");
		setDescription("Select a polymorphic theorem to instantiate");
		this.poContext = poContext;
		this.factory = factory;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label projectLabel = new Label(container, SWT.NONE);
		projectLabel.setText("&Project:");
		
		projectCombo = new Combo(container, SWT.READ_ONLY);
		projectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (poContext.inMathExtensions())
			projectCombo.setItems(new String[]{DatabaseUtilities.THEORIES_PROJECT});
		else 
			projectCombo.setItems(new String[]{DatabaseUtilities.THEORIES_PROJECT, 
					poContext.getParentRoot().getRodinProject().getElementName()});
		
		Label theoryLabel = new Label(container, SWT.NONE);
		theoryLabel.setText("&Theory:");
		
		theoryCombo = new Combo(container, SWT.READ_ONLY);
		theoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		TableViewer viewer = new TableViewer(container, SWT.FULL_SELECTION);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new TheoremLabelProvider());
		
		theoremsTable = viewer.getTable();
		theoremsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		theoremsTable.setHeaderVisible(true);
		theoremsTable.setLinesVisible(true);
		
		TableColumn nameCol = new TableColumn(theoremsTable, SWT.CENTER);
		nameCol.setWidth(150);
		nameCol.setText(COL_NAMES[0]);
		
		TableColumn theoremCol = new TableColumn(theoremsTable, SWT.CENTER);
		theoremCol.setWidth(439);
		theoremCol.setText(COL_NAMES[1]);
		viewer.setColumnProperties(COL_NAMES);
		viewer.setInput(Collections.singletonList(new DeployedTheorem("Issam", ProverUtilities.BTRUE)));
	}
	
	static class TheoremLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}

		@Override
		public String getColumnText(Object arg0, int arg1) {
			IDeployedTheorem theorem = (IDeployedTheorem) arg0;
			switch (arg1){
			case 0 : return Boolean.toString(false);
			case 1 : return theorem.getName();
			case 2 : return theorem.getTheorem().toString();
			}
			return null;
		}
		
	}

}
