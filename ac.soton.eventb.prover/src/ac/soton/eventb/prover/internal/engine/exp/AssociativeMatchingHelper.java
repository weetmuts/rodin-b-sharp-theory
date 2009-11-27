package ac.soton.eventb.prover.internal.engine.exp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.engine.MatchFinder;
import ac.soton.eventb.prover.internal.engine.Binding;

/**
 * Internal API. TODO Improve effeciency and verify correctness!
 * @author maamria
 *
 */
public class AssociativeMatchingHelper {

	/**
	 * the formula factory
	 */
	private static FormulaFactory factory = FormulaFactory.getDefault();
	/**
	 * the match finder
	 */
	private static MatchFinder finder = MatchFinder.getDefault();
	
	/**
	 * <p>Calculates a binding between the associative expression whose tag is <code>tag</code> and children are <code>patterns</code> and 
	 * the associative expression whose tag is <code>tag</code> and children are <code>expressions</code>.</p>
	 * Note: the returned binding is not immutable.
	 * <p>
	 * @param expressions an array of expressions
	 * @param patterns an array of expressions of the pattern
	 * @param isAc whether the operator is associative commutative or only associative
	 * @param tag the tag 
	 * @param existingBiding the binding constructed so far
	 * @return the final binding
	 * 
	 */
	public static IBinding match(Expression[] expressions, Expression[] patterns, boolean isAc, int tag, IBinding existingBiding){
		assert patterns.length <= expressions.length;
		// index both sets of expressions
		SortedSet<IndexedExpression> indexedExpressions = createSortedExpSet(expressions);
		SortedSet<IndexedExpression> indexedPatterns = createSortedExpSet(patterns);
		// keep track of free identifiers of the pattern
		List<IndexedExpression> patternIdents = new ArrayList<IndexedExpression>();
		IBinding finalBinding = null;
		// CASE 1: associative commutative operator
		if(isAc){
			// a sorted set with the ordering on MatchInfoHolder
			SortedSet<PossibleMatchesInfo> matchesInfos = new TreeSet<PossibleMatchesInfo>();
			// get all possible matches for all patterns bar free identifiers which we will do later
			for (IndexedExpression pExpIndexer : indexedPatterns){
				if(pExpIndexer.expression instanceof FreeIdentifier){
					patternIdents.add(pExpIndexer);
					continue;
				}
				// get all possible matches for the current indexed pattern expression
				Map<IndexedExpression, IBinding> possibleMatches = getPossibleMatches(indexedExpressions, pExpIndexer, existingBiding);
				// if no match is available no point continuing
				if(possibleMatches == null)
					return null;
				// add the match information to the sorted set
				matchesInfos.add(new PossibleMatchesInfo(pExpIndexer, possibleMatches));
			}
			// try a mapping 
			// this keeps track of the previous mapping in case we fail
			Set<MatchEntry> oldMapping = new HashSet<MatchEntry>();
			Set<MatchEntry> currentMapping = null;
			// keeps track of the available expressions . initially all exp are available
			List<IndexedExpression> listOfAvailableExpressions = createIndexedExpList(expressions);
			while((currentMapping= getPotentialMapping(matchesInfos, oldMapping))!= null){
				oldMapping = currentMapping;
				finalBinding = Binding.createBinding();
				// list of used expressions
				ArrayList<IndexedExpression> usedExpressions = new ArrayList<IndexedExpression>();
				boolean problemMapping = false;
				for (MatchEntry entry : currentMapping){
					IBinding b = entry.binding;
					// attempt to insert this local binding, if fails break out
					if(!finalBinding.insertAllMappings(b)){
						problemMapping= true;
						break;
					}
					// add to used expressions
					usedExpressions.add(entry.matchExpression);
				}
				// if there was a problem mapping, get a new potential mapping
				if(problemMapping){
					continue;
				}
				// remove all used expressions
				listOfAvailableExpressions.removeAll(usedExpressions);
				// make sure we have enough expression for our free identifiers
				assert listOfAvailableExpressions.size() > patternIdents.size();
				// for all bar the last one, give a match
				for (int i = 0; i < patternIdents.size()-1; i++){
					// if a problem mapping break out
					if(!finalBinding.putMapping(
							(FreeIdentifier)patternIdents.get(i).expression, 
							listOfAvailableExpressions.get(i).expression)){
						problemMapping = true;
						break;
					}
					//remove the used expression
					listOfAvailableExpressions.remove(i);
				}
				// if no problems occurred above
				if(!problemMapping){
					// map the final ident
					if(!finalBinding.putMapping(
							// the free ident
							(FreeIdentifier)(patternIdents.get(patternIdents.size()-1).expression), 
							// the final expression : make an associative one if necessary
							(extractExpressions(listOfAvailableExpressions).length > 1 ? 
									factory.makeAssociativeExpression(tag, extractExpressions(listOfAvailableExpressions), null) : 
									extractExpressions(listOfAvailableExpressions)[0]))){
						continue;
					}
					// if all ok, break out of the while loop
					break;
				}
					
			}
		}
		else {
			
		}
		return finalBinding;
	}
	/**
	 * Returns a list of indexed expressions where the index of each expression is its index in <code>array</code>.<p>
	 * @param array of expressions
	 * @return a list of indexed expressions
	 */
	private static List<IndexedExpression> createIndexedExpList(Expression[] array){
		List<IndexedExpression> expressionsIndexer = new ArrayList<IndexedExpression>(array.length);
		int index = 0;
		for (Expression e : array){
			expressionsIndexer.add(new IndexedExpression(e, index));
			index++;
		}
		return expressionsIndexer;
	}
	/**
	 * Returns a sorted set whose elements are the elements of <code>array</code> indexed. The order in which 
	 * the set store its members is given by the order defined on <code>IndexedExpression</code>.
	 * @param array of expressions
	 * @return a sorted set of indexed expressions
	 */
	private static SortedSet<IndexedExpression> createSortedExpSet(Expression[] array){
		SortedSet<IndexedExpression> expressionsIndexer = new TreeSet<IndexedExpression>();
		int index = 0;
		for (Expression e : array){
			expressionsIndexer.add(new IndexedExpression(e, index));
			index++;
		}
		return expressionsIndexer;
	}
	/**
	 * Returns an array of expressions that were stored in each indexer within <code>indexers</code>.
	 * <p>No guarantees about the order.
	 * @param indexers the list of expression indexers
	 * @return the array of expressions
	 */
	private static Expression[] extractExpressions(List<IndexedExpression> indexers){
		Expression[] exps = new Expression[indexers.size()];
		int index = 0;
		for (IndexedExpression indexer : indexers){
			exps[index] = indexer.expression;
			index++;
		}
		return exps;
	}
	/**
	 * Returns a map between the possible matches of the expression in <code>pExpIndexer</code> and the resultant bindings.<p>
	 * Callers must ensure that the indexed expression in <code>pExpIndexer</code> is not a free identifier.</p>
	 * @param expressions the indexed expression
	 * @param pExpIndexer the indexed pattern expression
	 * @param existingBinding the binding constructed so far 
	 * @return a map of possible matches and resultant bindings or <code>null</code> if no matches were found
	 */
	private static Map<IndexedExpression, IBinding> getPossibleMatches(
			SortedSet<IndexedExpression> expressions, IndexedExpression pExpIndexer, IBinding existingBinding){
		// make sure we are not trying to find matches for a free identifier
		assert !(pExpIndexer.expression instanceof FreeIdentifier);
		LinkedHashMap<IndexedExpression, IBinding> map = new LinkedHashMap<IndexedExpression, IBinding>();
		// we check each indexed expression
		for (IndexedExpression expIndex : expressions){
			IBinding binding = finder.calculateBindings(expIndex.expression, pExpIndexer.expression);
			// if binding exists and insertable in existing binding, add this as a match
			if(binding != null && existingBinding.isBindingInsertable(binding)){
				map.put(expIndex, binding);
			}
		}
		// if map is empty
		if(map.size() == 0)
			return null;
		return map;
	}
	/**
	 * Naive implementation.
	 * @param allPossibleMappings
	 * @param oldMapping
	 * @return
	 */
	private static Set<MatchEntry> getPotentialMapping(SortedSet<PossibleMatchesInfo> allPossibleMappings, Set<MatchEntry> oldMapping){
		List<IndexedExpression> usedExpressions = new ArrayList<IndexedExpression>();
		Set<MatchEntry> matchEntries = oldMapping;
		while (matchEntries.equals(oldMapping)) {
			matchEntries = new HashSet<MatchEntry>();
			for (PossibleMatchesInfo holder : allPossibleMappings) {
				Expression chosen = null;
				IndexedExpression indexer = null;
				while (holder.possibleMatches.keySet().iterator().hasNext()) {
					indexer = holder.possibleMatches.keySet().iterator().next();
					chosen = indexer.expression;
					if (oldMapping.contains(new MatchEntry(
							holder.patternExpression, indexer,
							holder.possibleMatches.get(indexer)))) {
						continue;
					}
					if (!usedExpressions.contains(indexer)) {
						break;
					}
				}
				// no binding is possible if chosen is null
				if (chosen == null)
					return null;
				usedExpressions.add(indexer);
				matchEntries.add(new MatchEntry(holder.patternExpression,
						indexer, holder.possibleMatches.get(indexer)));
			}
		}
		return matchEntries;
	}
}
/**
 * Objects of this class represent an expression associated with an index.
 * @author maamria
 *
 */
