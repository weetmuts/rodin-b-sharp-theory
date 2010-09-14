/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IElementTransformer;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.AbstractOperatorExtension;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.maths.extensions.OperatorTransformer;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class TheoryOperatorDefinitionPOGModule extends
		TheoryAbstractExtensionModule<ISCNewOperatorDefinition> {

	/**
	 * 
	 */
	protected static final String OPERATOR_WD_PO = "Operator Well-Definedness Preservation";
	protected static final String OPERATOR_WD_POSTFIX = "/Op-WD";
	protected static final String OPERATOR_COMMUT_PO = "Operator Commutativity";
	protected static final String OPERATOR_COMMUT_POSTFIX = "/Op-COMMUT";
	protected static final String OPERATOR_ASSOC_PO = "Operator Associativity";
	protected static final String OPERATOR_ASSOC_POSTFIX = "/Op-ASSOC";

	public static final IModuleType<TheoryOperatorDefinitionPOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".theoryOperatorDefinitionModule"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected IElementTransformer<ISCNewOperatorDefinition, Set<IFormulaExtension>> getTransformer() {
		// TODO Auto-generated method stub
		return new OperatorTransformer();
	}

	@Override
	protected ISCNewOperatorDefinition[] getExtensionElements(
			ISCTheoryRoot parent) throws CoreException {
		// TODO Auto-generated method stub
		return parent.getSCNewOperatorDefinitions();
	}

	@Override
	protected void generateCorrespondingPOs(IFormulaExtension extension,
			IProgressMonitor monitor) throws CoreException {
		if (extension instanceof AbstractOperatorExtension<?>) {
			AbstractOperatorExtension<?> operatorExtension = (AbstractOperatorExtension<?>) extension;

			// generate WD-PO
			Predicate wdPOPredicate = operatorExtension
					.getWellDefinednessChecker(factory, typeEnvironment);
			IPOPredicateSet hyp = target
					.getPredicateSet(TheoryTypeParametersPOGModule.ABS_HYP_NAME);
			IRodinElement origin = (IRodinElement) operatorExtension.getOrigin();
			if (wdPOPredicate != null) {
				IPOGNature nature = makeNature(OPERATOR_WD_PO);
				createPO(
						target,
						operatorExtension.getId() + OPERATOR_WD_POSTFIX,
						nature,
						hyp,
						null,
						makePredicate(wdPOPredicate,
								origin),
						new IPOGSource[0], new IPOGHint[0], true, monitor);
			}

			// generate Commut-PO
			Predicate commutPOPredicate = operatorExtension
					.getCommutativityChecker(factory, typeEnvironment);
			if (commutPOPredicate != null) {
				IPOGNature nature = makeNature(OPERATOR_COMMUT_PO);
				createPO(
						target,
						operatorExtension.getId() + OPERATOR_COMMUT_POSTFIX,
						nature,
						hyp,
						null,
						makePredicate(commutPOPredicate,
								origin),
						new IPOGSource[0], new IPOGHint[0], true, monitor);
			}
			// generate Assos-PO
			Predicate assosPOPredicate = operatorExtension
					.getAssociativityChecker(factory, typeEnvironment);
			if (assosPOPredicate != null) {
				IPOGNature nature = makeNature(OPERATOR_ASSOC_PO);
				
				createPO(
						target,
						operatorExtension.getId() + OPERATOR_ASSOC_POSTFIX,
						nature,
						hyp,
						null,
						makePredicate(assosPOPredicate,
								origin),
						new IPOGSource[0], new IPOGHint[0], true, monitor);
			}
		}
	}

}
