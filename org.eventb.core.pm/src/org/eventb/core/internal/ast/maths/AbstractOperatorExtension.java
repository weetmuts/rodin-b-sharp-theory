/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.maths;

import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.maths.AstUtilities;
import org.eventb.core.ast.maths.IOperatorExtension;
import org.eventb.core.ast.maths.OperatorExtensionProperties;
import org.eventb.internal.core.ast.extension.ExtensionKind;

/**
 * Basic implementation for an operator extension.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractOperatorExtension implements IOperatorExtension  {
	
	protected OperatorExtensionProperties properties;
	private String operatorGroup;
	protected IOperatorTypingRule operatorTypingRule;
	protected boolean isCommutative = false;
	protected boolean isAssociative = false;
	/**
	 * Source could be <code>IRodinElement</code>
	 */
	protected Object source;

	/**
	 * Constructs an operator extension within the specified operator group
	 * using the supplied details.
	 * 
	 * @param operatorID
	 *            the operator ID
	 * @param syntax
	 *            the syntax symbol
	 * @param formulaType
	 *            formula type
	 * @param notation
	 *            the notaion
	 * @param groupID
	 * @param isCommutative
	 *            whether operator is commutative
	 * @param isAssocaitive
	 *            whether operator is associative
	 * @param operatorTypingRule
	 *            the typing rule
	 * @param directDefinition
	 *            the definition if any
	 * @param source
	 *            the origin
	 */
	protected AbstractOperatorExtension(OperatorExtensionProperties properties,
			boolean isCommutative, boolean isAssociative,
			IOperatorTypingRule operatorTypingRule,
			Object source) {
		this.properties = properties;
		this.isCommutative = isCommutative;
		this.isAssociative = isAssociative;
		this.operatorTypingRule = operatorTypingRule;
		this.source = source;
		this.operatorGroup = properties.getGroupID() == null ? AstUtilities
				.getGroupFor(properties.getFormulaType(), properties.getNotation(),
						this.operatorTypingRule.getArity()) : properties.getGroupID();
	}

	@Override
	public String getSyntaxSymbol() {
		return properties.getSyntax();
	}

	@Override
	public String getId() {
		return properties.getOperatorID();
	}

	@Override
	public String getGroupId() {
		return operatorGroup;
	}

	@Override
	public Object getOrigin() {
		return source;
	}

	public boolean equals(Object o) {
		if(o == this)
			return true;
		if (o == null || !(o instanceof AbstractOperatorExtension)) {
			return false;
		}
		AbstractOperatorExtension abs = (AbstractOperatorExtension) o;
		return abs.properties.equals(properties)
				&& abs.isAssociative == isAssociative
				&& abs.isCommutative == isCommutative
				&& abs.operatorTypingRule.equals(operatorTypingRule);
		
	}

	public int hashCode() {
		return 17 * properties.hashCode() + (new Boolean(isAssociative)).hashCode() 
				+ (new Boolean(isCommutative)).hashCode() +
				operatorTypingRule.hashCode();
	}
	
	public String toString(){
		return properties.getSyntax() +"==" + operatorTypingRule.toString();
	}
	
	public boolean isCommutative(){
		return isCommutative;
	}
	
	@Override
	public boolean isAssociative() {
		return isAssociative;
	}
	
	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(properties.getNotation(), properties.getFormulaType(),
				ExtensionFactory.makeAllExpr(ExtensionFactory.makeArity(
						operatorTypingRule.getArity(), operatorTypingRule.getArity())), false);
	}
	
}
