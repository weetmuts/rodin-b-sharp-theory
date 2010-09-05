/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class PredicateOperatorExtension extends AbstractOperatorExtension implements IPredicateExtension{

	boolean isCommutative;
	
	public PredicateOperatorExtension(FormulaFactory factory,
			String operatorID, String syntax, FormulaType formulaType,
			Notation notation, boolean isCommutative, Formula<?> directDefinition,
			Predicate wdCondition, HashMap<String, Type> opArguments) {
		super(factory, operatorID, syntax, formulaType, notation, directDefinition,
				wdCondition, opArguments);
		this.isCommutative = isCommutative;
	}
	
	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(notation, formulaType,
				ExtensionFactory.makeAllExpr(ExtensionFactory
						.makeArity(opArguments.size(),
								opArguments.size())), false);
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
		Expression[] children = predicate.getChildExpressions();
		if (children.length == opArguments.size()) {

			String[] arguments = opArguments.keySet().toArray(
					new String[children.length]);
			for (int i = 0; i < arguments.length; i++) {
				instantiations.addArgumentMapping(arguments[i],
						children[i]);
			}
			Type[] argumentTypes = opArguments.values().toArray(
					new Type[arguments.length]);
			for (int i = 0; i < children.length; i++) {
				if (!instantiations.unifyTypes(argumentTypes[i],
						children[i].getType())) {
					tcMediator.sameType(argumentTypes[i],
							children[i].getType());
				}
			}
		}
	}

}
