/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
public class TheoryRewriteRuleFilterModule extends SCFilterModule{

	private final IModuleType<TheoryRewriteRuleFilterModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryRewriteRuleFilterModule");
	
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRule rule = (IRewriteRule) element;
		// Check left hand side
		if (!rule.hasFormula()) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSUndefError);
			return false;
		}
		Formula<?> lhsForm = parseAndCheckPatternFormula(rule, repository.getFormulaFactory(), repository.getTypeEnvironment());
		
		if(lhsForm == null){
			return false;
		}
		if(lhsForm instanceof FreeIdentifier){
			createProblemMarker(rule, FORMULA_ATTRIBUTE,
					TheoryGraphProblem.LHSIsIdentErr);
			return false;
		}
		WDStrictChecker checker = new WDStrictChecker();
		lhsForm.accept(checker);
		if (!checker.wdStrict){
			createProblemMarker(rule, FORMULA_ATTRIBUTE, 
					TheoryGraphProblem.LHS_IsNotWDStrict);
			return false;
		}
		if(!CoreUtilities.checkAgainstTypeParameters(rule, lhsForm, repository.getTypeEnvironment(), this)){
			return false;
		}
		// 
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
	
	/**
	 * Parses and type checks the formula occurring as an attribute to the given
	 * element. The formula may contain predicate variables.
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @return the parsed formula
	 * @throws CoreException
	 */
	protected Formula<?> parseAndCheckPatternFormula(
			IFormulaElement element, FormulaFactory ff,
			ITypeEnvironment typeEnvironment)
			throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		IParseResult result = ff.parsePredicatePattern(form, V2, null);
		if (result.hasProblem()) {
			result = ff.parseExpressionPattern(form, V2, null);
			if (CoreUtilities.issueASTProblemMarkers(element, attributeType, result, this)) {
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
		if (CoreUtilities.issueASTProblemMarkers(element, attributeType, tcResult, this)) {
			return null;
		}
		return formula;
	}
	
	static final class WDStrictChecker extends DefaultVisitor{
		
		private boolean wdStrict = true;
		
		/**
		 * Returns whether the check reached the conclusion that the visited formula may not be WD strict.	
		 * @return whether the visited formula is WD strict
		 */
		public boolean isWdStrict() {
			return wdStrict;
		}

		@Override
		public boolean enterEXISTS(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterFORALL(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLAND(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLOR(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLIMP(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}
		
		@Override
		public boolean enterLEQV(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}
		
	}

}
