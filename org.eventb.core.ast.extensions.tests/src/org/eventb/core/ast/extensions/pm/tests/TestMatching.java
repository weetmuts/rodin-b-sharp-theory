/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.tests.BasicAstExtTest;
import org.junit.Test;

/**
 * <p>
 * Unit tests for matching formulae.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implemented for the new matching facilities using
 *         {@link ISpecialization}.
 * @version 2.0
 * @see
 * @since 1.0
 */
public class TestMatching extends BasicAstExtTest {

	private ITypeEnvironment formulaTypeEnv;
	
	private ITypeEnvironment patternTypeEnv;
	
	/**
	 * The default setup method create the basic type environments for
	 * type-checking formulae and patterns respectively.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		formulaTypeEnv = typeEnvironment(new String[] { "S" },
				new String[] { "g", "h", "c" }, new String[] { "ℙ(ℤ × S)", "ℙ(S × S)", "S" });

		patternTypeEnv = typeEnvironment(new String[] { "S", "T" },
				new String[] { "f", "g", "h", "s", "c" }, new String[] { "ℙ(S × T)",
		"ℙ(S × T)", "ℙ(S × ℤ)", "ℙ(S)", "T" });
	}

	/**
	 * Utility method for testing a specialization.
	 * 
	 * @param msg
	 *            the debugging message.
	 * @param specialization
	 *            the specilization object under test.
	 * @param factory
	 *            the expected formular factory.
	 * @param expected
	 *            the expected string for the specialization.
	 */
	private void testSpecialisation(String msg, ISpecialization specialization,
			FormulaFactory factory, String expected) {
		if (expected == null) {
			assertNull(msg, specialization);
		} else {
			assertEquals(
					msg + ": Incorrect formula factory for specialization",
					factory, specialization.getFactory());
			String[] split = expected.split("\\s*" + Pattern.quote("|][|")
					+ "\\s*", -1);
			assertEquals(msg + ": Incorrect format for expected string", 2,
					split.length);
			
			GivenType[] specTypes = specialization.getTypes();
			FreeIdentifier[] specIdents = specialization.getFreeIdentifiers();
			PredicateVariable[] specVariables = specialization.getPredicateVariables();
			List<String> actualSubts = new ArrayList<String>(
					specTypes.length + specIdents.length + specVariables.length);
			for (GivenType givenType : specTypes) {
				actualSubts.add(givenType + "=" + specialization.get(givenType));
			}
			for (FreeIdentifier ident : specIdents) {
				actualSubts.add(ident + "=" + specialization.get(ident));
			}
			for (PredicateVariable variable : specVariables) {
				actualSubts.add(variable + "=" + specialization.get(variable));
			}
			
			String[] expectedTypeSubsts;
			if (split[0].equals("")) {
				expectedTypeSubsts = new String[0];
			} else {
				expectedTypeSubsts = split[0].split("\\s*\\|\\|\\s*");
			}
			for (String expectedTypeSubst : expectedTypeSubsts) {
				assertEquals(msg + ": Incorrect type substitution for "
						+ expectedTypeSubst, 2,
						Collections.frequency(actualSubts, expectedTypeSubst));
				actualSubts.remove(expectedTypeSubst);
				actualSubts.remove(expectedTypeSubst);
			}

			String[] expectedIdentSubsts;
			if (split[1].equals("")) {
				expectedIdentSubsts = new String[0];
			} else {
				expectedIdentSubsts = split[1].split("\\s*\\|\\|\\s*");
			}
			for (String expectedIdentSubst : expectedIdentSubsts) {
				assertEquals(msg + ": Incorrect identifier substitution for "
						+ expectedIdentSubst, 1,
						Collections.frequency(actualSubts, expectedIdentSubst));
				actualSubts.remove(expectedIdentSubst);				
			}
			
			assertTrue(msg + ": Unexpected subtitutions " + actualSubts,
					actualSubts.isEmpty());
		}
	}

	/**
	 * Utility method for testing expression matching. The input formula and
	 * pattern are type-checked using {@link #formulaTypeEnv} and
	 * {@link #patternTypeEnv} respectively.
	 * 
	 * @param msg
	 *            the debug message
	 * @param formulaString
	 *            the input formula string.
	 * @param patternString
	 *            the input pattern string
	 * @param expectedString
	 *            the expected pretty print of the resulting specialization or
	 *            <code>null</code> if the matching is expected to fail.
	 * @throws CoreException if some unexpected error occurs.
	 */
	private void testExpressionMatching(String msg, String formulaString,
			String patternString, String expectedString) throws CoreException {
		Formula<?> formula = expression(formulaString);
		formula.typeCheck(formulaTypeEnv);
		Formula<?> pattern = expression(patternString);
		pattern.typeCheck(patternTypeEnv);
		testMatching(msg, formula, pattern, expectedString);
	}

