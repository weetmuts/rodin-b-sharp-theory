package org.eventb.core.ast.extensions.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;
import org.eventb.core.internal.ast.extensions.maths.CompleteDatatypeExtension;
import org.eventb.core.internal.ast.extensions.maths.OperatorArgument;

/**
 * 
 * Abstract class for tests of PM/AST Extensions
 * <p>
 * TODO cache extensions between tests
 * 
 * @author maamria
 * 
 */
public abstract class BasicAstExtTest extends TestCase {

	protected FormulaFactory factory;
	protected ITypeEnvironment environment;

	// cache the extensions we use, no need to recreate
	private IPredicateExtension PRIME_EXTENSION;
	private Set<IFormulaExtension> REAL_TYPE_EXTENSIONS;
	private IPredicateExtension IS_EMPTY_EXTENSION;
	private IPredicateExtension SAME_SIZE_EXTENSION;
	private IExpressionExtension SEQ_EXTENSION;
	private IExpressionExtension SEQ_SIZE_EXTENSION;
	private IExpressionExtension SEQ_TAIL_EXTENSION;
	private IExpressionExtension SEQ_HEAD_EXTENSION;
	private IExpressionExtension AND_EXTENSION;
	private IExpressionExtension OR_EXTENSION;
	private IExpressionExtension SEQ_CONCAT_EXTENSION;
	private Set<IFormulaExtension> LIST_EXTENSIONS;
	private Set<IFormulaExtension> DIRECTION_EXTENSIONS;
	private IExpressionExtension SIZE_LIST_EXTENSION;
	private IExpressionExtension OPPOSITE_EXTENSION;
	private IExpressionExtension NOT_EXTENSION;

	public static final Type BOOL = FormulaFactory.getDefault().makeBooleanType();
	public static final Type Z = FormulaFactory.getDefault().makeIntegerType();
	public static final Predicate TRUE = FormulaFactory.getDefault().makeLiteralPredicate(Formula.BTRUE, null);
	public static final Predicate FALSE = FormulaFactory.getDefault().makeLiteralPredicate(Formula.BFALSE, null);

	public OperatorExtensionProperties operatorExtensionProperties(String operatorID, String syntax,
			FormulaType formulaType, Notation notation, String groupID) {
		return MathExtensionsFactory.getOperatorExtensionProperties(operatorID, syntax, formulaType, notation, groupID);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = FormulaFactory.getDefault();
		environment = factory.makeTypeEnvironment();
	}

	@Override
	protected void tearDown() throws Exception {
		factory = null;
		environment = null;
		super.tearDown();
	}

	public ITypeEnvironment typeEnvironment(String[] givenTypes, String[] names, String[] types) throws CoreException {
		ITypeEnvironment env = factory.makeTypeEnvironment();
		for (String t : givenTypes) {
			env.addGivenSet(t);
		}
		for (int i = 0; i < names.length; i++) {
			env.addName(names[i], type(types[i]));
		}
		return env;
	}

	public FreeIdentifier ident(String name, String type) throws CoreException {
		return factory.makeFreeIdentifier(name, null, type(type));
	}

	public PredicateVariable predVar(String name) throws CoreException {
		return factory.makePredicateVariable(name, null);
	}

	public void addCond() throws CoreException {
		factory = factory.withExtensions(Collections.singleton((IFormulaExtension) FormulaFactory.getCond()));
	}

	public void removeCond() throws CoreException {
		removeExtension(FormulaFactory.getCond());
	}

	public void removeExtensions(Set<IFormulaExtension> toRemove) throws CoreException {
		// get the current set of extensions
		Set<IFormulaExtension> exts = factory.getExtensions();
		// remove cond -- does not affect ff since a copy of exts is returned
		exts.removeAll(toRemove);
		// set the factory appropriately
		factory = factory.withExtensions(exts);
	}

	public void removeExtension(IFormulaExtension toRemove) throws CoreException {
		// get the current set of extensions
		Set<IFormulaExtension> exts = factory.getExtensions();
		// remove cond -- does not affect ff since a copy of exts is returned
		exts.remove(toRemove);
		// set the factory appropriately
		factory = factory.withExtensions(exts);
	}

	public static String[] makeSList(String... strings) {
		return strings;
	}

