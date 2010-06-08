package ac.soton.eventb.prover.engine;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;

/**
 * <p>Common protocol for a binding.</p>
 * <p>A binding provides a container for <code>FreeIdentifier</code> to <code>Expression</code>
 *  mappings as well as a container for <code>PredicateVariable</code> to <code>Predicate</code> mappings.</p>
 *  <p>Starting from an empty binding, the container can be augmented with new bindings that satisfy certain conditions.</p>
 *  <p>Initially, after construction the binding is mutable. New bindings can be added.</p>
 *  <p>At the end of the matching process, the binding should be made immutable. This indicates the end of the matching process with success.</p>
 *  <p>After the binding is made immutable, it will be safe to get the bindings (both expression and predicate bindings) and the new type environment.</p>
 * @author maamria
 *
 */
public interface IBinding {
	
	/**
	 * Returns whether this binding is immutable.
	 * @return whether this binding is immutable
	 */
	public boolean isImmutable();
	/**
	 * Returns whether the specified binding is insertable in this binding.
	 * @param binding the other binding 
	 * @return whether the other binding is insertable 
	 */
	public boolean isBindingInsertable(IBinding binding);
	/**
	 * Adds the mapping between <code>ident</code> and <code>e</code> to the binding if conditions to do so are met.
	 * <p>Returns whether the mapping has been successfully added.</p>
	 * @param ident the free identifier
	 * @param e the expression
	 * @return whether the mapping has been added
	 */
	public boolean putMapping(FreeIdentifier ident, Expression e);
	
	/**
	 * Adds the mapping between <code>var</code> and <code>p</code> to the binding if conditions to do so are met.
	 * <p>Returns whether the mapping has been successfully added.</p>
	 * @param var the predicate variable
	 * @param p the predicate
	 * @return whether the mapping has been added
	 */
	public boolean putPredicateMapping(PredicateVariable var, Predicate p);
	
	/**
	 * Adds all the mappings in <code>another</code> to the current binding if the conditions of 
	 * inserting mappings are met.
	 * <p>Returns whether the binding <code>another</code> has been successfully added 
	 * to the current binding</p>
	 * @param another the other binding
	 * @return whether the binding <code>another</code> has been inserted
	 */
	public boolean insertAllMappings(IBinding another);
	/**
	 * Makes the binding immutable. Therefore, new mappings cannot be added.
	 * <p>Call this method when the matching process has finished.</p>
	 */
	public void makeImmutable();
	/**
	 * Returns the expression mappings.
	 * <p>Callers must ensure that the binding is immutable.</p>
	 * @return the expression mappings
	 */
	public Map<FreeIdentifier, Expression> getMappings();
	/**
	 * Returns the predicate mappings.
	 * <p>Callers must ensure that the binding is immutable.</p>
	 * @return the predicate mappings
	 */
	public Map<PredicateVariable, Predicate> getPredicateMappings();
	/**
	 * Returns the type environment of the binding
	 * @return
	 */
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
	
	public void setAssociativePredicateComplement(AssociativePredicateComplement comp);
	
	public AssociativePredicateComplement getAssociativePredicateComplement();
}
