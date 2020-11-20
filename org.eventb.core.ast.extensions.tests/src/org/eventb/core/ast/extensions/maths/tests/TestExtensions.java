/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.maths.tests;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.tests.BasicAstExtTest;


/**
 * 
 * Parsing, type checking and WD tests for the example extensions defined for PM tests
 * 
 * @author maamria
 *
 */
public class TestExtensions extends BasicAstExtTest{
	
	public class ParsingTest{
		
		private Set<IFormulaExtension> addedExts;
		private String[] formsNoParse;
		private String[] formsParse;
		private boolean isPredicate;

		public ParsingTest(Set<IFormulaExtension> addedExts,
				String[] formsParse, String[] formsNoParse, boolean isPredicate){
			this.addedExts = addedExts;
			this.formsNoParse = formsNoParse;
			this.formsParse = formsParse;
			this.isPredicate = isPredicate;
		}
		
		public ParsingTest(IFormulaExtension[] addedExts,
				String[] formsParse, String[] formsNoParse, boolean isPredicate){
			this(TestExtensions.set(addedExts), formsParse, formsNoParse, isPredicate);
		}
		
		public void test()throws CoreException{
			TestExtensions.this.addExtensions(TestExtensions.array(addedExts));
			for (String form : formsNoParse){
				assertNotParses(form, isPredicate);
			}
			for(String form : formsParse){
				assertParses(form, isPredicate);
			}
		}
	}
	
	/**
	 * Test for correct parsing of the test/sample extensions
	 */
	public void testExtensions_001_Parsing() throws Exception{
		new ParsingTest(makeEList(primeExtension()),
				makeSList("prime(n)", "prime(2)"), makeSList("prime()"), true).test();
	}
	
	public void testExtensions_002_Parsing() throws Exception{
		new ParsingTest(makeEList(sameSizeExtension()), makeSList("sameSize(s,s)","sameSize(s,{1,2,3})"),
				makeSList("sameSize()"), true).test();
	}
	
	public void testExtensions_003_Parsing() throws Exception{
		new ParsingTest(makeEList(isEmptySeqExtension()), makeSList("isEmpty(s)", "isEmpty(∅)"),
				makeSList("isEmpty", "isEmpty(s1,s2)", "s1 isEmpty s2"), true ).test();
	}
	
	public void testExtensions_004_Parsing() throws Exception{
		new ParsingTest(makeEList(seqExtension()), makeSList("seq(a)", "seq(BOOL)","seq({1,2})"),
				makeSList("seq", "seq(1,2)", "1 seq 2"), false ).test();
	}
	
	public void testExtensions_005_Parsing() throws Exception{
		new ParsingTest(makeEList(seqSizeExtension()), makeSList("seqSize(s)", "seqSize(BOOL)"),
				makeSList("seqSize", "seqSize(s1,s2)", "s1 seqSize s2"), false ).test();
	}
	
	public void testExtensions_006_Parsing() throws Exception{
		new ParsingTest(makeEList(seqTailExtension()), makeSList("seqTail(s)", "seq(∅)"),
				makeSList("seqTail", "seqTail(s1,s2)", "s1 seqTail s2"), false).test();
	}
	
	public void testExtensions_007_Parsing() throws Exception{
		new ParsingTest(makeEList(seqHeadExtension()), makeSList("seqHead(s)", "seq(∅)"),
				makeSList("seqHead", "seqHead(s1,s2)", "s1 seqHead s2"), false ).test();
	}
	
	public void testExtensions_008_Parsing() throws Exception{
		new ParsingTest(makeEList(andExtension()), makeSList("a AND b", "TRUE AND FALSE", "0 AND 2 AND TRUE"),
				makeSList("AND", "AND(a,b)", "AND()"), false ).test();
	}
	
	public void testExtensions_009_Parsing() throws Exception{
		new ParsingTest(makeEList(orExtension()), makeSList("a OR b", "TRUE OR FALSE", "0 OR 2 OR 1"),
				makeSList("OR", "OR(a,b)", "OR"), false ).test();
	}
	
