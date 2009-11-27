package ac.soton.eventb.prover.engine;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;

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

	/**
	 * <p>Returns the final binding.</p>
	 * <p>Callers should ensure that this method is called after a call to <code>IBinding.makeImmutable()</code> is made.</p>
	 * @return the final binding
	 * @throws UnsupportedOperationException if called before binding is made immutable
	 */
	public Map<FreeIdentifier, Expression> getMappings();
	
	/**
	 * <p>Returns the internal type environment of this binding.</p>
	 * <p>Callers should ensure that this method is called after a call to <code>IBinding.makeImmutable()</code> is made.</p>
	 * @return the internal type environment
	 * @throws UnsupportedOperationException if called before binding is made immutable
	 */
	public ITypeEnvironment getTypeEnvironment();
	
	/**
	 * <p>Makes the binding immutable, and therefore, not accepting new mappings.</p>
	 * <p>A call to this method will generate the internal type environment of this binding.</p>
	 */
	public void makeImmutable();
	
	/**
	 * <p>Adds the given mapping between <code>ident</code> and <code>e</code> if the following three conditions hold:</p>
	 *<ul>
	 * <p>1- If a binding for <code>ident</code> already exists, then the new binding has to equal the old binding.</p>
	 * <p>2- If a binding for <code>ident</code> does not exist, then the type of <code>ident</code> and <code>e</code> must be unifiable.</p>
	 * <p>3- If <code>ident</code> is a given type, then <code>e</code> must be a type expression.</p>
	 * </ul>
	 * <p>Adding a new mapping is possible so long as the binding is not immutable.</p>
	 * <p>Returns whether the mapping has been insterted (therefore meeting the aforementioned conditions).</p>
	 * @param ident the pattern identifier
	 * @param e the expression
	 * @return whether the mapping is added
	 * @throws UnsupportedOperationException if called after binding is made immutable
	 */
	public boolean putMapping(FreeIdentifier ident, Expression e);
	
	/**
	 *  <p>Returns whether all mapping in <code>another</code> have been successfully added to this binding.</p> 
	 * @param another the other binding
	 * @return whether all entries of <code>another</code> have been successfully added
	 * @throws UnsupportedOperationException if called after binding is made immutable
	 * @throws IllegalArgumentException if <code>another</code> is not immutable
	 */
	public boolean insertAllMappings(IBinding another);
	
	/**
	 * Returns whether this binding is immutable.
	 * @return <code>true</code> if this binding is immutable
	 */
	public boolean isImmutable();
	
	/**
	 * Returns whether the potential mapping between <code>ident</code> and <code>e</code> is an acceptable mapping i.e., satisfies 
	 * <code>IBinding.putMapping(FreeIdentifier, Expression)</code> constraints.
	 * <p>If the binding is immutable, this method returns <code>false</code>.</p>
	 * @param ident the free identifier to map
	 * @param e the expression
	 * @return <code>true</code> if the mapping is acceptable
	 */
	public boolean isMappingInsertable(FreeIdentifier ident, Expression e);
	
	/**
	 * Returns whether <code>binding</code> is insertable in this binding.
	 * <p>If the binding is immutable, this method returns <code>false</code>.</p>
	 * <p>Callers must ensure that <code>binding</code> is immutable.</p>
	 * @param binding the binding to insert
	 * @return whether all the mappings in <code>binding</code> are acceptable
	 */
	public boolean isBindingInsertable(IBinding binding);
}