class IndexedExpression implements Comparable<IndexedExpression>{
	Expression expression;
	int index;
	
	public IndexedExpression(Expression expression, int index) {
		this.expression = expression;
		this.index = index;
	}
	
	@Override
	public int compareTo(IndexedExpression indexer) {
		if(indexer.equals(this)){
			return 0;
		}
		else if (index > indexer.index){
			return 1;
		}
		return -1;
	}
	
	public boolean equals(Object o){
		if(o instanceof IndexedExpression){
			if(((IndexedExpression)o).expression.equals(expression) && 
				((IndexedExpression)o).index == index){
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		return "["+expression + " @ "+index+"]";
	}
}
/**
 * Objects of this class represent a match between an expression (indexed) and a pattern (indexed) and the resultant binding.
 * @author maamria
 *
 */
class MatchEntry{
	IBinding binding;
	IndexedExpression matchExpression;
	IndexedExpression patternExpression;
	
	public MatchEntry(IndexedExpression patternExpression, IndexedExpression matchExpression, IBinding binding){
		this.matchExpression = matchExpression;
		this.patternExpression = patternExpression;
		this.binding = binding;
	}
	
	public String toString(){
		return "["+patternExpression + " -> "+matchExpression+"]";
	}
}
/**
 * Objects of this class store a pattern expression (indexed) together with a mapping between possible matches
 * (also indexed) and the resultant bindings.
 * @author maamria
 *
 */
class PossibleMatchesInfo implements Comparable<PossibleMatchesInfo>{
	IndexedExpression patternExpression;
	Map<IndexedExpression, IBinding> possibleMatches;
	
	public PossibleMatchesInfo(IndexedExpression patternExpression, Map<IndexedExpression, IBinding> possibleMatches){
		this.patternExpression = patternExpression;
		this.possibleMatches = possibleMatches;
	}

	@Override
	public int compareTo(PossibleMatchesInfo info) {
		if(info.equals(this)){
			return 0;
		}
		else if (possibleMatches.size() > info.possibleMatches.size()){
			return 1;
		}
		return -1;
	}
	
	public boolean equals(Object o){
		if(o instanceof PossibleMatchesInfo){
			if(((PossibleMatchesInfo) o).patternExpression.equals(patternExpression) &&
					((PossibleMatchesInfo) o).possibleMatches.equals(possibleMatches))
				return true;
		}
		return false;
	}
	
	public String toString(){
		return "["+patternExpression+" -> "+possibleMatches.keySet()+"]";
	}
}
