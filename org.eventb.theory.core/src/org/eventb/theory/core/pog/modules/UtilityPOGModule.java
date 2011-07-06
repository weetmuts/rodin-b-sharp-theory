/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.pog.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.theory.core.wd.DComputer;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public abstract class UtilityPOGModule extends POGProcessorModule {

	public static boolean DEBUG_TRIVIAL = false;

	protected static final IPOGSource[] NO_SOURCES = new IPOGSource[0];
	protected static final IPOGHint[] NO_HINTS = new IPOGHint[0];
	protected static final List<IPOGPredicate> EMPTY_PREDICATES = new ArrayList<IPOGPredicate>(0);

	protected FormulaFactory factory;

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);

		factory = repository.getFormulaFactory();
	}

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		factory = null;
		super.endModule(element, repository, monitor);
	}

	/**
	 * Returns a local hypotheses selection hint.
	 * 
	 * @param target
	 *            the PO target
	 * @param sequentName
	 *            the sequent name
	 * @param upperLimit
	 *            the upper limit of hypotheses to include
	 * @return local hypotheses selection hint
	 * @throws RodinDBException
	 */
	protected IPOGHint getLocalHypothesisSelectionHint(IPORoot target,
			String sequentName, IPOPredicateSet upperLimit)
			throws RodinDBException {
		return makeIntervalSelectionHint(upperLimit,
				getSequentHypothesis(target, sequentName));
	}

	/**
	 * Returns the list of metavariables in the given type environment. This method assumes
	 * that all names that are not given sets are metavariables.
	 * @param typeEnvironment the type environment
	 * @return the list of metavariables
	 */
	protected List<FreeIdentifier> getMetavariables(
			ITypeEnvironment typeEnvironment) {
		FormulaFactory factory = typeEnvironment.getFormulaFactory();
		Set<String> all = typeEnvironment.clone().getNames();
		all.removeAll(MathExtensionsUtilities.getGivenSetsNames(typeEnvironment));
		List<FreeIdentifier> vars = new ArrayList<FreeIdentifier>();
		for (String name : all) {
			vars.add(factory.makeFreeIdentifier(name, null,
					typeEnvironment.getType(name)));
		}
		return vars;
	}
	
	/**
	 * Returns the D WD condition of the given formula.
	 * @param formula the formula
	 * @return the D WD condition
	 */
	protected Predicate getDWDCondition(Formula<?> formula){
		assert formula != null && formula.isTypeChecked();
		DComputer computer = new DComputer(factory);
		return computer.getWDLemma(formula);
	}
}
