package org.eventb.theory.rbp.engine;

import org.eventb.core.ast.Expression;

/**
 * An implementation of a complement to an associative expression.
 * @author maamria
 *
 */
public final class AssociativeExpressionComplement implements AssociativeComplement<Expression>{

	private int tag;
	private Expression toAppend;
	private Expression toPrepend;
	
	public AssociativeExpressionComplement(int tag, Expression toAppend, 
			Expression toPrepend){
		this.tag = tag;
		this.toAppend = toAppend;
		this.toPrepend = toPrepend;
	}

	public int getTag() {
		return tag;
	}

	public Expression getToAppend() {
		return toAppend;
	}

	public Expression getToPrepend() {
		return toPrepend;
	}
	
	
}
