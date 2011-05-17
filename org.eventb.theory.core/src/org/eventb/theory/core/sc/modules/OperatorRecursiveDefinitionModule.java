/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.LinkedHashSet;
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
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.core.sc.states.IOperatorInformation.RecursiveDefinition;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
public class OperatorRecursiveDefinitionModule extends SCProcessorModule{

	private static final IModuleType<OperatorRecursiveDefinitionModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID+ ".operatorRecursiveDefinitionModule");
	
	private IOperatorInformation operatorInformation;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOperatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		IRecursiveOperatorDefinition[] definitions = newOperatorDefinition.getRecursiveOperatorDefinitions();
		if (definitions.length == 1){
			processRecursiveDefinitions(definitions, newOperatorDefinition, scNewOperatorDefinition, repository, monitor);
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
				if (!typeEnvironment.contains(inductiveArgument) || 
						!(typeEnvironment.getType(inductiveArgument) instanceof ParametricType)) {
					setError();
					createProblemMarker(definition, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE, 
							TheoryGraphProblem.ArgumentNotExistOrNotParametric, inductiveArgument);
				}
				else {
					IOperatorArgument operatorArgument = null;
					for (IOperatorArgument arg : operatorInformation.getOperatorArguments()){
						if (arg.getArgumentName().equals(inductiveArgument)){
							operatorArgument = arg;
							break;
						}
					}
					if (operatorArgument == null){
						setError();
						throw new IllegalStateException("Illegal state : operator argument not found ("+inductiveArgument);
					}
					RecursiveDefinition actualDefinition = new RecursiveDefinition(operatorArgument);
					ParametricType inductiveType = (ParametricType) operatorArgument.getArgumentType();
					// Check this for safety
					IDatatype datatype = (IDatatype) inductiveType.getExprExtension().getOrigin();
					IRecursiveDefinitionCase[] cases = definition.getRecursiveDefinitionCases();
					if (cases.length < 1){
						setError();
						createProblemMarker(definition, TheoryGraphProblem.NoRecCasesError);
					}
					else {
						processCases(cases, actualDefinition, datatype);
					}
				}
			}
		}
	}

	protected void processCases(IRecursiveDefinitionCase[] cases,
			RecursiveDefinition actualDefinition, IDatatype datatype) throws CoreException {
		
		Set<IExpressionExtension> coveredConstructors = new LinkedHashSet<IExpressionExtension>();
		Set<IExpressionExtension> datatypeConstructors = datatype.getConstructors();
		
		for (IRecursiveDefinitionCase definitionCase : cases){
			if (!definitionCase.hasExpressionString()){
				setError();
				createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE, 
						GraphProblem.ExpressionUndefError);
			}
			else {
				String caseString = definitionCase.getExpressionString();
				IParseResult parseResult = factory.parseExpression(caseString, LanguageVersion.V2, null);
				if (CoreUtilities.issueASTProblemMarkers(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
						parseResult, this)){
					setError();
				}
				else {
					Expression caseExpression = parseResult.getParsedExpression();
					if (!(caseExpression instanceof ExtendedExpression) ||
							!(datatypeConstructors.contains(((ExtendedExpression)caseExpression).getExtension()))){
						setError();
						createProblemMarker(definitionCase, 
								EventBAttributes.EXPRESSION_ATTRIBUTE, 
								TheoryGraphProblem.InductiveCaseNotAppropriateExp, caseExpression.toString());
					}
					else {
						ExtendedExpression constructorExp = (ExtendedExpression) caseExpression;
						Expression[] childExpressions = constructorExp.getChildExpressions();
						boolean error = false;
						for (Expression childExpression : childExpressions){
							if (!(childExpression instanceof FreeIdentifier)){
								setError();
								error = true;
								createProblemMarker(definitionCase, 
										EventBAttributes.EXPRESSION_ATTRIBUTE,
										TheoryGraphProblem.ConsArgNotIdentInCase, 
										childExpression.toString());
							}
							else{
								if (operatorInformation.isAllowedIdentifier((FreeIdentifier)childExpression)){
									setError();
									error = true;
									createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE, 
											TheoryGraphProblem.IdentCannotBeUsedAsConsArg, 
											((FreeIdentifier)childExpression).toString());
								}
							}
						}
						if (error){
							continue;
						}
						FreeIdentifier inductiveArg = actualDefinition.getOperatorArgument().toFreeIdentifier(factory);
						Predicate predicate = factory.makeRelationalPredicate(Formula.EQUAL, inductiveArg, constructorExp, null);
						ITypeCheckResult tcResult = predicate.typeCheck(typeEnvironment);
						if(tcResult.hasProblem()){
							createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE, 
									TheoryGraphProblem.UnableToTypeCase);
						}
						else {
							ITypeEnvironment localTypeEnvironment = typeEnvironment.clone();
							localTypeEnvironment.addAll(tcResult.getInferredEnvironment());
							operatorInformation.getExtension(sourceOfExtension, factory)
						}
						
					}
				}
			}
		}
	}
	
	private void setError(){
		operatorInformation.setHasError();
	}

}
