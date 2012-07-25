package org.eventb.theory.core.tests.sc;

import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eventb.core.ast.LanguageVersion.V2;
import static org.rodinp.core.RodinMarkerUtil.RODIN_PROBLEM_MARKER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAccuracyElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.theory.core.IApplicabilityElement;
import org.eventb.theory.core.ICompleteElement;
import org.eventb.theory.core.IHasErrorElement;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.tests.TheoryTest;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Based on BasicSCTest, some duplication needs to be looked at.
 * 
 * @author maamria
 * 
 */
public abstract class BasicTheorySCTest extends TheoryTest {

	@Override
	protected void runBuilder() throws CoreException {
		super.runBuilder();
		for (IEventBRoot root : sourceRoots)
			assertTrue("ill-formed markers", GraphProblemTest.check(root));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sourceRoots.clear();
	}

	@Override
	protected void tearDown() throws Exception {
		sourceRoots.clear();
		super.tearDown();
	}

	private final List<IEventBRoot> sourceRoots = new ArrayList<IEventBRoot>();

	@Override
	protected ITheoryRoot createTheory(String bareName) throws RodinDBException {
		ITheoryRoot root = super.createTheory(bareName);
		sourceRoots.add(root);
		addRoot(root.getSCTheoryRoot());
		return root;
	}

	public BasicTheorySCTest() {
		super();
	}

	public BasicTheorySCTest(String name) {
		super(name);
	}

	public static void isPredicateOperator(ISCNewOperatorDefinition operatorDefinition) throws RodinDBException {
		assertEquals("operator should be predicate", IOperatorProperties.FormulaType.PREDICATE,
				operatorDefinition.getFormulaType());
	}

	public static void isExpressionOperator(ISCNewOperatorDefinition operatorDefinition) throws RodinDBException {
		assertEquals("operator should be expression", IOperatorProperties.FormulaType.EXPRESSION,
				operatorDefinition.getFormulaType());
	}

	public static void isInfix(ISCNewOperatorDefinition operatorDefinition) throws RodinDBException {
		assertEquals("operator should be infix", IOperatorProperties.Notation.INFIX,
				operatorDefinition.getNotationType());
	}

	public static void isPrefix(ISCNewOperatorDefinition operatorDefinition) throws RodinDBException {
		assertEquals("operator should be prefix", IOperatorProperties.Notation.PREFIX,
				operatorDefinition.getNotationType());
	}

	public static void isAssociative(ISCNewOperatorDefinition definition) throws RodinDBException {
		assertEquals("operator should be associative", true, definition.isAssociative());
	}

	public static void isCommutative(ISCNewOperatorDefinition definition) throws RodinDBException {
		assertEquals("operator should be associative", true, definition.isCommutative());
	}

	public static void hasError(ISCNewOperatorDefinition definition) throws RodinDBException {
		assertEquals("operator should have error", true, definition.hasError());
	}