	/**
	 * Utility method for testing predicate matching. The input formula and
	 * pattern are type-checked using {@link #formulaTypeEnv} and
	 * {@link #patternTypeEnv} respectively.
	 * 
	 * @param msg
	 *            the debug message
	 * @param formulaString
	 *            the input formula string.
	 * @param patternString
	 *            the input pattern string
	 * @param expectedString
	 *            the expected pretty print of the resulting specialization or
	 *            <code>null</code> if the matching is expected to fail.
	 * @throws CoreException if some unexpected error occurs.
	 */
	private void testPredicateMatching(String msg, String formulaString,
			String patternString, String expectedString) throws CoreException {
		Formula<?> formula = predicate(formulaString);
		formula.typeCheck(formulaTypeEnv);
		Formula<?> pattern = predicate(patternString);
		pattern.typeCheck(patternTypeEnv);
		testMatching(msg, formula, pattern, expectedString);
	}

	/**
	 * Utility method for testing matching between an input formula and pattern. 
	 *  
	 * @param msg the debug message.
	 * @param formula the input formula
	 * @param pattern the input pattern
	 * @param expectedString
	 *            the expected pretty print of the resulting specialization or
	 *            <code>null</code> if the matching is expected to fail.
	 */
	private void testMatching(String msg, Formula<?> formula, Formula<?> pattern,
			String expectedString) {
		ISpecialization specialization = formula.getFactory().makeSpecialization();
		specialization = Matcher.match(specialization, formula, pattern);
		if (expectedString == null) {
			assertNull(msg + ": Does not expect a match", specialization);
		}
		else {
			assertNotNull(msg + ": Expect a match", specialization);
			testSpecialisation(msg, specialization, formula.getFactory(),
					expectedString);
		}		
	}

	/**************************************************************************
	 * (BEGIN) Test Atomic Expression Matcher                                 *
	 **************************************************************************/

	/**
	 * Tests the matcher for associative expressions.
	 * 
	 * @throws CoreException
	 *             if some unexpected error occurs.
	 */
	@Test
	public void test_AssociativeExpressionMatcher() throws CoreException {
		// {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
//		testExpressionMatching("Union matches", "{1} ∪ a ∪ b", "{x} ∪ s", "S=ℤ || S=ℤ || x=1 || s=a ∪ b");
		
		testExpressionMatching("Forward composition matches", "g;{y↦c}", "f;{x↦c}", "S=ℤ || T=S |][| x=y || f=g || c=c");
		testExpressionMatching("Forward composition does not match", "g;{y↦c}", "h;{x↦c}", null);
		testExpressionMatching("Forward composition matches", "g;h;{y↦c}", "f;{x↦c}", "S=ℤ || T=S |][| x=y || f=g;h || c=c");

		testExpressionMatching("Forward composition matches", "{c↦y};g", "{c↦x};f", "S=ℤ || T=S |][| x=y || f=g || c=c");
		testExpressionMatching("Forward composition matches", "{c↦y};g", "{c↦x};h", null);
		testExpressionMatching("Forward composition matches", "{c↦y};g;h", "{c↦x};f", "S=ℤ || T=S |][| x=y || f=g;h || c=c");

		
	}
	
