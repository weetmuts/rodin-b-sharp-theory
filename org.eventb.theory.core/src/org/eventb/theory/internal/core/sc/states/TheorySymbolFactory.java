/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISymbolInfo;
import org.eventb.internal.core.sc.symbolTable.ISymbolProblem;
import org.eventb.internal.core.sc.symbolTable.ITypedSymbolProblem;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.LabelSymbolInfo;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 *         TODO more symbols needed here
 * 
 */
@SuppressWarnings("restriction")
public class TheorySymbolFactory {

	private static TheorySymbolFactory factory = new TheorySymbolFactory();

	private static LocalTypeParameterSymbolProblem localTypeParameterSymbolProblem = new LocalTypeParameterSymbolProblem();
	private static OperatorIDSymbolProblem operatorIDSymbolProblem = new OperatorIDSymbolProblem();
	private static LocalOperatorArgumentSymbolProblem operatorArgumentSymbolProblem = new LocalOperatorArgumentSymbolProblem();
	private static RulesBlockSymbolProblem rulesBlockSymbolProblem = new RulesBlockSymbolProblem();
	private static TheoremSymbolProblem theoremSymbolProblem = new TheoremSymbolProblem();
	private static LocalMetavariableSymbolProblem localMetavariableSymbolProblem = new LocalMetavariableSymbolProblem();
	private static RewriteRuleSymbolProblem rewriteRuleSymbolProblem = new RewriteRuleSymbolProblem();
	private static InferenceRuleSymbolProblem inferenceRuleSymbolProblem = new InferenceRuleSymbolProblem();
	private static RhsSymbolProblem rhsSymbolProblem = new RhsSymbolProblem();

	public ILabelSymbolInfo makeLocalRewriteRule(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo(symbol, ISCRewriteRule.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, rewriteRuleSymbolProblem);
	}
	
	public ILabelSymbolInfo makeLocalInferenceRule(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo( symbol, ISCInferenceRule.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, inferenceRuleSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalRulesBlock(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo(symbol, ISCProofRulesBlock.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, rulesBlockSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalTheorem(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCTheorem.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				theoremSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalOperatorArgument(String name,
			boolean persistent, IIdentifierElement element, String parentName) {
		return new IdentifierSymbolInfo(name, ISCOperatorArgument.ELEMENT_TYPE,
				persistent, element, EventBAttributes.IDENTIFIER_ATTRIBUTE,
				parentName, operatorArgumentSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalOperator(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo(symbol,
				ISCNewOperatorDefinition.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				operatorIDSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalTypeParameter(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCTypeParameter.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localTypeParameterSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalMetavariable(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCMetavariable.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localMetavariableSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalRHS(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol,
				ISCRewriteRuleRightHandSide.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				rhsSymbolProblem);
	}

	public static TheorySymbolFactory getInstance() {
		return factory;
	}

	private static class LocalTypeParameterSymbolProblem extends
			TypeParameterSymbolProblem {

		public LocalTypeParameterSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryTypeParameterNameConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryTypeParameterNameConflictWarning,
					symbolInfo.getSymbol());
		}

	}

	private static class LocalMetavariableSymbolProblem extends
			MetavariableSymbolProblem {

		public LocalMetavariableSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryMetaVarNameConflict,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryMetaVarNameConflict,
					symbolInfo.getSymbol());
		}

	}

	private static class LocalOperatorArgumentSymbolProblem extends
			OperatorArgumentSymbolProblem {

		public LocalOperatorArgumentSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.OperatorArgumentNameConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.OperatorArgumentNameConflictWarning,
					symbolInfo.getSymbol());
		}

	}

	private static class InferenceRuleSymbolProblem implements ISymbolProblem {

		public InferenceRuleSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.InferenceRuleLabelConflictError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.InferenceRuleLabelConflictWarning,
					symbolInfo.getSymbol());

		}
	}
	private static class RewriteRuleSymbolProblem implements ISymbolProblem {

		public RewriteRuleSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryLabelConflictError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryLabelConflictWarning,
					symbolInfo.getSymbol());

		}

	}

	private static class RulesBlockSymbolProblem implements ISymbolProblem {

		public RulesBlockSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RulesBlockLabelProblemError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RulesBlockLabelProblemWarning,
					symbolInfo.getSymbol());

		}

	}

	private static class TheoremSymbolProblem implements ISymbolProblem {

		public TheoremSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoremLabelProblemError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoremLabelProblemWarning,
					symbolInfo.getSymbol());

		}

	}

	private static class OperatorIDSymbolProblem implements ISymbolProblem {

		public OperatorIDSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.OperatorIDConflictError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.OperatorIDConflictWarning,
					symbolInfo.getSymbol());

		}

	}

	private abstract static class TypeParameterSymbolProblem implements
			ITypedSymbolProblem {

		public TypeParameterSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedTheoryTypeParameterError;
		}

	}

	private abstract static class MetavariableSymbolProblem implements
			ITypedSymbolProblem {

		public MetavariableSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedMetavariableError;
		}

	}

	private abstract static class OperatorArgumentSymbolProblem implements
			ITypedSymbolProblem {

		public OperatorArgumentSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedOperatorArgumentError;
		}

	}

	private static class RhsSymbolProblem implements ISymbolProblem {

		public RhsSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RhsLabelConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RhsLabelConflictWarning,
					symbolInfo.getSymbol());
		}

	}
}
