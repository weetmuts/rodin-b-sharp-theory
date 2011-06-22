/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.AstUtilities;
import org.eventb.theory.core.AstUtilities.PositionPoint;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;

/**
 * @author maamria
 *
 */
public abstract class ExtendedPositionApplication extends DefaultPositionApplication{

	public ExtendedPositionApplication(Predicate hyp, IPosition position) {
		super(hyp, position);
	}
	
	public Point getOperatorPosition(Predicate predicate, String predStr) {
		Formula<?> subFormula = predicate.getSubFormula(position);
		if (subFormula instanceof ExtendedExpression) {
			ExtendedExpression exp = (ExtendedExpression) subFormula;
			IFormulaExtension extension = exp.getExtension();
			if(AstUtilities.isATheoryExtension(extension)){
				PositionPoint point = AstUtilities.getPositionOfOperator(exp, predStr);
				return new Point(point.getX(), point.getY());
			}
		}
		if (subFormula instanceof ExtendedPredicate) {
			ExtendedPredicate pred = (ExtendedPredicate) subFormula;
			IFormulaExtension extension = pred.getExtension();
			if(AstUtilities.isATheoryExtension(extension)){
				PositionPoint point = AstUtilities.getPositionOfOperator(pred, predStr);
				return new Point(point.getX(), point.getY());
			}
		}
		return super.getOperatorPosition(predicate, predStr);
	}

}
