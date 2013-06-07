package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorsLabelSymbolTable;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

public class AxiomaticOperatorFilterModule extends SCFilterModule {

	private final IModuleType<AxiomaticOperatorFilterModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".axiomaticOperatorFilterModule");

	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	@SuppressWarnings("restriction")
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IAxiomaticOperatorDefinition opDef = (IAxiomaticOperatorDefinition) element;
		ITheoryRoot theoryRoot = opDef.getAncestor(ITheoryRoot.ELEMENT_TYPE);
		String opLabel = opDef.getLabel();
		// check against the symbol table for operator labels
		OperatorsLabelSymbolTable labelSymbolTable = (OperatorsLabelSymbolTable) repository
				.getState(OperatorsLabelSymbolTable.STATE_TYPE);
		ILabelSymbolInfo symbolInfo = labelSymbolTable.getSymbolInfo(opLabel);
		if (symbolInfo == null) {
			return false;
		}
		// check ID is unique
		String operatorId = AstUtilities.makeOperatorID(theoryRoot.getComponentName(), opLabel);
		if (!AstUtilities.checkOperatorID(operatorId, factory)) {
			createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorIDExistsError, opLabel);
			return false;
		}
		String syntax = opLabel;
		// check syntax
		if (typeEnvironment.contains(syntax)) {
			createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorSynIsATypeParError,
					syntax);
			return false;
		}
		if (!FormulaFactory.checkSymbol(syntax) || syntax.contains(" ")) {
			createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorInvalidSynError, syntax);
			return false;
		}
		if (!AstUtilities.checkOperatorSyntaxSymbol(syntax, factory)) {
			createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorSynExistsError, syntax);
			return false;
		}
		if (!opDef.hasFormulaType()) {
			createProblemMarker(opDef, TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
					TheoryGraphProblem.OperatorFormTypeMissingError, opLabel);
			return false;
		}
		FormulaType formType = opDef.getFormulaType();
		symbolInfo.setAttributeValue(TheoryAttributes.FORMULA_TYPE_ATTRIBUTE, AstUtilities.isExpressionOperator(formType));
		if (!opDef.hasNotationType()) {
			createProblemMarker(opDef, TheoryAttributes.NOTATION_TYPE_ATTRIBUTE,
					TheoryGraphProblem.OperatorNotationTypeMissingError, opLabel);
			return false;
		}
		Notation notation = opDef.getNotationType();
		symbolInfo.setAttributeValue(TheoryAttributes.NOTATION_TYPE_ATTRIBUTE, notation.toString());
		// check against use of POSTFIX as it is not supported yet
		if (notation.equals(Notation.POSTFIX)) {
			createProblemMarker(opDef, TheoryAttributes.NOTATION_TYPE_ATTRIBUTE, TheoryGraphProblem.OperatorCannotBePostfix);
			return false;
		}
		// Infix predicates not supported
		if (formType.equals(FormulaType.PREDICATE) && notation.equals(Notation.INFIX)) {
			createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorPredOnlyPrefix);
			return false;
		}
		if (!opDef.hasAssociativeAttribute()) {
			createProblemMarker(opDef, TheoryAttributes.ASSOCIATIVE_ATTRIBUTE, TheoryGraphProblem.OperatorAssocMissingError,
					opLabel);
			return false;
		}
		symbolInfo.setAttributeValue(TheoryAttributes.ASSOCIATIVE_ATTRIBUTE, opDef.isAssociative());
		if (!opDef.hasCommutativeAttribute()) {
			createProblemMarker(opDef, TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
					TheoryGraphProblem.OperatorCommutMissingError, opLabel);
			return false;
		}
		symbolInfo.setAttributeValue(TheoryAttributes.COMMUTATIVE_ATTRIBUTE, opDef.isCommutative());
		if (formType.equals(FormulaType.EXPRESSION) & !opDef.hasType()) {
			createProblemMarker(opDef, TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.TypeAttrMissingError,
					opDef.getLabel());
			return false;
		} else if (formType.equals(FormulaType.PREDICATE) && opDef.hasType() && opDef.getType().length()>0) {
			createProblemMarker(opDef, TheoryAttributes.TYPE_ATTRIBUTE,
					TheoryGraphProblem.AxiomaticPredicateOpDoesNotReqTypeWarn, opDef.getLabel());
		}
		if (formType.equals(FormulaType.EXPRESSION)) {
			IParseResult result = factory.parseType(opDef.getType(), LanguageVersion.V2);
			if (CoreUtilities.issueASTProblemMarkers(opDef, TheoryAttributes.TYPE_ATTRIBUTE, result, this)) {
				return false;
			}
			Type opType = result.getParsedType();
			symbolInfo.setAttributeValue(TheoryAttributes.TYPE_ATTRIBUTE, opType.toString());
		}
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
	}

	@Override
	public void endModule(ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		super.endModule(repository, monitor);
	}
}