package ac.soton.eventb.prover.internal.engine;

import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.engine.IBinding;

public abstract class PredicateMatcher<P extends Predicate> implements IPredicateMatcher {
	
	protected MatcherEngine engine;
	protected Class<P> type;
	
	protected PredicateMatcher(Class<P> type){
		engine =  MatcherEngine.getDefault();
		this.type = type;
	}

	@Override
	public boolean match(Predicate form, Predicate pattern,
			IBinding existingBinding) {
		P pForm = cast(form);
		P pPattern = cast(pattern);
		return gatherBindings(pForm, pPattern, existingBinding);
		
	}
	
	protected abstract boolean gatherBindings(P form, P pattern, IBinding existingBinding);	

	protected abstract P cast(Predicate p);
	
	public Class<P> getType(){
		return type;
	}
}
