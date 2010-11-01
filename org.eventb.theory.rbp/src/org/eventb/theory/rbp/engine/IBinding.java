package org.eventb.theory.rbp.engine;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;

/**
 * Common protocol for a binding responsible for collecting all information accumulated suring a single matching process.
 * <p>
 * A matching process attempts to match a formula against a pattern (which itself is a formula). The distinction between the pattern
 * and the formula ensures that the correct way of matching is followed, as the process is always a one-way matching process.
 * <p>
 * Bindings can be mutable or immutable. A mutable binding can be augmented with new matching information, whereas an immutable binding
 * cannot. A binding should be kept mutable for as long as the matching process. After matching finishes, it should be made immutable.
 * <p>
 * Two types of mappings are stored in a binding. Expression mappings are mappings between free identifiers in the pattern and expressions
 * in the formula. Predicate mappings are mappings between predicate variables in the pattern and predicates in the formula. 
 * <p>
 * In some cases where the pattern is an associative formula, a partial match can be obtained since formula are always flattened when rewriting.
 * This, however, should explicitly be requested.
 * <p>
 * Each binding is associated with a formula factory to ensure consistency of mathematical extensions used across a matching process.
 * <p> This interface is not intended to be implemented by clients.
 * @author maamria
 * @version 1.0
 *
 */
public interface IBinding {
	
	/**
	 * Returns whether this binding is immutable.
	 * @return whether this binding is immutable
	 */
	public boolean isImmutable();
	
	/**
	 * Makes the binding immutable. Therefore, new mappings cannot be added.
	 * <p>Call this method when the matching process has finished.</p>
	 */
	public void makeImmutable();
	
	/**
	 * Adds the mapping between <code>ident</code> and <code>e</code> to the binding if conditions to do so are met.
	 * <p>Returns whether the mapping has been successfully added.</p>
	 * @param ident the free identifier
	 * @param e the expression
	 * @return whether the mapping has been added
	 */
	public boolean putMapping(FreeIdentifier ident, Expression e);
	
	/**
	 * Adds the mapping of the type specified by the given free identifier and the supplied type.
	 * @param ident the type identifier
	 * @param type the type
	 * @return whether the mapping has been inserted
	 */
	public boolean putTypeMapping(FreeIdentifier ident, Type type);
	
	/**
	 * Adds the mapping between <code>var</code> and <code>p</code> to the binding if conditions to do so are met.
	 * <p>Returns whether the mapping has been successfully added.</p>
	 * @param var the predicate variable
	 * @param p the predicate
	 * @return whether the mapping has been added
	 */
	public boolean putPredicateMapping(PredicateVariable var, Predicate p);
	
	/**
	 * Returns whether the specified binding is insertable in this binding.
	 * @param binding the other binding 
	 * @return whether the given binding is insertable 
	 */
	public boolean isBindingInsertable(IBinding binding);
	
	/**
	 * Adds all the mappings in <code>another</code> to the current binding if the conditions of 
	 * inserting mappings are met.
	 * <p>Returns whether the binding <code>another</code> has been successfully added 
	 * to the current binding</p>
	 * @param another the other binding
	 * @return whether the binding <code>another</code> has been inserted
	 */
	public boolean insertBinding(IBinding another);
	
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
	 * Returns the type environment assigning types to the pattern free variables
	 * that are compatible with their matches in the matched formula.
	 * <p>For example, this type environment is used to typecheck the right hand sides of rewrite rules so that
	 * applying the substitutions is allowed.</p>
	 * <p>Callers must ensure that the binding is immutable.</p>
	 * 
	 * @return the type environment
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
	 * <p>This only applies to associative (including associative commutative) expressions and predicates.</p>
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
	
	/**
	 * Keeps track of the predicates that are unmatched in the case where a partial match is acceptable.
	 * @param comp the associative complement object
	 */
	public void setAssociativePredicateComplement(AssociativePredicateComplement comp);
	
	/**
	 * Returns an object containing information about unmatched predicates.
	 * @return the associative complement
	 */
	public AssociativePredicateComplement getAssociativePredicateComplement();
	
	/**
	 * Checks whether two types (a pattern and an instance) can be considered as matchable.
	 * <p>
	 * If the two types are matchable, the binding will be augmented with any infered information.
	 * @param expressionType the type of the instance
	 * @param patternType the type of the pattern
	 * @return whether the two types are unifyable
	 */
	public boolean canUnifyTypes(Type expressionType, Type patternType);
	
	/**
	 * Returns the formula factory used by this binding.
	 * @return the formula factory
	 */
	public FormulaFactory getFormulaFactory();
}
