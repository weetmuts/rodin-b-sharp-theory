/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
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
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.internal.ui.EventBStyledText;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;
import org.eventb.theory.rbp.utils.ProverUtilities;

@SuppressWarnings("restriction")
public class TheoremInstantiatorWizardPage extends WizardPage {

	private List<IDeployedTheorem> deployedTheorems;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	
	private GivenType[] givenTypes;
	private StyledText[] texts;
	private Map<GivenType, String> instantiations;
	
	/**
	 * Create the wizard.
	 */
	public TheoremInstantiatorWizardPage(List<IDeployedTheorem> deployedTheorems, 
			FormulaFactory factory, ITypeEnvironment typeEnvironment) {
		super("instantiateTheorem");
		setTitle("Instantiate theorem");
		setDescription("Provide instantiations for type parameters");
		
		this.deployedTheorems = deployedTheorems;
		this.factory = factory;
		this.typeEnvironment = typeEnvironment;
		Set<GivenType> givenTypesSet = getGivenTypes();
		givenTypes = givenTypesSet.toArray(new GivenType[givenTypesSet.size()]);
		texts = new StyledText[givenTypes.length];
		instantiations = new LinkedHashMap<GivenType, String>();
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(4, false));
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setText("Instantiations:");
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
					Type parsedType = factory.parseType(textStr, LanguageVersion.V2).getParsedType();
					if (parsedType == null){
						instantiations.remove(givenTypes[k]);
						texts[k].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						dialogChanged();
						return;
					}
					Expression typeExpression = parsedType.toExpression(factory);
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
	
	/**
	 * Ensures that both text fields are set correctly.
	 */
	protected void dialogChanged() {
		if (instantiations.size() != givenTypes.length){
			updateStatus("instantiation must be provided for all type parameters");
			return;
		}
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
		for (IDeployedTheorem deployedTheorem : deployedTheorems){
			Predicate theorem = deployedTheorem.getTheorem();
			Predicate substitutedTheorem = (Predicate) subtitute(theorem.toString(), subs);
			strings.add(substitutedTheorem.toString());
		}
		return strings;
	}

	private Formula<?> subtitute(String srcStr, Map<FreeIdentifier, String> substs) {
		Formula<?> result = parseFormula(srcStr);
		Map<FreeIdentifier, Expression> exprSubsts = convert(substs);
		return result.substituteFreeIdents(exprSubsts, factory);
	}

	private Map<FreeIdentifier, Expression> convert(Map<FreeIdentifier, String> substs) {
		Map<FreeIdentifier, Expression> result = new HashMap<FreeIdentifier, Expression>(
				substs.size());
		for (FreeIdentifier key : substs.keySet()) {
			FreeIdentifier ident = factory.makeFreeIdentifier(key.getName(), null);
			Expression expr = DLib.mDLib(factory).parseExpression(substs.get(key));
			result.put(ident, expr);
		}
		return result;
	}
	
	private Formula<?> parseFormula(String formStr) {
		Formula<?> result = DLib.mDLib(factory).parsePredicate(formStr);
		if (result == null) {
			result = DLib.mDLib(factory).parseExpression(formStr);
		}
		return result;
	}
	
	private Set<GivenType> getGivenTypes (){
		Set<GivenType> set = new LinkedHashSet<GivenType>();
		for(IDeployedTheorem deployedTheorem : deployedTheorems){
			set.addAll(deployedTheorem.getTheorem().getGivenTypes());
		}
		return set;
	}
}
