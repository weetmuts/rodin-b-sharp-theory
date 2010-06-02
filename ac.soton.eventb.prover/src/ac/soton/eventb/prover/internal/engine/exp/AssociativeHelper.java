package ac.soton.eventb.prover.internal.engine.exp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;

import ac.soton.eventb.prover.engine.AssociativeExpressionComplement;
import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.engine.MatchFinder;
import ac.soton.eventb.prover.internal.engine.Binding;

public class AssociativeHelper {

	/**
	 * the formula factory
	 */
	static FormulaFactory factory = FormulaFactory.getDefault();
	/**
	 * the match finder
	 */
	static MatchFinder finder = MatchFinder.getDefault();
	
	// RETURN NULL if a problem
	public static IBinding match(
			Expression[] expressions, 
			Expression[] patterns, 
			boolean isAc, int tag, 
			IBinding existingBinding){
		if(patterns.length > expressions.length){
			return null;
		}
		// index exp of the formula to match
		SortedSet<IndexedExpression> indexedExpSet = createSortedExpSet(expressions);
		// that of the pattern
		SortedSet<IndexedExpression> indexedPatSet = createSortedExpSet(patterns);
		// indexed idents
		List<IndexedExpression> patternIdents = new ArrayList<IndexedExpression>();
		// The binding to return HAS TO BE TOTAL MATCH - No leftovers
		IBinding finalBinding = null;
		
		///////////////////////////////////////////////////////////////////////////////
		// Matching starts here
		///////////////////////////////////////////////////////////////////////////////
		// this set will be SORTED by the SIZE of possible matches of each pattern
		SortedSet<PossibleMatchesHolder> possibleMatchesSet = 
			new TreeSet<PossibleMatchesHolder>();
		// get all possible matches for all patterns bar free identifiers which we will do later
		for (IndexedExpression indExpPat : indexedPatSet){
			if(indExpPat.getExpression() instanceof FreeIdentifier){
				patternIdents.add(indExpPat);
				continue;
			}
			// get all possible matches for the current indexed pattern expression
			Map<IndexedExpression, IBinding> possibleMatches = 
				getPossibleMatches(indexedExpSet, indExpPat, existingBinding);
			// if not matches found for this pattern return a null binding.
			if(possibleMatches == null){
				return null;
			}
			// if it has at least one match
			possibleMatchesSet.add(new PossibleMatchesHolder(
					indExpPat, possibleMatches));
		}
		////////////////////////////////////////////////////////////////////////////////////////
		// AC matching
		////////////////////////////////////////////////////////////////////////////////////////
		if(isAc){
			Set<IndexedExpression> availableIndExpSet = createSortedExpSet(expressions);
			List<MatchingEntry> entries = getPotentialMapping(possibleMatchesSet);
			if(entries == null){
				return null;
			}
			ArrayList<IndexedExpression> usedIndExpressions = new ArrayList<IndexedExpression>();
			finalBinding = Binding.createBinding(null, null, false);
			for(MatchingEntry entry : entries){
				IBinding b = entry.getBinding();
				if(!finalBinding.insertAllMappings(b)){
					return null;
				}
				usedIndExpressions.add(entry.getMatchIndExpression());
			}
			availableIndExpSet.removeAll(usedIndExpressions);
			// if we have fewer expression (to match) than free indetifiers, NULL
			if(availableIndExpSet.size() < patternIdents.size()){
				return null;
			}
			// map each of those identifiers
			int numberOfIdents = patternIdents.size();
			if(numberOfIdents != 0){
				IndexedExpression[] availableIndExpArray = 
					availableIndExpSet.toArray(new IndexedExpression[availableIndExpSet.size()]);
				for (int i = 0 ; i < numberOfIdents-1; i++){
					if (!finalBinding.putMapping(
							(FreeIdentifier)patternIdents.get(i).getExpression(), 
							availableIndExpArray[i].getExpression()))
					{
						return null;
					}
					availableIndExpSet.remove(availableIndExpArray[i]);
				}
				// we should have one left
				if(!finalBinding.putMapping(
						(FreeIdentifier) patternIdents.get(numberOfIdents-1).getExpression(), 
						makeACExpression(availableIndExpArray, tag))){
					return null;
				}
			}
			// if no identifier, check if we still have expression , ACCEPT_PARTIAL_MAPPING
			else {
				if(availableIndExpSet.size()!=0)
				{
					if(!existingBinding.isPartialMatchAcceptable()){
						return null;
					}
					else {
						Expression leftOver = makeACExpression(
								new ArrayList<IndexedExpression>(availableIndExpSet), tag);
						finalBinding.setAssociativeExpressionComplement(
								new AssociativeExpressionComplement(tag, leftOver, null));
						}
				}
			}
			
		}
		////////////////////////////////////////////////////////////////////////////////////////
		// Associative matching
		////////////////////////////////////////////////////////////////////////////////////////
		else {
			return null;
		}
		return finalBinding;
	}
	
	static Expression makeACExpression(ArrayList<IndexedExpression> indExps, int tag){
		if(indExps.size() == 1){
			return indExps.get(0).getExpression();
		}
		else {
			Expression[] exps = new Expression[indExps.size()];
			int i = 0;
			for (IndexedExpression ie : indExps){
				exps[i] = ie.getExpression();
				i++;
			}
			return factory.makeAssociativeExpression(tag, exps, null);
		}
	}
	
