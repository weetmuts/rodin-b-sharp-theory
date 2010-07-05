package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Predicate;

/**
 * An implementation of a complement to an associative predicate.
 * @author Issam Maamria
 *
 */
public class AssociativePredicateComplement implements AssociativeComplement<Predicate>{

	private int tag;
	private Predicate toAppend;
	private Predicate toPrepend;
	
	public AssociativePredicateComplement(int tag, Predicate toAppend, 
			Predicate toPrepend){
		this.tag = tag;
		this.toAppend = toAppend;
		this.toPrepend = toPrepend;
	}
	
	public int getTag() {
		return tag;
	}

	public Predicate getToAppend() {
		return toAppend;
	}

	public Predicate getToPrepend() {
		return toPrepend;
	}

}