	public void testExtensions_010_Parsing() throws Exception{
		new ParsingTest(makeEList(seqConcatExtension()), makeSList("s1 seqConcat s2", "s1 seqConcat s2 seqConcat s3"),
				makeSList("seqConcat", "seqConcat(s1, s2)", "seqConcat()"), false).test();
	}
	
	public void testExtensions_011_Parsing() throws Exception{
		new ParsingTest( listExtensions() , makeSList("List(A)", "List(a)", "cons(x, l)", "nil", "head(l)", "tail(l)"), 
				makeSList(), false ).test();
	}
	
	public void testExtensions_012_Parsing() throws Exception{
		new ParsingTest( directionExtensions() , makeSList("DIRECTION", "NORTH", "WEST", "EAST", "SOUTH"), 
				makeSList(), false ).test();
	}
	
	public void testExtensions_013_Parsing() throws Exception{
		new ParsingTest(makeEList(listSizeExtension()), makeSList("cons(x0,l0)", "listSize(l0)", "listSize(nil)", "listSize(cons(x,l0))"),
				makeSList("listSize", "listSize()", "listSize(nil, nil)"), false ).test();
	}
	
	public void testExtensions_014_Parsing() throws Exception{
		new ParsingTest(makeEList(oppositeExtension()), makeSList("opposite(d)", "opposite(NORTH)", "opposite(SOUTH)"),
				makeSList("opposite", "opposite()"), false ).test();
	}
	
	public class TCTest {
		
		private Set<IFormulaExtension> addedExts;
		private String[] tcForms;
		private String[] notTcForms;
		private String[] givenTypes;
		private String[] names;
		private String[] types;

		public TCTest(Set<IFormulaExtension> addedExts,
				String[] tcForms, String[] notTcForms, 
				String[] givenTypes,
				String[] names, String...types){
			this.addedExts = addedExts;
			this.tcForms = tcForms;
			this.notTcForms = notTcForms;
			this.names = names;
			this.types = types;
			this.givenTypes = givenTypes;
		}
		
		public TCTest(IFormulaExtension[] addedExts,
				String[] tcForms, String[] notTcForms, 
				String[] givenTypes, String[] names, String...types){
			this(TestExtensions.set(addedExts), tcForms, notTcForms, givenTypes, names, types);
		}
		
		public void test() throws CoreException{
			TestExtensions.this.addExtensions(TestExtensions.array(addedExts));
			addTypes(givenTypes);
			addNames(names, types);
			for (String form : tcForms){
				typeCheck(form);
			}
			for (String form : notTcForms){
				notTypeCheck(form);
			}
		}
	}
	
	public void testExtensions_015_TC() throws CoreException{
		new TCTest(makeEList(primeExtension()), makeSList("prime(2)", "prime(0)", "prime(-1)", "prime(x+y)"), makeSList("prime(BOOL)", "prime({1,2})", "prime(TRUE)"), makeSList(), makeSList("x", "y"), makeSList("ℤ", "ℤ"));
	}
	
	public void testExtensions_016_TC() throws CoreException{
		new TCTest(makeEList(sameSizeExtension()), makeSList("sameSize({1,2}, {TRUE, FALSE})", "sameSize(ℕ, BOOL)", "sameSize(s, BOOL)", "sameSize(s, t)"),
				makeSList("sameSize(ℕ, ∅)", "sameSize(s, 1)"), makeSList("S", "T"), makeSList("s", "t"), makeSList("ℙ(S)", "ℙ(T)")).test();
	}
	
	public void testExtensions_017_TC() throws CoreException{
		new TCTest(makeEList(isEmptySeqExtension()), makeSList("isEmpty(s)", "isEmpty(∅⦂ℤ↔S)", "isEmpty({1↦a})"), makeSList("isEmpty(∅)", "isEmpty({1})"), makeSList("S"), makeSList("s", "a"), makeSList("ℤ↔S", "S")).test();
	}
	
