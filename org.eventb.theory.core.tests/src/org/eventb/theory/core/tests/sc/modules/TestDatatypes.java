package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.theory.core.IConstructorArgument;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.modules.DatatypeConstructorModule;
import org.eventb.theory.core.sc.modules.DatatypeDestructorModule;
import org.eventb.theory.core.sc.modules.DatatypeModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @see DatatypeModule
 * @see DatatypeConstructorModule
 * @see DatatypeDestructorModule
 * @author maamria
 * 
 */
public class TestDatatypes extends BasicTheorySCTestWithThyConfig {

	/**
	 * No Error
	 */
	@Test
	public void testDatatypes_001_NoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons", "nil"),
				new String[][] { makeSList("head", "tail"), makeSList() }, new String[][] { makeSList("T", "List(T)"),
						makeSList() });
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		containsTypeParameters(scTheoryRoot, "T");
		ISCDatatypeDefinition datatype = getDatatype(scTheoryRoot, "List");
		doesNotHaveError(datatype);
	}

	/**
	 * Datatype name missing
	 */
	@Test
	public void testDatatypes_002_DtNameMissing() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IDatatypeDefinition dt = root.createChild(IDatatypeDefinition.ELEMENT_TYPE, null, null);

		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		getDatatypes(scTheoryRoot);
		containsMarkers(root, true);
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * Illegal identifier
	 */
	@Test
	public void testDatatypes_003_IllegIdent() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IDatatypeDefinition dt = addDatatypeDefinition(root, "finite", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		IDatatypeDefinition dt1 = addDatatypeDefinition(root, "#", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		getDatatypes(scTheoryRoot);
		containsMarkers(root, true);
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(dt1, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * Name clash with type par
	 */
	@Test
	public void testDatatypes_004_ClashTypePar() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = root.createChild(IDatatypeDefinition.ELEMENT_TYPE, null, null);
		dt.setIdentifierString("T", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		getDatatypes(scTheoryRoot);
		containsMarkers(root, true);
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.DatatypeNameAlreadyATypeParError, "T");
	}

	/**
	 * Local theory dt conflicts between names
	 */
	@Test
	public void testDatatypes_005_LocNameConf() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons", "nil"),
				new String[][] { makeSList("head", "tail"), makeSList() }, new String[][] { makeSList("T", "List(T)"),
						makeSList() });
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		IDatatypeDefinition dt1 = addDatatypeDefinition(root, "cons", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		IDatatypeDefinition dt2 = addDatatypeDefinition(root, "nil", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		IDatatypeDefinition dt3 = addDatatypeDefinition(root, "head", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		IDatatypeDefinition dt4 = addDatatypeDefinition(root, "tail", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		containsTypeParameters(scTheoryRoot, "T");
		getDatatype(scTheoryRoot, "List");
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(dt1, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(dt2, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(dt3, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(dt4, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * error in type arguments
	 */
	@Test
	public void testDatatypes_006_TypeArgNoArg() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons", "nil"),
				new String[][] { makeSList("head", "tail"), makeSList() }, new String[][] { makeSList("T", "List(T)"),
						makeSList() });
		ITypeArgument arg = dt.createChild(ITypeArgument.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		containsMarkers(dt, true);
		containsMarkers(root, true);
		hasMarker(arg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE);
	}

	/**
	 * type argument type not defined
	 */
	@Test
	public void testDatatypes_007_TypeArgUndef() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons", "nil"),
				new String[][] { makeSList("head", "tail"), makeSList() }, new String[][] { makeSList("T", "List(T)"),
						makeSList() });
		ITypeArgument arg = addTypeArgument(dt, "S");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		containsMarkers(dt, true);
		containsMarkers(root, true);
		hasMarker(arg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE);
	}

	/**
	 * type argument redundant
	 */
	@Test
	public void testDatatypes_008_TypeArgRedund() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons", "nil"),
				new String[][] { makeSList("head", "tail"), makeSList() }, new String[][] { makeSList("T", "List(T)"),
						makeSList() });
		ITypeArgument arg = addTypeArgument(dt, "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		containsMarkers(dt, true);
		containsMarkers(root, true);
		hasMarker(arg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE);
	}

	/**
	 * missing element constructors
	 */
	@Test
	public void testDatatypes_009_NoElmnCons() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList(), new String[][] {},
				new String[][] {});
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.DatatypeHasNoConsError, "List");
	}

	/**
	 * no base constructors
	 */
	@Test
	public void testDatatypes_010_NoBaseCons() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons"),
				new String[][] { makeSList("head", "tail") }, new String[][] { makeSList("T", "List(T)") });
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.DatatypeHasNoBaseConsError, "List");
	}

	/**
	 * constructor missing identifier
	 */
	@Test
	public void testDatatypes_011_NoConsIdent() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons"),
				new String[][] { makeSList("head", "tail") }, new String[][] { makeSList("T", "List(T)") });
		dt.createChild(IDatatypeConstructor.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * constructor name unparsable
	 */
	@Test
	public void testDatatypes_012_UnparsCons() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList("finite"),
				new String[][] {makeSList()}, new String[][] {makeSList()});
		dt.createChild(IDatatypeConstructor.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * constructor a type par
	 */
	@Test
	public void testDatatypes_013_ConsIsTypePar() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList("T"),
				new String[][] {makeSList()}, new String[][] {makeSList()});
		IDatatypeConstructor cons = dt.getDatatypeConstructors()[0];
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(cons, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.ConstructorNameAlreadyATypeParError, "T");
	}
	
	/**
	 * constructor name clash
	 */
	@Test
	public void testDatatypes_014_ConsNameClash() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons", "nil"),
				new String[][] { makeSList("head", "tail"), makeSList() }, new String[][] { makeSList("T", "List(T)"),
						makeSList() });
		IDatatypeDefinition dt = addDatatypeDefinition(root, "AnotherList", makeSList(), makeSList("head"), new String[][] {makeSList()},
				new String[][] {makeSList()});
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "AnotherList");
		hasError(scDt);
		getDatatypes(scTheoryRoot, "List", "AnotherList");
		hasMarker(dt, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * missing destructor argument identifier
	 */
	@Test
	public void testDatatypes_015_NoDestIdent() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList(),
				new String[][] {}, new String[][] {});
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons", new String[]{}, new String[]{});
		IConstructorArgument dest = dtCons.createChild(IConstructorArgument.ELEMENT_TYPE, null, null);
		dest.setType("T", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * destructor name unparsable
	 */
	@Test
	public void testDatatypes_016_UnparsDest() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList(),
				new String[][] {}, new String[][] {});
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons", new String[]{}, new String[]{});
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "card", "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}

	/**
	 * destructor name a type par
	 */
	@Test
	public void testDatatypes_017_DestIsTypePar() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList(),
				new String[][] {}, new String[][] {});
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons", new String[]{}, new String[]{});
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "T", "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.DestructorNameAlreadyATypeParError, "T");
	}
	/**
	 * destructor MISSING type
	 */
	@Test
	public void testDatatypes_018_NoDestType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList(),
				new String[][] {}, new String[][] {});
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons", new String[]{}, new String[]{});
		IConstructorArgument dest = dtCons.createChild(IConstructorArgument.ELEMENT_TYPE, null, null);
		dest.setIdentifierString("head", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.MissingDestructorTypeError, "head");
	}
	
	/**
	 * destructor name clash
	 */
	@Test
	public void testDatatypes_019_DestNameClash() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("cons", "nil"),
				new String[][] { makeSList("head", "tail"), makeSList() }, new String[][] { makeSList("T", "List(T)"),
						makeSList() });
		IDatatypeDefinition dt = addDatatypeDefinition(root, "AnotherList", makeSList(), makeSList(), new String[][] {},
				new String[][] {});
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "anotherCons", new String[]{}, new String[]{});
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "nil", "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "AnotherList");
		hasError(scDt);
		getDatatypes(scTheoryRoot, "List", "AnotherList");
		hasMarker(dest, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.IdenIsAConsNameError, "nil");
	}
	
	/**
	 * destructor type issue
	 */
	@Test
	public void testDatatypes_020_DestTypeIssue() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList(),
				new String[][] {}, new String[][] {});
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons", new String[]{}, new String[]{});
		IConstructorArgument dest = dtCons.createChild(IConstructorArgument.ELEMENT_TYPE, null, null);
		dest.setIdentifierString("head", null);
		dest.setType("ttT", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, TheoryAttributes.TYPE_ATTRIBUTE);
	}
	@Test
	public void testDatatypes_021() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("T", "S"));
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List", makeSList("T"), makeSList(),
				new String[][] {}, new String[][] {});
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons", new String[]{}, new String[]{});
		IConstructorArgument dest = dtCons.createChild(IConstructorArgument.ELEMENT_TYPE, null, null);
		dest.setIdentifierString("head", null);
		dest.setType("S", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.TypeIsNotRefTypeError, "S");
	}
	
	// add tests for admissibility 
}
