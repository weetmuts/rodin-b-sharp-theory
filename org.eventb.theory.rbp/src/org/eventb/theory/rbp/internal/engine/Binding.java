package org.eventb.theory.rbp.internal.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.theory.rbp.engine.AssociativeExpressionComplement;
import org.eventb.theory.rbp.engine.AssociativePredicateComplement;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.utils.TypeMatcher;

/**
 * <p>An implementation of a binding.</p>
 * 
 * <p>In order to create a new binding object, call {@link MatcherEngine.createBinding()}.</p>
 * @see IBinding
 * @author maamria
 *
 */
final class Binding implements IBinding{

	// mappings stores
	private Map<FreeIdentifier, Expression> binding;
	private Map<FreeIdentifier, Type> typeParametersInstantiations;
	private Map<PredicateVariable, Predicate> predBinding;
	// factory used
	final private FormulaFactory factory;
	// state information
	private boolean isImmutable = false;
	// type environment generated if the matching process is a success
	final private ITypeEnvironment typeEnvironment;
	// matching information
	final private Formula<?> pattern;
	final private Formula<?> formula;
	private boolean isPartialMatchAcceptable;
	private AssociativeExpressionComplement expComplement;
	private AssociativePredicateComplement predComplement;
	
	protected Binding(Formula<?> formula, Formula<?> pattern, boolean isPartialMatchAcceptable, FormulaFactory factory){
		this.formula = formula;
		this.pattern = pattern;
		this.isPartialMatchAcceptable = isPartialMatchAcceptable;
		binding =  new HashMap<FreeIdentifier, Expression>();
		typeParametersInstantiations = new HashMap<FreeIdentifier, Type>();
		predBinding = new HashMap<PredicateVariable, Predicate>();
		this.factory = factory;
		typeEnvironment = factory.makeTypeEnvironment();
	}

	public boolean isImmutable() {
		return isImmutable;
	}
	
	public String toString(){
		return "Pattern: "+pattern +", Formula: "+formula + ", Binding: "+binding;
	}
	
	public boolean isBindingInsertable(IBinding binding) {
		// mutable binding are not insertable
		if(!binding.isImmutable())
			return false;
		// cannot insert into an immutable binding
		if(isImmutable)
			return false;
		Map<FreeIdentifier, Expression> identMap = ((Binding)binding).binding;
		Map<PredicateVariable, Predicate> predMap = ((Binding) binding).predBinding;
		for (FreeIdentifier ident : identMap.keySet())
		{
			if(!isMappingInsertable(ident, identMap.get(ident))){
				return false;
			}
		}
		for(PredicateVariable var: predMap.keySet()){
			if(!isPredicateMappingInsertable(var, predMap.get(var))){
				return false;
			}
		}
		return true;
	}
	
	public boolean putMapping(FreeIdentifier ident, Expression e){
		if(isImmutable)
			throw new UnsupportedOperationException(
					"Trying to add a mapping after the matching process finished.");
		if(!c1_CanUnifyTypes(e.getType(),ident.getType()) || 
				!c2_IdentifierIsGivenType(ident, e) ||
				(binding.get(ident) !=null && !e.equals(binding.get(ident)))){
			return false;
		}
		binding.put(ident, e);
		return true;
	}
	
	public boolean insertBinding(IBinding another) {
		if(!another.isImmutable())
			throw new IllegalArgumentException(
				"Trying to add mappings from a mutable binding.");
		if(isImmutable)
			throw new UnsupportedOperationException(
					"Trying to add mappings after the matching process finished.");
		for(FreeIdentifier ident : another.getMappings().keySet()){
			if(!putMapping(ident, another.getMappings().get(ident))){
				return false;
			}
		}
		for (PredicateVariable var: another.getPredicateMappings().keySet()){
			if(!putPredicateMapping(var, another.getPredicateMappings().get(var))){
				return false;
			}
		}
		return true;
	}

	public void makeImmutable(){
		isImmutable = true;
		for (FreeIdentifier ident : typeParametersInstantiations.keySet()){
			binding.put(ident, typeParametersInstantiations.get(ident).toExpression(factory));
		}
		for(FreeIdentifier ident: binding.keySet()){
			Type newType = binding.get(ident).getType();
			typeEnvironment.addName(ident.getName(), newType);
		}
		
	}
	