	public void testExtensions_018_TC() throws CoreException{
		new TCTest(makeEList(seqExtension()), makeSList("seq(BOOL)", "seq({1,2,3})", "seq({1↦2})", "{1↦2} ∈ seq(ℤ)"), makeSList("seq(∅)", "seq(0)"), makeSList(), makeSList(), makeSList()).test();
	}
	
	public void testExtensions_019_TC() throws CoreException{
		new TCTest(makeEList(seqSizeExtension()), makeSList("seqSize(s) = n", "seqSize({1↦2})", "seqSize(∅⦂ℤ↔S)"), makeSList("seqSize({1})", "seqSize(∅)"),
				makeSList("S"), makeSList("s", "n"), makeSList("ℤ↔S", "ℤ")).test();
	}
	
	public void testExtensions_020_TC() throws CoreException{
		new TCTest(makeEList(seqTailExtension()), makeSList("seqTail(s)=s", "seqTail(∅⦂ℤ↔S)", "seqTail({1↦2})"), makeSList("seqTail(∅)", "seqTail(S)"),
				makeSList("S"), makeSList("s"), makeSList("ℤ↔S")).test();
	}
	
	public void testExtensions_021_TC() throws CoreException{
		new TCTest(makeEList(seqHeadExtension()), makeSList("seqHead(s) = e", "seqHead(∅⦂ℤ↔S)", "seqHead({1↦2})"), makeSList("seqHead(∅)", "seqHead(S)"),
				makeSList("S"), makeSList("s", "e"), makeSList("ℤ↔S", "S")).test();
	}
	
	public void testExtensions_022_TC() throws CoreException{
		new TCTest(makeEList(andExtension()), makeSList("a AND b", "a AND b AND c", "(TRUE AND FALSE)= FALSE"), makeSList("0 AND 1", "TRUE AND BOOL"),
				makeSList(), makeSList("a", "b", "c"), makeSList("BOOL", "BOOL","BOOL")).test();
	}
	
	public void testExtensions_023_TC() throws CoreException{
		new TCTest(makeEList(orExtension()), makeSList("a OR b", "a OR b OR c", "(TRUE OR FALSE)= FALSE"), makeSList("0 OR 1", "TRUE OR BOOL"),
				makeSList(), makeSList("a", "b", "c"), makeSList("BOOL", "BOOL","BOOL")).test();
	}
	
	public void testExtensions_024_TC() throws CoreException{
		new TCTest(makeEList(seqConcatExtension()), makeSList("s1 seqConcat s1", "(s1 seqConcat s2) = s3", "s1 seqConcat s2 seqConcat ∅"), makeSList("{1} seqConcat ∅"),
				makeSList("S"), makeSList("s1", "s2", "s3"), makeSList("ℤ↔S", "ℤ↔S","ℤ↔S")).test();
	}
	
	public void testExtensions_025_TC() throws CoreException{
		new TCTest(listExtensions(), makeSList("List(S)", "List({1,2})", "nil⦂List(S)", "cons(x, nil⦂List(S))", "head(cons(x, nil⦂List(S)))", "tail(l)"), 
				makeSList(), makeSList("S"), makeSList("x", "l"), makeSList("S", "List(S)")).test();
	}
	
	public void testExtensions_026_TC() throws CoreException{
		new TCTest(directionExtensions(), makeSList("DIRECTION", "NORTH", "SOUTH", "EAST", "WEST"),
				makeSList(), makeSList(), makeSList(), makeSList());
	}
	
	public void testExtensions_027_TC() throws CoreException{
		new TCTest(makeEList(listSizeExtension()), makeSList("listSize(cons(x, l))", "listSize(cons(x, l)) = 1", "listSize(nil⦂List(S))=0"),
				makeSList("listSize(1)", "listSize(cons(x, l))"), makeSList("S"), makeSList("x", "l"), makeSList("S", "List(S)"));
	}
	
