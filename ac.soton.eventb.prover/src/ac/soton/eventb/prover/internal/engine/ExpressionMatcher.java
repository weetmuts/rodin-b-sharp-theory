package ac.soton.eventb.prover.internal.engine;

import org.eventb.core.ast.Expression;

import ac.soton.eventb.prover.engine.IBinding;

public abstract class ExpressionMatcher<E extends Expression> implements IExpressionMatcher {

	protected MatcherEngine engine;
	
	protected Class<E> type;
	
	protected ExpressionMatcher(Class<E> type){
		engine = MatcherEngine.getDefault();
		this.type = type;
	}

	public boolean match(Expression form, Expression pattern,
			IBinding existingBinding) {
		E eForm = cast(form);
		E ePattern = cast(pattern);
		return gatherBindings(eForm, ePattern, existingBinding);
		
	}

	protected abstract boolean gatherBindings(E form, E pattern, IBinding existingBinding);
	
	protected abstract E cast(Expression e);
	
	public Class<E> getType(){
		return type;
	}
}
