package org.eventb.core.ast.extensions.maths.tests;

import static org.eventb.core.ast.extensions.maths.AstUtilities.getNotation;

import java.util.Arrays;
import java.util.Collections;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;
import org.eventb.core.ast.extensions.tests.BasicAstExtTest;

/**
 * Basic tests mainly for {@link AstUtilities} class
 * @author maamria
 *
 */
public class TestBasicASTMaths extends BasicAstExtTest{

	/**
	 * Test equality of <code>OperatorExtensionProperties</code>
	 */
	public void testBasicASTMaths_001_OpExtPropEquality() throws Exception{
		OperatorExtensionProperties oep1 = operatorExtensionProperties("opId", "syn", FormulaType.EXPRESSION, 
				Notation.INFIX, "opGroupId");
		OperatorExtensionProperties oep2 = operatorExtensionProperties("opId", "syn", FormulaType.EXPRESSION, 
				Notation.INFIX, "opGroupId");
		assertEquals(oep1, oep2);
	}
	
	public void testBasicASTMaths_002_OpExtPropNotEquality() throws Exception{
		OperatorExtensionProperties oep1 = operatorExtensionProperties("opId", "syn", FormulaType.EXPRESSION, 
				Notation.INFIX, "opGroupId");
		OperatorExtensionProperties oep2 = operatorExtensionProperties("opId", "op", FormulaType.EXPRESSION, 
				Notation.INFIX, "opGroupId");
		assertNotSame(oep1, oep2);
	}
	
	/**
	 * Test AstUtilities class methods
	 */
	public void testAstUtilities_003_NotationConv() throws Exception{
		assertEquals(Notation.INFIX, getNotation(Notation.INFIX.toString()));
		assertEquals(Notation.PREFIX, getNotation(Notation.PREFIX.toString()));
		// default to prefix
		assertEquals(Notation.PREFIX, getNotation("any notation"));
	}
	
	public void testUtilities_004_isAssociative() throws Exception{
		addExtensions(array(seqConcatExtension(), andExtension()));
		assertTrue("extension expected to be associative but is not", AstUtilities.isAssociative((ExtendedExpression)expression("s1 seqConcat s2")));
		assertTrue("extension expected to be associative but is not", AstUtilities.isAssociative((ExtendedExpression)expression("TRUE AND b")));
		assertFalse("extension expected not to be associative but is", AstUtilities.isAssociative((ExtendedExpression) expression("seq(A)")));
	}
	
	public void testUtilities_005_isAC() throws Exception{
		addExtensions(array(seqConcatExtension(), andExtension()));
		assertTrue("extension expected to be AC but is not", AstUtilities.isAC((ExtendedExpression)expression("a AND b")));
		assertFalse("extension expected not to be AC but is", AstUtilities.isAC((ExtendedExpression) expression("seq(A)")));
		assertFalse("extension expected not to be AC but is", AstUtilities.isAC((ExtendedExpression) expression("s1 seqConcat s2")));
	}
	
	public void testUtilities_006_isAnAssociativeExtension() throws Exception{
		assertTrue("extension expected to be assoc but is not", AstUtilities.isAnAssociativeExtension(andExtension()));
		assertTrue("extension expected to be assoc but is not", AstUtilities.isAnAssociativeExtension(orExtension()));
		assertTrue("extension expected to be assoc but is not", AstUtilities.isAnAssociativeExtension(seqConcatExtension()));
		assertFalse("extension expected not to be assoc but is", AstUtilities.isAnAssociativeExtension(seqExtension()));
		assertFalse("extension expected not to be assoc but is", AstUtilities.isAnAssociativeExtension(isEmptySeqExtension()));
	}
	
	public void testUtilities_007_isACommutativeExtension() throws Exception{
		assertTrue("extension expected to be commut but is not", AstUtilities.isACommutativeExtension(andExtension()));
		assertTrue("extension expected to be commut but is not", AstUtilities.isACommutativeExtension(orExtension()));
		assertFalse("extension expected not to be commut but is ", AstUtilities.isACommutativeExtension(seqConcatExtension()));
		assertFalse("extension expected not to be commut but is", AstUtilities.isACommutativeExtension(seqExtension()));
		assertFalse("extension expected not to be commut but is", AstUtilities.isACommutativeExtension(isEmptySeqExtension()));
	}
	