	/**
	 * Tests the matcher for atomic expressions. An atomic expression should only
	 * match with itself. Polymorphic atomic expressions can only be matched if
	 * their types are matches.
	 * 
	 * @throws CoreException
	 *             if some unexpected error occurs.
	 */
	@Test
	public void test_AtomicExpressionMatching() throws CoreException {
		// {INTEGER, NATURAL, NATURAL1, BOOL, TRUE, FALSE, EMPTYSET, KPRED,
		// KSUCC, KPRJ1_GEN, KPRJ2_GEN, KID_GEN}
		testExpressionMatching("ℤ matches ℤ", "ℤ", "ℤ", " |][| ");
		testExpressionMatching("ℤ does not match ℕ", "ℤ", "ℕ", null);
		testExpressionMatching("ℤ does not match ℕ1", "ℤ", "ℕ1", null);
		testExpressionMatching("ℤ does not match BOOL", "ℤ", "BOOL", null);
		testExpressionMatching("ℤ does not match TRUE", "ℤ", "TRUE", null);
		testExpressionMatching("ℤ does not match FALSE", "ℤ", "FALSE", null);
		testExpressionMatching("ℤ does not match ∅", "∅ ⦂ ℙ(ℤ)", "ℕ", null);
		testExpressionMatching("ℤ does not match prj1", "ℤ",
				"prj1 ⦂ ℙ(ℤ × BOOL × ℤ)", null);
		testExpressionMatching("ℤ does not match prj2", "ℤ",
				"prj2 ⦂ ℙ(ℤ × BOOL × BOOL)", null);
		testExpressionMatching("ℤ does not match id", "ℤ", "id ⦂ ℙ(ℤ × ℤ)",
				null);

		testExpressionMatching("ℕ matches ℕ", "ℕ", "ℕ", " |][| ");
		testExpressionMatching("ℕ1 matches ℕ1", "ℕ1", "ℕ1", " |][| ");
		testExpressionMatching("BOOL matches BOOL", "BOOL", "BOOL", " |][| ");
		testExpressionMatching("TRUE matches TRUE", "TRUE", "TRUE", " |][| ");
		testExpressionMatching("FALSE matches FALSE", "FALSE", "FALSE", " |][| ");
		testExpressionMatching("∅ (of integers) matches ∅ (of integers)",
				"∅ ⦂ ℙ(ℤ)", "∅ ⦂ ℙ(ℤ)", " |][| ");
		testExpressionMatching(
				"∅ (of integers) does not match ∅ (of booleans)", "∅ ⦂ ℙ(ℤ)",
				"∅ ⦂ ℙ(BOOL)", null);
		testExpressionMatching("prj1 matches prj1", "prj1 ⦂ ℙ(ℤ × BOOL × ℤ)",
				"prj1 ⦂ ℙ(ℤ × BOOL × ℤ)", " |][| ");
		testExpressionMatching("prj2 matches prj2",
				"prj2 ⦂ ℙ(ℤ × BOOL × BOOL)", "prj2 ⦂ ℙ(ℤ × BOOL × BOOL)", " |][| ");
		testExpressionMatching("id (on integers) matches id (on integers)",
				"id ⦂ ℙ(ℤ × ℤ)", "id ⦂ ℙ(ℤ × ℤ)", " |][| ");
		testExpressionMatching(
				"id (on integers) does not match id (on booleans)",
				"id ⦂ ℙ(ℤ × ℤ)", "id ⦂ ℙ(BOOL × BOOL)", null);
	}

	/**************************************************************************
	 * (END) Test Atomic Expression Matcher                                   *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Binary Expression Matcher                                 *                                
	 **************************************************************************/

