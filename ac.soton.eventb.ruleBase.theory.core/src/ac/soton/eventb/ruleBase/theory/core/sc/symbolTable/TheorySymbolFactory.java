/*******************************************************************************
 * Copyright (c) 2008-2009 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     University of Southampton - accommodate for theory
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc.symbolTable;

import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.ISCSet;
import ac.soton.eventb.ruleBase.theory.core.ISCVariable;
import ac.soton.eventb.ruleBase.theory.core.sc.TheoryGraphProblem;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ILabelSymbolInfo;

/**
 * @author maamria
 * 
 */
public final class TheorySymbolFactory {

	private static class LocalTheorySetSymbolProblem extends
			TheorySetSymbolProblem {

		public LocalTheorySetSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheorySetNameConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheorySetNameConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class LocalVariableSymbolProblem extends
			VariableSymbolProblem {

		public LocalVariableSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryVariableNameConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryVariableNameConflictWarning,
					symbolInfo.getSymbol());
		}

	}

	private static class RewriteRuleSymbolProblem implements ISymbolProblem {

		public RewriteRuleSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryLabelConflictError, symbolInfo
							.getSymbol());

		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoryLabelConflictWarning, symbolInfo
							.getSymbol());

		}

	}

	private abstract static class TheorySetSymbolProblem implements
			ITypedSymbolProblem {

		public TheorySetSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedTheorySetError;
		}

	}

	private abstract static class VariableSymbolProblem implements
			ITypedSymbolProblem {

		public VariableSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedTheoryVariableError;
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
					TheoryGraphProblem.RhsLabelConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RhsLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}
	private static TheorySymbolFactory factory = new TheorySymbolFactory();
	private static LocalTheorySetSymbolProblem localTheorySetSymbolProblem = new LocalTheorySetSymbolProblem();
	private static LocalVariableSymbolProblem localVariableSymbolProblem = new LocalVariableSymbolProblem();

	private static RewriteRuleSymbolProblem rewriteRuleSymbolProblem = new RewriteRuleSymbolProblem();
	private static RhsSymbolProblem rhsSymbolProblem = new RhsSymbolProblem();

	private TheorySymbolFactory() {
		// singleton
	}

	public ILabelSymbolInfo makeLocalRewriteRule(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo(symbol, ISCRewriteRule.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, rewriteRuleSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalVariable(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCVariable.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localVariableSymbolProblem);
	}

	public IIdentifierSymbolInfo makeTheorySet(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCSet.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localTheorySetSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalRHS(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCRewriteRuleRightHandSide.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				rhsSymbolProblem);
	}
	
	public static TheorySymbolFactory getInstance() {
		return factory;
	}

}
