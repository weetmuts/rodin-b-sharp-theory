/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.core.pog.states.TheoremsAccumulator;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public abstract class TheoryPOGBaseModule extends POGProcessorModule {

	public static boolean DEBUG_TRIVIAL = false;

	protected static final IPOGSource[] NO_SOURCES = new IPOGSource[0];
	protected static final IPOGHint[] NO_HINTS = new IPOGHint[0];
	protected static final List<IPOGPredicate> emptyPredicates = new ArrayList<IPOGPredicate>(
			0);

	protected FormulaFactory factory;
	protected TheoremsAccumulator accumulator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);

		factory = repository.getFormulaFactory();
		accumulator = (TheoremsAccumulator) repository.getState(TheoremsAccumulator.STATE_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		factory = null;
		accumulator = null;
		super.endModule(element, repository, monitor);
	}

	/**
	 * Returns a list of hypotheses in the given theorems accumulator.
	 * 
	 * @param accumulator
	 *            the theorems accumulator
	 * @param root
	 *            the SC theory root
	 * @return the list of hypotheses
	 */
	protected static List<IPOGPredicate> getHyps(
			TheoremsAccumulator accumulator, ISCTheoryRoot root) {
		Map<String, Predicate> preds = accumulator.getHypotheses();
		List<IPOGPredicate> hyps = new LinkedList<IPOGPredicate>();
		for (String key : preds.keySet()) {
			hyps.add(makePredicate(preds.get(key), root.getTheorem(key)));
		}
		return hyps;
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
}
