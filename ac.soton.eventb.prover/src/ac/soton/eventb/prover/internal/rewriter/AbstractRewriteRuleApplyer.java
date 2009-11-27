package ac.soton.eventb.prover.internal.rewriter;

import org.eventb.core.ast.FormulaFactory;

import ac.soton.eventb.prover.base.IRuleBaseManager;
import ac.soton.eventb.prover.base.RuleBaseManager;
import ac.soton.eventb.prover.engine.MatchFinder;
import ac.soton.eventb.prover.engine.SimpleBinder;

/**
 * An abstract rewriter.
 * <p>This class may be subclassed to create specialised rewriters.</p>
 * @author maamria
 *
 */
public abstract class AbstractRewriteRuleApplyer {

	protected MatchFinder finder;
	protected SimpleBinder simpleBinder;
	protected IRuleBaseManager manager;
	protected FormulaFactory factory;
	
	protected AbstractRewriteRuleApplyer(){
		finder = MatchFinder.getDefault();
		simpleBinder =  SimpleBinder.getDefault();
		manager = RuleBaseManager.getDefault();
		factory = FormulaFactory.getDefault();
	}
}