	/**
	 * Tests the matcher for binary expresion.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_BinaryExpressionMatching() throws CoreException {
		// {MAPSTO, REL, TREL, SREL, STREL, PFUN, TFUN, PINJ, TINJ, PSUR, TSUR,
		// TBIJ, SETMINUS, CPROD, DPROD, PPROD, DOMRES, DOMSUB, RANRES, RANSUB,
		// UPTO, MINUS, DIV, MOD, EXPN, FUNIMAGE, RELIMAGE}
		testExpressionMatching("Maps-to matches", "TRUE \u21a6 FALSE", "TRUE \u21a6 FALSE", " |][| ");
		testExpressionMatching("Maps-to does not match", "TRUE \u21a6 FALSE", "FALSE \u21a6 x", null);
		testExpressionMatching("Relation matches", "ℤ \u2194 BOOL", "ℤ \u2194 BOOL", " |][| ");
		testExpressionMatching("Relation does not match", "ℤ \u2194 BOOL", "BOOL \u2194 T", null);
		testExpressionMatching("Total relation matches", "ℤ \ue100 BOOL", "ℤ \ue100 BOOL", " |][| ");
		testExpressionMatching("Total relation does not match", "ℤ \ue100 BOOL", "BOOL \ue100 T", null);
		
		testExpressionMatching("Surjective relation matches", "ℤ \ue101 BOOL", "ℤ \ue101 BOOL", " |][| ");
		testExpressionMatching("Total surjective relation matches", "ℤ \ue102 BOOL", "ℤ \ue102 BOOL", " |][| ");
		testExpressionMatching("Partial function matches", "ℤ \u21f8 BOOL", "ℤ \u21f8 BOOL", " |][| ");
		testExpressionMatching("Total function matches", "ℤ \u2192 BOOL", "ℤ \u2192 BOOL", " |][| ");
		testExpressionMatching("Partial injection matches", "ℤ \u2914 BOOL", "ℤ \u2914 BOOL", " |][| ");
		testExpressionMatching("Total injection matches", "ℤ \u21a3 BOOL", "ℤ \u21a3 BOOL", " |][| ");
		testExpressionMatching("Partial surjection matches", "ℤ \u2900 BOOL", "ℤ \u2900 BOOL", " |][| ");
		testExpressionMatching("Total surjection matches", "ℤ \u21a0 BOOL", "ℤ \u21a0 BOOL", " |][| ");
		testExpressionMatching("Bijection matches", "ℤ \u2916 BOOL", "ℤ \u2916 BOOL", " |][| ");

		testExpressionMatching("Set-minus matches", "ℤ \u2216 ℕ", "ℤ \u2216 ℕ", " |][| ");
		testExpressionMatching("Cartesian product matches", "ℤ \u00d7 BOOL", "ℤ \u00d7 BOOL", " |][| ");
		testExpressionMatching("Direct product matches", "g \u2297 {0↦c}", "f \u2297 g", "S=ℤ || T=S  |][| f=g || g={0 ↦ c}");
		testExpressionMatching("Direct product does not match", "g \u2297 {0↦c}", "f \u2297 h", null);
		testExpressionMatching("Parallel product matches", "g \u2225 {0↦c}", "f \u2225 g", "S=ℤ || T=S  |][| f=g || g={0 ↦ c}");
		testExpressionMatching("Parallel product does not match", "g \u2225 {0↦c}", "f \u2225 h", null);
		testExpressionMatching("Domain restriction matches", "{1} \u25c1 g", "s \u25c1 f", "S=ℤ || T=S  |][| s={1} || f=g");
		testExpressionMatching("Domain restriction does not match", "{1} \u25c1 g", "s \u25c1 h", null);
		testExpressionMatching("Domain subtraction matches", "{1} \u2a64 g", "s \u2a64 f", "S=ℤ || T=S  |][| s={1} || f=g");
		testExpressionMatching("Domain subtraction does not match", "{1} \u2a64 g", "s \u2a64 h", null);
		testExpressionMatching("Range restriction matches", "g \u25b7 {c}", "f \u25b7 {c}", "S=ℤ || T=S  |][| c=c || f=g");
		testExpressionMatching("Range restriction does not match", "g \u25b7 {c}", "h \u25b7 {1}", null);
		testExpressionMatching("Range subtraction matches", "g \u2a65 {c}", "f \u2a65 {c}", "S=ℤ || T=S  |][| c=c || f=g");
		testExpressionMatching("Range subtraction does not matches", "g \u2a65 {c}", "h \u2a65 {1}", null);

		testExpressionMatching("Upto matches", "0 \u2025 1", "0 \u2025 1", " |][| ");
		testExpressionMatching("Minus matches", "0 \u2212 1", "0 \u2212 1", " |][| ");
		testExpressionMatching("Division matches", "0 \u00f7 1", "0 \u00f7 1", " |][| ");
		testExpressionMatching("Modulo matches", "0 mod 1", "0 mod 1", " |][| ");
		testExpressionMatching("Exponentiation matches", "0 \u005e 1", "0 \u005e 1", " |][| ");
		testExpressionMatching("Function application matches", "g(1)", "f(y)", "S=ℤ || T=S  |][| f=g || y=1");
		testExpressionMatching("Function application does not match", "g(1)", "h(y)", null);
		testExpressionMatching("Relational image matches", "g[s]", "f[t]", "S=ℤ || T=S  |][| t=s || f=g");
		testExpressionMatching("Relational image does not match", "g[s]", "h[t]", null);
	}
	
	/**************************************************************************
	 * (END) Test Binary Expression Matcher                                   *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Bool Expression Matcher                                   *
	 **************************************************************************/

	/**
	 * Tests the matcher for binary expressions.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_BoolExpressionMatching() throws CoreException {
		// {KBOOL}
		testExpressionMatching("Bool expression matches", "bool(1 ∈ dom(g))", "bool(y ∈ dom(f))", "S=ℤ || T=S  |][| f=g || y=1");
		testExpressionMatching("Bool expression does not match", "bool(1 ∈ dom(g))", "bool(y ∈ dom(h))", null);
	}
	
	/**************************************************************************
	 * (END) Test Bool Expression Matcher                                     *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Bound Identifier Declaration Matcher                      *
	 **************************************************************************/