	public void testExtensions_028_TC() throws CoreException{
		new TCTest(makeEList(oppositeExtension()), makeSList("opposite(NORTH)", "opposite(SOUTH)", "opposite(EAST)","opposite(WEST)"),
				makeSList(), makeSList(), makeSList(), makeSList());
	}
	
	public class WDTest {
		
		private Set<IFormulaExtension> addedExts;
		private String[] formulae;
		private String[] expectedWDs;
		private String[] givenTypes;
		private String[] names;
		private String[] types;

		public WDTest(Set<IFormulaExtension> addedExts, String[] formulae, 
				String[] expectedWDs, String[] givenTypes, String[] names, String[] types){
			this.addedExts = addedExts;
			this.formulae = formulae;
			this.expectedWDs = expectedWDs;
			this.givenTypes = givenTypes;
			this.names = names;
			this.types = types;
		}
		
		public WDTest(IFormulaExtension[] addedExts, String[] formulae, 
				String[] expectedWDs, String[] givenTypes, String[] names, String[] types){
			this(TestExtensions.set(addedExts), formulae, expectedWDs, givenTypes, names, types);
		}
		
		public void test() throws CoreException{
			TestExtensions.this.addExtensions(TestExtensions.array(addedExts));
			addTypes(givenTypes);
			assert names.length == types.length;
			addNames(names, types);
			assert formulae.length == expectedWDs.length;
			int i = 0 ;
			for (String form : formulae){
				Formula<?> formula = typeCheck(form);
				equalFormulae(tcPredicate(expectedWDs[i]), formula.getWDPredicate());
				i++;
			}
		}
	}
	
	public void testExtensions_029_WD() throws Exception{
		new WDTest(makeEList(primeExtension()),
				makeSList("prime(k)", "prime(0)"), 
				makeSList("k > 1", "0 > 1"), 
				makeSList(), 
				makeSList("k"), 
				makeSList("ℤ")).test();
	}
	
	public void testExtensions_030_WD() throws Exception{
		new WDTest(makeEList(sameSizeExtension()),
				makeSList("sameSize(a, {1,2,3})", "sameSize(a, ℤ)"), 
				makeSList("finite(a) ∧ finite({1,2,3})", "finite(a) ∧ finite(ℤ)"), 
				makeSList(), 
				makeSList("a"), 
				makeSList("ℙ(A)")).test();
	}
	
	public void testExtensions_031_WD() throws Exception{
		new WDTest(makeEList(seqExtension()),
				makeSList("seq(A)", "seq(ℤ)", "seq(seq({1,2}))"), 
				makeSList("⊤", "⊤", "⊤"), 
				makeSList("A"), 
				makeSList(), 
				makeSList()).test();
	}
	
	public void testExtensions_032_WD() throws Exception{
		new WDTest(makeEList(seqSizeExtension()),
				makeSList("seqSize(s)", "seqSize({1↦TRUE})", "seqSize(∅ ⦂ ℤ↔BOOL)"), 
				makeSList("s∈seq(S)", "{1↦TRUE}∈seq(BOOL)", "∅∈seq(BOOL)"), 
				makeSList("S"), 
				makeSList("s"), 
				makeSList("ℤ↔S")).test();
	}
	
	public void testExtensions_033_WD() throws Exception{
		new WDTest(makeEList(isEmptySeqExtension()),
				makeSList("isEmpty(s)", "isEmpty({1↦TRUE})", "isEmpty(∅ ⦂ ℤ↔BOOL)"), 
				makeSList("s∈seq(S)", "{1↦TRUE}∈seq(BOOL)", "∅∈seq(BOOL)"),
				makeSList("S"), 
				makeSList("s"), 
				makeSList("ℤ↔S")).test();
	}
	