	static Expression makeACExpression(IndexedExpression[] indExps, int tag){
		if(indExps.length == 1){
			return indExps[0].getExpression();
		}
		else {
			Expression[] exps = new Expression[indExps.length];
			int i = 0;
			for (IndexedExpression ie : indExps){
				exps[i] = ie.getExpression();
				i++;
			}
			return factory.makeAssociativeExpression(tag, exps, null);
		}
	}
	
	static List<MatchingEntry> getPotentialMapping(SortedSet<PossibleMatchesHolder> possibleMappings){
		List<IndexedExpression> usedIndExpressions = new ArrayList<IndexedExpression>();
		List<MatchingEntry> matchEntries = new ArrayList<MatchingEntry>();
		for (PossibleMatchesHolder pmh : possibleMappings){
			IndexedExpression indPatExp = pmh.getPatternExpression();
			IndexedExpression chosen = null;
			MatchingEntry oneMatchEntry = null;
			// choose one 
			for(IndexedExpression indExp : pmh.getPossibleMatches().keySet()){
				if(usedIndExpressions.contains(indExp)){
					continue;
				}
				chosen = indExp;
			}
			if(chosen == null){
				return null;
			}
			usedIndExpressions.add(chosen);
			oneMatchEntry = new MatchingEntry(
					indPatExp, 
					chosen, 
					pmh.getPossibleMatches().get(chosen));
			matchEntries.add(oneMatchEntry);
		}
		return matchEntries;
	}
	
	static SortedSet<IndexedExpression> createSortedExpSet(Expression[] array){
		SortedSet<IndexedExpression> expressionsIndexer = new TreeSet<IndexedExpression>();
		int index = 0;
		for (Expression e : array){
			expressionsIndexer.add(new IndexedExpression(e, index));
			index++;
		}
		return expressionsIndexer;
	}
	
	static Map<IndexedExpression, IBinding> getPossibleMatches(
			SortedSet<IndexedExpression> expToMatchAgainst, 
			IndexedExpression indexedPatternExp, 
			IBinding existingBinding){
		
		// make sure we are not trying to find matches for a free identifier
		// this should never happen anyway
		assert !(indexedPatternExp.expression instanceof FreeIdentifier);
		
		LinkedHashMap<IndexedExpression, IBinding> map = new LinkedHashMap<IndexedExpression, IBinding>();
		// we check each indexed expression
		for (IndexedExpression indExp : expToMatchAgainst){
			IBinding binding = finder.calculateBindings(
					indExp.getExpression(), 
					indexedPatternExp.getExpression(), 
					false);
			// if binding exists and insertable in existing binding, add this as a match
			if(binding != null && existingBinding.isBindingInsertable(binding)){
				map.put(indExp, binding);
			}
		}
		// only return a map when it is not empty TO ENSURE THAT EVERY PATTERN HAS AT LEAST ONE MATCH
		if(map.size() == 0)
			return null;
		return map;
	}
	
	// Helper classes
	
	static class PossibleMatchesHolder implements Comparable<PossibleMatchesHolder>{
		private IndexedExpression patternExpression;
		private Map<IndexedExpression, IBinding> possibleMatches;
		
		public PossibleMatchesHolder(IndexedExpression patternExpression, Map<IndexedExpression, IBinding> possibleMatches){
			this.patternExpression = patternExpression;
			this.possibleMatches = possibleMatches;
		}

		public int compareTo(PossibleMatchesHolder info) {
			if(info.equals(this)){
				return 0;
			}
			else if (possibleMatches.size() > info.possibleMatches.size()){
				return 1;
			}
			return -1;
		}
		
		public boolean equals(Object o){
			if(o instanceof PossibleMatchesHolder){
				if(((PossibleMatchesHolder) o).patternExpression.equals(patternExpression) &&
						((PossibleMatchesHolder) o).possibleMatches.equals(possibleMatches))
					return true;
			}
			return false;
		}
		
		public String toString(){
			return "["+patternExpression+" -> ("+possibleMatches.keySet()+")]";
		}
		
		public IndexedExpression getPatternExpression() {
			return patternExpression;
		}

		public Map<IndexedExpression, IBinding> getPossibleMatches() {
			return possibleMatches;
		}
	}

	static class IndexedExpression implements Comparable<IndexedExpression>{
		private Expression expression;
		private int index;
		
		public IndexedExpression(Expression expression, int index) {
			this.expression = expression;
			this.index = index;
		}

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
		
		public Expression getExpression() {
			return expression;
		}

		public int getIndex() {
			return index;
		}
	}

	static class MatchingEntry{
		private IBinding binding;
		private IndexedExpression matchIndExpression;
		private IndexedExpression patternIndExpression;

		public MatchingEntry(IndexedExpression patternIndExpression, IndexedExpression matchIndExpression, IBinding binding){
			this.matchIndExpression = matchIndExpression;
			this.patternIndExpression = patternIndExpression;
			this.binding = binding;
		}
		
		public String toString(){
			return "["+patternIndExpression + " -> "+matchIndExpression+"]";
		}
		
		public IBinding getBinding() {
			return binding;
		}

		public IndexedExpression getMatchIndExpression() {
			return matchIndExpression;
		}

		public IndexedExpression getPatternIndExpression() {
			return patternIndExpression;
		}
	}
	
}