	// There is no separate tests for bound identifier declaration. They are
	// tested together with quantified expressions and quantified predicates.
	
	/**************************************************************************
	 * (END) Test Bound Identifier Declaration Matcher                        *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Bound Identifier Matcher                                  *
	 **************************************************************************/

	// There is no separate tests for bound identifier. They are tested together
	// with quantified expressions and quantified predicates.
	
	/**************************************************************************
	 * (END) Test Bound Identifier Matcher                                    *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Extended Expression Matcher                               *
	 **************************************************************************/

	/**
	 * Tests the matcher for extended expressions.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_ExtendedExpressionMatching() throws CoreException {
		addExtensions(seqExtension());

		// ensure we use a compatible type environment
		formulaTypeEnv = AstUtilities.getTypeEnvironmentForFactory(formulaTypeEnv, factory);
		patternTypeEnv = AstUtilities.getTypeEnvironmentForFactory(patternTypeEnv, factory);
		
		testExpressionMatching("Sequence matches", "seq(S)", "seq(T)", "T=S  |][| ");
		testExpressionMatching("Sequence does not match", "seq(S)", "seq(ℤ)", null);
		
		// TODO Add more tests here for extended expressions.
	}
	
	/**************************************************************************
	 * (END) Test Extended Expression Matcher                                 *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Integer Literal Matcher                                   *
	 **************************************************************************/

	/**
	 * Tests the matcher for extended expressions.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_IntegerLiteralMatching() throws CoreException {
		testExpressionMatching("1 matches 1", "1", "1", " |][| ");
		testExpressionMatching("1 does not matches 0", "0", "1", null);
	}
	
	/**************************************************************************
	 * (END) Test Integer Literal Matcher                                     *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Quantified Expression Matcher                             *
	 **************************************************************************/

