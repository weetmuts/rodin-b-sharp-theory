package ac.soton.eventb.prover.internal.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.utils.GeneralUtilities;

/**
 * <p>An implementation of a binding.</p>
 * 
 * <p>Using the interface <code>IBiding</code> is preferred.</p>
 * <p>In order to create a new binding object, call {@link Binding.createBinding()}</code></p>
 * @author maamria
 *
 */
public class Binding implements IBinding{

	private Map<FreeIdentifier, Expression> binding;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private boolean isImmutable = false;

	private Binding(){
		binding =  new HashMap<FreeIdentifier, Expression>();
		factory = FormulaFactory.getDefault();
		typeEnvironment = factory.makeTypeEnvironment();
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
					"Trying to access mappings while still calculating the binding.");
		return typeEnvironment.clone();
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
		return true;
	}

	public boolean isMappingInsertable(FreeIdentifier ident, Expression e) {
		if(isImmutable)
			return false;
		if(!c1_CanUnifyTypes(e.getType(),ident.getType()) || 
				!c2_IdentifierIsGivenType(ident, e)){
			return false;
		}
		if(binding.get(ident) != null){
			if(!binding.get(ident).equals(e)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isBindingInsertable(IBinding binding) {
		if(!binding.isImmutable())
			return false;
		if(isImmutable)
			return false;
		Map<FreeIdentifier, Expression> map = ((Binding)binding).getInternalBinding();
		for (FreeIdentifier ident : map.keySet())
		{
			if(!isMappingInsertable(ident, map.get(ident))){
				return false;
			}
		}
		return true;
	}
	
	public boolean isImmutable() {
		return isImmutable;
	}
	
	public void makeImmutable(){
		isImmutable = true;
		for(FreeIdentifier ident: binding.keySet()){
			Type newType = binding.get(ident).getType();
			typeEnvironment.addName(ident.getName(), newType);
		}
	}
	
	/**
	 * This is not intended to be called by clients.
	 * @return the internal binding
	 */
	public Map<FreeIdentifier, Expression> getInternalBinding(){
		return binding;
	}
	
	protected boolean c2_IdentifierIsGivenType(FreeIdentifier ident, Expression exp){
		Set<GivenType> allPGivenTypes = ident.getGivenTypes();
		if(isIdentAGivenType(ident, allPGivenTypes)){
			return exp.isATypeExpression();
		}
		return true;
	}
	
	protected boolean c1_CanUnifyTypes(Type expressionType, Type identifierType){
		return GeneralUtilities.canUnifyTypes(expressionType, identifierType);
	}
	
	protected boolean isIdentAGivenType(FreeIdentifier i, Set<GivenType> types){
		for(GivenType gt : types){
			if(i.equals(gt.toExpression(factory))){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an empty binding.
	 * @return an empty binding
	 */
	public static IBinding createBinding(){
		return new Binding();
	}

}
