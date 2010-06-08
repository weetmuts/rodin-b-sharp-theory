package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Predicate;

/**
 * An implementation of a complement to an associative predicate.
 * @author Issam Maamria
 *
 */
public class AssociativePredicateComplement implements AssociativeComplement<Predicate>{

	public int getTag() {
		return 0;
	}

	public Predicate getToAppend() {
		return null;
	}

	public Predicate getToPrepend() {
		return null;
	}

}