	public Map<FreeIdentifier, Expression> getMappings(){
		if(!isImmutable)
			throw new UnsupportedOperationException(
					"Trying to access mappings while still calculating the binding.");
		Map<FreeIdentifier, Expression> finalBinding = new HashMap<FreeIdentifier, Expression>();
		for (FreeIdentifier ident : binding.keySet()){
			Expression exp = binding.get(ident);
			Type newType = exp.getType();
			FreeIdentifier newIdent = factory.makeFreeIdentifier(ident.getName(), null, newType);
			finalBinding.put(newIdent, exp);
		}
		return finalBinding;
	}
	
	public ITypeEnvironment getTypeEnvironment() {
		if(!isImmutable)
			throw new UnsupportedOperationException(
					"Trying to access type environment while still calculating the binding.");
		return typeEnvironment.clone();
	}
	
	public Formula<?> getFormula() {
		return formula;
	}
	
	public Formula<?> getPattern() {
		return pattern;
	}
	
	public boolean isPartialMatchAcceptable() {
		return isPartialMatchAcceptable;
	}
	
	public void setAssociativeExpressionComplement(
			AssociativeExpressionComplement comp) {
		this.expComplement = comp;
	}
	
	public AssociativeExpressionComplement getAssociativeExpressionComplement() {
		return expComplement;
	}
	
	/**
	 * Returns whether the types of the expression and the identifier are compatible. 
	 * @param expressionType
	 * @param identifierType
	 * @return the two types are compatible
	 */
	protected boolean c1_CanUnifyTypes(Type expressionType, Type identifierType){
		return TypeMatcher.canUnifyTypes(expressionType, identifierType, this);
	}
	
	/**
	 * Checks the condition when the identifier is a given type in which case the 
	 * expression has to be a type expression.
	 * @param ident
	 * @param exp
	 * @return whether the condition is met
	 */
	protected boolean c2_IdentifierIsGivenType(FreeIdentifier ident, Expression exp){
		Set<GivenType> allPGivenTypes = ident.getGivenTypes();
		if(isIdentAGivenType(ident, allPGivenTypes)){
			return exp.isATypeExpression();
		}
		return true;
	}
	
	/**
	 * Checks whether the identifier is a given type.
	 * @param i the identifier
	 * @param types the set of given types
	 * @return whether the identifier is a given type
	 */
	protected boolean isIdentAGivenType(FreeIdentifier i, Set<GivenType> types){
		for(GivenType gt : types){
			if(i.equals(gt.toExpression(factory))){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks whether an individual mapping is insertable.
	 * @param ident the identifier
	 * @param e the expression
	 * @return whether an individual mapping is insertable
	 */
	protected boolean isMappingInsertable(FreeIdentifier ident, Expression e) {
		if(isImmutable||
				!c1_CanUnifyTypes(e.getType(),ident.getType()) || 
				!c2_IdentifierIsGivenType(ident, e)||
				(binding.get(ident) != null && !e.equals(binding.get(ident)))){
			return false;
		}
		
		return true;
	}

	protected boolean isPredicateMappingInsertable(PredicateVariable var, Predicate p){
		if(
				isImmutable || 
				(predBinding.get(var) != null && !p.equals(predBinding.get(var)))
				)
			return false;
		
		return true;
	}
	
	public Map<PredicateVariable, Predicate> getPredicateMappings() {
		if(!isImmutable)
			throw new UnsupportedOperationException(
					"Trying to access mappings while still calculating the binding.");
		return Collections.unmodifiableMap(predBinding);
	}

	public boolean putPredicateMapping(PredicateVariable var, Predicate p) {
		if(isImmutable)
			throw new UnsupportedOperationException(
					"Trying to add a mapping after the matching process finished.");
		if(predBinding.get(var) != null && !p.equals(predBinding.get(var))){
			return false;
		}
		predBinding.put(var, p);
		return true;
	}

	public AssociativePredicateComplement getAssociativePredicateComplement() {
		// TODO Auto-generated method stub
		return predComplement;
	}

	public void setAssociativePredicateComplement(
			AssociativePredicateComplement comp) {
		// TODO Auto-generated method stub
		this.predComplement = comp;
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		// TODO Auto-generated method stub
		return factory;
	}

	@Override
	public boolean putTypeMapping(FreeIdentifier ident, Type type) {
		if(!isMappingInsertable(ident, type.toExpression(factory))){
			return false;
		}
		if(typeParametersInstantiations.get(ident) != null
				&& !typeParametersInstantiations.get(ident).equals(type)){
			return false;
		}
		typeParametersInstantiations.put(ident, type);
		return true;
	}
}
