package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.SetExtension;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

/**
 * TODO emulate associative matching
 * @since 1.0
 * @author maamria
 *
 */
public class SetExtensionMatcher extends ExpressionMatcher<SetExtension> {

	public SetExtensionMatcher(){
		super(SetExtension.class);
	}
	
	@Override
	protected boolean gatherBindings(SetExtension form, SetExtension pattern,
			IBinding existingBinding)  {
		if(form.getMembers().length == 1 && pattern.getMembers().length == 1){
			Expression formMem = form.getMembers()[0];
			Expression patternMem = pattern.getMembers()[0];
			if(MatchingFactory.match(formMem, patternMem, existingBinding)){
				return true;
			}
		}
		return false;
		
	}

	@Override
	protected SetExtension cast(Expression e) {
		// TODO Auto-generated method stub
		return (SetExtension) e;
	}

}
