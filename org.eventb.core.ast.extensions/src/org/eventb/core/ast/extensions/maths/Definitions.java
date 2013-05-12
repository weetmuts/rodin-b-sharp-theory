package org.eventb.core.ast.extensions.maths;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

public class Definitions {

	public enum DefinitionType {Direct, Recursive, Axiomatic}
	
	/**
	 * 
	 * A direct definition
	 * @author maamria
	 *
	 */
	public static final class DirectDefintion implements IDefinition{
		private Formula<?> directDefintion;
		
		public DirectDefintion(Formula<?> directDefinition){
			this.directDefintion = directDefinition;
		}
		
		public Formula<?> getDefinition(){
			return directDefintion;
		}

		@Override
		public DefinitionType getDefinitionType() {
			// TODO Auto-generated method stub
			return DefinitionType.Direct;
		}
	}
	
	/**
	 * An axiomatic definition
	 * @author maamria
	 *
	 */
	public static final class AxiomaticDefinition implements IDefinition{
		private List<Predicate> axioms;
		
		public AxiomaticDefinition(List<Predicate> axioms){
			this.axioms = axioms;
		}
		
		public List<Predicate> getAxioms(){
			return Collections.unmodifiableList(axioms);
		}

		@Override
		public DefinitionType getDefinitionType() {
			// TODO Auto-generated method stub
			return DefinitionType.Axiomatic;
		}
	}

	/**
	 * A recursive definition
	 * @author maamria
	 *
	 */
	public static final class RecursiveDefinition implements IDefinition{
		
		private FreeIdentifier operatorArgument;
		
		private Map<Expression, Formula<?>> recursiveCases;
		
		public RecursiveDefinition(FreeIdentifier operatorArgument, Map<Expression, Formula<?>> recursiveCases){
			this.operatorArgument = operatorArgument;
			this.recursiveCases = recursiveCases;
		}
		
		public FreeIdentifier getOperatorArgument(){
			return operatorArgument;
		}
		
		public Map<Expression, Formula<?>> getRecursiveCases(){
			return Collections.unmodifiableMap(recursiveCases);
		}

		@Override
		public DefinitionType getDefinitionType() {
			// TODO Auto-generated method stub
			return DefinitionType.Recursive;
		}
		
	}

	/**
	 * Marker interface.
	 * @author maamria
	 *
	 */
	public static interface IDefinition{
		public DefinitionType getDefinitionType();
	}
	
}
