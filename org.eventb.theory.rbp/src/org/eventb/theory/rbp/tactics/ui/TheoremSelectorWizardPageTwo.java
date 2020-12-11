/*******************************************************************************
* Copyright (c) 2011, 2020 University of Southampton and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.ui.EventBStyledText;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoremSelectorWizardPageTwo extends WizardPage {
	
	private ITypeEnvironment typeEnvironment;
	private FormulaFactory factory;
	
	private GivenType[] givenTypes;
	private StyledText[] texts;
	
	private List<ISCTheorem> SCTheorems;
	private Map<GivenType, String> instantiations;
	
	public TheoremSelectorWizardPageTwo(ITypeEnvironment typeEnvironment) {
		super("selectTheorems");
		setTitle("Instantiate theorems");
		setDescription("Instantiate polymorphic theorems");
		this.typeEnvironment = typeEnvironment;
		this.factory = typeEnvironment.getFormulaFactory();
	}

	@Override
	public void createControl(Composite parent) {
		// nothing to do here
		setControl(new Composite(parent, NONE));
	}
	
	@Override
	public void setVisible(boolean visible) {
		Composite parent = getControl().getParent();
		if (visible){
			init();
			Composite container = new Composite(parent, SWT.NULL);
			container.setLayout(new GridLayout(4, false));
			Label lblNewLabel_1 = new Label(container, SWT.NONE);
			lblNewLabel_1.setText("Instantiations:");
			if (givenTypes.length < 1){
				lblNewLabel_1.setText("No type instantiations required, press the Finish button to proceed.");
			}
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);
			for (int i  = 0 ; i < givenTypes.length; i++){
				new Label(container, SWT.NONE);
				Label label = new Label(container, SWT.NONE);
				label.setText(givenTypes[i].getName());
				texts[i] = new StyledText(container, SWT.BORDER);
				texts[i].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				final int k = i;
				texts[i].addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent arg0) {
						StyledText text = texts[k];
						String textStr = text.getText();
						if (textStr == null || textStr.equals("")){
							instantiations.remove(givenTypes[k]);
							texts[k].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
							dialogChanged();
							return;
						}
						Type parsedType = factory.parseType(textStr).getParsedType();
						if (parsedType == null){
							instantiations.remove(givenTypes[k]);
							texts[k].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
							dialogChanged();
							return;
						}
						Expression typeExpression = parsedType.toExpression();
						for (GivenType ident : typeExpression.getGivenTypes()){
							if (!ProverUtilities.isGivenSet(typeEnvironment, ident.getName())){
								instantiations.remove(givenTypes[k]);
								texts[k].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
								dialogChanged();
								return;
							}
						}
						instantiations.put(givenTypes[k], textStr);
						texts[k].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
						dialogChanged();
					}
				});
				new EventBStyledText(texts[i], true);
				texts[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
				texts[i].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			}
			setControl(container);
			dialogChanged();
		}
		else {
			// else reset control
			setControl(new Composite(parent, SWT.NULL));
		}
		parent.layout();
		super.setVisible(visible);
	}
	
	@Override
	public TheoremSelectorWizardPageOne getPreviousPage() {
		return (TheoremSelectorWizardPageOne) super.getPreviousPage();
	}
	
	public TheoremSelectorWizard getWizard(){
		return (TheoremSelectorWizard) super.getWizard();
	}

	private void init() {
		SCTheorems = getPreviousPage().getSelectedTheorem();
		Set<GivenType> typesSet = getGivenTypes();
		givenTypes = typesSet.toArray(new GivenType[typesSet.size()]);
		texts = new StyledText[givenTypes.length];
		instantiations = new LinkedHashMap<GivenType, String>();
	}
	
	/**
	 * Ensures that both text fields are set correctly.
	 */
	protected void dialogChanged() {
		if (instantiations.size() != givenTypes.length){
			updateStatus("instantiation must be provided for all type parameters");
			return;
		}
		getWizard().setTheorems(getTheoremsStrings());
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);

	}
	
	public List<String> getTheoremsStrings(){
		if (instantiations.size() != givenTypes.length){
			return null;
		}
		List<String> strings = new ArrayList<String>();
		// do the subs here
		Map<FreeIdentifier, String> subs = new LinkedHashMap<FreeIdentifier, String>();
		for (GivenType gType : instantiations.keySet()){
			subs.put(factory.makeFreeIdentifier(gType.getName(), null, factory.makePowerSetType(gType)), instantiations.get(gType));
		}
		try {
			for (ISCTheorem deployedTheorem : SCTheorems){
				Predicate theorem;
					theorem = deployedTheorem.getPredicate(typeEnvironment);
					Predicate substitutedTheorem = (Predicate) substitute(theorem.toString(), subs);
					strings.add(substitutedTheorem.toString());
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strings;
	}

	private Formula<?> substitute(String srcStr, Map<FreeIdentifier, String> substs) {
		Formula<?> result = parseFormula(srcStr);
		Map<FreeIdentifier, Expression> exprSubsts = convert(substs);
		return result.substituteFreeIdents(exprSubsts);
	}

	private Map<FreeIdentifier, Expression> convert(Map<FreeIdentifier, String> substs) {
		Map<FreeIdentifier, Expression> result = new HashMap<FreeIdentifier, Expression>(
				substs.size());
		for (FreeIdentifier key : substs.keySet()) {
			FreeIdentifier ident = factory.makeFreeIdentifier(key.getName(), null);
			Expression expr = factory.parseExpression(substs.get(key), null).getParsedExpression();
			result.put(ident, expr);
		}
		return result;
	}
	
	private Formula<?> parseFormula(String formStr) {
		Formula<?> result = 
				factory.parsePredicate(formStr, null).getParsedPredicate();
		if (result == null) {
			result = factory.parseExpression(formStr, null).getParsedExpression();
		}
		return result;
	}
	
	private Set<GivenType> getGivenTypes(){
		Set<GivenType> set = new LinkedHashSet<GivenType>();
		try {
			for(ISCTheorem deployedTheorem : SCTheorems){
				set.addAll(deployedTheorem.getPredicate(typeEnvironment).getGivenTypes());
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;
	}

}
