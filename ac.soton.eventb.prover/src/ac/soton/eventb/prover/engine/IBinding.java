package ac.soton.eventb.prover.engine;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;

/**
 * <p></p>
 * <p>Common protocol for a binding.</p>
 * <p>A binding provides a container for <code>FreeIdentifier</code> to <code>Expression</code>
 *  mappings.</p>
 *  <p>Starting from an empty binding, the container can be augmented with new bindings that satisfy certain conditions.</p>
 *  <p>Initially, after construction the binding is mutable. New bindings can be added.</p>
 *  <p>At the end of the matching process, the binding should be made immutable. This indicates the end of the matching process with success.</p>
 *  <p>After the binding is made immutable, it will be safe to get the bindings and the new type environment.</p>
 * @author maamria
 *
 */
public interface IBinding {
	
	public boolean isImmutable();
	
	public boolean isBindingInsertable(IBinding binding);
	
	public boolean putMapping(FreeIdentifier ident, Expression e);
	
	public boolean putPredicateMapping(PredicateVariable var, Predicate p);
	
	public boolean insertAllMappings(IBinding another);
	
	public void makeImmutable();
	
	public Map<FreeIdentifier, Expression> getMappings();
	
	public Map<PredicateVariable, Predicate> getPredicateMappings();
	
	public ITypeEnvironment getTypeEnvironment();	
	
	/**
	 * Returns the formula to be matched against a pattern.
	 * @return the formula to be matched
	 */
	public Formula<?> getFormula();
	
	/**
	 * Returns the pattern formula against which matching is carried out.
	 * @return the pattern formula
	 */
	public Formula<?> getPattern();
	
	/**
	 * Returns whether a partial match is acceptable.
	 * <p>This only applies to associative (including associative commutative) expressions.</p>
	 * @return whether a partial match is acceptable
	 */
	public boolean isPartialMatchAcceptable();
	
	/**
	 * Keeps track of the expressions that are unmatched in the case where a partial match is acceptable.
	 * @param comp the associative complement object
	 */
	public void setAssociativeExpressionComplement(AssociativeExpressionComplement comp);
	
	/**
	 * Returns an object containing information about unmatched expressions.
	 * @return the associative complement
	 */
	public AssociativeExpressionComplement getAssociativeExpressionComplement();
	
}
