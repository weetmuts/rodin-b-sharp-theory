package org.eventb.theory.rbp.tactics.applications;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.AstUtilities.PositionPoint;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.ManualRewriteReasoner;
import org.eventb.theory.rbp.reasoners.input.RewriteInput;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.IPositionApplication;

/**
 * 
 * @author maamria
 *
 */
public class RewriteTacticApplication extends DefaultPositionApplication implements IPositionApplication {

	private static final String TACTIC_ID = RbPPlugin.PLUGIN_ID + ".RbP0";
	
	private RewriteInput input;

	public RewriteTacticApplication(RewriteInput input) {
		super(input.predicate, input.position);
		this.input = input;
	}

	public Point getHyperlinkBounds(String parsedString,
			Predicate parsedPredicate) {
		return getOperatorPosition(parsedPredicate,
				parsedString);
	}

	public String getHyperlinkLabel() {
		return input.description;
	}

	public ITactic getTactic(String[] inputs, String globalInput) {
		ManualRewriteReasoner reasoner = new ManualRewriteReasoner();
		return BasicTactics.reasonerTac(reasoner, input);
	}

	public String getTacticID() {
		return TACTIC_ID;
	}
	
	public Point getOperatorPosition(Predicate predicate, String predStr) {
		Formula<?> subFormula = predicate.getSubFormula(position);
		if (subFormula instanceof ExtendedExpression) {
			ExtendedExpression exp = (ExtendedExpression) subFormula;
			// TODO temporary fix to core issue about redlinks
			// (can't handle rewriting with constructor/destructor at root) 
			// Uncomment the lines below when core fixes it (DefaultTacticProvider)
//			IFormulaExtension extension = exp.getExtension();
//			if(AstUtilities.isATheoryExtension(extension)){
				PositionPoint point = AstUtilities.getPositionOfOperator(exp, predStr);
				return new Point(point.getX(), point.getY());
//			}
		}
		if (subFormula instanceof ExtendedPredicate) {
			ExtendedPredicate pred = (ExtendedPredicate) subFormula;
//			IFormulaExtension extension = pred.getExtension();
//			if(AstUtilities.isATheoryExtension(extension)){
				PositionPoint point = AstUtilities.getPositionOfOperator(pred, predStr);
				return new Point(point.getX(), point.getY());
//			}
		}
		return super.getOperatorPosition(predicate, predStr);
	}
}
