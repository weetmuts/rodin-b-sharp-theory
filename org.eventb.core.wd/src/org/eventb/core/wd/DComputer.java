/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - Adaptation for the D-library
 *******************************************************************************/
package org.eventb.core.wd;

import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LEQV;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MOD;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor2;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IPredicateExtension;

/**
 * An implementation of well-definedness conditions computer for the D operator.
 * 
 * <p> TODO For the future base implementation on paper : <q>Efficient Well-definedness Checking</q>.
 * @author maamria, Laurent Voisin
 * @since 1.0
 */
public class DComputer implements ISimpleVisitor2{
	
	private DFormulaBuilder fb;

	/**
	 * This is the WD condition, it is the result of the last visit.
	 */
	private Predicate lemma;

	public DComputer(FormulaFactory formulaFactory) {
		this.fb = new DFormulaBuilder(formulaFactory);
	}
	
	/**
	 * Returns the well-definedness condition of the given formula.
	 * 
	 * <p> The returned formula is flattened.
	 * 
	 * @param formula the formula
	 * @return the WD condition
	 */
	public Predicate getWDLemma(Formula<?> formula) {
		assert formula.isTypeChecked();
		return wd(formula).flatten(fb.ff);
	}
	
	/**
	 * Returns the well-definedness condition of the given formula.
	 * 
	 * @param formula the formula
	 * @return the WD condition
	 */
	protected Predicate wd(Formula<?> formula) {
		formula.accept(this);
		return lemma;
	}
	
	/**
	 * Conjuncts the well-definedness condition of <code>left</code> and <code>right</code>.
	 * @param left the left predicate
	 * @param right the right predicate
	 * @return the conjunction of well-definedness of <code>left</code> and <code>right</code>
	 */
	protected Predicate wd(Formula<?> left, Formula<?> right) {
		return fb.land(wd(left), wd(right));
	}

	/**
	 * Conjuncts the well-definedness condition of <code>children</code>.
	 * @param children the children predicates
	 * @return the conjunction of well-definedness of <code>children</code>
	 */
	protected Predicate wd(Formula<?>... children) {
		final int length = children.length;
		final Predicate[] wds = new Predicate[length];
		for (int i = 0; i < length; i++) {
			wds[i] = wd(children[i]);
		}
		return fb.land(wds);
	}
	
	/**
	 * Generates a predicate that can be added as part of the well-definedness condition of a binary expression.
	 * @param expr the binary expression
	 * @param left the left expression
	 * @param right the right expression
	 * @return the additional WD predicate
	 */
	protected Predicate binExprWD(BinaryExpression expr, Expression left, Expression right) {
		switch (expr.getTag()) {
		case DIV:
			return fb.notZero(right);
		case MOD:
			return fb.land(fb.nonNegative(left), fb.positive(right));
		case EXPN:
			return fb.land(fb.nonNegative(left), fb.nonNegative(right));
		case FUNIMAGE:
			return fb.land(fb.inDomain(left, right), fb.partial(left));
		default:
			return fb.btrue;
		}
	}
	
	/**
	 * Returns the complement WD predicate of the unary expression with respect to its child.
	 * @param expr the unary expression
	 * @param child the child expression
	 * @return the additional WD predicate
	 */
	protected Predicate uExprWD(UnaryExpression expr, Expression child) {
		switch (expr.getTag()) {
		case KCARD:
			return fb.finite(child);
		case KMIN:
			return fb.land(fb.notEmpty(child), fb.bounded(child, true));
		case KMAX:
			return fb.land(fb.notEmpty(child), fb.bounded(child, false));
		case KINTER:
			return fb.notEmpty(child);
		default:
			return fb.btrue;
		}
	}
	
	/**
	 * Returns the additional WD predicate that corresponds to each child of <code>formula</code>.
	 * @param initialWD the initial WD
	 * @param formula the formula
	 * @return the additional WD condition
	 */
	protected Predicate addChildrenWD(Predicate initialWD, IExtendedFormula formula) {
		final Predicate exprWD = wd(formula.getChildExpressions());
		final Predicate predWD = wd(formula.getChildPredicates());
		final Predicate childWD = fb.land(exprWD, predWD);
		return fb.land(initialWD, childWD);
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		lemma = wd(assignment.getExpressions());
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		lemma = wd(assignment.getSet());
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		final BoundIdentDecl[] primedIdents = assignment.getPrimedIdents();
		lemma = fb.forall(primedIdents, wd(assignment.getCondition()));
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		lemma = fb.btrue;
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitIntegerLiteral(IntegerLiteral expression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		lemma = fb.btrue;
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		lemma = wd(expression.getChildren());
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		final Expression left = expression.getLeft();
		final Expression right = expression.getRight();
		lemma = fb.land(wd(left), wd(right), binExprWD(expression, left, right));
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		lemma = wd(expression.getPredicate());
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		final BoundIdentDecl[] decls = expression.getBoundIdentDecls();
		final Predicate pred = expression.getPredicate();
		final Expression expr = expression.getExpression();
		// \forall (wd(pred) AND pred=>wd(expr))
		final Predicate childrenWD = fb.forall(decls,//
				fb.land(wd(pred), fb.limp(pred, wd(expr))));
		final Predicate localWD;
		switch (expression.getTag()) {
		case QUNION:
		case CSET:
			localWD = fb.btrue;
			break;
		case QINTER:
			localWD = fb.exists(decls, pred);
			break;
		default:
			assert false;
			localWD = null;
			break;
		}
		lemma = fb.land(childrenWD, localWD);
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		lemma = wd(expression.getMembers());
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		final Expression child = expression.getChild();
		lemma = fb.land(wd(child), uExprWD(expression, child));
	}

	
	
	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		lemma = wd(predicate.getChildren());
	}

