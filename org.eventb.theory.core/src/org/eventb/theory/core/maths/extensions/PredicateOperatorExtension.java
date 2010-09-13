/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.core.maths.PredicateOperatorTypingRule;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class PredicateOperatorExtension extends AbstractOperatorExtension<IPredicateExtension>
		implements IPredicateExtension {

	public PredicateOperatorExtension(
			String operatorID, String syntax, FormulaType formulaType,
			Notation notation, boolean isCommutative,
			Predicate directDefinition, Predicate wdCondition,
			List<IOperatorArgument> opArguments, List<GivenType> typeParameters, IRodinElement source) {
		super(operatorID, syntax, formulaType, notation, isCommutative,
				directDefinition, wdCondition, opArguments, source);
		
		this.operatorTypeRule = new PredicateOperatorTypingRule(this);
		Collections.sort(opArguments);
		for(IOperatorArgument arg : opArguments){
			this.operatorTypeRule.addOperatorArgument(arg);
		}
		this.operatorTypeRule.addTypeParameters(typeParameters);
		this.operatorTypeRule.setWDPredicate(wdCondition);

	}

	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(notation, formulaType,
				ExtensionFactory.makeAllExpr(ExtensionFactory.makeArity(
						opArguments.size(), opArguments.size())), false);
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator tcMediator) {
		
	}
	

	public boolean equals(Object o){
		if(!(o instanceof PredicateOperatorExtension)){
			return false;
		}
		PredicateOperatorExtension operatorExtension = (PredicateOperatorExtension) o;
		boolean equals =
			this.operatorID.equals(operatorExtension.operatorID);
		return equals;
	}
	
	protected PredicateOperatorTypingRule getTypingRule(){
		return (PredicateOperatorTypingRule) operatorTypeRule;
	}

}
