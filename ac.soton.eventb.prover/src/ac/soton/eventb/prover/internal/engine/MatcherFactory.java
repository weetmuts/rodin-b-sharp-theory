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

/**
 * <p>This class enables a mechanism by which concrete matchers for specific type of formulas are provided.</p>
 * @author maamria
 * @see IExpressionMatcher
 * @see IPredicateMatcher
 */
public class MatcherFactory {

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
	
	
	private static HashMap<Class<? extends Expression>, IExpressionMatcher>  EXP_MATCHERS= new 
		HashMap<Class<? extends Expression>, IExpressionMatcher>();
	private static HashMap<Class<? extends Predicate>, IPredicateMatcher> PRED_MATCHERS = new
		HashMap<Class<? extends Predicate>, IPredicateMatcher>();
	
	static {
		// expression matchers
		EXP_MATCHERS.put(aeMatcher.getType(), aeMatcher);
		EXP_MATCHERS.put(ateMatcher.getType(), ateMatcher);
		EXP_MATCHERS.put(beMatcher.getType(), beMatcher);
		EXP_MATCHERS.put(boeMatcher.getType(), boeMatcher);
		EXP_MATCHERS.put(lMatcher.getType(), lMatcher);
		EXP_MATCHERS.put(qeMatcher.getType(), qeMatcher);
		EXP_MATCHERS.put(seMatcher.getType(), seMatcher);
		EXP_MATCHERS.put(ueMatcher.getType(), ueMatcher);
		EXP_MATCHERS.put(biMatcher.getType(), biMatcher);
		// predicate matchers
		PRED_MATCHERS.put(rpMatcher.getType(), rpMatcher);
		PRED_MATCHERS.put(apMatcher.getType(), apMatcher);
		PRED_MATCHERS.put(bpMatcher.getType(), bpMatcher);
		PRED_MATCHERS.put(lpMatcher.getType(), lpMatcher);
		PRED_MATCHERS.put(qpMatcher.getType(), qpMatcher);
		PRED_MATCHERS.put(spMatcher.getType(), spMatcher);
		PRED_MATCHERS.put(upMatcher.getType(), upMatcher);
	}
	
	/**
	 * <p>Returns the appropriate matcher for the specific class of expression specified by <code>clazz</code>.</p>
	 * @param clazz the class of the formula
	 * @return the matcher
	 */
	public static  IExpressionMatcher getExpressionMatcher(Class<? extends Expression> clazz){
		return EXP_MATCHERS.get(clazz);
		
	}
	/**
	 * <p>Returns the appropriate matcher for the specific class of predicate specified by <code>clazz</code>.</p>
	 * @param clazz the class of the formula
	 * @return the matcher
	 */
	public static IPredicateMatcher getPredicateMatcher(Class<? extends Predicate> clazz){
		return PRED_MATCHERS.get(clazz);
	}
}
