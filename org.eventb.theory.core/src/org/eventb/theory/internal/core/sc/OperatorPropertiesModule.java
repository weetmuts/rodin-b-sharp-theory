/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import static org.eventb.core.ast.extension.IOperatorProperties.Notation;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class OperatorPropertiesModule extends SCProcessorModule {

	IModuleType<OperatorPropertiesModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".operatorPropertiesModule");

	private ITypeEnvironment typeEnvironment;
	private IOperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		if (target != null) {
			INewOperatorDefinition opDef = (INewOperatorDefinition) element;
			List<String> args = CoreUtilities.getOperatorArguments(typeEnvironment);
			// by this point the operator definition has all the attributes 
			// need to check for allowable extension kinds 
			boolean isCommutative = opDef.isCommutative();
			boolean isAssos = opDef.isAssociative();
			Notation notation = opDef.getNotationType();
			int arity = args.size();
			FormulaType formType = opDef.getFormulaType();
			if(!checkOperatorProperties(opDef,formType, notation, arity, isAssos, isCommutative, args)){
				operatorInformation.setHasError();
			}
			if(operatorInformation.getWdCondition() == null){
				operatorInformation.setHasError();
			}
		}

	}
	
	protected boolean checkOperatorProperties(INewOperatorDefinition opDef, FormulaType formType, 
			Notation notation, int arity, boolean isAssos, boolean isCommutative, List<String> args)
	throws RodinDBException{
		String opID = opDef.getLabel();
		switch (formType) 
		{
		case EXPRESSION:{
			switch(notation)
			{
			case PREFIX:{
				// Prefix exp cannot be associative
				if(isAssos){
					createProblemMarker(opDef, TheoryAttributes.ASSOCIATIVE_ATTRIBUTE, TheoryGraphProblem.OperatorExpPrefixCannotBeAssos);
					return false;
				}
				// Check commutativity
				if(isCommutative && !checkCommutativity(args)){
					createProblemMarker(opDef, TheoryAttributes.COMMUTATIVE_ATTRIBUTE, TheoryGraphProblem.OperatorCannotBeCommutError, opID);
					return false;
				}
				break;
			}
			case INFIX:{
				// Infix exp operator needs at least two arguments
				if(arity < 2){
					createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorExpInfixNeedsAtLeastTwoArgs);
					return false;
				}
				else {
					// Check associativity
					if(isAssos && !checkAssociativity(args)){
						createProblemMarker(opDef,
								TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
								TheoryGraphProblem.OperatorCannotBeAssosWarning, opID);
						return false;
					}
					// Check commutativity
					if(isCommutative && !checkCommutativity(args)){
						createProblemMarker(opDef, TheoryAttributes.COMMUTATIVE_ATTRIBUTE, TheoryGraphProblem.OperatorCannotBeCommutError, opID);
						return false;
					}
				}
				break;
			}
			// Postfix not supported yet
			case POSTFIX:{
				createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorExpCannotBePostfix);
				return false;
			}
			}
			break;
		}
		case PREDICATE:{
			// Predicate operators need at least one argument
			if(arity < 1){
				createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorPredNeedOneOrMoreArgs);
				return false;
			}
			// Predicate operators cannot be associative
			if(isAssos){
				createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorPredCannotBeAssos);
				return false;
			}
			// Check commutativity
			if(isCommutative && !checkCommutativity(args)){
				createProblemMarker(opDef,
						TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
						TheoryGraphProblem.OperatorCannotBeCommutError, opID);
				return false;
			}
			switch(notation)
			{
			case PREFIX:{
				return true;
			}
			// Infix and postfix predicate not supported yet
			case INFIX:
			case POSTFIX:{
				createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorPredOnlyPrefix);
				return false;
			}
			}
			break;
		}
		}
		return true;
	}
	
	/**
	 * An operator can be commutative if it can have two arguments of the same type.
	 * @param args the operator arguments
	 * @return whether this operator can be commutative
	 */
	protected boolean checkCommutativity(List<String> args) {
		boolean ok = (args.size() == 2);
		Type type = null;
		for(String arg : args){
			if(type == null){
				type = typeEnvironment.getType(arg);
			}
			ok &= (type.equals(typeEnvironment.getType(arg)));
		}
		return ok;
	}
	
	/**
	 * An operator can be associative if it can have at least two arguments of the same type, which has to be the same
	 * as the resultant type.
	 * @param args the operator arguments
	 * @return whether this operator can be associative
	 */
	protected boolean checkAssociativity(List<String> args) {
		boolean ok = operatorInformation.isExpressionOperator();
		ok &= (args.size() >= 2);
		Type type = null;
		for(String arg : args){
			if(type == null){
				type = typeEnvironment.getType(arg);
			}
			ok &= (type.equals(typeEnvironment.getType(arg)));
		}
		ok &= (type.equals(operatorInformation.getResultantType()));
		
		return ok;
	}
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

}
