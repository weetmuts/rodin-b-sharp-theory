package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.core.pm.basis.PredicateMatcher;

/**
 * TODO better matching
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class AssociativePredicateMatcher extends PredicateMatcher<AssociativePredicate> {


	public AssociativePredicateMatcher(){
		super(AssociativePredicate.class);
	}
	
	@Override
	protected boolean gatherBindings(AssociativePredicate form,
			AssociativePredicate pattern, IBinding existingBinding){
		// if tag is diff
		if(form.getTag() != pattern.getTag())
			return false;
		
		// get the children
		Predicate[] formChildren = form.getChildren();
		Predicate[] patternChildren = pattern.getChildren();
		// work with binary representations
		if(formChildren.length != 2 || patternChildren.length != 2
				|| formChildren.length != patternChildren.length){
			return false;
		}
		Predicate formChild1 = formChildren[0];
		Predicate patternChild1 = patternChildren[0];
		Predicate formChild2 = formChildren[1];
		Predicate patternChild2 = patternChildren[1];
		
		if(patternChild1 instanceof PredicateVariable){
			if(!existingBinding.putPredicateMapping((PredicateVariable) patternChild1, formChild1)) {
				return false;
			}
		}
		else {
			if(!matchingFactory.match(formChild1, patternChild1, existingBinding)){
				return false;
			}
		}
		if(patternChild2 instanceof PredicateVariable){
			if(!existingBinding.putPredicateMapping((PredicateVariable) patternChild2, formChild2)) {
				return false;
			}
		}
		else {
			if(!matchingFactory.match(formChild2, patternChild2, existingBinding)){
				return false;
			}
		}
		
		return true;
		
	}

	@Override
	protected AssociativePredicate getPredicate(Predicate p) {
		return (AssociativePredicate) p;
	}

}
