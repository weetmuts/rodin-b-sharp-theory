package org.eventb.theory.core.tests.sc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.junit.Test;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinMarkerUtil;

/**
 * A class to test the arity of the different problem messages.
 * @author maamria
 * 
 */
public class GraphProblemTest {

	private static class Spec implements Comparable<Spec> {

		public final TheoryGraphProblem problem;
		public final int arity;

		public Spec(final TheoryGraphProblem problem, final int arity) {
			this.problem = problem;
			this.arity = arity;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Spec && problem.equals(((Spec) obj).problem);
		}

		@Override
		public int hashCode() {
			return problem.hashCode();
		}

		@Override
		public String toString() {
			return problem.toString() + "/" + arity;
		}

		public int compareTo(Spec o) {
			return problem.compareTo(o.problem);
		}
	}

	private static Spec spec(final TheoryGraphProblem problem, final int arity) {
		return new Spec(problem, arity);
	}
	
	private static Spec[] specs = new Spec[] {
		spec(TheoryGraphProblem.MetavariableNameConflictError, 1),
		spec(TheoryGraphProblem.TypeParameterNameConflictError, 1),
		spec(TheoryGraphProblem.OperatorArgumentNameConflictError, 1),
		spec(TheoryGraphProblem.OperatorHasMoreThan1DefError, 1),
		spec(TheoryGraphProblem.OperatorCannotBeCommutError, 1),
		spec(TheoryGraphProblem.OperatorCannotBeAssosError, 1),
		spec(TheoryGraphProblem.OperatorWithSameSynJustBeenAddedError, 1),
		spec(TheoryGraphProblem.OpCannotReferToTheseIdents, 1),
		spec(TheoryGraphProblem.UntypedTypeParameterError, 1),
		
		spec(TheoryGraphProblem.UntypedOperatorArgumentError, 1),
		spec(TheoryGraphProblem.DatatypeNameAlreadyATypeParError, 1),
		spec(TheoryGraphProblem.TypeArgMissingError, 1),
		spec(TheoryGraphProblem.TypeArgNotDefinedError, 1),
		spec(TheoryGraphProblem.TypeArgRedundWarn, 1),
		spec(TheoryGraphProblem.DatatypeHasNoConsError, 1),
		spec(TheoryGraphProblem.DatatypeHasNoBaseConsError, 1),
		spec(TheoryGraphProblem.ConstructorNameAlreadyATypeParError, 1),
		spec(TheoryGraphProblem.DestructorNameAlreadyATypeParError, 1),
		spec(TheoryGraphProblem.MissingDestructorNameError, 0),
		spec(TheoryGraphProblem.MissingDestructorTypeError, 1),
		spec(TheoryGraphProblem.MissingConstructorNameError, 0),
		
		spec(TheoryGraphProblem.MissingDatatypeNameError, 0),
		spec(TheoryGraphProblem.TypeIsNotRefTypeError, 1),
		spec(TheoryGraphProblem.IdentIsNotTypeParError, 1),
		spec(TheoryGraphProblem.IdenIsADatatypeNameError, 1),
		spec(TheoryGraphProblem.IdenIsAConsNameError, 1),
		spec(TheoryGraphProblem.IdenIsADesNameError, 1),
		spec(TheoryGraphProblem.OperatorSynConflictError, 1),
		spec(TheoryGraphProblem.OperatorIDExistsError, 1),
		spec(TheoryGraphProblem.OperatorSynMissingError, 1),
		spec(TheoryGraphProblem.OperatorSynExistsError, 1),
		spec(TheoryGraphProblem.OperatorSynIsATypeParError, 1),
		spec(TheoryGraphProblem.OperatorFormTypeMissingError, 1),
		
		spec(TheoryGraphProblem.OperatorNotationTypeMissingError, 1),
		spec(TheoryGraphProblem.OperatorAssocMissingError, 1),
		spec(TheoryGraphProblem.OperatorCommutMissingError, 1),
		spec(TheoryGraphProblem.TypeAttrMissingError, 1),
		spec(TheoryGraphProblem.WDPredMissingError, 0),
		spec(TheoryGraphProblem.MissingFormulaError, 0),
		spec(TheoryGraphProblem.OperatorHasNoDefError, 1),
		spec(TheoryGraphProblem.OperatorDefNotExpError, 1),
		spec(TheoryGraphProblem.OperatorDefNotPredError, 1),
		spec(TheoryGraphProblem.OperatorInvalidSynError, 1),
		spec(TheoryGraphProblem.RulesBlockLabelProblemError, 1),
		spec(TheoryGraphProblem.TheoremPredMissingError, 1),
		
		spec(TheoryGraphProblem.TheoremLabelProblemError, 1),
		spec(TheoryGraphProblem.ApplicabilityUndefError, 0),
		spec(TheoryGraphProblem.RedundantImportWarning, 1),
		spec(TheoryGraphProblem.ImportTheoryMissing, 0),
		spec(TheoryGraphProblem.ImportTheoryNotExist, 1),
		spec(TheoryGraphProblem.InferenceGivenBTRUEPredWarn, 0),
		spec(TheoryGraphProblem.InferenceInferBTRUEPredErr, 0),
		spec(TheoryGraphProblem.UntypedMetavariableError, 1),
		
		spec(TheoryGraphProblem.DescNotSupplied, 1),
		spec(TheoryGraphProblem.CompleteUndefWarning, 0),
		spec(TheoryGraphProblem.LhsAndRhsNotSynClassMatching, 2),
		spec(TheoryGraphProblem.LHSFormulaMissingError, 0),
		spec(TheoryGraphProblem.RHSTypesNotSubsetOfLHSTypes, 1),
		spec(TheoryGraphProblem.CondTypesNotSubsetOfLHSTypes, 1),
		spec(TheoryGraphProblem.CondIdentsNotSubsetOfLHSIdents, 1),
		spec(TheoryGraphProblem.RHSIdentsNotSubsetOfLHSIdents, 1),
		spec(TheoryGraphProblem.RHSFormulaMissingError, 0),
		spec(TheoryGraphProblem.RuleSideNotTheoryFormula, 0),
		
		spec(TheoryGraphProblem.RuleTypeMismatchError, 2),
		spec(TheoryGraphProblem.RewriteRuleLabelConflictError, 1),
		spec(TheoryGraphProblem.InferenceRuleLabelConflictError, 1),
		spec(TheoryGraphProblem.OperatorExpPrefixCannotBeAssos, 0),
		spec(TheoryGraphProblem.OperatorPredOnlyPrefix, 0),
		spec(TheoryGraphProblem.OperatorExpCannotBePostfix, 0),
		spec(TheoryGraphProblem.OperatorPredNeedOneOrMoreArgs, 0),
		spec(TheoryGraphProblem.OperatorPredCannotBeAssos, 0),
		spec(TheoryGraphProblem.OperatorExpInfixNeedsAtLeastTwoArgs, 0),
		spec(TheoryGraphProblem.RhsLabelConflictError, 1),
		spec(TheoryGraphProblem.CondUndefError, 0),
		spec(TheoryGraphProblem.RuleNoRhsError, 1),
		spec(TheoryGraphProblem.RuleInfersError, 1),
		spec(TheoryGraphProblem.NoRuleDescWarning, 1),
		
		spec(TheoryGraphProblem.LHSIsIdentErr, 0),
		spec(TheoryGraphProblem.RHSPredVarsNOTSubsetOFLHS, 0),
		spec(TheoryGraphProblem.NonTypeParOccurError, 1),
		spec(TheoryGraphProblem.InferenceRuleNotApplicableError, 1),
		spec(TheoryGraphProblem.InferenceRuleBackward, 1),
		spec(TheoryGraphProblem.InferenceRuleForward, 1),
		spec(TheoryGraphProblem.InferenceRuleBoth, 1),
		spec(TheoryGraphProblem.LHS_IsNotWDStrict, 0),
		spec(TheoryGraphProblem.ImportDepCircularity, 2),
		
		spec(TheoryGraphProblem.IndRedundantImportWarn, 0),
		spec(TheoryGraphProblem.ImportConflict, 2),
		spec(TheoryGraphProblem.ArgumentNotExistOrNotParametric, 1),
		spec(TheoryGraphProblem.InductiveCaseMissing, 0),
		spec(TheoryGraphProblem.ExprIsNotDatatypeConstr, 0),
		spec(TheoryGraphProblem.ConstrAlreadyCovered, 0),
		spec(TheoryGraphProblem.ExprNotApproInductiveCase, 0),
		spec(TheoryGraphProblem.ConstrArgumentNotIdentifier, 1),
		spec(TheoryGraphProblem.OperatorCannotBePostfix, 0),
		spec(TheoryGraphProblem.InductiveArgMissing, 0),
		
		spec(TheoryGraphProblem.NoRecCasesError, 0),
		spec(TheoryGraphProblem.InductiveCaseNotAppropriateExp, 1),
		spec(TheoryGraphProblem.ConsArgNotIdentInCase, 1),
		spec(TheoryGraphProblem.IdentCannotBeUsedAsConsArg, 1),
		spec(TheoryGraphProblem.UnableToTypeCase, 0),
		spec(TheoryGraphProblem.RecCaseAlreadyCovered, 0),
		
		spec(TheoryGraphProblem.TypeMissmatchOfRecDef, 0),
		spec(TheoryGraphProblem.NoCoverageAllRecCase, 0),
		spec(TheoryGraphProblem.RecOpTypeNotConsistent, 2),
		spec(TheoryGraphProblem.OpArgExprNotSet, 1),
		spec(TheoryGraphProblem.InadmissibleDatatypeError, 1),
		spec(TheoryGraphProblem.AxiomaticBlockLabelProblemError, 1),
		spec(TheoryGraphProblem.AxiomaticTypeNameAlreadyATypeParError, 1)
	};
	
