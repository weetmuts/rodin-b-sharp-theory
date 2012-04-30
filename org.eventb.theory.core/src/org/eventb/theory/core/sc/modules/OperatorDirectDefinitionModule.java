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
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.core.wd.DComputer;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorDirectDefinitionModule extends SCProcessorModule {

	private static final IModuleType<OperatorDirectDefinitionModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".operatorDirectDefinitionModule");

	private IOperatorInformation operatorInformation;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOperatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		IDirectOperatorDefinition[] definitions = newOperatorDefinition.getDirectOperatorDefinitions();
		if (definitions.length == 1 && !operatorInformation.hasError()){
			processDirectDefinitions(definitions, newOperatorDefinition, scNewOperatorDefinition, repository, monitor);
		}

	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		operatorInformation = null;
		factory = null;
		typeEnvironment = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected void processDirectDefinitions(
			IDirectOperatorDefinition[] definitions,
			INewOperatorDefinition newOperatorDefinition,
			ISCNewOperatorDefinition scNewOperatorDefinition,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException, RodinDBException {
		IDirectOperatorDefinition definition = definitions[0];
		Formula<?> defFormula = processDirectDefinition(definition,
				repository);
		String label = newOperatorDefinition.getLabel();
		if (defFormula != null) {
			if (MathExtensionsUtilities.isExpressionOperator(operatorInformation.getFormulaType())) {
				if (defFormula instanceof Expression) {
					createSCDirectDefinition(defFormula, scNewOperatorDefinition,
							definition, repository, monitor);
					operatorInformation.setResultantType(((Expression) defFormula)
									.getType());
					operatorInformation.setDefinition(new IOperatorInformation.DirectDefintion(defFormula));
					if (operatorInformation.getWdCondition() == null) {
						Predicate wdPredicate = defFormula.getWDPredicate(factory);
						scNewOperatorDefinition.setPredicate(wdPredicate, monitor);
						operatorInformation.addWDCondition(wdPredicate);
					}
					Predicate dwdPredicate = getDWDPredicate(defFormula);
					operatorInformation.setD_WDCondition(dwdPredicate);
					scNewOperatorDefinition.setWDCondition(dwdPredicate, monitor);
				} else {
					setError();
					createProblemMarker(definition,
							TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
							TheoryGraphProblem.OperatorDefNotExpError,
							label);
				}
			} else {
				if (defFormula instanceof Predicate) {
					createSCDirectDefinition(defFormula, scNewOperatorDefinition,
							definition, repository, monitor);
					operatorInformation.setDefinition(new IOperatorInformation.DirectDefintion(defFormula));
					if (operatorInformation.getWdCondition() == null) {
						Predicate wdPredicate = defFormula
								.getWDPredicate(factory);
						scNewOperatorDefinition.setPredicate(wdPredicate, monitor);
						operatorInformation.addWDCondition(wdPredicate);
					}
					Predicate dwdPredicate = getDWDPredicate(defFormula);
					operatorInformation.setD_WDCondition(dwdPredicate);
					scNewOperatorDefinition.setWDCondition(dwdPredicate, monitor);

				} else {
					setError();
					createProblemMarker(definition,
							TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
							TheoryGraphProblem.OperatorDefNotPredError,
							label);
				}
			}
		} else {
			setError();
		}
	}

	/**
	 * Create the statically checked direct definition corresponding to the
	 * given direct definition.
	 * 
	 * @param definitionFormula
	 *            the direct definition formula
	 * @param scOperatorDefinition
	 *            the SC operator definition
	 * @param definition
	 *            the direct definition
	 * @param repository
	 *            the state repository
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	protected void createSCDirectDefinition(Formula<?> definitionFormula,
			ISCNewOperatorDefinition scOperatorDefinition,
			IDirectOperatorDefinition definition,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		ISCDirectOperatorDefinition scDef = scOperatorDefinition
				.getDirectOperatorDefinition(definition.getElementName());
		scDef.create(null, monitor);
		scDef.setSCFormula(definitionFormula, monitor);
		scDef.setSource(definition, monitor);
	}

	/**
	 * Processes the given direct definition.
	 * 
	 * @param definition
	 *            the direct definition
	 * @param repository
	 *            the state repository
	 * @return the formula contained in the direct definition
	 * @throws CoreException
	 */
	protected Formula<?> processDirectDefinition(
			IDirectOperatorDefinition definition, ISCStateRepository repository)
			throws CoreException {
		if (definition.hasFormula()) {
			Formula<?> formula = ModulesUtils.parseFormula(definition, factory, this);
			if (formula != null) {
				formula = ModulesUtils.checkFormula(definition, formula, typeEnvironment, this);
				FreeIdentifier[] idents = formula.getFreeIdentifiers();
				List<String> notAllowed = new ArrayList<String>();
				for (FreeIdentifier ident : idents) {
					if (!operatorInformation.isAllowedIdentifier(ident)) {
						notAllowed.add(ident.getName());
					}
				}
				if (notAllowed.size() != 0) {
					createProblemMarker(definition,
							TheoryAttributes.FORMULA_ATTRIBUTE,
							TheoryGraphProblem.OpCannotReferToTheseIdents,
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

	private void setError() throws CoreException{
		operatorInformation.setHasError();
	}
	
	private Predicate getDWDPredicate(Formula<?> formula){
		DComputer computer = new DComputer(factory);
		return computer.getWDLemma(formula);
	}

}