	public List<OperatorArgument> operatorArguments(String[] names, String... types) throws CoreException {
		assert names.length == types.length;
		List<OperatorArgument> operatorArguments = new ArrayList<OperatorArgument>();
		int i = 0;
		for (String name : names) {
			operatorArguments.add(new OperatorArgument(i, name, factory.parseType(types[i], LanguageVersion.V2)
					.getParsedType()));
			i++;
		}
		return operatorArguments;
	}

	public Predicate predicate(String str) throws CoreException {
		return factory.parsePredicate(str, LanguageVersion.V2, null).getParsedPredicate();
	}

	public Predicate tcPredicate(String str) throws CoreException {
		Predicate parsedPredicate = factory.parsePredicate(str, LanguageVersion.V2, null).getParsedPredicate();
		if (parsedPredicate == null)
			fail("expected string to parse as predicate but was not");
		ITypeCheckResult typeCheck = parsedPredicate.typeCheck(environment);
		if (typeCheck.hasProblem())
			fail("expected string to parse as predicate and type check but was not");
		return parsedPredicate;
	}

	public Expression expression(String str) throws CoreException {
		return factory.parseExpression(str, LanguageVersion.V2, null).getParsedExpression();
	}

	public Expression tcExpression(String str) throws CoreException {
		Expression parsedExpression = factory.parseExpression(str, LanguageVersion.V2, null).getParsedExpression();
		parsedExpression.typeCheck(environment);
		return parsedExpression;
	}

	public Expression tcExpression(String str, String[] gTypes, String names[], String... types) throws CoreException {
		Expression parsedExpression = factory.parseExpression(str, LanguageVersion.V2, null).getParsedExpression();
		ITypeEnvironment te = typeEnvironment(gTypes, names, types);
		parsedExpression.typeCheck(te);
		return parsedExpression;
	}

	public Predicate tcPredicate(String str, String[] gTypes, String names[], String... types) throws CoreException {
		Predicate parsedPredicate = factory.parsePredicate(str, LanguageVersion.V2, null).getParsedPredicate();
		ITypeEnvironment te = typeEnvironment(gTypes, names, types);
		parsedPredicate.typeCheck(te);
		return parsedPredicate;
	}

	public GivenType givenType(String str) throws CoreException {
		return factory.makeGivenType(str);
	}

	public Type type(String str) throws CoreException {
		return factory.parseType(str, LanguageVersion.V2).getParsedType();
	}

	public IPredicateExtension primeExtension() throws CoreException {
		if (PRIME_EXTENSION != null)
			return PRIME_EXTENSION;
		OperatorExtensionProperties properties = new OperatorExtensionProperties("primeId", "prime",
				FormulaType.PREDICATE, Notation.PREFIX, "primeGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("n", factory.makeIntegerType());
		}
		PRIME_EXTENSION = MathExtensionsFactory.getPredicateExtension(properties, false, operatorArguments,
				predicate("n > 1"), predicate("n > 1"), null);
		return PRIME_EXTENSION;
	}

	public IPredicateExtension sameSizeExtension() throws CoreException {
		if (SAME_SIZE_EXTENSION != null)
			return SAME_SIZE_EXTENSION;
		OperatorExtensionProperties properties = new OperatorExtensionProperties("sameSizeId", "sameSize",
				FormulaType.PREDICATE, Notation.PREFIX, "sameSizeGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("s1", type("ℙ(T)"));
			operatorArguments.put("s2", type("ℙ(S)"));
		}
		SAME_SIZE_EXTENSION = MathExtensionsFactory.getPredicateExtension(properties, true, operatorArguments,
				predicate("finite(s1) ∧ finite(s2)"), predicate("finite(s1) ∧ finite(s2)"), null);
		return SAME_SIZE_EXTENSION;
	}

