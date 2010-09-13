/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class DirectOperatorDefinitionModule extends SCProcessorModule {

	IModuleType<DirectOperatorDefinitionModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".directOperatorDefinitionModule");

	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private IOperatorInformation operatorInformation;

	private int index = 0;

	private static final String DEF_PREF = "DEF";

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOpDef = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOpDef = (ISCNewOperatorDefinition) target;
		IDirectOperatorDefinition[] opDefs = newOpDef
				.getDirectOperatorDefinitions();
		if (opDefs != null) {
			if (opDefs.length == 1) {
				IDirectOperatorDefinition definition = opDefs[0];
				Formula<?> defFormula = processDefinition(definition,
						scNewOpDef, repository, monitor);
				String label = newOpDef.getLabel();
				if (defFormula != null) {
					if (TheoryCoreFacade.isExpressionOperator(operatorInformation.getFormulaType())) {
						if (defFormula instanceof Expression) {
							createSCDirectDefinition(defFormula, scNewOpDef,
									definition, repository, monitor);
							operatorInformation.setDirectDefinition(defFormula);
							if (operatorInformation.getWdCondition() == null) {
								Predicate wdPredicate = defFormula
										.getWDPredicate(factory);
								scNewOpDef.setPredicate(wdPredicate, monitor);
								operatorInformation.setWdCondition(wdPredicate);
							}
						} else {
							operatorInformation.setHasError();
							createProblemMarker(newOpDef,
									TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
									TheoryGraphProblem.OperatorDefNotExpError,
									label);
						}
					} else {
						if (defFormula instanceof Predicate) {
							createSCDirectDefinition(defFormula, scNewOpDef,
									definition, repository, monitor);
							operatorInformation.setDirectDefinition(defFormula);
							if (operatorInformation.getWdCondition() == null) {
								Predicate wdPredicate = defFormula
										.getWDPredicate(factory);
								scNewOpDef.setPredicate(wdPredicate, monitor);
								operatorInformation.setWdCondition(wdPredicate);
							}

						} else {
							operatorInformation.setHasError();
							createProblemMarker(newOpDef,
									TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
									TheoryGraphProblem.OperatorDefNotPredError,
									label);
						}
					}
				}
				else {
					operatorInformation.setHasError();
				}
			} else {
				operatorInformation.setHasError();
				if (opDefs.length == 0) {
					createProblemMarker(newOpDef,
							EventBAttributes.LABEL_ATTRIBUTE,
							TheoryGraphProblem.OperatorNoDirectDefError,
							newOpDef.getLabel());
				} else {
					createProblemMarker(
							newOpDef,
							EventBAttributes.LABEL_ATTRIBUTE,
							TheoryGraphProblem.OperatorHasMoreThan1DirectDefError,
							newOpDef.getLabel());
				}
			}

		}

	}

	/**
	 * @param defFormula
	 * @param scNewOpDef
	 * @param definition
	 * @param repository
	 * @param monitor
	 */
	private void createSCDirectDefinition(Formula<?> defFormula,
			ISCNewOperatorDefinition scNewOpDef,
			IDirectOperatorDefinition definition,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		ISCDirectOperatorDefinition scDef = scNewOpDef
				.getDirectOperatorDefinition(DEF_PREF + index++);
		scDef.create(null, monitor);
		scDef.setSCFormula(defFormula, monitor);
		scDef.setSource(definition, monitor);
	}

	/**
	 * @param definition
	 * @param scNewOpDef
	 * @param repository
	 * @param monitor
	 */
	private Formula<?> processDefinition(IDirectOperatorDefinition definition,
			ISCNewOperatorDefinition scNewOpDef, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		if (definition.hasFormula()) {
			Formula<?> formula = CoreUtilities.parseAndCheckFormula(definition,
					factory, typeEnvironment, this);
			if (formula != null
					&& checkAgainstReferencedIdentifiers(definition, formula)) {
				return formula;
			}
		} else {
			createProblemMarker(definition, TheoryAttributes.FORMULA_ATTRIBUTE,
					TheoryGraphProblem.MissingFormulaAttrError);
		}
		return null;
	}

	private boolean checkAgainstReferencedIdentifiers(
			IDirectOperatorDefinition def, Formula<?> form)
			throws CoreException {
		FreeIdentifier[] idents = form.getFreeIdentifiers();
		List<String> notAllowed = new ArrayList<String>();
		for (FreeIdentifier ident : idents) {
			if (!operatorInformation.isAllowedIdentifier(ident)) {
				notAllowed.add(ident.getName());
			}
		}
		if (notAllowed.size() != 0) {
			createProblemMarker(def, TheoryAttributes.FORMULA_ATTRIBUTE,
					TheoryGraphProblem.OpCannotReferToTheseTypes,
					CoreUtilities.toString(notAllowed));
			return false;
		}
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

}
