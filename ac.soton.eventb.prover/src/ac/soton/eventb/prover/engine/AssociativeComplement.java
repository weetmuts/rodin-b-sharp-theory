package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Formula;

public interface AssociativeComplement<F extends Formula<F>> {

	public int getTag();
	
	public F getToAppend();
	
	public F getToPrepend();
	
}