	@Override
	public void visitPredicateVariable(PredicateVariable predVar) {
		lemma = fb.btrue;
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		lemma = wd(predicate.getLeft(), predicate.getRight());
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		lemma = wd(predicate.getExpression());
	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		IPredicateExtension extension = predicate.getExtension();
		final DMediator wdMed = new DMediator(fb);
		final Predicate extensionWD = extension.getWDPredicate(predicate, wdMed);
		if (extension.conjoinChildrenWD()) {
			lemma = addChildrenWD(extensionWD, predicate);
		} else {
			lemma = extensionWD;
		}
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		IExpressionExtension extension = expression.getExtension();
		final DMediator wdMed = new DMediator(fb);
		final Predicate extensionWD = extension.getWDPredicate(expression, wdMed);
		if (extension.conjoinChildrenWD()) {
			lemma = addChildrenWD(extensionWD, expression);
		} else {
			lemma = extensionWD;
		}
	}
	
	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		final Predicate[] children = predicate.getChildren();
		switch (predicate.getTag()) {
		case LAND:
			lemma = landWD(children);
			break;
		case LOR:
			lemma = lorWD(children);
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}
	
	protected Predicate lorWD(Predicate[] children) {
		if (children.length == 0) {
			return fb.btrue;
		}
		if (children.length == 1) {
			return wd(children[0]);
		}
		Predicate[] subPredicates = getSubPredicates(children, 1);
		Predicate subWD = landWD(subPredicates);
		final Predicate subPred = fb.lor(subPredicates);
		final Predicate child = children[0];
		subWD = fb.lor(fb.land(wd(child), fb.limp(fb.negate(child), subWD)), fb.land(subWD, fb.limp(fb.negate(subPred), wd(child))));
		return subWD;
	}

	protected Predicate landWD(Predicate[] children) {
		if (children.length == 0) {
			return fb.btrue;
		}
		if (children.length == 1) {
			return wd(children[0]);

		}
		Predicate[] subPredicates = getSubPredicates(children, 1);
		Predicate subWD = landWD(subPredicates);
		final Predicate subPred = fb.land(subPredicates);
		final Predicate child = children[0];
		subWD = fb.lor(fb.land(wd(child), fb.limp(child, subWD)), fb.land(subWD, fb.limp(subPred, wd(child))));
		return subWD;
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		final Predicate left = predicate.getLeft();
		final Predicate right = predicate.getRight();
		switch (predicate.getTag()) {
		case LIMP:
			lemma = fb.lor(fb.land(wd(left), wd(right)), fb.land(wd(left), fb.negate(left)), fb.land(wd(right), right));
			break;
		case LEQV:
			lemma = fb.land(wd(left), wd(right));
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}
	
	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();
		final Predicate child = predicate.getPredicate();
		// the WD is \forall child OR \exists (wd(child) AND child)
		if (predicate.getTag() == Formula.EXISTS) {
			lemma = fb.lor(fb.forall(decls, wd(child)), fb.exists(decls, fb.land(wd(child), child)));
		}
		// the WD is \forall child OR \exists (wd(child) AND not child)
		else {
			lemma = fb.lor(fb.forall(decls, wd(child)), fb.exists(decls, fb.land(wd(child), fb.negate(child))));
		}
	}
	
	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		lemma = wd(predicate.getChild());
	}
	
	protected Predicate[] getSubPredicates(Predicate[] predicates, int startingIndex) {
		if (startingIndex >= predicates.length || startingIndex < 0) {
			return new Predicate[0];
		}
		Predicate[] newPreds = new Predicate[predicates.length - startingIndex];
		for (int k = startingIndex; k < predicates.length; k++) {
			newPreds[k - startingIndex] = predicates[k];
		}
		return newPreds;
	}
}