	public void containsTypeParameters(ISCTheoryRoot theory, String... strings) throws RodinDBException {
		ISCTypeParameter[] types = theory.getSCTypeParameters();

		assertEquals("wrong number of constants", strings.length, types.length);

		if (strings.length == 0)
			return;

		Set<String> nameSet = getIdentifierNameSet(types);

		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	public void containsTheorems(ISCTheoryRoot root, FormulaFactory factory, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCTheorem[] theorems = root.getTheorems();
		
		containsPredicates("theorem", factory, environment, labels, strings, theorems);
	}

	protected void containsPredicates(String type, FormulaFactory factory, ITypeEnvironment environment,
			String[] labels, String[] strings, ISCTheorem[] theorems) throws RodinDBException{
		assert labels.length == strings.length;
		assertEquals("wrong number [" + type + "]", strings.length, theorems.length);
		
		if (theorems.length == 0)
			return;
		
		List<String> labelList = getLabelList(theorems);
		
		for (int k=0; k<labels.length; k++) {
			int index = labelList.indexOf(labels[k]);
			assertTrue("should contain " + type + " " + labels[k], index != -1);
			String predicate = theorems[index].getPredicateString();
			assertEquals("wrong " + type, 
					getNormalizedPredicate(strings[k], factory, environment), 
					predicate);
		}
		
	}

	public void importsTheories(ISCTheoryRoot scRoot, String... names) throws RodinDBException {
		ISCImportTheory[] importedTheories = scRoot.getImportTheories();
		assertEquals("wrong number of import clauses", names.length, importedTheories.length);
		if (names.length == 0)
			return;
		Set<String> nameSet = getImportNameSet(importedTheories);
		for (String name : names)
			assertTrue("should contain " + name, nameSet.contains(name));
	}
	
	public ISCProofRulesBlock getProofRulesBlock(ISCTheoryRoot scRoot, String label) throws RodinDBException{
		ISCProofRulesBlock[] proofRulesBlocks = scRoot.getProofRulesBlocks();
		for (ISCProofRulesBlock block : proofRulesBlocks){
			if (block.getLabel().equals(label)){
				return block;
			}
		}
		fail("no block " + label + " found in theory " + scRoot.getRodinFile());
		return null;
	}
	
	public ISCProofRulesBlock[] getProofRulesBlocks(ISCTheoryRoot scRoot, String... labels) throws RodinDBException{
		ISCProofRulesBlock[] blocks = scRoot.getProofRulesBlocks();
		assertEquals("wrong number of block", labels.length, blocks.length);
		if (labels.length == 0)
			return blocks;
		Set<String> nameSet = getLabelNameSet(blocks);
		for (String string : labels)
			assertTrue("should contain " + string, nameSet.contains(string));
		return blocks;
	}
	
	public ISCRewriteRuleRightHandSide getRightHandSide(ISCRewriteRule rule, String label) throws RodinDBException{
		ISCRewriteRuleRightHandSide[] rhss = rule.getRuleRHSs();
		for (ISCRewriteRuleRightHandSide rhs : rhss){
			if (rhs.getLabel().equals(label)){
				return rhs;
			}
		}
		fail("no rhs "+label + " found in rule "+rule.getElementName());
		return null;
	}
	
	public ISCRewriteRule getRewriteRule(ISCTheoryRoot scRoot, String block,  String label) throws RodinDBException{
		ISCProofRulesBlock rulesBlock = getProofRulesBlock(scRoot, block);
		assertNotNull(rulesBlock);
		ISCRewriteRule[] rules = rulesBlock.getRewriteRules();
		for (ISCRewriteRule rule : rules){
			if (rule.getLabel().equals(label)){
				return rule;
			}
		}
		fail("no rule " + label + " found in block "+ block+" in theory " + scRoot.getRodinFile());
		return null;
	}
	
	public ISCInferenceRule getInferenceRule(ISCTheoryRoot scRoot, String block,  String label) throws RodinDBException{
		ISCProofRulesBlock rulesBlock = getProofRulesBlock(scRoot, block);
		assertNotNull(rulesBlock);
		ISCInferenceRule[] rules = rulesBlock.getInferenceRules();
		for (ISCInferenceRule rule : rules){
			if (rule.getLabel().equals(label)){
				return rule;
			}
		}
		fail("no rule " + label + " found in block "+ block+" in theory " + scRoot.getRodinFile());
		return null;
	}

	public ISCDatatypeDefinition getDatatype(ISCTheoryRoot scRoot, String name) throws RodinDBException {
		ISCDatatypeDefinition[] dts = scRoot.getSCDatatypeDefinitions();
		for (ISCDatatypeDefinition dt : dts) {
			if (dt.getIdentifierString().equals(name)) {
				return dt;
			}
		}
		fail("no datatype " + name + " found in theory " + scRoot.getRodinFile());
		return null;
	}
	
	public ISCNewOperatorDefinition getOperatorDefinition(ISCTheoryRoot scRoot, String syntax) throws RodinDBException{
		ISCNewOperatorDefinition[] ops = scRoot.getSCNewOperatorDefinitions();
		for (ISCNewOperatorDefinition op : ops) {
			if (op.getLabel().equals(syntax)) {
				return op;
			}
		}
		fail("no operator " + syntax + " found in theory " + scRoot.getRodinFile());
		return null;
	}

	public ISCDatatypeDefinition[] getDatatypes(ISCTheoryRoot root, String... names) throws RodinDBException {
		ISCDatatypeDefinition[] scDts = root.getSCDatatypeDefinitions();
		assertEquals("wrong number of datatypes", names.length, scDts.length);
		if (names.length == 0)
			return scDts;
		Set<String> nameSet = getIdentifierNameSet(scDts);
		for (String string : names)
			assertTrue("should contain " + string, nameSet.contains(string));
		return scDts;
	}
	
	public ISCNewOperatorDefinition[] getOperators(ISCTheoryRoot root, String... syntaxes) throws RodinDBException {
		ISCNewOperatorDefinition[] scOps = root.getSCNewOperatorDefinitions();
		assertEquals("wrong number of operators", syntaxes.length, scOps.length);
		if (syntaxes.length == 0)
			return scOps;
		Set<String> nameSet = getLabelNameSet(scOps);
		for (String string : syntaxes)
			assertTrue("should contain " + string, nameSet.contains(string));
		return scOps;
	}

	protected Set<String> getImportNameSet(ISCImportTheory[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for (ISCImportTheory element : elements)
			if (element != null)
				names.add(element.getImportTheory().getComponentName());
		return names;
	}

	protected Set<String> getIdentifierNameSet(ISCIdentifierElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for (ISCIdentifierElement element : elements)
			if (element != null)
				names.add(element.getIdentifierString());
		return names;
	}

	protected Set<String> getLabelNameSet(ILabeledElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for (ILabeledElement element : elements)
			names.add(element.getLabel());
		return names;
	}
	
	protected List<String> getLabelList(ISCPredicateElement[] predicateElements) throws RodinDBException {
		List<String> list = new ArrayList<String>(predicateElements.length);
		for (ISCPredicateElement element : predicateElements) {
			ILabeledElement labeledElement = (ILabeledElement) element;
			list.add(labeledElement.getLabel());
		}
		return list;
	}

	public String getNormalizedExpression(String input, FormulaFactory factory, ITypeEnvironment environment) {
		Expression expr = factory.parseExpression(input, V2, null).getParsedExpression();
		expr.typeCheck(environment);
		assertTrue(expr.isTypeChecked());
		return expr.toStringWithTypes();
	}

	public String getNormalizedPredicate(String input, FormulaFactory factory, ITypeEnvironment environment) {
		Predicate pred = factory.parsePredicate(input, V2, null).getParsedPredicate();
		pred.typeCheck(environment);
		assertTrue(pred.isTypeChecked());
		return pred.toStringWithTypes();
	}

	public void containsMarkers(IInternalElement element, boolean yes)
			throws CoreException {
		final IFile file = element.getResource();
		final IMarker[] markers = file.findMarkers(RODIN_PROBLEM_MARKER, true,
				DEPTH_INFINITE);

		if (yes) {
			assertTrue("Should contain markers", markers.length != 0);
		} else if (markers.length != 0) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Unexpected markers found on element " + element + ":");
			for (final IMarker marker : markers) {
				sb.append("\n\t");
				sb.append(marker.getAttribute(MESSAGE));
			}
			fail(sb.toString());
		}
	}
	
	public void hasMarker(IRodinElement element, IAttributeType attrType) throws Exception {
		hasMarker(element, attrType, null);
	}

	public void hasMarker(IRodinElement element) throws Exception {
		hasMarker(element, null);
	}

	public void hasNotMarker(IRodinElement element, IRodinProblem problem) throws Exception {
		IRodinFile file = (IRodinFile) element.getOpenable();
		IMarker[] markers = file.getResource().findMarkers(RodinMarkerUtil.RODIN_PROBLEM_MARKER, true,
				IResource.DEPTH_INFINITE);
		for (IMarker marker : markers) {
			IRodinElement elem = RodinMarkerUtil.getElement(marker);
			if (elem != null && elem.equals(element))
				if (problem == null || problem.getErrorCode().equals(RodinMarkerUtil.getErrorCode(marker)))
					fail("surplus problem marker on element");
		}
	}

	public void hasNotMarker(IRodinElement element) throws Exception {
		hasNotMarker(element, null);
	}

	public void hasMarker(IRodinElement element, IAttributeType attrType, IRodinProblem problem, String... args)
			throws Exception {
		IRodinFile file = (IRodinFile) element.getOpenable();
		IMarker[] markers = file.getResource().findMarkers(RodinMarkerUtil.RODIN_PROBLEM_MARKER, true,
				IResource.DEPTH_INFINITE);
		for (IMarker marker : markers) {
			IRodinElement elem = RodinMarkerUtil.getInternalElement(marker);
			if (elem != null && elem.equals(element)) {
				if (attrType != null) {
					IAttributeType attributeType = RodinMarkerUtil.getAttributeType(marker);
					assertEquals("problem not attached to attribute", attrType, attributeType);
				}
				if (problem == null)
					return;
				if (problem.getErrorCode().equals(RodinMarkerUtil.getErrorCode(marker))) {
					String[] pargs = RodinMarkerUtil.getArguments(marker);
					assertEquals(args.length, pargs.length);
					for (int i = 0; i < args.length; i++) {
						assertEquals(args[i], pargs[i]);
					}
					return;
				}
			}
		}
		fail("problem marker missing from element"
				+ ((attrType != null) ? " (attribute: " + attrType.getId() + ")" : ""));
	}

	public void isNotAccurate(IAccuracyElement element) throws RodinDBException {
		boolean acc = element.isAccurate();

		assertEquals("element is accurate", false, acc);
	}
	
	public void isComplete(ICompleteElement elm) throws RodinDBException{
		boolean comp = elm.isComplete();
		assertEquals("element is incomplete",true, comp);
	}
	
	public void isAutomatic(IApplicabilityElement elm) throws RodinDBException{
		boolean auto = elm.isAutomatic();
		assertTrue("element is not automatic", auto);
	}
	
	public void isInteractive(IApplicabilityElement elm) throws RodinDBException{
		boolean inter = elm.isInteractive();
		assertTrue("element is not interactive", inter);
	}
	
	public void isNotAutomatic(IApplicabilityElement elm) throws RodinDBException{
		boolean auto = elm.isAutomatic();
		assertFalse("element is automatic", auto);
	}
	
	public void isNotInteractive(IApplicabilityElement elm) throws RodinDBException{
		boolean inter = elm.isInteractive();
		assertFalse("element is interactive", inter);
	}
	
	public void isNotComplete(ICompleteElement elm) throws RodinDBException{
		boolean comp = elm.isComplete();
		assertEquals("element is complete",false, comp);
	}

	public void isAccurate(IAccuracyElement element) throws RodinDBException {
		boolean acc = element.isAccurate();

		assertEquals("element is not accurate", true, acc);
	}
	
	public void hasError(IHasErrorElement element) throws RodinDBException{
		boolean error = element.hasError();
		assertTrue("element "+element + " should have error", error);
	}
	
	public void doesNotHaveError(IHasErrorElement element) throws RodinDBException{
		boolean error = element.hasError();
		assertFalse("element "+element + " should not have error", error);
	}
}
