/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

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
		IRecursiveOperatorDefinition[] recDefs = newOpDef
				.getRecursiveOperatorDefinitions();
		if (opDefs.length + recDefs.length == 1) {
			// process direct definition if any
			processDirectDefinitions(opDefs, newOpDef, scNewOpDef, repository, monitor);
			// process recursive definition if any
			//processRecursiveDefinition(recDefs, newOpDef, scNewOpDef, repository, monitor);
		} else {
			operatorInformation.setHasError();
			if (opDefs.length + recDefs.length == 0) {
				createProblemMarker(newOpDef, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorNoDirectDefError,
						newOpDef.getLabel());
			} else {
				createProblemMarker(newOpDef, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorHasMoreThan1DefError,
						newOpDef.getLabel());
			}
		}
	}

	protected void processRecursiveDefinition(IRecursiveOperatorDefinition[] recDefs,
			INewOperatorDefinition newOpDef,
			ISCNewOperatorDefinition scNewOpDef, ISCStateRepository repository,
			IProgressMonitor monitor) throws RodinDBException {
		if (recDefs.length == 1) {
			IRecursiveOperatorDefinition definition = recDefs[0];
			String inductiveArg = definition.getInductiveArgument();
			if (!typeEnvironment.getNames().contains(inductiveArg)
					|| !(typeEnvironment.getType(inductiveArg) instanceof ParametricType)) {
				createProblemMarker(definition,
						TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
						TheoryGraphProblem.ArgumentNotExistOrNotParametric,
						inductiveArg);
			} else {
				ParametricType parametricType = (ParametricType) typeEnvironment
						.getType(inductiveArg);
				IRecursiveDefinitionCase[] cases = definition
						.getRecursiveDefinitionCases();
				Set<IFormulaExtension> coveredConstuctors = new LinkedHashSet<IFormulaExtension>();
				for (IRecursiveDefinitionCase defCase : cases) {
					if (!defCase.hasExpressionString()) {
						createProblemMarker(defCase,
								EventBAttributes.EXPRESSION_ATTRIBUTE,
								TheoryGraphProblem.InductiveCaseMissing);
					}
					String defCaseExpr = defCase.getExpressionString();
					IParseResult result = factory.parseExpression(defCaseExpr,
							V2, null);

					if (!CoreUtilities
							.issueASTProblemMarkers(defCase,
									EventBAttributes.EXPRESSION_ATTRIBUTE,
									result, this)) {
						Expression caseExp = result.getParsedExpression();
						if (caseExp instanceof ExtendedExpression) {
							IExpressionExtension formulaExtension = ((ExtendedExpression) caseExp)
									.getExtension();
							if (formulaExtension.getOrigin() == parametricType
									.getExprExtension().getOrigin()) {
								IDatatype datatype = (IDatatype) formulaExtension
										.getOrigin();
								if (!datatype.isConstructor(formulaExtension)) {
									createProblemMarker(
											defCase,
											EventBAttributes.EXPRESSION_ATTRIBUTE,
											TheoryGraphProblem.ExprIsNotDatatypeConstr);
								} else if (coveredConstuctors
										.contains(formulaExtension)) {
									createProblemMarker(
											defCase,
											EventBAttributes.EXPRESSION_ATTRIBUTE,
											TheoryGraphProblem.ConstrAlreadyCovered);
								}

								else {
									Expression[] childExpressions = ((ExtendedExpression)caseExp).getChildExpressions();
									ITypeEnvironment localTypeEnvironment = typeEnvironment.clone();
									for (Expression childExpression: childExpressions){
										if (!(childExpression instanceof FreeIdentifier) || 
												typeEnvironment.contains(childExpression.toString())){
											createProblemMarker(defCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
													TheoryGraphProblem.ConstrArgumentNotIdentifier, childExpression.toString());
										}
										else {
											// make a predicate to type check
											Predicate typeCheckPredicate = 
												factory.makeRelationalPredicate(Formula.EQUAL, 
														factory.makeFreeIdentifier(inductiveArg, null),
														childExpression, null);
											ITypeCheckResult typeCheckResult = typeCheckPredicate.typeCheck(localTypeEnvironment);
											assert !typeCheckResult.hasProblem();
											localTypeEnvironment.addAll(typeCheckResult.getInferredEnvironment());
											// process definition
										}
									}
									coveredConstuctors.add(formulaExtension);
								}
							} else {
								createProblemMarker(
										defCase,
										EventBAttributes.EXPRESSION_ATTRIBUTE,
										TheoryGraphProblem.ExprNotApproInductiveCase);
							}
						} else {
							createProblemMarker(
									defCase,
									EventBAttributes.EXPRESSION_ATTRIBUTE,
									TheoryGraphProblem.ExprNotApproInductiveCase);
						}
					}
				}
			}
		}
	}

	protected void processDirectDefinitions(IDirectOperatorDefinition[] opDefs,
			INewOperatorDefinition newOpDef,
			ISCNewOperatorDefinition scNewOpDef, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		if (opDefs.length == 1) {
			IDirectOperatorDefinition definition = opDefs[0];
			Formula<?> defFormula = processDefinition(definition, scNewOpDef,
					repository, monitor);
			String label = newOpDef.getLabel();
			if (defFormula != null) {
				if (MathExtensionsUtilities
						.isExpressionOperator(operatorInformation
								.getFormulaType())) {
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
			Formula<?> formula = parseAndCheckFormula(definition, factory,
					typeEnvironment);
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
					GeneralUtilities.toString(notAllowed));
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

	/**
	 * Parses and type checks the formula occurring as an attribute to the given
	 * element.
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed formula
	 * @throws CoreException
	 */
	protected Formula<?> parseAndCheckFormula(IFormulaElement element,
			FormulaFactory ff, ITypeEnvironment typeEnvironment)
			throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		IParseResult result = ff.parsePredicate(form, V2, null);
		if (result.hasProblem()) {
			result = ff.parseExpression(form, V2, null);
			if (CoreUtilities.issueASTProblemMarkers(element, attributeType,
					result, this)) {
				return null;
			} else {
				formula = result.getParsedExpression();
			}
		} else {
			formula = result.getParsedPredicate();
		}

		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				createProblemMarker(element, attributeType,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if (CoreUtilities.issueASTProblemMarkers(element, attributeType,
				tcResult, this)) {
			return null;
		}
		return formula;
	}

}
