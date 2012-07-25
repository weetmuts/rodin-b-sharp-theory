package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.sc.modules.TheoremModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;

/**
 * @see TheoremModule
 * @author maamria
 * 
 */
public class TestTheorems extends BasicTheorySCTestWithThyConfig {

	/**
	 * No Error
	 */
	public void testTheorems_001() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTheorem(root, THEOREM_LABEL, "1=1");
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		containsTheorems(root.getSCTheoryRoot(), root.getFormulaFactory(), 
				root.getFormulaFactory().makeTypeEnvironment(), makeSList(THEOREM_LABEL), makeSList("1=1"));
	}

	/**
	 * Missing label
	 */
	public void testTheorems_002() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheorem thm = root.createChild(ITheorem.ELEMENT_TYPE, null, null);
		thm.setPredicateString("1=1", null);
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		containsMarkers(root, true);
		hasMarker(root.getTheorems()[0], EventBAttributes.LABEL_ATTRIBUTE);
	}

	/**
	 * Missing predicate
	 */
	public void testTheorems_003() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheorem thm = root.createChild(ITheorem.ELEMENT_TYPE, null, null);
		thm.setLabel(THEOREM_LABEL, null);
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		containsMarkers(root, true);
		hasMarker(root.getTheorems()[0], EventBAttributes.PREDICATE_ATTRIBUTE);
	}

	/**
	 * Unparsable
	 */
	public void testTheorems_004() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTheorem(root, THEOREM_LABEL, "asdas ad");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		containsMarkers(root, true);
		hasMarker(root.getTheorems()[0], EventBAttributes.PREDICATE_ATTRIBUTE);
	}

}