	public void testExtensions_034_WD() throws Exception{
		new WDTest(makeEList(seqTailExtension()),
				makeSList("seqTail(s)", "seqTail(∅ ⦂ ℤ↔S)", "seqTail({1↦TRUE})"), 
				makeSList("s∈seq(S) ∧ ¬isEmpty(s)", "∅∈seq(S) ∧ ¬isEmpty(∅⦂ℤ↔S)", "{1↦TRUE}∈seq(BOOL) ∧ ¬isEmpty({1↦TRUE})"), 
				makeSList("S"), 
				makeSList("s"), 
				makeSList("ℤ↔S")).test();
	}
	
	public void testExtensions_035_WD() throws Exception{
		new WDTest(makeEList(seqHeadExtension()),
				makeSList("seqHead(s)", "seqHead(∅ ⦂ ℤ↔S)", "seqHead({1↦TRUE})"), 
				makeSList("s∈seq(S) ∧ ¬isEmpty(s)", "∅∈seq(S) ∧ ¬isEmpty(∅⦂ℤ↔S)", "{1↦TRUE}∈seq(BOOL) ∧ ¬isEmpty({1↦TRUE})"), 
				makeSList("S"), 
				makeSList("s"), 
				makeSList("ℤ↔S")).test();
	}
	
	public void testExtensions_036_WD() throws Exception{
		new WDTest(makeEList(andExtension()),
				makeSList("TRUE AND FALSE", "TRUE AND b AND FALSE"), 
				makeSList("⊤", "⊤"), 
				makeSList(), 
				makeSList("b"), 
				makeSList("BOOL")).test();
	}
	
	public void testExtensions_037_WD() throws Exception{
		new WDTest(makeEList(orExtension()),
				makeSList("TRUE OR FALSE", "TRUE OR b OR FALSE"), 
				makeSList("⊤", "⊤"), 
				makeSList(), 
				makeSList("b"), 
				makeSList("BOOL")).test();
	}
	
	/**
	 * TODO order of predicates in WD matters, it should not because & is AC
	 */
	public void testExtensions_038_WD() throws Exception{
		new WDTest(makeEList(seqConcatExtension()),
				makeSList("s0 seqConcat ∅", 
						"s0 seqConcat s1 seqConcat s2", 
						"s0 seqConcat s1 seqConcat s2 seqConcat s3"), 
				makeSList("s0∈seq(S) ∧ ∅∈seq(S)", 
						"s0∈seq(S) ∧ s1∈seq(S) ∧ ((s0 seqConcat s1) ∈seq(S)) ∧ s2∈seq(S) ", 
						"s0∈seq(S) ∧ s1∈seq(S) ∧ (s0 seqConcat s1) ∈seq(S) ∧ s2∈seq(S)∧ (s0 seqConcat s1 seqConcat s2) ∈seq(S) ∧ s3∈seq(S)"), 
				makeSList("S"), 
				makeSList("s0", "s1", "s2", "s3"), 
				makeSList("ℤ↔S", "ℤ↔S", "ℤ↔S", "ℤ↔S")).test();
	}
	
	/**
	 * Tests for axiomatic type definitions
	 */
	public void testExtensions_039_Parsing() throws Exception{
		addExtensions(realTypeExtensions());
		assertParses("REAL", false);
		assertParses("a∈REAL", true);
		assertParses("REAL × ℤ", false);
		assertParses("REAL × {BOOL}", false);
		assertNotNull("should parse as type but did not", 
				factory.parseType("REAL").getParsedType());
	}
	
	public void testExtensions_040_TC() throws Exception{
		addExtensions(realTypeExtensions());
		typeCheck("REAL");
		typeCheck("a∈REAL");
		typeCheck("REAL × {BOOL}");
		notTypeCheck("1∈REAL");
	}
	
	public void testExtensions_041_WD() throws Exception{
		addExtensions(realTypeExtensions());
		equalFormulae(predicate("⊤"), expression("REAL").getWDPredicate());
	}
}
