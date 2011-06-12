package org.eventb.theory.core.sc.modules;

import java.util.Map;

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
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
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
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.core.sc.states.RecursiveDefinitionInfo;
import org.eventb.theory.core.sc.states.IOperatorInformation.RecursiveDefinition;
import org.eventb.theory.core.sc.states.IRecursiveDefinitionInfo.CaseEntry;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

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
	private IOperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRecursiveOperatorDefinition definition = (IRecursiveOperatorDefinition) element;
		ISCRecursiveOperatorDefinition scDefinition = (ISCRecursiveOperatorDefinition) target;
		IRecursiveDefinitionCase[] recursiveDefinitionCases = definition.getRecursiveDefinitionCases();
		if (recursiveDefinitionCases.length > 0) {
			processCases(recursiveDefinitionCases, scDefinition, repository, monitor);
			recursiveDefinitionInfo.makeImmutable();
			processCasesFormulae(definition, scDefinition, repository, monitor);
		}
	}
	
	protected void processCases(IRecursiveDefinitionCase[] origins,
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
	
	public void processCasesFormulae(IRecursiveOperatorDefinition definition, ISCRecursiveOperatorDefinition scDefinition,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		Map<IRecursiveDefinitionCase, CaseEntry> baseEntries = recursiveDefinitionInfo.getBaseEntries();
		Map<IRecursiveDefinitionCase, CaseEntry> inductiveEntries = recursiveDefinitionInfo.getInductiveEntries();
		if (!baseEntries.isEmpty() || !inductiveEntries.isEmpty()){
			ISCNewOperatorDefinition scParent = scDefinition.getAncestor(ISCNewOperatorDefinition.ELEMENT_TYPE);
			INewOperatorDefinition parent = definition.getAncestor(INewOperatorDefinition.ELEMENT_TYPE);
			boolean error = false;
			if (!recursiveDefinitionInfo.coveredAllConstructors()) {
				createProblemMarker(definition, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE, TheoryGraphProblem.NoCoverageAllRecCase);
				error = true;
			} else {
				boolean isExpression = MathExtensionsUtilities.isExpressionOperator(operatorInformation.getFormulaType());
				Type resultantType = null;
				RecursiveDefinition recursiveDefinition = new RecursiveDefinition(recursiveDefinitionInfo.getInductiveArgument());
				// process base cases
				for (IRecursiveDefinitionCase defCase : baseEntries
						.keySet()) {
					if (!defCase.hasFormula()) {
						createProblemMarker(defCase,TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.MissingFormulaAttrError);
						error = true;
					} else {
						CaseEntry caseEntry = baseEntries.get(defCase);
						Formula<?> formula = ModulesUtils
								.parseAndCheckFormula(defCase,factory,caseEntry.getLocalTypeEnvironment(),this);
						if (formula != null) {
							if (isExpression&& !(formula instanceof Expression)) {
								createProblemMarker(parent,TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,TheoryGraphProblem.OperatorDefNotExpError,parent.getLabel());
								error = true;
							}
							if (!isExpression
									&& !(formula instanceof Predicate)) {
								createProblemMarker(parent,TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,TheoryGraphProblem.OperatorDefNotPredError,parent.getLabel());
								error = true;
							}
							if (isExpression) {
								Expression expression = (Expression) formula;
								if (resultantType == null) {
									resultantType = expression.getType();
									operatorInformation.setResultantType(resultantType);
								}
								if (!expression.getType().equals(resultantType)){
									createProblemMarker(defCase, TheoryAttributes.FORMULA_ATTRIBUTE, 
											TheoryGraphProblem.RecOpTypeNotConsistent, resultantType, expression.getType());
									error = true;
								}
								else {
									recursiveDefinition.addRecursiveCase(caseEntry.getCaseExpression(), formula);
								}
							} else {
								recursiveDefinition.addRecursiveCase(caseEntry.getCaseExpression(), formula);
							}
						}
					}
				}
				FormulaFactory localFactory = factory.withExtensions(
						MathExtensionsUtilities.singletonExtension(operatorInformation.getInterimExtension()));
				// process inductive cases
				for (IRecursiveDefinitionCase defCase : inductiveEntries
						.keySet()) {
					if (!defCase.hasFormula()) {
						createProblemMarker(defCase,
								TheoryAttributes.FORMULA_ATTRIBUTE,
								TheoryGraphProblem.MissingFormulaAttrError);
						error = true;
					} else {
						CaseEntry caseEntry = inductiveEntries.get(defCase);
						Formula<?> formula = ModulesUtils.parseAndCheckFormula(defCase,localFactory,caseEntry.getLocalTypeEnvironment(),this);
						if (formula != null) {
							if (isExpression
									&& !(formula instanceof Expression)) {
								createProblemMarker(parent,TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,TheoryGraphProblem.OperatorDefNotExpError,parent.getLabel());
								error = true;
							}
							if (!isExpression
									&& !(formula instanceof Predicate)) {
								createProblemMarker(parent,TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,TheoryGraphProblem.OperatorDefNotPredError,parent.getLabel());
								error = true;
							}
							if (isExpression) {
								Expression expression = (Expression) formula;
								if (!expression.getType().equals(resultantType)){
									createProblemMarker(defCase, TheoryAttributes.FORMULA_ATTRIBUTE, 
											TheoryGraphProblem.RecOpTypeNotConsistent, resultantType, expression.getType());
									error = true;
								}
								else {
									recursiveDefinition.addRecursiveCase(caseEntry.getCaseExpression(), formula);
								}
							} else {
								recursiveDefinition.addRecursiveCase(caseEntry.getCaseExpression(), formula);
							}
						}
						else {
							error = true;
						}
					}
				}
				if (error || !recursiveDefinitionInfo.isAccurate()){
					operatorInformation.setHasError();
				}
				else {
					operatorInformation.setDefinition(recursiveDefinition);
					if (operatorInformation.getWdCondition() == null) {
						operatorInformation.addWDCondition(MathExtensionsUtilities.BTRUE);
						scParent.setPredicate(MathExtensionsUtilities.BTRUE, monitor);
						for (IRecursiveDefinitionCase defCase : baseEntries.keySet()){
							createSCCase(defCase, scDefinition, recursiveDefinition, baseEntries, monitor);
						}
						for (IRecursiveDefinitionCase defCase : inductiveEntries.keySet()){
							createSCCase(defCase, scDefinition,	recursiveDefinition,inductiveEntries,monitor);
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
		operatorInformation = (IOperatorInformation) repository.getState(IOperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		operatorInformation = null;
		factory = null;
		typeEnvironment = null;
		recursiveDefinitionInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	/**
	 * Creates the statically checked recursive definition case.
	 * @param origin the recursive case origin
	 * @param parent the SC recursive definition parent
	 * @param recursiveDefinition the recursive definition
	 * @param entries the entries
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	protected void createSCCase(IRecursiveDefinitionCase origin,
			ISCRecursiveOperatorDefinition parent,
			RecursiveDefinition recursiveDefinition,
			Map<IRecursiveDefinitionCase, CaseEntry> entries,
			IProgressMonitor monitor) throws RodinDBException {
		ISCRecursiveDefinitionCase scDefCase = parent.getRecursiveDefinitionCase(origin.getElementName());
		scDefCase.create(null, monitor);
		CaseEntry entry = entries.get(origin);
		scDefCase.setExpression(entry.getCaseExpression(), monitor);
		scDefCase.setSCFormula(recursiveDefinition.getRecursiveCases().get(entry.getCaseExpression()), monitor);
		scDefCase.setSource(origin, monitor);
	}
}
