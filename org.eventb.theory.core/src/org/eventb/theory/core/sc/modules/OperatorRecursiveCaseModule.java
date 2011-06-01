package org.eventb.theory.core.sc.modules;

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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.RecursiveDefinitionInfo;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorRecursiveCaseModule extends SCProcessorModule {

	private static final IModuleType<OperatorRecursiveCaseModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorRecursiveCaseModule");
	
	private FormulaFactory factory;
	private RecursiveDefinitionInfo recursiveDefinitionInfo;
	private ITypeEnvironment typeEnvironment;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRecursiveOperatorDefinition definition = (IRecursiveOperatorDefinition) element;
		ISCRecursiveOperatorDefinition scDefinition = (ISCRecursiveOperatorDefinition) target;
		IRecursiveDefinitionCase[] recursiveDefinitionCases = definition
				.getRecursiveDefinitionCases();
		if (recursiveDefinitionCases.length > 0) {
			process(recursiveDefinitionCases, scDefinition, repository, monitor);
		}
	}
	
	protected void process(IRecursiveDefinitionCase[] origins,
			ISCRecursiveOperatorDefinition target, ISCStateRepository repository,
			IProgressMonitor monitor)  throws CoreException{
		for (IRecursiveDefinitionCase definitionCase : origins) {
			if (!definitionCase.hasExpressionString()) {
				createProblemMarker(definitionCase,
						EventBAttributes.EXPRESSION_ATTRIBUTE,
						GraphProblem.ExpressionUndefError);
				recursiveDefinitionInfo.setNotAccurate();
				continue;
			} else {
				String caseString = definitionCase.getExpressionString();
				IParseResult parseResult = factory.parseExpression(caseString,
						LanguageVersion.V2, null);
				if (CoreUtilities.issueASTProblemMarkers(definitionCase,
						EventBAttributes.EXPRESSION_ATTRIBUTE, parseResult,
						this)) {
					recursiveDefinitionInfo.setNotAccurate();
					continue;
				} else {
					Expression caseExpression = parseResult
							.getParsedExpression();
					if (!(caseExpression instanceof ExtendedExpression) || 
							!(recursiveDefinitionInfo.isConstructor(((ExtendedExpression) caseExpression).getExtension()))) {
						createProblemMarker(
								definitionCase,
								EventBAttributes.EXPRESSION_ATTRIBUTE,
								TheoryGraphProblem.InductiveCaseNotAppropriateExp,
								caseExpression.toString());
						recursiveDefinitionInfo.setNotAccurate();
						continue;
					} else {
						ExtendedExpression constructorExp = (ExtendedExpression) caseExpression;
						Expression[] childExpressions = constructorExp
								.getChildExpressions();
						for (Expression childExpression : childExpressions) {
							if (!(childExpression instanceof FreeIdentifier)) {
								recursiveDefinitionInfo.setNotAccurate();
								createProblemMarker(
										definitionCase,
										EventBAttributes.EXPRESSION_ATTRIBUTE,
										TheoryGraphProblem.ConsArgNotIdentInCase,
										childExpression.toString());
								continue;
							} else {
								// identifier is used before
								if (typeEnvironment.contains(((FreeIdentifier) childExpression).getName())) {
									recursiveDefinitionInfo.setNotAccurate();
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
						IExpressionExtension extension = ((ExtendedExpression) caseExpression).getExtension();
						FreeIdentifier inductiveArg = recursiveDefinitionInfo.getInductiveArgument();
						Predicate predicate = factory.makeRelationalPredicate(
								Formula.EQUAL, inductiveArg, constructorExp,null);
						ITypeCheckResult tcResult = predicate.typeCheck(typeEnvironment);
						if (tcResult.hasProblem()) {
							createProblemMarker(definitionCase,
									EventBAttributes.EXPRESSION_ATTRIBUTE,
									TheoryGraphProblem.UnableToTypeCase);
							continue;
						} else {
							if (recursiveDefinitionInfo.isCoveredConstuctor(extension)) {
								recursiveDefinitionInfo.setNotAccurate();
								createProblemMarker(
										definitionCase,
										EventBAttributes.EXPRESSION_ATTRIBUTE,
										TheoryGraphProblem.RecCaseAlreadyCovered);
								continue;
							} else {
								ITypeEnvironment localTypeEnvironment = typeEnvironment.clone();
								localTypeEnvironment.addAll(tcResult.getInferredEnvironment());
								// we do this because it's type checked
								constructorExp = (ExtendedExpression) ((RelationalPredicate) predicate).getRight();
								recursiveDefinitionInfo.addEntry(definitionCase, constructorExp, localTypeEnvironment);
							}
						}

					}
				}
			}
		}
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		recursiveDefinitionInfo = (RecursiveDefinitionInfo) repository.getState(RecursiveDefinitionInfo.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		recursiveDefinitionInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
