package ac.soton.eventb.prover.internal.engine;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.internal.engine.exp.AssociativeExpressionMatcher;
import ac.soton.eventb.prover.internal.engine.exp.AtomicExpressionMatcher;
import ac.soton.eventb.prover.internal.engine.exp.BinaryExpressionMatcher;
import ac.soton.eventb.prover.internal.engine.exp.BoolExpressionMatcher;
import ac.soton.eventb.prover.internal.engine.exp.BoundIdentifierMatcher;
import ac.soton.eventb.prover.internal.engine.exp.IntegerLiteralMatcher;
import ac.soton.eventb.prover.internal.engine.exp.QuantifiedExpressionMatcher;
import ac.soton.eventb.prover.internal.engine.exp.SetExtensionMatcher;
import ac.soton.eventb.prover.internal.engine.exp.UnaryExpressionMatcher;
import ac.soton.eventb.prover.internal.engine.pred.AssociativePredicateMatcher;
import ac.soton.eventb.prover.internal.engine.pred.BinaryPredicateMatcher;
import ac.soton.eventb.prover.internal.engine.pred.LiteralPredicateMatcher;
import ac.soton.eventb.prover.internal.engine.pred.QuantifiedPredicateMatcher;
import ac.soton.eventb.prover.internal.engine.pred.RelationalPredicateMatcher;
import ac.soton.eventb.prover.internal.engine.pred.SimplePredicateMatcher;
import ac.soton.eventb.prover.internal.engine.pred.UnaryPredicateMatcher;

public class MatchersDatabase {

	private static final RelationalPredicateMatcher rpMatcher = new RelationalPredicateMatcher();
	private static final AssociativeExpressionMatcher aeMatcher = new AssociativeExpressionMatcher();
	private static final AssociativePredicateMatcher apMatcher = new AssociativePredicateMatcher();
	private static final AtomicExpressionMatcher ateMatcher =  new AtomicExpressionMatcher();
	private static final BinaryExpressionMatcher beMatcher = new BinaryExpressionMatcher();
	private static final BinaryPredicateMatcher bpMatcher = new BinaryPredicateMatcher();
	private static final BoolExpressionMatcher boeMatcher = new BoolExpressionMatcher();
	private static final BoundIdentifierMatcher biMatcher = new BoundIdentifierMatcher();
	private static final IntegerLiteralMatcher lMatcher = new IntegerLiteralMatcher();
	private static final LiteralPredicateMatcher lpMatcher= new LiteralPredicateMatcher(); 
	private final static QuantifiedExpressionMatcher qeMatcher = new QuantifiedExpressionMatcher(); 
	private final static QuantifiedPredicateMatcher qpMatcher = new QuantifiedPredicateMatcher(); 
	private final static SetExtensionMatcher seMatcher = new SetExtensionMatcher(); 
	private final static SimplePredicateMatcher spMatcher= new SimplePredicateMatcher(); 
	private final static UnaryExpressionMatcher ueMatcher= new UnaryExpressionMatcher(); 
	private final static UnaryPredicateMatcher upMatcher = new UnaryPredicateMatcher();
	
	
	public static HashMap<Class<? extends Expression>, IExpressionMatcher>  EXP_MATCHERS= new 
		HashMap<Class<? extends Expression>, IExpressionMatcher>();
	public static HashMap<Class<? extends Predicate>, IPredicateMatcher> PRED_MATCHERS = new
		HashMap<Class<? extends Predicate>, IPredicateMatcher>();
	
	static {
		// exp matchers
		EXP_MATCHERS.put(aeMatcher.getType(), aeMatcher);
		EXP_MATCHERS.put(ateMatcher.getType(), ateMatcher);
		EXP_MATCHERS.put(beMatcher.getType(), beMatcher);
		EXP_MATCHERS.put(boeMatcher.getType(), boeMatcher);
		EXP_MATCHERS.put(lMatcher.getType(), lMatcher);
		EXP_MATCHERS.put(qeMatcher.getType(), qeMatcher);
		EXP_MATCHERS.put(seMatcher.getType(), seMatcher);
		EXP_MATCHERS.put(ueMatcher.getType(), ueMatcher);
		EXP_MATCHERS.put(biMatcher.getType(), biMatcher);
		// pred matchers
		PRED_MATCHERS.put(rpMatcher.getType(), rpMatcher);
		PRED_MATCHERS.put(apMatcher.getType(), apMatcher);
		PRED_MATCHERS.put(bpMatcher.getType(), bpMatcher);
		PRED_MATCHERS.put(lpMatcher.getType(), lpMatcher);
		PRED_MATCHERS.put(qpMatcher.getType(), qpMatcher);
		PRED_MATCHERS.put(spMatcher.getType(), spMatcher);
		PRED_MATCHERS.put(upMatcher.getType(), upMatcher);
	}
}