	/**
	 * Tests the matcher for quantified expressions.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_QuantifiedExpressionMatching() throws CoreException {
		// {QUNION, QINTER, CSET}
		testExpressionMatching("QUnion matches", "⋃x · x ∈ ℕ ∣ 0‥x", "⋃y · y ∈ ℕ ∣ 0‥y", " |][| ");
		testExpressionMatching("QUnion does not matches", "⋃x · 2∗x ∈ ℕ ∣ 0‥2∗x", "⋃y · y ∈ ℕ ∣ 0‥y", null);
		testExpressionMatching("QUnion matches", "⋃x · x ∈ ℙ(ℤ) ∣ g[x]", "⋃s · s ∈ ℙ(S) ∣ f[s]", "S=ℤ || T=S  |][| f=g");
		testExpressionMatching("QUnion does not match", "⋃x · x ∈ ℙ(ℕ) ∣ g[x]", "⋃s · s ∈ ℙ(S) ∣ f[s]", null);
		testExpressionMatching("QUnion matches", "⋃x,y · x ∈ ℕ ∣ x‥y", "⋃a,b · a ∈ ℕ ∣ a‥b", " |][| ");
		testExpressionMatching("QUnion does not matches", "⋃x,y · x ∈ ℕ ∣ x‥2∗y", "⋃a,b · a ∈ ℕ ∣ a‥b", null);
		testExpressionMatching("QUnion matches", "⋃x,y · x ∈ ℙ(ℤ) ∣ g[x∖y]", "⋃s,t · s ∈ ℙ(S) ∣ f[s∖t]", "S=ℤ || T=S  |][| f=g");
		testExpressionMatching("QUnion does not match", "⋃x,y · x ∈ ℙ(ℤ) ∣ g[x∖y]", "⋃s · s ∈ ℙ(S) ∣ f[s]", null);
		
		testExpressionMatching("QInter matches", "⋂x · x ∈ ℕ ∣ 0‥x", "⋂y · y ∈ ℕ ∣ 0‥y", " |][| ");
		testExpressionMatching("QInter does not matches", "⋂x · 2∗x ∈ ℕ ∣ 0‥2∗x", "⋂y · y ∈ ℕ ∣ 0‥y", null);
		testExpressionMatching("QInter matches", "⋂x · x ∈ ℙ(ℤ) ∣ g[x]", "⋂s · s ∈ ℙ(S) ∣ f[s]", "S=ℤ || T=S  |][| f=g");
		testExpressionMatching("QInter does not match", "⋂x · x ∈ ℙ(ℕ) ∣ g[x]", "⋂s · s ∈ ℙ(S) ∣ f[s]", null);
		testExpressionMatching("QInter matches", "⋂x,y · x ∈ ℕ ∣ x‥y", "⋂a,b · a ∈ ℕ ∣ a‥b", " |][| ");
		testExpressionMatching("QInter does not matches", "⋂x,y · x ∈ ℕ ∣ x‥2∗y", "⋂a,b · a ∈ ℕ ∣ a‥b", null);
		testExpressionMatching("QInter matches", "⋂x,y · x ∈ ℙ(ℤ) ∣ g[x∖y]", "⋂s,t · s ∈ ℙ(S) ∣ f[s∖t]", "S=ℤ || T=S  |][| f=g");
		testExpressionMatching("QInter does not match", "⋂x,y · x ∈ ℙ(ℤ) ∣ g[x∖y]", "⋂s · s ∈ ℙ(S) ∣ f[s]", null);

		testExpressionMatching("CSet matches", "{x · x ∈ ℕ ∣ x}", "{y · y ∈ ℕ ∣ y}", " |][| ");
		testExpressionMatching("CSet does not matches", "{x · 2∗x ∈ ℕ ∣ 2∗x}", "{y · y ∈ ℕ ∣ y}", null);
		testExpressionMatching("CSet matches", "{x · x ∈ ℙ(ℤ) ∣ g[x]}", "{s · s ∈ ℙ(S) ∣ f[s]}", "S=ℤ || T=S  |][| f=g");
		testExpressionMatching("CSet does not match", "{x · x ∈ ℙ(ℕ) ∣ g[x]}", "{s · s ∈ ℙ(S) ∣ f[s]}", null);
		testExpressionMatching("CSet matches", "{x,y · x ∈ ℕ ∣ x‥y}", "{a,b · a ∈ ℕ ∣ a‥b}", " |][| ");
		testExpressionMatching("CSet does not matches", "{x,y · x ∈ ℕ ∣ x‥2∗y}", "{a,b · a ∈ ℕ ∣ a‥b}", null);
		testExpressionMatching("CSet matches", "{x,y · x ∈ ℙ(ℤ) ∣ g[x∖y]}", "{s,t · s ∈ ℙ(S) ∣ f[s∖t]}", "S=ℤ || T=S  |][| f=g");
		testExpressionMatching("CSet does not match", "{x,y · x ∈ ℙ(ℤ) ∣ g[x∖y]}", "{s · s ∈ ℙ(S) ∣ f[s]}", null);
	}
	
	/**************************************************************************
	 * (END) Test Quantified Expression Matcher                               *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Set Extension Matcher                                     *
	 **************************************************************************/

	/**
	 * Tests the matcher for set extension.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_SetExtensionMatching() throws CoreException {
		testExpressionMatching("Set Extension matches", "{1}", "{c}", "T=ℤ |][| c=1");
		testExpressionMatching("Set Extension matches", "{c}", "{c}", "T=S |][| c=c");
		testExpressionMatching("Set Extension does not match", "{c}", "{1}", null);

		testExpressionMatching("Set Extension matches", "{1, x}", "{y, c}", "T=ℤ |][| c=x || y=1");
		testExpressionMatching("Set Extension matches", "{c, x}", "{y, c}", "T=S |][| c=x || y=c");
		testExpressionMatching("Set Extension does not match", "{c}", "{y, c}", null);
		testExpressionMatching("Set Extension does not match", "{c, x}", "{c}", null);

	}
	
	/**************************************************************************
	 * (END) Test Set Extension Matcher                                       *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Unary Expression Matcher                                  *
	 **************************************************************************/

