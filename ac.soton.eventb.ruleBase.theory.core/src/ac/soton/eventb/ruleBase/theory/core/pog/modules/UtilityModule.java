/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
@SuppressWarnings("restriction")
public abstract class UtilityModule extends POGProcessorModule {
	
	public static boolean DEBUG_TRIVIAL = false;
	
	protected POGNatureFactory natureFactory = POGNatureFactory.getInstance();

	
	protected static final IPOGSource[] NO_SOURCES = new IPOGSource[0];
	protected static final IPOGHint[] NO_HINTS = new IPOGHint[0];
	protected static final List<IPOGPredicate> emptyPredicates = new ArrayList<IPOGPredicate>(0);

	protected Predicate btrue;
	protected Predicate bfalse;
	protected FormulaFactory factory;

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		factory = FormulaFactory.getDefault();
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
		bfalse = factory.makeLiteralPredicate(Formula.BFALSE, null);
	}
	
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		factory = null;
		btrue = null;
		bfalse = null;
		super.endModule(element, repository, monitor);
	}
	
	private boolean goalIsNotRestricting(Predicate goal) {
		if (goal instanceof RelationalPredicate) {
			RelationalPredicate relGoal = (RelationalPredicate) goal;
			switch (relGoal.getTag()) {
			case Formula.IN:
			case Formula.SUBSETEQ:
				Expression expression = relGoal.getRight();
				Type type = expression.getType();
				Type baseType = type.getBaseType(); 
				if (baseType == null)
					return false;
				Expression typeExpression = baseType.toExpression(factory);
				if (expression.equals(typeExpression))
					return true;
				break;
			default:
				return false;
			}
		}
		return false;
	}

	protected boolean goalIsTrivial(Predicate goal) {
		return goal.equals(btrue) || goalIsNotRestricting(goal);
	}

	protected void debugTraceTrivial(String sequentName) {
		System.out.println("POG: " + getClass().getSimpleName() + ": Filtered trivial PO: " + sequentName);
	}

}


/**
 * All predicates stored in a PO file have an associated source reference.
 * @see IPOPredicate
 * 
 * @author Stefan Hallerstede
 *
 */
class POGPredicate implements IPOGPredicate {
	
	private final IRodinElement source;
	private final Predicate predicate;
	
	/**
	 * Creates a predicate with an associated source reference to be stored in a PO file.
	 * @param predicate a predicate
	 * @param source an associated source
	 */
	POGPredicate(Predicate predicate, IRodinElement source) {
		this.source = source;
		this.predicate = predicate;
	}
	
	/**
	 * Returns the source reference for the predicate.
	 * 
	 * @return the source reference for the predicate
	 * @throws RodinDBException if there was a problem accessing the source reference
	 */
	public IRodinElement getSource() throws RodinDBException {
		return source;
	}
	
	/**
	 * Returns the predicate.
	 * 
	 * @return the predicate
	 */
	public Predicate getPredicate() {
		return predicate;
	}

}

