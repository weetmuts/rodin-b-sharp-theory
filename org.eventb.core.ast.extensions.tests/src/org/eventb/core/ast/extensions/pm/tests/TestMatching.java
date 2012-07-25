package org.eventb.core.ast.extensions.pm.tests;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.tests.BasicAstExtTest;

public class TestMatching extends BasicAstExtTest {

	public Matcher matcher(){
		return new Matcher(factory);
	}
	
	public IBinding match(Formula<?> formula, Formula<?> pattern, boolean apm){
		return matcher().match(formula, pattern, apm);
	}
	
	public void bindingContains(IBinding binding, FreeIdentifier ident) throws CoreException{
		assertTrue("binding expected to exist and not be mutable",binding!= null && binding.isImmutable());
		assertTrue("binding should contain "+ident +" but does not" , 
				binding.getExpressionMappings().keySet().contains(ident));
	}
	
	public class MatchingTest<F extends Formula<?>>{
		
		private F formula;
		private F pattern;
		private boolean isFailure;
		private FreeIdentifier[] matchedIdents;
		private PredicateVariable[] matchedvars;
		private ITypeEnvironment resultingEnvironment;
		private boolean isPMA;
		
		public MatchingTest(F formula, F pattern, 
				boolean isPMA, boolean isFailure,
				FreeIdentifier[] matchedIdents, 
				PredicateVariable[] matchedvars,
				ITypeEnvironment resultingEnvironment){
			this.formula = formula;
			this.pattern = pattern;
			this.isPMA = isPMA;
			this.isFailure = isFailure;
			// pass null for the followings to avoid checks
			this.matchedIdents = matchedIdents;
			this.matchedvars = matchedvars;
			this.resultingEnvironment = resultingEnvironment;
		}
		
		public void test() throws Exception{
			IBinding binding = TestMatching.this.match(formula, pattern, isPMA);
			if (isFailure){
				assertNull("binding expected to be null but was not", binding);
			}
			else {
				assertNotNull("binding expected to be not null but was", binding);
				assertTrue("binding expected to be immutable but was not", binding.isImmutable());
				if (resultingEnvironment != null)
					assertEquals("type environments should be equal but were not", 
							resultingEnvironment, binding.getTypeEnvironment());
				if (matchedIdents != null)
					assertContains(binding.getExpressionMappings().keySet(), matchedIdents);
				if (matchedvars != null)
				assertContains(binding.getPredicateMappings().keySet(), matchedvars);
			}
		}
	}
	
	public void testMatching_000_NotSameClass() throws Exception{
		new MatchingTest<Expression>(tcExpression("TRUE"), tcExpression("1"),
				false, true, null, 
				null, null).test();
		
		new MatchingTest<Expression>(tcExpression("0"), tcExpression("card({1})"),
				false, true, null, 
				null, null).test();
		
		new MatchingTest<Expression>(tcExpression("{0}"), tcExpression("BOOL"),
				false, true, null, 
				null, null).test();
		
		new MatchingTest<Predicate>(tcPredicate("1=1"), tcPredicate("finite({1})"),
				false, true, null, 
				null, null).test();
	}
	
	/**
	 * {INTEGER, NATURAL, NATURAL1, BOOL, TRUE, FALSE, EMPTYSET, KPRED, KSUCC,
	 * KPRJ1_GEN, KPRJ2_GEN, KID_GEN}
	 */
	public void testMatching_001_AtomicExpressionMatching() throws Exception{
		new MatchingTest<Expression>(tcExpression("BOOL"), tcExpression("BOOL"),
				false, false, null, 
				null, null).test();
		new MatchingTest<Expression>(tcExpression("TRUE"), tcExpression("FALSE"),
				false, true, null, 
				null, null).test();
		new MatchingTest<Expression>(tcExpression("ℤ"), tcExpression("ℕ"),
				false, true, null, 
				null, null).test();
	}
	
}