	public void testUtilities_008_isExpressionOperator() throws Exception{
		assertTrue(AstUtilities.isExpressionOperator(FormulaType.EXPRESSION));
		assertFalse(AstUtilities.isExpressionOperator(FormulaType.PREDICATE));
	}
	
	public void testUtilities_009_getGivenSetsNames_isGivenSet() throws Exception{
		addTypes("S", "T", "Q");
		addNames(makeSList("a", "b", "c", "d"), makeSList("S", "ℙ(T)↔Q", "ℙ(T)", "R"));
		assertContains(AstUtilities.getGivenSetsNames(environment), "S", "T", "Q");
		assertNotContain(AstUtilities.getGivenSetsNames(environment), "R");
		assertTrue(AstUtilities.isGivenSet(environment, "S"));
		assertTrue(AstUtilities.isGivenSet(environment, "T"));
		assertTrue(AstUtilities.isGivenSet(environment, "Q"));
		assertFalse(AstUtilities.isGivenSet(environment, "R"));
	}
	
	public void testUtilities_010_createTypeExpression() throws Exception{
		addExtensions(listExtensions());
		addExtensions(directionExtensions());
		Type listType = AstUtilities.createTypeExpression("List", 
				Collections.singletonList("T"), factory);
		assertEquals("expected types to be equal but were not", type("List(T)"), listType);
		
		Type dType = AstUtilities.createTypeExpression("DIRECTION", 
				Collections.<String>emptyList(), factory);
		assertEquals("expected types to be equal but were not", type("DIRECTION"), dType);
	}
	
	public void testUtilities_011_checkOperatorID() throws Exception{
		assertTrue("id expected to be ok but is not", AstUtilities.checkOperatorID("oppositeId", factory));
		addExtensions(oppositeExtension());
		assertFalse("is expected not to be ok but is", AstUtilities.checkOperatorID("oppositeId", factory));
	}
	
	public void testUtilities_012_checkOperatorSyntaxSymbol() throws Exception{
		assertFalse("is expected not to be ok but is", AstUtilities.checkOperatorSyntaxSymbol("finite", factory));
		assertFalse("is expected not to be ok but is", AstUtilities.checkOperatorSyntaxSymbol("card", factory));
		assertTrue("id expected to be ok but is not", AstUtilities.checkOperatorSyntaxSymbol("opposite", factory));
		addExtensions(oppositeExtension());
		assertFalse("is expected not to be ok but is", AstUtilities.checkOperatorSyntaxSymbol("opposite", factory));
	}
	
	public void testUtilities_013_checkGroupID() throws Exception{
		assertTrue("id expected to be ok but is not", AstUtilities.checkGroupID("oppositeGroup", factory));
		addExtensions(oppositeExtension());
		assertFalse("is expected not to be ok but is", AstUtilities.checkGroupID("oppositeGroup", factory));
	}
	
	public void testUtilities_014_getGivenTypes() throws Exception{
		addExtensions(listExtensions());
		assertContains(AstUtilities.getGivenTypes(type("T")), givenType("T"));
		assertContains(AstUtilities.getGivenTypes(type("List(T)")), givenType("T"));
		assertContains(AstUtilities.getGivenTypes(type("T×S")), givenType("T"), givenType("S"));
		assertContains(AstUtilities.getGivenTypes(type("ℙ(T)")), givenType("T"));
		assertContains(AstUtilities.getGivenTypes(type("ℙ(T×S)↔Q")), givenType("T"), givenType("S"), givenType("Q"));
		assertContains(AstUtilities.getGivenTypes(type("List(T×S)")), givenType("T"), givenType("S"));
		assertContains(AstUtilities.getGivenTypes(type("ℙ(List(T)×BOOL)")), givenType("T"));
		assertContains(AstUtilities.getGivenTypes(type("T×List(S)")), givenType("T"), givenType("S"));
	}
	