	/**
	 * Tests the matcher for unary expressions.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_UnaryExpressionMatching() throws CoreException {
		// {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KMIN, KMAX, CONVERSE,
		// UNMINUS}
		testExpressionMatching("Cardinality matches", "card(g[s])", "card(f[y])", "S=ℤ || T=S |][| f=g || y=s");
		testExpressionMatching("Cardinality does not match", "card(g[s])", "card(h[y])", null);

		testExpressionMatching("Power set matches", "ℙ(g[s])", "ℙ(f[y])", "S=ℤ || T=S |][| f=g || y=s");
		testExpressionMatching("Power set does not match", "ℙ(g[s])", "ℙ(h[y])", null);

		testExpressionMatching("Power set 1 matches", "ℙ1(g[s])", "ℙ1(f[y])", "S=ℤ || T=S |][| f=g || y=s");
		testExpressionMatching("Power set 1 does not match", "ℙ1(g[s])", "ℙ1(h[y])", null);

		testExpressionMatching("Generalised Union matches", "union({g[s]})", "union({f[y]})", "S=ℤ || T=S |][| f=g || y=s");
		testExpressionMatching("Generalised Union does not match", "union(g[s])", "union(h[y])", null);

		testExpressionMatching("Generalised Intersection matches", "inter({g[s]})", "inter({f[y]})", "S=ℤ || T=S |][| f=g || y=s");
		testExpressionMatching("Generalised Intersection does not match", "inter(g[s])", "inter(h[y])", null);

		testExpressionMatching("Domain matches", "dom(g)", "dom(f)", "S=ℤ || T=S |][| f=g");
		testExpressionMatching("Domain does not match", "dom(g)", "dom(h)", null);

		testExpressionMatching("Range matches", "ran(g)", "ran(f)", "S=ℤ || T=S |][| f=g");
		testExpressionMatching("Range does not match", "ran(g)", "ran(h)", null);

		testExpressionMatching("Minimum matches", "min(s)", "min(t)", " |][| t=s");
		testExpressionMatching("Minimum matches", "min({1,2})", "min(t)", " |][| t={1,2}");
		testExpressionMatching("Minimum does not match", "min(s)", "min({1,2})", null);
		
		testExpressionMatching("Maximum matches", "max(s)", "max(t)", " |][| t=s");
		testExpressionMatching("Maximum matches", "max({1,2})", "max(t)", " |][| t={1,2}");
		testExpressionMatching("Maximum does not match", "max(s)", "max({1,2})", null);
		
		testExpressionMatching("Converse matches", "g∼", "f∼", "S=ℤ || T=S |][| f=g");
		testExpressionMatching("Converse does not match", "g∼", "h∼", null);

		testExpressionMatching("Unary Minus matches", "−x", "−y", " |][| y=x");
		testExpressionMatching("Unary Minus does not match", "−1", "−y", null); // This does not match since -1 is parsed as an integer literal.
		testExpressionMatching("Unary Minus does not match", "x", "−1", null);

	}
	
	/**************************************************************************
	 * (END) Test Unary Expression Matcher                                    *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Binary Predicate Matcher                                  *
	 **************************************************************************/

	/**
	 * Tests the matcher for binary predicates.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_BinaryPredicateMatching() throws CoreException {
		// {LIMP, LEQV}
		testPredicateMatching("Implication matches", "x = 1 ⇒ c ∈ ran(g)", "y = 1 ⇒ c ∈ ran(f)", "S=ℤ || T=S  |][| c=c || f=g || y=x");
		testPredicateMatching("Implication does not match", "x = 1 ⇒ c ∈ ran(g)", "y = 1 ⇒ 2 ∈ ran(h)", null);

		testPredicateMatching("Equivalent matches", "x = 1 ⇔ c ∈ ran(g)", "y = 1 ⇔ c ∈ ran(f)", "S=ℤ || T=S  |][| c=c || f=g || y=x");
		testPredicateMatching("Equivalent does not match", "x = 1 ⇔ c ∈ ran(g)", "y = 1 ⇔ 2 ∈ ran(h)", null);
	}
	
	/**************************************************************************
	 * (END) Test Binary Predicate Matcher                                    *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Extended Predicate Matcher                                *
	 **************************************************************************/

	/**
	 * Tests the matcher for extended predicates.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_ExtendedPredicateMatching() throws CoreException {
		addExtensions(primeExtension());

		// ensure we use a compatible type environment
		formulaTypeEnv = AstUtilities.getTypeEnvironmentForFactory(formulaTypeEnv, factory);
		patternTypeEnv = AstUtilities.getTypeEnvironmentForFactory(patternTypeEnv, factory);
		
		testPredicateMatching("Prime matches", "prime(x − 2)", "prime(y)", " |][| y=x − 2");
		testPredicateMatching("Prime matches", "prime(x − 2)", "prime(y − 2)", " |][| y=x");
		testPredicateMatching("Prime does not match", "prime(x)", "prime(y − 2)", null);
	}
	
	/**************************************************************************
	 * (END) Test Extended Predicate Matcher                                  *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Literal Predicate Matcher                                 *
	 **************************************************************************/

