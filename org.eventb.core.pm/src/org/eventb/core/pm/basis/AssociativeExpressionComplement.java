package org.eventb.core.pm.basis;

import org.eventb.core.ast.Expression;

/**
 * An implementation of a complement to an associative expression.
 * @author maamria
 *
 */
public final class AssociativeExpressionComplement implements IAssociativeComplement<Expression>{

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
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Tag : "+ tag +"\n");
		builder.append("Expression to append : "+ toAppend + "\n");
		builder.append("Expression to prepend : "+ toPrepend);
		return builder.toString();
	}
}
