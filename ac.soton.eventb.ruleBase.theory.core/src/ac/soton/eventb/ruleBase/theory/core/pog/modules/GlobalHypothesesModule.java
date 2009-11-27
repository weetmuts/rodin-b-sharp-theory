/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISCSet;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ISCVariable;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GlobalHypothesesModule extends UtilityModule {

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		index = 0;
		
		typeEnvironment = repository.getTypeEnvironment();
	}
	
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		super.endModule(element, repository, monitor);
	}

	private static String PRD_NAME_PREFIX = "PRD";
	protected ITypeEnvironment typeEnvironment;
	protected int index;

	protected void fetchSetsAndVariables(
			ISCTheoryRoot root, 
			IPOPredicateSet rootSet, 
			IProgressMonitor monitor) throws RodinDBException {
		for (ISCSet set : root.getSCSets()) {
			FreeIdentifier identifier = fetchIdentifier(set);
			createIdentifier(rootSet, identifier, monitor);
		}
		for (ISCVariable var : root.getSCVariables()) {
			FreeIdentifier identifier = fetchIdentifier(var);
			createIdentifier(rootSet, identifier, monitor);
		}
	}

	protected void createIdentifier(
			IPOPredicateSet predSet, 
			FreeIdentifier identifier, 
			IProgressMonitor monitor) throws RodinDBException {
		String idName = identifier.getName();
		Type type = identifier.getType();
		IPOIdentifier poIdentifier = predSet.getIdentifier(idName);
		poIdentifier.create(null, monitor);
		poIdentifier.setType(type, monitor);
	}

	protected FreeIdentifier fetchIdentifier(ISCIdentifierElement ident) throws RodinDBException {
		FreeIdentifier identifier = ident.getIdentifier(factory);
		typeEnvironment.add(identifier);
		return identifier;
	}

	protected void savePOPredicate(IPOPredicateSet rootSet, ISCPredicateElement element, IProgressMonitor monitor) throws RodinDBException {
		IPOPredicate predicate = rootSet.getPredicate(PRD_NAME_PREFIX + index++);
		predicate.create(null, monitor);
		predicate.setPredicateString(element.getPredicateString(), monitor);
		predicate.setSource(((ITraceableElement) element).getSource(), monitor);
	}

	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
		throws CoreException {
		
		// all is done in the initialisation part

	}

}