	private static Map<TheoryGraphProblem, Spec> specMap = 
			new EnumMap<TheoryGraphProblem, Spec>(TheoryGraphProblem.class);
		static {
			for (Spec spec : specs) {
				specMap.put(spec.problem, spec);
			}
		}
		
		/**
		 * check whether the messages loaded from the properties take the correct number of parameters.
		 */
		@Test
		public void testArguments() throws Exception {
			for (Spec spec : specs) {
				assertEquals("wrong number of arguments", spec.arity, spec.problem.getArity());
			}
		}
		
		/**
		 * check whether the messages loaded from the properties file are complete
		 */
		@Test
		public void testMessages() throws Exception {
			Set<IRodinProblem> problems = new HashSet<IRodinProblem>(specs.length * 4 / 3 + 1);
			for (Spec spec : specs) {
				problems.add(spec.problem);
			}
			for (IRodinProblem problem : TheoryGraphProblem.values()) {
				boolean found = problems.contains(problem);
				assertTrue("No spec for problem " + problem, found);
				
			}
			//assertEquals("wrong number of problems", specs.length, GraphProblem.values().length);
		}

		
		public static boolean check(IEventBRoot root) throws CoreException {
			boolean ok = true;
			IMarker[] markers = 
				root.getResource().findMarkers(
						RodinMarkerUtil.RODIN_PROBLEM_MARKER, 
						true, 
						IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				String errorCode = RodinMarkerUtil.getErrorCode(marker);
				TheoryGraphProblem problem;
				try {
					problem = TheoryGraphProblem.valueOfErrorCode(errorCode);
				} catch (IllegalArgumentException e) {
					// not a graph problem
					continue;
				}
				Spec spec = specMap.get(problem);
				assertNotNull("missing problem spec", spec);
				int k = RodinMarkerUtil.getArguments(marker).length;
				if (spec.arity != k) {
					ok = false;
					System.out.println("Wrong number of arguments " + 
							problem.toString() + "/" + k + " expected: " + spec.arity);
				}
			}
			return ok;
		}
}