	public void testUtilities_015_subChildren() throws Exception{
		Expression[] exps = new Expression[]{expression("1+2"), 
				expression("BOOL"), 
				expression("0"), 
				expression("T"), 
				expression("ℙ(T×S)↔Q")};
		
		assertEquals("array of expression expected to be equal but is not",
				Arrays.asList(AstUtilities.subChildren(0, exps)), 
				Arrays.asList(new Expression[]{expression("1+2")}));
		
		assertEquals("array of expression expected to be equal but is not",
				Arrays.asList(AstUtilities.subChildren(1, exps)), 
				Arrays.asList(new Expression[]{expression("1+2"), expression("BOOL")}));
		
		assertEquals("array of expression expected to be equal but is not",
				Arrays.asList(AstUtilities.subChildren(2, exps)), 
				Arrays.asList(new Expression[]{expression("1+2"), expression("BOOL"), expression("0")}));
		
		assertEquals("array of expression expected to be equal but is not",
				Arrays.asList(AstUtilities.subChildren(3, exps)), 
				Arrays.asList(new Expression[]{expression("1+2"), expression("BOOL"), expression("0"), expression("T")}));
		
		assertEquals("array of expression expected to be equal but is not",
				Arrays.asList(AstUtilities.subChildren(4, exps)), 
				Arrays.asList(new Expression[]{expression("1+2"), expression("BOOL"), expression("0"), expression("T"), expression("ℙ(T×S)↔Q")}));
		
		assertEquals("array of expression expected to be equal but is not",
				Arrays.asList(AstUtilities.subChildren(5, exps)), 
				Arrays.asList(new Expression[]{expression("1+2"), expression("BOOL"), expression("0"), expression("T"), expression("ℙ(T×S)↔Q")}));
		
		assertEquals("array of expression expected to be equal but is not",
				Arrays.asList(AstUtilities.subChildren(-1, exps)), 
				Arrays.asList(new Expression[]{}));
	}
	
	public void testUtilities_016_unflatten() throws Exception{
		addExtensions(orExtension());
		addExtensions(seqExtension());
		addExtensions(isEmptySeqExtension());
		// case assoc op
		equalFormulae(expression("a OR b"), AstUtilities.unflatten((IExtendedFormula) expression("a OR b"), factory));
		equalFormulae(expression("(a OR b) OR c"), AstUtilities.unflatten((IExtendedFormula) expression("a OR b OR c"), factory));
		equalFormulae(expression("(((a OR b) OR c) OR d) OR f"),
				AstUtilities.unflatten((IExtendedFormula) expression("a OR b OR c OR d OR f") , factory));
		equalFormulae(expression("a OR (b OR c)"), AstUtilities.unflatten((IExtendedFormula) expression("a OR (b OR c)"), factory));
		equalFormulae(expression("(a OR (b OR (c OR d))) OR f"), AstUtilities.unflatten((IExtendedFormula) expression("a OR (b OR (c OR d)) OR f"), factory));
		// other cases
		equalFormulae(expression("seq(a)"), AstUtilities.unflatten((IExtendedFormula) expression("seq(a)"), factory));
		equalFormulae(predicate("isEmpty(s)"), AstUtilities.unflatten((IExtendedFormula) predicate("isEmpty(s)"), factory));
	}
	
	public void testUtilities_017_isDatatypeType() throws Exception{
		addExtensions(realTypeExtensions());
		addExtensions(listExtensions());
		assertTrue("expected to be a datatype type but was not", AstUtilities.isDatatypeType(type("List(T)")));
		assertTrue("expected to be a datatype type but was not", AstUtilities.isDatatypeType(type("List(ℙ(T×ℤ))")));
		assertFalse("expected to not be a datatype type but was", AstUtilities.isDatatypeType(type("REAL")));
		assertFalse("expected to not be a datatype type but was", AstUtilities.isDatatypeType(type("BOOL")));
	}
}
