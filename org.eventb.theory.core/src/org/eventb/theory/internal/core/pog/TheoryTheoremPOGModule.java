/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryTheoremPOGModule extends TheoryPOGBaseModule {

	public static final IModuleType<TheoryTheoremPOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryTheoremModule"); //$NON-NLS-1

	private final static String THEOREM_WD_SUFFIX = "/WD-THM";
	private final static String THEOREM_S_SUFFIX = "/S-THM";

	private final static String THEOREM_WD_DESC = "Well-Definedness of Theorem";
	private final static String THEOREM_SOUNDNESS_DESC = "Therorem Soundness";

	private ITypeEnvironment typeEnvironment;
	private POGNatureFactory natureFactory;

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		natureFactory = POGNatureFactory.getInstance();
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IPORoot target = repository.getTarget();
		IRodinFile rodinFile = (IRodinFile) element;
		ISCTheoryRoot root = (ISCTheoryRoot) rodinFile.getRoot();
		ISCTheorem[] theorems = root.getTheorems();
		IPOPredicateSet hyp = target
				.getPredicateSet(TheoryTypeParametersPOGModule.ABS_HYP_NAME);
		for (ISCTheorem theorem : theorems) {
			String name = theorem.getLabel();
			IPOGSource[] sources = new IPOGSource[] { makeSource(
					IPOSource.DEFAULT_ROLE, theorem.getSource()) };
			Predicate poPredicate = theorem.getPredicate(factory,
					typeEnvironment);
			if (!isTrivial(poPredicate)) {
				String sequentName1 = name + THEOREM_S_SUFFIX;
				createPO(target, sequentName1,
						natureFactory.getNature(THEOREM_SOUNDNESS_DESC), hyp,
						getHyps(accumulator, root), makePredicate(poPredicate, theorem.getSource()),
						sources, new IPOGHint[] {
								getLocalHypothesisSelectionHint(target, sequentName1, hyp)
						}, true, monitor);
				Predicate wdPredicate = poPredicate.getWDPredicate(factory);
				if (!isTrivial(wdPredicate)) {
					String sequentName2 = name + THEOREM_WD_SUFFIX;
					createPO(target, name + THEOREM_WD_SUFFIX,
							natureFactory.getNature(THEOREM_WD_DESC), hyp,
							getHyps(accumulator, root),
							makePredicate(wdPredicate, theorem.getSource()),
							sources,  new IPOGHint[] {
							getLocalHypothesisSelectionHint(target, sequentName2, hyp)
							}, true, monitor);
				}
				accumulator.addHypothesis(poPredicate, name);
			}
		}
		accumulator.makeImmutable();

	}

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		natureFactory = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
	
}
