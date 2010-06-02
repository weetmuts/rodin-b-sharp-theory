package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Expression;

public class AssociativeExpressionComplement implements AssociativeComplement<Expression>{

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
