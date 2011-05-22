/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.core.sc.states.IOperatorInformation.RecursiveDefinition;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorRecursiveDefinitionModule extends SCProcessorModule {

	private static final IModuleType<OperatorRecursiveDefinitionModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".operatorRecursiveDefinitionModule");

	private IOperatorInformation operatorInformation;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOperatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		IRecursiveOperatorDefinition[] definitions = newOperatorDefinition
				.getRecursiveOperatorDefinitions();
		if (definitions.length == 1) {
			processRecursiveDefinitions(definitions, newOperatorDefinition,
					scNewOperatorDefinition, repository, monitor);
		}
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
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

	protected void processRecursiveDefinitions(
			IRecursiveOperatorDefinition[] operatorDefinitions,
			INewOperatorDefinition operatorDefinition,
			ISCNewOperatorDefinition scNewOperatorDefinition,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		if (operatorDefinitions.length == 1) {
			IRecursiveOperatorDefinition definition = operatorDefinitions[0];
			if (!definition.hasInductiveArgument()) {
				setError();
				createProblemMarker(definition,
						TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
						TheoryGraphProblem.InductiveArgMissing);
			} else {
				String inductiveArgument = definition.getInductiveArgument();
				if (!typeEnvironment.contains(inductiveArgument)
						|| !(typeEnvironment.getType(inductiveArgument) instanceof ParametricType)) {
					setError();
					createProblemMarker(definition,
							TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
							TheoryGraphProblem.ArgumentNotExistOrNotParametric,
							inductiveArgument);
				} else {
					IOperatorArgument operatorArgument = null;
					for (IOperatorArgument arg : operatorInformation
							.getOperatorArguments()) {
						if (arg.getArgumentName().equals(inductiveArgument)) {
							operatorArgument = arg;
							break;
						}
					}
					if (operatorArgument == null) {
						setError();
						throw new IllegalStateException(
								"Illegal state : operator argument not found ("
										+ inductiveArgument);
					}
					RecursiveDefinition actualDefinition = new RecursiveDefinition(
							operatorArgument);
					ParametricType inductiveType = (ParametricType) operatorArgument
							.getArgumentType();
					// Check this for safety
					IDatatype datatype = (IDatatype) inductiveType
							.getExprExtension().getOrigin();
					IRecursiveDefinitionCase[] cases = definition
							.getRecursiveDefinitionCases();
					if (cases.length < 1) {
						setError();
						createProblemMarker(definition,
								TheoryGraphProblem.NoRecCasesError);
					} else {
						ISCRecursiveOperatorDefinition recursiveOperatorDefinition = scNewOperatorDefinition
								.getRecursiveOperatorDefinition(operatorDefinition
										.getElementName());
						recursiveOperatorDefinition.create(null, monitor);
						recursiveOperatorDefinition.setInductiveArgument(
								inductiveArgument, monitor);
						recursiveOperatorDefinition.setSource(definition,
								monitor);
						processCases(cases, recursiveOperatorDefinition,
								actualDefinition, operatorDefinition,
								scNewOperatorDefinition, datatype, repository,
								monitor);
					}
				}
			}
		}
	}

	protected void processCases(IRecursiveDefinitionCase[] cases,
			ISCRecursiveOperatorDefinition recursiveOperatorDefinition,
			RecursiveDefinition actualDefinition,
			INewOperatorDefinition operatorDefinition,
			ISCNewOperatorDefinition scNewOperatorDefinition,
			IDatatype datatype, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		Set<IExpressionExtension> coveredConstructors = new LinkedHashSet<IExpressionExtension>();
		Set<IExpressionExtension> datatypeConstructors = datatype
				.getConstructors();

		Map<IRecursiveDefinitionCase, CaseEntry> mappedCases = new LinkedHashMap<IRecursiveDefinitionCase, CaseEntry>();

		for (IRecursiveDefinitionCase definitionCase : cases) {
			if (!definitionCase.hasExpressionString()) {
				setError();
				createProblemMarker(definitionCase,
						EventBAttributes.EXPRESSION_ATTRIBUTE,
						GraphProblem.ExpressionUndefError);
				continue;
			} else {
				String caseString = definitionCase.getExpressionString();
				IParseResult parseResult = factory.parseExpression(caseString,
						LanguageVersion.V2, null);
				if (CoreUtilities.issueASTProblemMarkers(definitionCase,
						EventBAttributes.EXPRESSION_ATTRIBUTE, parseResult,
						this)) {
					setError();
					continue;
				} else {
					Expression caseExpression = parseResult
							.getParsedExpression();

					if (!(caseExpression instanceof ExtendedExpression)
							|| !(datatypeConstructors
									.contains(((ExtendedExpression) caseExpression)
											.getExtension()))) {
						setError();
						createProblemMarker(
								definitionCase,
								EventBAttributes.EXPRESSION_ATTRIBUTE,
								TheoryGraphProblem.InductiveCaseNotAppropriateExp,
								caseExpression.toString());
						continue;
					} else {
						ExtendedExpression constructorExp = (ExtendedExpression) caseExpression;
						Expression[] childExpressions = constructorExp
								.getChildExpressions();
						boolean error = false;
						for (Expression childExpression : childExpressions) {
							if (!(childExpression instanceof FreeIdentifier)) {
								setError();
								error = true;
								createProblemMarker(
										definitionCase,
										EventBAttributes.EXPRESSION_ATTRIBUTE,
										TheoryGraphProblem.ConsArgNotIdentInCase,
										childExpression.toString());
								continue;
							} else {
								if (operatorInformation
										.isAllowedIdentifier((FreeIdentifier) childExpression)) {
									setError();
									error = true;
									createProblemMarker(
											definitionCase,
											EventBAttributes.EXPRESSION_ATTRIBUTE,
											TheoryGraphProblem.IdentCannotBeUsedAsConsArg,
											((FreeIdentifier) childExpression)
													.toString());
									continue;
								}
							}
						}
						if (error) {
							continue;
						}
						IExpressionExtension extension = ((ExtendedExpression) caseExpression)
								.getExtension();
						FreeIdentifier inductiveArg = actualDefinition
								.getOperatorArgument()
								.toFreeIdentifier(factory);
						Predicate predicate = factory.makeRelationalPredicate(
								Formula.EQUAL, inductiveArg, constructorExp,
								null);
						ITypeCheckResult tcResult = predicate
								.typeCheck(typeEnvironment);
						if (tcResult.hasProblem()) {
							createProblemMarker(definitionCase,
									EventBAttributes.EXPRESSION_ATTRIBUTE,
									TheoryGraphProblem.UnableToTypeCase);
							continue;
						} else {
							if (coveredConstructors.contains(extension)) {
								createProblemMarker(
										definitionCase,
										EventBAttributes.EXPRESSION_ATTRIBUTE,
										TheoryGraphProblem.RecCaseAlreadyCovered);
								continue;
							} else {
								coveredConstructors.add(extension);
								ITypeEnvironment localTypeEnvironment = typeEnvironment
										.clone();
								localTypeEnvironment.addAll(tcResult
										.getInferredEnvironment());
								constructorExp = (ExtendedExpression) ((RelationalPredicate) predicate)
										.getRight();
								CaseEntry entry = new CaseEntry(constructorExp,
										localTypeEnvironment);
								mappedCases.put(definitionCase, entry);
							}
						}

					}
				}
			}
		}
		if (!coveredConstructors.containsAll(datatypeConstructors)) {
			createProblemMarker(operatorDefinition,
					TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
					TheoryGraphProblem.NoCoverageAllRecCase);
		} else {
			Map<IRecursiveDefinitionCase, CaseEntry> baseEntries = new LinkedHashMap<IRecursiveDefinitionCase, CaseEntry>();
			Map<IRecursiveDefinitionCase, CaseEntry> inductiveEntries = new LinkedHashMap<IRecursiveDefinitionCase, CaseEntry>();
			for (IRecursiveDefinitionCase defcase : mappedCases.keySet()) {
				CaseEntry entry = mappedCases.get(defcase);
				Expression caseExpression = entry.caseExpression;
				boolean isBase = true;
				for (FreeIdentifier ident : caseExpression.getFreeIdentifiers()) {
					if (ident.getType().equals(caseExpression.getType())) {
						isBase = false;
						inductiveEntries.put(defcase, entry);
						break;
					}
				}
				if (isBase) {
					baseEntries.put(defcase, entry);
				}
			}
			// need to infer type
			Type type = null;
			for (IRecursiveDefinitionCase defCase : baseEntries.keySet()) {
				if (!defCase.hasFormula()) {
					createProblemMarker(defCase,
							TheoryAttributes.FORMULA_ATTRIBUTE,
							TheoryGraphProblem.MissingFormulaAttrError);
					continue;
				}
				CaseEntry caseEntry = baseEntries.get(defCase);
				Formula<?> defFormula = ModulesUtils.parseAndCheckFormula(
						defCase, factory, caseEntry.localTypeEnvironment, this);
				if (defFormula != null) {
					if (MathExtensionsUtilities
							.isExpressionOperator(operatorInformation
									.getFormulaType())) {
						if (defFormula instanceof Expression) {
							Type expType = ((Expression) defFormula).getType();
							if (type == null) {
								type = expType;
								operatorInformation.setResultantType(expType);
								operatorInformation.setWdCondition(MathExtensionsUtilities.BTRUE);
							}
							if (!expType.equals(type)) {
								setError();
								createProblemMarker(
										defCase,
										TheoryAttributes.FORMULA_ATTRIBUTE,
										TheoryGraphProblem.TypeMissmatchOfRecDef);
								continue;
							}
							ISCRecursiveDefinitionCase scDefCase = recursiveOperatorDefinition
									.getRecursiveDefinitionCase(defCase
											.getElementName());
							scDefCase.create(null, monitor);
							scDefCase.setExpression(caseEntry.caseExpression,
									monitor);
							scDefCase.setSCFormula(defFormula, monitor);
							scDefCase.setSource(defCase, monitor);
							actualDefinition.addRecursiveCase(
									caseEntry.caseExpression, defFormula);
						} else {
							setError();
							createProblemMarker(operatorDefinition,
									TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
									TheoryGraphProblem.OperatorDefNotExpError,
									operatorDefinition.getLabel());
						}
					} else {
						if (defFormula instanceof Predicate) {
							ISCRecursiveDefinitionCase scDefCase = recursiveOperatorDefinition
									.getRecursiveDefinitionCase(defCase
											.getElementName());
							scDefCase.create(null, monitor);
							scDefCase.setExpression(caseEntry.caseExpression,
									monitor);
							scDefCase.setSCFormula(defFormula, monitor);
							scDefCase.setSource(defCase, monitor);
							actualDefinition.addRecursiveCase(
									caseEntry.caseExpression, defFormula);
						} else {
							setError();
							createProblemMarker(operatorDefinition,
									TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
									TheoryGraphProblem.OperatorDefNotPredError,
									operatorDefinition.getLabel());
						}
					}
				} else {
					setError();
				}

			}
			if (!operatorInformation.hasError()) {
				FormulaFactory localFactory = factory.withExtensions(
						MathExtensionsUtilities.singletonExtension(
								operatorInformation.getExtension(operatorDefinition, factory)));
				for (IRecursiveDefinitionCase defCase : inductiveEntries.keySet()){
					if (!defCase.hasFormula()) {
						createProblemMarker(defCase,
								TheoryAttributes.FORMULA_ATTRIBUTE,
								TheoryGraphProblem.MissingFormulaAttrError);
						continue;
					}
					CaseEntry caseEntry = inductiveEntries.get(defCase);
					Formula<?> defFormula = ModulesUtils.parseAndCheckFormula(
							defCase, localFactory, caseEntry.localTypeEnvironment, this);
					if (defFormula != null) {
						if (MathExtensionsUtilities
								.isExpressionOperator(operatorInformation
										.getFormulaType())) {
							if (defFormula instanceof Expression) {
								Type expType = ((Expression) defFormula).getType();
								if (!expType.equals(type)) {
									setError();
									createProblemMarker(
											defCase,
											TheoryAttributes.FORMULA_ATTRIBUTE,
											TheoryGraphProblem.TypeMissmatchOfRecDef);
									continue;
								}
								ISCRecursiveDefinitionCase scDefCase = recursiveOperatorDefinition
										.getRecursiveDefinitionCase(defCase
												.getElementName());
								scDefCase.create(null, monitor);
								scDefCase.setExpression(caseEntry.caseExpression,
										monitor);
								scDefCase.setSCFormula(defFormula, monitor);
								scDefCase.setSource(defCase, monitor);
								actualDefinition.addRecursiveCase(
										caseEntry.caseExpression, defFormula);
							} else {
								setError();
								createProblemMarker(operatorDefinition,
										TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
										TheoryGraphProblem.OperatorDefNotExpError,
										operatorDefinition.getLabel());
							}
						} else {
							if (defFormula instanceof Predicate) {
								ISCRecursiveDefinitionCase scDefCase = recursiveOperatorDefinition
										.getRecursiveDefinitionCase(defCase
												.getElementName());
								scDefCase.create(null, monitor);
								scDefCase.setExpression(caseEntry.caseExpression,
										monitor);
								scDefCase.setSCFormula(defFormula, monitor);
								scDefCase.setSource(defCase, monitor);
								actualDefinition.addRecursiveCase(
										caseEntry.caseExpression, defFormula);
							} else {
								setError();
								createProblemMarker(operatorDefinition,
										TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
										TheoryGraphProblem.OperatorDefNotPredError,
										operatorDefinition.getLabel());
							}
						}
					} else {
						setError();
					}
				}
			}
		}
	}

	private void setError() {
		operatorInformation.setHasError();
	}

	static class CaseEntry {
		Expression caseExpression;
		ITypeEnvironment localTypeEnvironment;

		public CaseEntry(Expression caseExpression,
				ITypeEnvironment localTypeEnvironment) {
			this.caseExpression = caseExpression;
			this.localTypeEnvironment = localTypeEnvironment;
		}
	}
}
