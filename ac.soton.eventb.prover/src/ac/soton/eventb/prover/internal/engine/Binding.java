package ac.soton.eventb.prover.internal.engine;

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

import ac.soton.eventb.prover.engine.AssociativeExpressionComplement;
import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.utils.ProverUtilities;

/**
 * <p>An implementation of a binding.</p>
 * 
 * <p>Using the interface <code>IBiding</code> is preferred.</p>
 * <p>In order to create a new binding object, call {@link Binding.createBinding()}</code></p>
 * @author maamria
 *
 */
public class Binding implements IBinding{

	/**
	 * Returns an empty binding.
	 * @return an empty binding
	 */
	public static IBinding createBinding(Formula<?> formula, Formula<?> pattern, boolean isPartialMatchAcceptable){
		return new Binding(formula, pattern, isPartialMatchAcceptable);
	}
	
	private Map<FreeIdentifier, Expression> binding;
	private Map<PredicateVariable, Predicate> predBinding;
	
	private FormulaFactory factory;
	
	private boolean isImmutable = false;
	
	private Formula<?> pattern;
	private Formula<?> formula;
	private boolean isPartialMatchAcceptable;
	private AssociativeExpressionComplement expComplement;
	private ITypeEnvironment typeEnvironment;
	
	private Binding(Formula<?> formula, Formula<?> pattern, boolean isPartialMatchAcceptable){
		this.formula = formula;
		this.pattern = pattern;
		this.isPartialMatchAcceptable = isPartialMatchAcceptable;
		binding =  new HashMap<FreeIdentifier, Expression>();
		predBinding = new HashMap<PredicateVariable, Predicate>();
		factory = FormulaFactory.getDefault();
		typeEnvironment = factory.makeTypeEnvironment();
	}

	public boolean isImmutable() {
		return isImmutable;
	}
	
	public boolean isBindingInsertable(IBinding binding) {
		if(!binding.isImmutable())
			return false;
		if(isImmutable)
			return false;
		Map<FreeIdentifier, Expression> map = ((Binding)binding).binding;
		for (FreeIdentifier ident : map.keySet())
		{
			if(!isMappingInsertable(ident, map.get(ident))){
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
				!c2_IdentifierIsGivenType(ident, e)){
			return false;
		}
		if(binding.get(ident) == null){
			binding.put(ident, e);
		}
		else {
			if(!binding.get(ident).equals(e)){
				return false;
			}
		}
		return true;
	}
	
	public boolean insertAllMappings(IBinding another) {
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
		setAssociativeExpressionComplement(
				another.getAssociativeExpressionComplement());
		return true;
	}

	public void makeImmutable(){
		isImmutable = true;
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
		return ProverUtilities.canUnifyTypes(expressionType, identifierType);
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
		if(isImmutable)
			return false;
		// IF types are OK
		if(!c1_CanUnifyTypes(e.getType(),ident.getType()) || 
				!c2_IdentifierIsGivenType(ident, e)){
			return false;
		}
		// IF mapping does not exist already, BUT if it does the expression has to be the same.
		if(binding.get(ident) != null){
			if(!binding.get(ident).equals(e)){
				return false;
			}
		}
		return true;
	}

	public Map<PredicateVariable, Predicate> getPredicateMappings() {
		if(!isImmutable)
			throw new UnsupportedOperationException(
					"Trying to access mappings while still calculating the binding.");
		return predBinding;
	}

	public boolean putPredicateMapping(PredicateVariable var, Predicate p) {
		if(isImmutable)
			throw new UnsupportedOperationException(
					"Trying to add a mapping after the matching process finished.");
		if(predBinding.get(var) == null){
			predBinding.put(var, p);
		}
		else {
			if(!predBinding.get(var).equals(p)){
				return false;
			}
		}
		return true;
	}
}
