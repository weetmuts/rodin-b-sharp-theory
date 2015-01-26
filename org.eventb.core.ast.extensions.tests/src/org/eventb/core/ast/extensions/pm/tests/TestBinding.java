package org.eventb.core.ast.extensions.pm.tests;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extensions.pm.MatchingFactory;
import org.eventb.core.ast.extensions.pm.engine.AssociativeExpressionComplement;
import org.eventb.core.ast.extensions.pm.engine.AssociativePredicateComplement;
import org.eventb.core.ast.extensions.pm.engine.Binding;
import org.eventb.core.ast.extensions.tests.BasicAstExtTest;

/**
 * Binding tests
 * 
 * @author maamria
 * 
 */
public class TestBinding extends BasicAstExtTest {

	protected MatchingFactory matchingFactory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		matchingFactory = MatchingFactory.getInstance();
	}

	@Override
	protected void tearDown() throws Exception {
		matchingFactory = null;
		super.tearDown();
	}

	public Binding binding(Formula<?> formula, Formula<?> pattern, boolean isPartialMatchAcceptable) throws Exception {
		return (Binding) matchingFactory.createBinding(formula, pattern, isPartialMatchAcceptable, factory);
	}

	public Binding binding(boolean isPartialMatchAcceptable) throws Exception {
		return (Binding) matchingFactory.createBinding(isPartialMatchAcceptable, factory);
	}

	/**
	 * tests start here
	 */
	public void testBinding_001_BindingCons() throws Exception {
		addExtensions(seqExtension());
		addTypes("A");
		try {
			binding(expression("seq(B)"), tcExpression("seq(A)"), true);
			fail("expected an exception but did not occur");
		} catch (Exception e) {
			assertEquals("expected illegal arg exception", IllegalArgumentException.class, e.getClass());
		}
	}

	public void testBinding_002_BindingCons() throws Exception {
		addExtensions(seqExtension());
		addTypes("B");
		try {
			binding(tcExpression("seq(B)"), expression("seq(A)"), true);
			fail("expected an exception but did not occur");
		} catch (Exception e) {
			assertEquals("expected illegal arg exception", IllegalArgumentException.class, e.getClass());
		}
	}

	public void testBinding_003_getFormula() throws Exception {
		addExtensions(seqExtension());
		addTypes("A");
		equalFormulae(tcExpression("seq(BOOL)"), binding(tcExpression("seq(BOOL)"), tcExpression("seq(A)"), true)
				.getFormula());
	}

	public void testBinding_004_getPattern() throws Exception {
		addExtensions(seqExtension());
		addTypes("A");
		equalFormulae(tcExpression("seq(A)"), binding(tcExpression("seq(BOOL)"), tcExpression("seq(A)"), true)
				.getPattern());
	}

	public void testBinding_004_getFF() throws Exception {
		addExtensions(seqExtension());
		assertEquals("expected equal formula factories, but found otherwise", factory, binding(false).getFormulaFactory());
		assertEquals("expected equal formula factories, but found otherwise", factory,
				binding(tcExpression("seq(BOOL)"), tcExpression("seq(BOOL)"), false).getFormulaFactory());
	}

	public void testBinding_005_getPMA() throws Exception {
		addExtensions(seqExtension());
		assertTrue(binding(true).isPartialMatchAcceptable());
		assertTrue(binding(tcExpression("seq(BOOL)"), tcExpression("seq(BOOL)"), true).isPartialMatchAcceptable());
		assertFalse(binding(false).isPartialMatchAcceptable());
		assertFalse(binding(tcExpression("seq(BOOL)"), tcExpression("seq(BOOL)"), false).isPartialMatchAcceptable());
	}

	public void testBinding_005_isImmutable() throws Exception {
		addExtensions(seqExtension());
		Binding binding = binding(true);
		assertFalse(binding.isImmutable());
		binding.makeImmutable();
		assertTrue(binding.isImmutable());
		binding = binding(tcExpression("seq(BOOL)"), tcExpression("seq(BOOL)"), false);
		assertFalse(binding.isImmutable());
		binding.makeImmutable();
		assertTrue(binding.isImmutable());
	}

	// binding immutable
	public void testBinding_006_putExpressionMapping() throws Exception {
		Binding binding = binding(true);
		binding.makeImmutable();
		try {
			binding.putExpressionMapping(ident("a", "A"), tcExpression("1+3"));
			fail("should have failed to add mapping");
		} catch (UnsupportedOperationException ex) {
			// expected
		}
	}

	// type unif conflict
	public void testBinding_007_putExpressionMapping() throws Exception {
		Binding binding = binding(true);
		boolean b = binding.putExpressionMapping(ident("a", "ℙ(BOOL)"), tcExpression("1+3"));
		assertFalse("should not have inserted the mapping but did", b);
	}

	// given type matched against a normal expr
	public void testBinding_008_putExpressionMapping() throws Exception {
		addExtensions(seqExtension());
		Binding binding = binding(true);
		boolean b = binding.putExpressionMapping(ident("A", "ℙ(A)"), tcExpression("{1}"));
		assertFalse("should not have inserted the mapping but did", b);

		binding = binding(true);
		b = binding.putExpressionMapping(ident("A", "ℙ(A)"), tcExpression("{BOOL}"));
		assertFalse("should not have inserted the mapping but did", b);

		binding = binding(true);
		b = binding.putExpressionMapping(ident("A", "ℙ(A)"), tcExpression("seq(ℤ)"));
		assertFalse("should not have inserted the mapping but did", b);

		binding = binding(true);
		b = binding.putExpressionMapping(ident("A", "ℙ(A)"), tcExpression("ℙ(BOOL)"));
		assertTrue("should not have inserted the mapping but did", b);

		binding = binding(true);
		b = binding.putExpressionMapping(ident("B", "ℙ(B)"), tcExpression("ℤ"));
		assertTrue("should not have inserted the mapping but did", b);
	}

	// enrty exists but different mapping
	public void testBinding_009_putExpressionMapping() throws Exception {
		Binding binding = binding(false);
		boolean b = binding.putExpressionMapping(ident("a", "ℙ(A)"), tcExpression("ℕ"));
		assertTrue(b);
		b = binding.putExpressionMapping(ident("a", "ℙ(A)"), tcExpression("ℤ"));
		assertFalse(b);
	}

	// binding immutable
	public void testBinding_010_putPredicateMapping() throws Exception {
		Binding binding = binding(true);
		binding.makeImmutable();
		try {
			binding.putPredicateMapping(predVar("$P"), tcPredicate("1=1"));
			fail("should have failed to add mapping");
		} catch (UnsupportedOperationException ex) {
			// expected
		}
	}

	// enrty exists but different mapping
	public void testBinding_011_putPredicateMapping() throws Exception {
		Binding binding = binding(false);
		boolean b = binding.putPredicateMapping(predVar("$P"), tcPredicate("2=1"));
		assertTrue(b);
		b = binding.putPredicateMapping(predVar("$P"), tcPredicate("3=3"));
		assertFalse(b);
	}
	
	// immutable
	public void testBinding_012_getCurrentMapping() throws Exception{
		Binding binding = binding(false);
		binding.makeImmutable();
		try {
			binding.getCurrentMapping(ident("a", "A"));
			fail("should have failed to get mapping");
		} catch(UnsupportedOperationException e) {
			// expected
		}
	}
	
	public void testBinding_013_getCurrentMapping() throws Exception{
		Binding binding = binding(false);
		binding.makeImmutable();
		try {
			binding.getCurrentMapping(predVar("$Q"));
			fail("should have failed to get mapping");
		} catch(UnsupportedOperationException e) {
			// expected
		}
	}
	// no errors
	public void testBinding_014_getCurrentMapping() throws Exception{
		Binding binding = binding(false);
		assertTrue(binding.putExpressionMapping(ident("a", "A"), tcExpression("1")));
		equalFormulae(tcExpression("1"), binding.getCurrentMapping(ident("a", "A")));
		assertTrue(binding.putPredicateMapping(predVar("$P"), tcPredicate("1=1")));
		equalFormulae(tcPredicate("1=1"), binding.getCurrentMapping(predVar("$P")));
		assertNull(binding.getCurrentMapping(predVar("$Q")));
		assertNull(binding.getCurrentMapping(ident("k", "BOOL")));
	}
	// type unification procedure
	public void testBinding_015_unifyTypes() throws Exception{
		addExtensions(listExtensions());
		Binding binding = binding(false);
		assertTrue(binding.unifyTypes(type("ℤ"), type("ℤ"), false));

		binding = binding(true);
		assertFalse(binding.unifyTypes(type("BOOL"), type("ℤ"), false));

		binding = binding(true);
		assertFalse(binding.unifyTypes(type("A"), type("BOOL"), false));

		binding = binding(true);
		assertTrue(binding.unifyTypes(type("BOOL"), type("BOOL"), false));

		binding = binding(true);
		assertTrue(binding.unifyTypes(type("BOOL×B"), type("A"), true));
		binding.makeImmutable();
		assertTrue(binding.getTypeEnvironment().contains("A"));

		binding = binding(true);
		assertTrue(binding.unifyTypes(type("ℙ(BOOL×BOOL)"), type("ℙ(A×B)"), true));
		binding.makeImmutable();
		assertTrue(binding.getTypeEnvironment().contains("A"));
		assertTrue(binding.getTypeEnvironment().contains("B"));

		binding = binding(true);
		assertFalse(binding.unifyTypes(type("BOOL×C"), type("ℙ(A×B)"), false));

		binding = binding(true);
		assertTrue(binding.unifyTypes(type("BOOL×ℤ"), type("A×B"), true));
		binding.makeImmutable();
		assertTrue(binding.getTypeEnvironment().contains("A"));
		assertTrue(binding.getTypeEnvironment().contains("B"));

		binding = binding(true);
		assertFalse(binding.unifyTypes(type("ℙ(BOOL×BOOL)"), type("A×B"), false));

		binding = binding(true);
		assertTrue(binding.unifyTypes(type("List(BOOL)"), type("List(A)"), false));

		binding = binding(true);
		assertFalse(binding.unifyTypes(type("BOOL×List(ℤ)"), type("List(A)"), false));

		// more complex augmentBinding set to true
		binding = binding(true);
		assertTrue(binding.unifyTypes(type("List(ℤ×List(BOOL))↔(BOOL×List(ℤ×(List(D×F))))"), type("List(A×B)↔(BOOL×List(C))"), true));
		binding.makeImmutable();
		ITypeEnvironment env = binding.getTypeEnvironment();
		assertEquals(type("ℙ(ℤ)"), env.getType("A"));
		assertEquals(type("ℙ(List(BOOL))"), env.getType("B"));
		assertEquals(type("ℙ(ℤ×(List(D×F)))"), env.getType("C"));

		// augment binding set to false
		binding = binding(true);
		assertTrue(binding.unifyTypes(type("List(ℤ×List(BOOL))↔(BOOL×List(ℤ×(List(D×F))))"), type("List(A×B)↔(BOOL×List(C))"), false));
		binding.makeImmutable();
		env = binding.getTypeEnvironment();
		assertFalse("should not contain name but does", env.contains("A"));
		assertFalse("should not contain name but does", env.contains("B"));
		assertFalse("should not contain name but does", env.contains("C"));
	}
	
	public void testBinding_016_getTypeEnvironment() throws Exception{
		Binding binding = binding(false);
		try {
			binding.getTypeEnvironment();
			fail("should have not been able to get type env, exception is expected as binding is mutable");
		} catch(UnsupportedOperationException e){
			// expected
		}
	}
	
	public void testBinding_017_getTypeEnvironment() throws Exception{
		addExtensions(listExtensions());
		Binding binding = binding(false);
		Expression tcExpression = tcExpression("cons(x, l)", 
				makeSList("B"), 
				makeSList("x", "l"), 
				makeSList("List(B)", "List(List(B))"));
		assertTrue("should insert the mapping but did not", binding.putExpressionMapping(ident("a", "List(A)"), 
				tcExpression));
		tcExpression = tcExpression("l0↦1", 
				makeSList("B"), 
				makeSList("l0"), 
				makeSList("List(B)"));
		assertTrue("should insert the mapping but did not", binding.putExpressionMapping(ident("b", "A×C"), 
				tcExpression));
		
		binding.makeImmutable();
		ITypeEnvironment typeEnvironment = binding.getTypeEnvironment();
		contains(typeEnvironment, 
				makeSList("a", "b", "A", "C"), 
				makeSList("List(List(B))", "List(B)×ℤ", "ℙ(List(B))", "ℙ(ℤ)"));
	}
	
	// no error
	public void testBinding_018_isBindingInsertable_insertBinding() throws Exception{
		Binding binding = binding(false);
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("b", "ℙ(A)"), 
						tcExpression("{e↦f}", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		
		Binding anotherBinding = binding(false);
		assertTrue("mapping should be inserted but is not",
				anotherBinding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				anotherBinding.putExpressionMapping(
						ident("c", "C"), 
						tcExpression("ℕ", makeSList(), makeSList(), makeSList())));
		anotherBinding.makeImmutable();
		
		assertTrue("binding should be insertable but is not", binding.isBindingInsertable(anotherBinding));
		assertTrue("binding should be insertable but is not", binding.insertBinding(anotherBinding));
		binding.makeImmutable();
		ITypeEnvironment env = binding.getTypeEnvironment();
		
		contains(env, makeSList("a", "A", "b", "c", "C"), makeSList("E×F", "ℙ(E×F)", "ℙ(E×F)", "ℙ(ℤ)", "ℙ(ℙ(ℤ))"));
	}
	// binding is immutable
	public void testBinding_019_isBindingInsertable_insertBinding() throws Exception{
		Binding binding = binding(false);
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("b", "ℙ(A)"), 
						tcExpression("{e↦f}", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		binding.makeImmutable();
		Binding anotherBinding = binding(false);
		assertTrue("mapping should be inserted but is not",
				anotherBinding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				anotherBinding.putExpressionMapping(
						ident("c", "C"), 
						tcExpression("ℕ", makeSList(), makeSList(), makeSList())));
		assertFalse("binding should not be insertable but is", binding.isBindingInsertable(anotherBinding));
		try{
			binding.insertBinding(anotherBinding);
			fail("should have raised an exception");
		} catch (UnsupportedOperationException e) {
			// expected
		}
	}
	
	// the other binding mutable
	public void testBinding_020_isBindingInsertable_insertBinding() throws Exception{
		Binding binding = binding(false);
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("b", "ℙ(A)"), 
						tcExpression("{e↦f}", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		Binding anotherBinding = binding(false);
		assertTrue("mapping should be inserted but is not",
				anotherBinding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				anotherBinding.putExpressionMapping(
						ident("c", "C"), 
						tcExpression("ℕ", makeSList(), makeSList(), makeSList())));
		assertFalse("binding should not be insertable but is", binding.isBindingInsertable(anotherBinding));
		try{
			binding.insertBinding(anotherBinding);
			fail("should have raised an exception");
		} catch (UnsupportedOperationException e) {
			// expected
		}
	}
	
	// binding clash
	public void testBinding_021_isBindingInsertable_insertBinding() throws Exception{
		Binding binding = binding(false);
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("b", "ℙ(A)"), 
						tcExpression("{e↦f}", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		Binding anotherBinding = binding(false);
		assertTrue("mapping should be inserted but is not",
				anotherBinding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e", makeSList("E"), makeSList("e"), makeSList("E"))));
		anotherBinding.makeImmutable();
		assertFalse("binding should not be insertable but is", binding.isBindingInsertable(anotherBinding));
		assertFalse("binding should not be insertable but is", binding.insertBinding(anotherBinding));
		
		Binding anotherBinding2 = binding(false);
		assertTrue("mapping should be inserted but is not",
				anotherBinding2.putExpressionMapping(
						ident("b", "ℙ(A)"), 
						tcExpression("{1↦f}", makeSList("F"), makeSList("f"), makeSList("F"))));
		anotherBinding.makeImmutable();
		assertFalse("binding should not be insertable but is", binding.isBindingInsertable(anotherBinding2));
		try{
			binding.insertBinding(anotherBinding2);
			fail("should have raised an exception");
		} catch (UnsupportedOperationException e) {
			// expected
		}
	}
	
	public void testBinding_022_complement() throws Exception{
		
		Binding binding = binding(false);
		// no error
		AssociativeExpressionComplement expComp = 
				new AssociativeExpressionComplement(AssociativeExpression.BINTER,tcExpression("{1}"),tcExpression("{2}"));
		AssociativePredicateComplement predComp = 
				new AssociativePredicateComplement(AssociativePredicate.LAND,tcPredicate("1=1"),tcPredicate("2=2"));
		
		binding.setAssociativeExpressionComplement(expComp);
		binding.setAssociativePredicateComplement(predComp);

		binding = binding(false);
		// immutable binding
		binding.makeImmutable();
		try{
			binding.setAssociativeExpressionComplement(expComp);
			fail("should not have added complement");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try{
			binding.setAssociativePredicateComplement(predComp);
			fail("should not have added complement");
		} catch (UnsupportedOperationException e) {
			// expected
		}

		binding = binding(false);
		// mutable binding
		binding.setAssociativeExpressionComplement(expComp);
		binding.setAssociativePredicateComplement(predComp);
		try{
			binding.getAssociativeExpressionComplement();
			fail("should not access complement");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try{
			binding.getAssociativePredicateComplement();
			fail("should not access complement");
		} catch (UnsupportedOperationException e) {
			// expected
		}
	}
	
	// TODO more rigour required to test getExpressionMappings()/getPredicateMappings()
	public void testBinding_023_getMappings() throws Exception{
		Binding binding = binding(false);
		// mutable binding
		try{
			binding.getExpressionMappings();
			fail("should have raised an exception");
		} catch(UnsupportedOperationException e){
			// expected
		}
		try{
			binding.getPredicateMappings();
			fail("should have raised an exception");
		} catch(UnsupportedOperationException e){
			// expected
		}
		// no error
		binding.makeImmutable();
		assertTrue("mappings should be empty but is not", binding.getPredicateMappings().isEmpty());
		assertTrue("mappings should be empty but is not", binding.getExpressionMappings().isEmpty());

		binding = binding(false);
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("a", "A"), 
						tcExpression("e↦f", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				binding.putExpressionMapping(
						ident("b", "ℙ(A)"), 
						tcExpression("{e↦f}", makeSList("E", "F"), makeSList("e", "f"), makeSList("E", "F"))));
		assertTrue("mapping should be inserted but is not",
				binding.putPredicateMapping(predVar("$P"), tcPredicate("1=1")));
		binding.makeImmutable();
		assertEquals("expected same set but found otherwise", 
				set(ident("a", "E×F"), ident("A", "ℙ(E×F)"), ident("b", "ℙ(E×F)")),
				binding.getExpressionMappings().keySet());
		assertEquals("expected same set but found otherwise", set(predVar("$P")), 
				binding.getPredicateMappings().keySet());
	}
}
