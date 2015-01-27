package org.eventb.theory.core.tests.sc.modules;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.theory.core.sc.TheoryGraphProblem.ConstructorNameAlreadyATypeParError;
import static org.eventb.theory.core.sc.TheoryGraphProblem.DatatypeHasNoBaseConsError;
import static org.eventb.theory.core.sc.TheoryGraphProblem.DatatypeHasNoConsError;
import static org.eventb.theory.core.sc.TheoryGraphProblem.DatatypeNameAlreadyATypeParError;
import static org.eventb.theory.core.sc.TheoryGraphProblem.DestructorNameAlreadyATypeParError;
import static org.eventb.theory.core.sc.TheoryGraphProblem.InvalidIdentForConstructor;
import static org.eventb.theory.core.sc.TheoryGraphProblem.InvalidIdentForDatatype;
import static org.eventb.theory.core.sc.TheoryGraphProblem.InvalidIdentForDestructor;

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
import org.eventb.theory.core.sc.modules.DatatypeModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @see DatatypeModule
 * @see DatatypeConstructorModule
 * @see DatatypeDestructorModule
 * @author maamria
 * @author asiehsalehi
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "finite");
		IDatatypeDefinition dt1 = addDatatypeDefinition(root, "#");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		getDatatypes(scTheoryRoot);
		containsMarkers(root, true);
		hasMarker(dt, IDENTIFIER_ATTRIBUTE, InvalidIdentForDatatype, "finite");
		hasMarker(dt1, IDENTIFIER_ATTRIBUTE, InvalidIdentForDatatype, "#");
	}

	/**
	 * Name clash with type par
	 */
	@Test
	public void testDatatypes_004_ClashTypePar() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		getDatatypes(scTheoryRoot);
		containsMarkers(root, true);
		hasMarker(dt, IDENTIFIER_ATTRIBUTE, DatatypeNameAlreadyATypeParError, "T");
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		IDatatypeDefinition dt1 = addDatatypeDefinition(root, "cons");
		IDatatypeDefinition dt2 = addDatatypeDefinition(root, "nil");
		IDatatypeDefinition dt3 = addDatatypeDefinition(root, "head");
		IDatatypeDefinition dt4 = addDatatypeDefinition(root, "tail");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		containsTypeParameters(scTheoryRoot, "T");
		getDatatype(scTheoryRoot, "List");
		hasMarker(dt, IDENTIFIER_ATTRIBUTE, InvalidIdentForDatatype, "List");
		hasMarker(dt1, IDENTIFIER_ATTRIBUTE, InvalidIdentForDatatype, "cons");
		hasMarker(dt2, IDENTIFIER_ATTRIBUTE, InvalidIdentForDatatype, "nil");
		hasMarker(dt3, IDENTIFIER_ATTRIBUTE, InvalidIdentForDatatype, "head");
		hasMarker(dt4, IDENTIFIER_ATTRIBUTE, InvalidIdentForDatatype, "tail");
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
		isNotAccurate(scTheoryRoot);
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		ITypeArgument arg = addTypeArgument(dt, "S");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		addTypeArgument(dt, "T");
		ITypeArgument arg = addTypeArgument(dt, "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "DT");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "DT");
		hasError(scDt);
		hasMarker(dt, IDENTIFIER_ATTRIBUTE, DatatypeHasNoConsError, "DT");
	}

	/**
	 * no base constructors
	 */
	@Test
	public void testDatatypes_010_NoBaseCons() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IDatatypeDefinition dt = addDatatypeDefinition(root, "DT");
		IDatatypeConstructor cons = addDatatypeConstructor(dt, "cons");
		addDatatypeDestructor(cons, "foo", "DT");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "DT");
		hasError(scDt);
		hasMarker(dt, IDENTIFIER_ATTRIBUTE, DatatypeHasNoBaseConsError, "DT");
	}

	/**
	 * constructor missing identifier
	 */
	@Test
	public void testDatatypes_011_NoConsIdent() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "DT");
		dt.createChild(IDatatypeConstructor.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "DT");
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "DT");
		IDatatypeConstructor cons = addDatatypeConstructor(dt, "finite");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "DT");
		hasError(scDt);
		hasMarker(cons, IDENTIFIER_ATTRIBUTE, InvalidIdentForConstructor,
				"finite");
	}

	/**
	 * constructor a type par
	 */
	@Test
	public void testDatatypes_013_ConsIsTypePar() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		addTypeArgument(dt, "T");
		IDatatypeConstructor cons = addDatatypeConstructor(dt, "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(cons, IDENTIFIER_ATTRIBUTE,
				ConstructorNameAlreadyATypeParError, "T");
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "DT");
		IDatatypeConstructor cons = addDatatypeConstructor(dt, "head");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "DT");
		hasError(scDt);
		getDatatypes(scTheoryRoot, "List", "DT");
		hasMarker(cons, IDENTIFIER_ATTRIBUTE, InvalidIdentForConstructor,
				"head");
	}

	/**
	 * missing destructor argument identifier
	 */
	@Test
	public void testDatatypes_015_NoDestIdent() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		addTypeArgument(dt, "T");
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons");
		IConstructorArgument dest = dtCons.createChild(IConstructorArgument.ELEMENT_TYPE, null, null);
		dest.setType("T", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		addTypeArgument(dt, "T");
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons");
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "card", "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, IDENTIFIER_ATTRIBUTE, InvalidIdentForDestructor, "card");
	}

	/**
	 * destructor name a type par
	 */
	@Test
	public void testDatatypes_017_DestIsTypePar() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "DT");
		addTypeArgument(dt, "T");
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons");
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "T", "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "DT");
		hasError(scDt);
		hasMarker(dest, IDENTIFIER_ATTRIBUTE,
				DestructorNameAlreadyATypeParError, "T");
	}
	/**
	 * destructor MISSING type
	 */
	@Test
	public void testDatatypes_018_NoDestType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons");
		IConstructorArgument dest = dtCons.createChild(IConstructorArgument.ELEMENT_TYPE, null, null);
		dest.setIdentifierString("head", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
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
		IDatatypeDefinition dt = addDatatypeDefinition(root, "DT");
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "someCons");
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "nil", "T");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "DT");
		hasError(scDt);
		getDatatypes(scTheoryRoot, "List", "DT");
		hasMarker(dest, IDENTIFIER_ATTRIBUTE, InvalidIdentForDestructor, "nil");
	}
	
	/**
	 * destructor type issue
	 */
	@Test
	public void testDatatypes_020_DestTypeIssue() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		addTypeArgument(dt, "T");
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons");
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "head", "ttT");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, TheoryAttributes.TYPE_ATTRIBUTE);
	}
	@Test
	public void testDatatypes_021() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("T", "S"));
		IDatatypeDefinition dt = addDatatypeDefinition(root, "List");
		addTypeArgument(dt, "T");
		IDatatypeConstructor dtCons = addDatatypeConstructor(dt, "cons");
		IConstructorArgument dest = addDatatypeDestructor(dtCons, "head", "S");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCDatatypeDefinition scDt = getDatatype(scTheoryRoot, "List");
		hasError(scDt);
		hasMarker(dest, TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.TypeIsNotRefTypeError, "S");
	}
	
	// add tests for admissibility 
}