	/**
	 * Tests the matcher for literal predicates.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_LiteralPredicateMatching() throws CoreException {
		// {BTRUE} or {BFALSE}
		testPredicateMatching("Truth matches", "⊤", "⊤", " |][| ");
		testPredicateMatching("Truth does not matches", "⊥", "⊤", null);
		testPredicateMatching("Truth does not matches", "x = 2", "⊤", null);

		testPredicateMatching("Falsify matches", "⊥", "⊥", " |][| ");
		testPredicateMatching("Falsify does not matches", "⊤", "⊥", null);
		testPredicateMatching("Falsify does not matches", "x = 2", "⊥", null);

	}

	/**************************************************************************
	 * (END) Test Literal Predicate Matcher                                   *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Multiple Predicate Matcher                                *
	 **************************************************************************/

	/**
	 * Tests the matcher for multiple predicates.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_MultiplePredicateMatching() throws CoreException {
		// {KPARTITION}
		testPredicateMatching("Partition matches", "partition(s, {c})", "partition(t, {c})", "T=S  |][| c=c || t=s");
		testPredicateMatching("Partition matches", "partition(s, {c}, a)", "partition(t, {c}, b)", "T=S  |][| b=a || c=c || t=s");
		testPredicateMatching("Partition matches", "partition(s, {1, 2})", "partition(t, s)", "S=ℤ  |][| s={1,2} || t=s");
		testPredicateMatching("Partition matches", "partition(s, {1, 2}, a)", "partition(t, s, b)", "S=ℤ  |][| b=a || s={1,2} || t=s");
		testPredicateMatching("Partition does not match", "partition(s, {c})", "partition(t, {c}, b)", null);
		testPredicateMatching("Partition does not match", "partition(s, {c}, a)", "partition(t, {c})", null);
	}
	
	/**************************************************************************
	 * (END) Test Multiple Predicate Matcher                                  *
	 **************************************************************************/

	/**************************************************************************
	 * (BEGIN) Test Quantified Predicate Matcher                              *
	 **************************************************************************/
	/**
	 * Tests the matcher for quantified predicates.
	 * 
	 * @throws CoreException
	 *             when unexpected error occurs.
	 */
	@Test
	public void test_QuantifiedPredicateMatching() throws CoreException {
		// {FORALL, EXISTS}
		testPredicateMatching("Universal quantification matches", "∀x · x ∈ ℕ ⇒ 0 ≤ x", "∀y · y ∈ ℕ ⇒ 0 ≤ y", " |][| ");
		testPredicateMatching("Universal quantification does not matches", "∀x · 2∗x ∈ ℕ ⇒ 0 ≤ 2∗x", "∀y · y ∈ ℕ ⇒ 0 ≤ y", null);
		testPredicateMatching("Universal quantification matches", "∀x · x ∈ ℙ(ℤ) ⇒ c ∈ g[x]", "∀s · s ∈ ℙ(S) ⇒ c ∈ f[s]", "S=ℤ || T=S  |][| c=c || f=g");
		testPredicateMatching("Universal quantification does not match", "∀x · x ∈ ℙ(ℕ) ⇒ c ∈ g[x]", "∀s · s ∈ ℙ(S) ⇒ c ∈ f[s]", null);
		testPredicateMatching("Universal quantification matches", "∀x,y · x ∈ ℕ ⇒ x ≤ y", "∀a,b · a ∈ ℕ ⇒ a ≤ b", " |][| ");
		testPredicateMatching("Universal quantification does not match", "∀x,y · 2∗x ∈ ℕ ⇒ 2∗x ≤ y", "∀a,b · a ∈ ℕ ⇒ a ≤ b", null);
		testPredicateMatching("Universal quantification matches", "∀x,y · x ∈ ℙ(ℤ) ⇒ c ∈ g[x∖y]", "∀s,t · s ∈ ℙ(S) ⇒ c ∈ f[s∖t]", "S=ℤ || T=S  |][| c=c || f=g");
		testPredicateMatching("Universal quantification does not match", "∀x,y · x ∈ ℙ(ℤ) ⇒ c ∈ g[x∖y]", "∀s · s ∈ ℙ(S) ⇒ c ∈ f[s]", null);
	}

	/**************************************************************************
	 * (END) Test Quantified Predicate Matcher                                *
	 **************************************************************************/

}
