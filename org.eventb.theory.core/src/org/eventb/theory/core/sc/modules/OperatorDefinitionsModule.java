/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
public class OperatorDefinitionsModule extends SCProcessorModule{

	private final IModuleType<OperatorDefinitionsModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID+ ".operatorDefinitionsModule");
	
	private IOperatorInformation operatorInformation;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOperatorDefinitions = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinitions = (ISCNewOperatorDefinition) target;
		IDirectOperatorDefinition[] opDefs = newOperatorDefinitions.getDirectOperatorDefinitions();
		IRecursiveOperatorDefinition[] recDefs = newOperatorDefinitions.getRecursiveOperatorDefinitions();
		if (opDefs.length + recDefs.length == 1) {
			// process direct definition if any
			processDirectDefinitions(opDefs, newOperatorDefinitions, scNewOperatorDefinitions, repository, monitor);
			// process recursive definition if any
			//processRecursiveDefinition(recDefs, newOpDef, scNewOpDef, repository, monitor);
		} else {
			operatorInformation.setHasError();
			if (opDefs.length + recDefs.length == 0) {
				createProblemMarker(newOperatorDefinitions, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorNoDirectDefError,
						newOperatorDefinitions.getLabel());
			} else {
				createProblemMarker(newOperatorDefinitions, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorHasMoreThan1DefError,
						newOperatorDefinitions.getLabel());
			}
		}
		
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		operatorInformation = (IOperatorInformation) repository.getState(IOperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/**
	 * Processes the direct definitions and checks against any potential errors.
	 * @param operatorDefinitions the direct operator definitions
	 * @param operatorDefinition the parent operator definition
	 * @param scNewOpDef the target SC operator definition
	 * @param repository the state repository
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected void processDirectDefinitions(IDirectOperatorDefinition[] operatorDefinitions,
			INewOperatorDefinition operatorDefinition, ISCNewOperatorDefinition scNewOpDef, 
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		if (operatorDefinitions.length == 1) {
			IDirectOperatorDefinition definition = operatorDefinitions[0];
			Formula<?> defFormula = processDirectDefinition(definition, repository);
			String label = operatorDefinition.getLabel();
			if (defFormula != null) {
				if (MathExtensionsUtilities.isExpressionOperator(operatorInformation.getFormulaType())) {
					if (defFormula instanceof Expression) {
						createSCDirectDefinition(defFormula, scNewOpDef,
								definition, repository, monitor);
						operatorInformation.setResultantType(((Expression) defFormula).getType());
						if (operatorInformation.getWdCondition() == null) {
							Predicate wdPredicate = defFormula
									.getWDPredicate(repository.getFormulaFactory());
							scNewOpDef.setPredicate(wdPredicate, monitor);
							operatorInformation.setWdCondition(wdPredicate);
						}
					} else {
						operatorInformation.setHasError();
						createProblemMarker(operatorDefinition,
								TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
								TheoryGraphProblem.OperatorDefNotExpError,
								label);
					}
				} else {
					if (defFormula instanceof Predicate) {
						createSCDirectDefinition(defFormula, scNewOpDef,
								definition, repository, monitor);
						if (operatorInformation.getWdCondition() == null) {
							Predicate wdPredicate = defFormula
									.getWDPredicate(repository.getFormulaFactory());
							scNewOpDef.setPredicate(wdPredicate, monitor);
							operatorInformation.setWdCondition(wdPredicate);
						}

					} else {
						operatorInformation.setHasError();
						createProblemMarker(operatorDefinition,
								TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
								TheoryGraphProblem.OperatorDefNotPredError,
								label);
					}
				}
			}
		}
	}
	
	/**
	 * Create the statically checked direct definition corresponding to the given direct definition.
	 * @param definitionFormula the direct definition formula
	 * @param scOperatorDefinition the SC operator definition
	 * @param definition the direct definition
	 * @param repository the state repository
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected void createSCDirectDefinition(Formula<?> definitionFormula,
			ISCNewOperatorDefinition scOperatorDefinition,
			IDirectOperatorDefinition definition,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		ISCDirectOperatorDefinition scDef = scOperatorDefinition.getDirectOperatorDefinition(definition.getElementName());
		scDef.create(null, monitor);
		scDef.setSCFormula(definitionFormula, monitor);
		scDef.setSource(definition, monitor);
	}
	
	/**
	 * Processes the given direct definition.
	 * 
	 * @param definition the direct definition
	 * @param repository the state repository
	 * @return the formula contained in the direct definition
	 * @throws CoreException
	 */
	protected Formula<?> processDirectDefinition(IDirectOperatorDefinition definition,
			 ISCStateRepository repository) throws CoreException {
		if (definition.hasFormula()) {
			Formula<?> formula = ModulesUtils.parseAndCheckFormula(definition, repository.getFormulaFactory(), 
					repository.getTypeEnvironment(), this);
			if (formula != null) {
				FreeIdentifier[] idents = formula.getFreeIdentifiers();
				List<String> notAllowed = new ArrayList<String>();
				for (FreeIdentifier ident : idents) {
					if (!operatorInformation.isAllowedIdentifier(ident)) {
						notAllowed.add(ident.getName());
					}
				}
				if (notAllowed.size() != 0) {
					createProblemMarker(definition, TheoryAttributes.FORMULA_ATTRIBUTE,
							TheoryGraphProblem.OpCannotReferToTheseTypes,
							GeneralUtilities.toString(notAllowed));
					return null;
				}
				return formula;
			}
		} else {
			createProblemMarker(definition, TheoryAttributes.FORMULA_ATTRIBUTE,
					TheoryGraphProblem.MissingFormulaAttrError);
		}
		return null;
	}
}