	public IExpressionExtension seqExtension() throws CoreException {
		if (SEQ_EXTENSION != null)
			return SEQ_EXTENSION;
		OperatorExtensionProperties properties = new OperatorExtensionProperties("seqId", "seq",
				FormulaType.EXPRESSION, Notation.PREFIX, "seqGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("a", type("ℙ(A)"));
		}
		SEQ_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, false, operatorArguments,
				type("ℙ(ℤ↔A)"), TRUE, TRUE, null);
		return SEQ_EXTENSION;
	}

	public IPredicateExtension isEmptySeqExtension() throws CoreException {
		if (IS_EMPTY_EXTENSION != null) {
			return IS_EMPTY_EXTENSION;
		}
		addExtensions(seqExtension());
		OperatorExtensionProperties properties = new OperatorExtensionProperties("isEmptyId", "isEmpty",
				FormulaType.PREDICATE, Notation.PREFIX, "seqGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("s", type("ℤ↔A"));
		}
		IS_EMPTY_EXTENSION = MathExtensionsFactory.getPredicateExtension(properties, false, operatorArguments,
				predicate("s ∈ seq(A)"), predicate("s ∈ seq(A)"), null);
		return IS_EMPTY_EXTENSION;
	}

	public IExpressionExtension seqSizeExtension() throws CoreException {
		if (SEQ_SIZE_EXTENSION != null)
			return SEQ_SIZE_EXTENSION;
		addExtensions(seqExtension());
		OperatorExtensionProperties properties = new OperatorExtensionProperties("seqSizeId", "seqSize",
				FormulaType.EXPRESSION, Notation.PREFIX, "seqGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("s", type("ℤ↔A"));
		}
		SEQ_SIZE_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, false, operatorArguments,
				Z, predicate("s ∈ seq(A)"), predicate("s ∈ seq(A)"), null);
		return SEQ_SIZE_EXTENSION;
	}

	public IExpressionExtension seqTailExtension() throws CoreException {
		if (SEQ_TAIL_EXTENSION != null)
			return SEQ_TAIL_EXTENSION;
		addExtensions(seqExtension(), isEmptySeqExtension());
		OperatorExtensionProperties properties = new OperatorExtensionProperties("seqTailId", "seqTail",
				FormulaType.EXPRESSION, Notation.PREFIX, "seqGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("s", type("ℤ↔A"));
		}
		SEQ_TAIL_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, false, operatorArguments,
				type("ℤ↔A"), predicate("s ∈ seq(A) ∧ ¬(isEmpty(s))"), predicate("s ∈ seq(A) ∧ ¬(isEmpty(s))"), null);
		return SEQ_TAIL_EXTENSION;
	}

	public IExpressionExtension seqHeadExtension() throws CoreException {
		if (SEQ_HEAD_EXTENSION != null)
			return SEQ_HEAD_EXTENSION;
		addExtensions(seqExtension(), isEmptySeqExtension());
		OperatorExtensionProperties properties = new OperatorExtensionProperties("seqHeadId", "seqHead",
				FormulaType.EXPRESSION, Notation.PREFIX, "seqGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("s", type("ℤ↔A"));
		}
		SEQ_HEAD_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, false, operatorArguments,
				type("A"), predicate("s ∈ seq(A) ∧ ¬(isEmpty(s))"), predicate("s ∈ seq(A) ∧ ¬(isEmpty(s))"), null);
		return SEQ_HEAD_EXTENSION;
	}

	public IExpressionExtension andExtension() throws CoreException {
		if (AND_EXTENSION != null)
			return AND_EXTENSION;
		OperatorExtensionProperties properties = new OperatorExtensionProperties("andId", "AND",
				FormulaType.EXPRESSION, Notation.INFIX, "boolGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("a1", BOOL);
			operatorArguments.put("a2", BOOL);
		}
		AND_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, true, true, operatorArguments, BOOL,
				TRUE, TRUE, null);
		return AND_EXTENSION;
	}

	public IExpressionExtension orExtension() throws CoreException {
		if (OR_EXTENSION != null)
			return OR_EXTENSION;
		OperatorExtensionProperties properties = new OperatorExtensionProperties("orId", "OR", FormulaType.EXPRESSION,
				Notation.INFIX, "boolGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("a1", BOOL);
			operatorArguments.put("a2", BOOL);
		}
		OR_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, true, true, operatorArguments, BOOL,
				TRUE, TRUE, null);
		return OR_EXTENSION;
	}

	public IExpressionExtension notExtension() throws CoreException {
		if (NOT_EXTENSION != null)
			return NOT_EXTENSION;
		OperatorExtensionProperties properties = new OperatorExtensionProperties("notId", "not",
				FormulaType.EXPRESSION, Notation.PREFIX, "notGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("a", BOOL);
		}
		NOT_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, false, operatorArguments, BOOL,
				TRUE, TRUE, null);
		return NOT_EXTENSION;
	}

	public IExpressionExtension seqConcatExtension() throws CoreException {
		if (SEQ_CONCAT_EXTENSION != null)
			return SEQ_CONCAT_EXTENSION;
		addExtensions(seqExtension());
		OperatorExtensionProperties properties = new OperatorExtensionProperties("seqConcatId", "seqConcat",
				FormulaType.EXPRESSION, Notation.INFIX, "seqConcatGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("s1", type("ℤ↔A"));
			operatorArguments.put("s2", type("ℤ↔A"));
		}
		SEQ_CONCAT_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, true, operatorArguments,
				type("ℤ↔A"), predicate("s1 ∈ seq(A) ∧ s2 ∈ seq(A)"), predicate("s1 ∈ seq(A) ∧ s2 ∈ seq(A)"), null);
		return SEQ_CONCAT_EXTENSION;
	}

	public Set<IFormulaExtension> listExtensions() throws CoreException {
		if (LIST_EXTENSIONS != null)
			return LIST_EXTENSIONS;
		Map<String, Map<String, String>> constructors = new LinkedHashMap<String, Map<String, String>>();
		{
			constructors.put("nil", Collections.<String, String> emptyMap());
			Map<String, String> consDest = new LinkedHashMap<String, String>();
			{
				consDest.put("head", "T");
				consDest.put("tail", "List(T)");
			}
			constructors.put("cons", consDest);
		}
		IDatatypeExtension ext = new CompleteDatatypeExtension("List", makeSList("T"), constructors);
		LIST_EXTENSIONS = factory.makeDatatype(ext).getExtensions();
		return LIST_EXTENSIONS;
	}

	public Set<IFormulaExtension> directionExtensions() throws CoreException {
		if (DIRECTION_EXTENSIONS != null)
			return DIRECTION_EXTENSIONS;
		Map<String, Map<String, String>> constructors = new LinkedHashMap<String, Map<String, String>>();
		{
			constructors.put("NORTH", Collections.<String, String> emptyMap());
			constructors.put("SOUTH", Collections.<String, String> emptyMap());
			constructors.put("EAST", Collections.<String, String> emptyMap());
			constructors.put("WEST", Collections.<String, String> emptyMap());
		}
		IDatatypeExtension ext = new CompleteDatatypeExtension("DIRECTION", makeSList(), constructors);
		DIRECTION_EXTENSIONS = factory.makeDatatype(ext).getExtensions();
		return DIRECTION_EXTENSIONS;
	}

	public IExpressionExtension listSizeExtension() throws CoreException {
		if (SIZE_LIST_EXTENSION != null)
			return SIZE_LIST_EXTENSION;
		Set<IFormulaExtension> listExtensions = listExtensions();
		addExtensions(listExtensions.toArray(new IFormulaExtension[listExtensions.size()]));

		OperatorExtensionProperties properties = new OperatorExtensionProperties("listSizeId", "listSize",
				FormulaType.EXPRESSION, Notation.PREFIX, "listSizeGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("l", type("List(T)"));
		}
		SIZE_LIST_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, false, operatorArguments,
				Z, TRUE, TRUE, null);
		return SIZE_LIST_EXTENSION;
	}

	public IExpressionExtension oppositeExtension() throws CoreException {
		if (OPPOSITE_EXTENSION != null)
			return OPPOSITE_EXTENSION;
		Set<IFormulaExtension> dirExtensions = directionExtensions();
		addExtensions(dirExtensions.toArray(new IFormulaExtension[dirExtensions.size()]));
		OperatorExtensionProperties properties = new OperatorExtensionProperties("oppositeId", "opposite",
				FormulaType.EXPRESSION, Notation.PREFIX, "oppositeGroup");
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		{
			operatorArguments.put("d", type("DIRECTION"));
		}
		OPPOSITE_EXTENSION = MathExtensionsFactory.getExpressionExtension(properties, false, false, operatorArguments,
				type("DIRECTION"), TRUE, TRUE, null);
		return OPPOSITE_EXTENSION;
	}

	public void addExtensions(IFormulaExtension... extensions) throws CoreException {
		factory = factory.withExtensions(set(extensions));
		// ensure we use a compatible type environment
		environment = AstUtilities.getTypeEnvironmentForFactory(environment, factory);
	}

	public void addExtensions(Set<IFormulaExtension> extensions) throws CoreException {
		factory = factory.withExtensions(extensions);
		// ensure we use a compatible type environment
		environment = AstUtilities.getTypeEnvironmentForFactory(environment, factory);
	}

	public void addNames(String[] idents, String... types) throws CoreException {
		assert idents.length == types.length;
		for (int i = 0; i < idents.length; i++) {
			environment.addName(idents[i], type(types[i]));
		}
	}

	public void addTypes(String... strings) {
		for (String str : strings) {
			environment.addGivenSet(str);
		}
	}

	public void assertTypeCheck(Formula<?> formula) {
		assertTrue("formula " + formula + " is not type checked", formula.isTypeChecked());
	}

	public void equalFormulae(Formula<?> expected, Formula<?> actual) {
		assertEquals("expected same formula but found different ", expected, actual);
	}

	public void assertParses(String str, boolean isPredicate) throws CoreException {
		if (isPredicate) {
			IParseResult res = factory.parsePredicate(str, LanguageVersion.V2, null);
			if (res.hasProblem()) {
				fail("expected string to parse as predicate, but did not");
			}
		} else {
			IParseResult res = factory.parseExpression(str, LanguageVersion.V2, null);
			if (res.hasProblem()) {
				fail("expected string to parse as expression, but did not");
			}
		}
	}

	public void assertNotParses(String str, boolean isPredicate) throws CoreException {
		if (isPredicate) {
			IParseResult res = factory.parsePredicate(str, LanguageVersion.V2, null);
			if (!res.hasProblem()) {
				fail("expected string to not parse as predicate, but did");
			}
		} else {
			IParseResult res = factory.parseExpression(str, LanguageVersion.V2, null);
			if (!res.hasProblem()) {
				fail("expected string to not parse as expression, but did");
			}
		}
	}

	public Formula<?> parse(String str) throws CoreException {
		IParseResult res = factory.parsePredicate(str, LanguageVersion.V2, null);
		if (res.hasProblem()) {
			res = factory.parseExpression(str, LanguageVersion.V2, null);
			if (res.hasProblem()) {
				fail("string could not be parsed as predicate nor expression");
			} else {
				return res.getParsedExpression();
			}
		}
		return res.getParsedPredicate();
	}

	public Formula<?> typeCheck(String str) throws CoreException {
		Formula<?> parse = parse(str);
		ITypeCheckResult typeCheck = parse.typeCheck(environment);
		if (typeCheck.hasProblem()) {
			fail("string formula could not be typecheck");
			return null;
		}
		return parse;
	}

	public void notTypeCheck(String str) throws CoreException {
		Formula<?> parse = parse(str);
		ITypeCheckResult typeCheck = parse.typeCheck(environment);
		if (!typeCheck.hasProblem()) {
			fail("string formula typechecked when expected not to typecheck");
		}
	}

	public static <E> Set<E> set(E... es) {
		return new LinkedHashSet<E>(Arrays.asList(es));
	}

	public static <E> E[] array(E... es) {
		return es;
	}

	public static IFormulaExtension[] array(Set<IFormulaExtension> set) {
		return set.toArray(new IFormulaExtension[set.size()]);
	}

	public static <E> void assertContains(Collection<E> col, E... es) {
		for (E e : es)
			assertTrue("collection should contain " + e + " but does not", col.contains(e));
	}

	public static <E> void assertNotContain(Collection<E> col, E... es) {
		for (E e : es)
			assertFalse("collection should not contain " + e + " but does", col.contains(e));
	}

	public void contains(ITypeEnvironment env, String[] names, String[] types) throws Exception {
		assert names.length == types.length;
		for (int i = 0; i < names.length; i++) {
			assertEquals("types expecected to be equal but were not", type(types[i]), env.getType(names[i]));
		}
	}

	public void reset() {
		factory = FormulaFactory.getDefault();
		environment = factory.makeTypeEnvironment();
	}

	public Set<IFormulaExtension> realTypeExtensions() throws Exception {
		if (REAL_TYPE_EXTENSIONS != null)
			return REAL_TYPE_EXTENSIONS;
		REAL_TYPE_EXTENSIONS = Collections.singleton(
				(IFormulaExtension)MathExtensionsFactory.getAxiomaticTypeExtension("REAL", "REAL ID", null));
		return REAL_TYPE_EXTENSIONS;
	}
}
