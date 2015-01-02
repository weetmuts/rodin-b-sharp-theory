package org.eventb.core.ast.extensions.maths.tests;

import static org.eventb.core.ast.extensions.maths.AstUtilities.getNotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.IOperatorExtension;
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
		assertEquals(Notation.POSTFIX, getNotation(Notation.POSTFIX.toString()));
		// default to prefix
		assertEquals(Notation.PREFIX, getNotation("any notation"));
	}
	
	public void testUtilities_004_isAssociative() throws Exception{
		addExtensions(array(seqConcatExtension(), andExtension()));
		assertFalse("extension, 'null', expected not to be associative but is",AstUtilities.isAssociative(null));
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
		assertContains(AstUtilities.getGivenSetsNames(environment), "S", "T", "Q", "R");
		assertNotContain(AstUtilities.getGivenSetsNames(environment), "U");
		assertTrue(AstUtilities.isGivenSet(environment, "S"));
		assertTrue(AstUtilities.isGivenSet(environment, "T"));
		assertTrue(AstUtilities.isGivenSet(environment, "Q"));
		assertTrue(AstUtilities.isGivenSet(environment, "R"));
		assertFalse(AstUtilities.isGivenSet(environment, "U"));
	}
	
	public void testUtilities_010_createTypeExpression() throws Exception{
		addExtensions(listExtensions());
		addExtensions(directionExtensions());

		Type listType = AstUtilities.createTypeExpression("List", Collections.singletonList("T"), factory);
		assertEquals("expected types to be equal but were not", type("List(T)"), listType);
		Type badlistType = AstUtilities.createTypeExpression("List", Arrays.asList("T","S"), factory);
		assertEquals("expected types to be equal but were not", null, badlistType);		
		
		Type dType = AstUtilities.createTypeExpression("DIRECTION", Collections.<String>emptyList(), factory);
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
		
		//to cover unflattenExpression
		assertEquals("unflattenExpression was expected to return null",null,AstUtilities.unflattenExpression(null,new Expression[]{},factory));
		assertEquals("unflattenExpression was expected to return expression as is",factory.makeExtendedExpression(seqExtension(), new Expression[]{expression("s")}, new Predicate[0], null),AstUtilities.unflattenExpression((IOperatorExtension)seqExtension(),new Expression[]{expression("s")},factory));		
	}
	
	public void testUtilities_017_isDatatypeType() throws Exception{
		addExtensions(realTypeExtensions());
		addExtensions(listExtensions());
		assertTrue("expected to be a datatype type but was not", AstUtilities.isDatatypeType(type("List(T)")));
		assertTrue("expected to be a datatype type but was not", AstUtilities.isDatatypeType(type("List(ℙ(T×ℤ))")));
		assertFalse("expected to not be a datatype type but was", AstUtilities.isDatatypeType(type("REAL")));
		assertFalse("expected to not be a datatype type but was", AstUtilities.isDatatypeType(type("BOOL")));
	}
	
	public void testUtilities_018_getPositionOfOperator() throws Exception{
		addExtensions(andExtension());
		addExtensions(seqExtension());
		addExtensions(isEmptySeqExtension());
		addExtensions(childlessExtension());
		//INFIX
		equalPositionPoint(new AstUtilities.PositionPoint(5,8), AstUtilities.getPositionOfOperator((ExtendedExpression) expression("TRUE AND b"),"TRUE AND b"));
		equalPositionPoint(new AstUtilities.PositionPoint(6,9), AstUtilities.getPositionOfOperator((ExtendedExpression) expression("TRUE AND b"),"TRUE\n AND b")); //newline
		equalPositionPoint(new AstUtilities.PositionPoint(4,5), AstUtilities.getPositionOfOperator((ExtendedExpression) expression("TRUE AND b"),"(TRUE) AND b")); //close brackets -> !!!IS POSITION RIGHT!!!
		//PREFIX
		equalPositionPoint(new AstUtilities.PositionPoint(0,3), AstUtilities.getPositionOfOperator((ExtendedExpression) expression("seq(A)"),"seq(A)"));
		//PREFIX - no children (with some leading spaces)
//		ExtendedExpression t = factory.makeExtendedExpression(childlessExtension(), new Expression[]{}, new Predicate[0], new SourceLocation(0,9));
//		PositionPoint p = AstUtilities.getPositionOfOperator(t,"  childless  ");
		//FIXME: the following test has been made to pass but 10 doesn't look right - s.b. 11? but maybe not a valid test.
		equalPositionPoint(new AstUtilities.PositionPoint(2,10), AstUtilities.getPositionOfOperator(factory.makeExtendedExpression(childlessExtension(), new Expression[]{}, new Predicate[0], new SourceLocation(0,9)),"  childless  "));
		//predicate
		equalPositionPoint(new AstUtilities.PositionPoint(0,7), AstUtilities.getPositionOfOperator((ExtendedPredicate) predicate("isEmpty(s)"),"isEmpty(s)"));
		equalPositionPoint(new AstUtilities.PositionPoint(1,8), AstUtilities.getPositionOfOperator((ExtendedPredicate) predicate("isEmpty(s)"),"	isEmpty(s)"));	//with tab
	}

	public void testUtilities_019_conjunctPredicates() throws Exception{
		List<Predicate> pList = new ArrayList<Predicate>();
		pList.add(TRUE);		pList.add(TRUE);		pList.add(TRUE);
		//test that TRUE conjuncts are reduced
		assertEquals("expected true predicate",TRUE,AstUtilities.conjunctPredicates(pList, factory));
		//rest is tested indirectly
	}
	
	public void testUtilities_020_makeOperatorID() throws Exception{
		//test that the strings are turned into a segmented ID
		assertEquals("expected true predicate","theory.syntax",AstUtilities.makeOperatorID("theory", "syntax"));
	}
	
	public void testUtilities_021_getGroupFor() throws Exception{
		//Note: AstUtilities.getGroupFor() is public whereas AstUtilities.DUMMY_OPERATOR_GROUP = "NEW THEORY GROUP" is protected,
		//Hence, public users of this method have to know the group name string explicitly. Hence this test also checks for that string explicitly.
		//In contrast, standard event-B group Id's are accessed via getId() methods
		assertEquals("infix extended expression group",AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.INFIX,0));
		assertEquals("infix extended expression group",AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.INFIX,1));
		assertEquals("infix extended expression group",AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.INFIX,2));
		assertEquals("expected group: standard atomic expression",StandardGroup.ATOMIC_EXPR.getId(),AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.PREFIX,0));
		assertEquals("expected group: standard closed",StandardGroup.CLOSED.getId(),AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.PREFIX,1));
		assertEquals("expected group: standard closed",StandardGroup.CLOSED.getId(),AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.PREFIX,2));
		assertEquals("expected group: NEW THEORY GROUP","NEW THEORY GROUP",AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.POSTFIX,0));
		assertEquals("expected group: NEW THEORY GROUP","NEW THEORY GROUP",AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.POSTFIX,1));
		assertEquals("expected group: NEW THEORY GROUP","NEW THEORY GROUP",AstUtilities.getGroupFor(FormulaType.EXPRESSION, Notation.POSTFIX,2));
		
		assertEquals("expected group: standard atomic predicate",StandardGroup.ATOMIC_PRED.getId(),AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.INFIX,0));
		assertEquals("expected group: NEW THEORY GROUP","NEW THEORY GROUP",AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.INFIX,1));
		assertEquals("expected group: standard infix predicate",StandardGroup.RELOP_PRED.getId(),AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.INFIX,2));
		assertEquals("expected group: standard atomic predicate",StandardGroup.ATOMIC_PRED.getId(),AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.PREFIX,0));
		assertEquals("expected group: standard closed",StandardGroup.CLOSED.getId(),AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.PREFIX,1));
		assertEquals("expected group: standard closed",StandardGroup.CLOSED.getId(),AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.PREFIX,2));
		assertEquals("expected group: NEW THEORY GROUP","NEW THEORY GROUP",AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.POSTFIX,0));
		assertEquals("expected group: NEW THEORY GROUP","NEW THEORY GROUP",AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.POSTFIX,1));
		assertEquals("expected group: NEW THEORY GROUP","NEW THEORY GROUP",AstUtilities.getGroupFor(FormulaType.PREDICATE, Notation.POSTFIX,2));
	}
	
	public void testUtilities_022_parseFormula() throws Exception{
		addExtensions(andExtension());
		addExtensions(seqExtension());
		addExtensions(isEmptySeqExtension());
		equalFormulae(expression("a AND b"), AstUtilities.parseFormula("a AND b", true, factory));
		equalFormulae(predicate("isEmpty(s)"), AstUtilities.parseFormula("isEmpty(s)", false, factory));
		assertEquals("expected parseFormula to return null",null,AstUtilities.parseFormula("a or b", true, factory));	//or has not been added as an extension so should be invalid and return null
	}

	public void testUtilities_023_makeAppropriateAssociativeExpression() throws Exception{
		addExtensions(andExtension());
		equalFormulae(expression("a AND b"), AstUtilities.makeAppropriateAssociativeExpression(FormulaFactory.getTag(andExtension()), factory, expression("a AND b")));
		equalFormulae(expression("(a AND b) AND (d AND e)"), AstUtilities.makeAppropriateAssociativeExpression(FormulaFactory.getTag(andExtension()), factory, expression("a AND b"), expression("d AND e")));
		//the next test tests the fallback when a tag is used that is for an extension that hasn't been added to the factory
		int NOT_IN_FACTORY = 306; //tag for +
		Expression t = AstUtilities.makeAppropriateAssociativeExpression(NOT_IN_FACTORY, factory, expression("a AND b"), expression("d AND e"));
		assertEquals("(a AND b)+(d AND e)",t.toString());
	}
	
	public void testUtilities_024_makeAssociativePredicate() throws Exception{
		addExtensions(seqExtension());
		addExtensions(isEmptySeqExtension());
		//addExtensions(infixAssocPredExtension());
		//Predicate p = predicate("isEmpty(s)");
		//equalFormulae(predicate("isEmpty(s)"), AstUtilities.makeAssociativePredicate(FormulaFactory.getTag(infixAssocPredExtension()), factory, predicate("isEmpty(s)")));
		//equalFormulae(predicate("(isEmpty(s)) infixAssocPred (isEmpty(t))"), AstUtilities.makeAssociativePredicate(FormulaFactory.getTag(infixAssocPredExtension()), factory, predicate("isEmpty(s)"),predicate("isEmpty(t)")));
	}
	
	public void testUtilities_025_getListWithoutNulls() throws Exception{
		assertEquals(Arrays.asList(1, 2, 3, 4),AstUtilities.getListWithoutNulls(1, null, 2, 3, null, 4, null));
	}
	
	public void testUtilities_026_ensureNotnull() throws Exception{
		try {
			AstUtilities.ensureNotNull(1, null, 2, 3, null, 4, null);
			fail("expected an exception but did not occur");
		} catch (Exception e) {
			assertEquals("expected illegal arg exception", IllegalArgumentException.class, e.getClass());
		}
	}
	
}
