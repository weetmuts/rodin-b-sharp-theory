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
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class UtilityModule extends POGProcessorModule {
	
	public static boolean DEBUG_TRIVIAL = false;

	
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
	
	protected Predicate makeCompletenessPredicate(
			ArrayList<Predicate> allConditions, Predicate lhsWD) {
		assert allConditions . size() > 0;
		Predicate toProve = null;
		Predicate right = null;
		if(allConditions.size() == 1){
			allConditions.trimToSize();
			right = (Predicate) allConditions.get(0);
		}
		else 
			right = factory.makeAssociativePredicate(Formula.LOR, 
					allConditions, 
					null);
		if(!lhsWD.equals(btrue)){
			toProve = factory.makeBinaryPredicate(Formula.LIMP, 
					lhsWD, 
					right, 
					null);
		}
		else {
			toProve = right;
		}
		return toProve;
	}
	protected Predicate makeCondWDPredicate(Predicate condWD, Predicate lhsWD){
		Predicate toProve = null;
		if(!lhsWD.equals(btrue)){
			toProve = factory.makeBinaryPredicate(Formula.LIMP, lhsWD, condWD, null);
		}
		else {
			toProve = condWD;
		}
		return toProve;
	}
	
	protected Predicate makeRhsWDorSoundnessPredicate(Predicate pred, Predicate lhsWD, Predicate condition){
		Predicate toProve = null;
		if(!lhsWD.equals(btrue)){
			Predicate conj;
			if(!condition.equals(btrue)){
				conj = factory.makeAssociativePredicate(Formula.LAND, 
						new Predicate[]{lhsWD, condition}, null);
			}
			else {
				conj = lhsWD;
			}
			toProve = factory.makeBinaryPredicate(Formula.LIMP, conj, pred, null);
		}
		else {
			if(!condition.equals(btrue)){
				toProve = factory.makeBinaryPredicate(Formula.LIMP, condition, pred, null);
			}
			else {
				toProve = pred;
			}
		}
		return toProve;
	}
	
	
	protected void debugTraceTrivial(String sequentName) {
		System.out.println("POG: " + getClass().getSimpleName() + ": Filtered trivial PO: " + sequentName);
	}

}
