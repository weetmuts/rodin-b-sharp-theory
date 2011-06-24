package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;

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
			if(matchingFactory.match(formMem, patternMem, existingBinding)){
				return true;
			}
		}
		return false;
	}

	@Override
	protected SetExtension getExpression(Expression e) {
		return (SetExtension) e;
	}

}
