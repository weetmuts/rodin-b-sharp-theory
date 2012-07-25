package org.eventb.theory.core.tests.sc;

import org.eventb.core.IPORoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;

/**
 * 
 * @author maamria
 *
 */
public class DeltaCheckingTests extends BasicTheorySCTestWithThyConfig {

	/**
	 * Ensures that the statically-checked file of a theory is modified only
	 * when needed.
	 */
	public void testDeltaTheory() throws Exception {
		final ITheoryRoot root = createTheory("thy");
		final ISCTheoryRoot sc = root.getSCTheoryRoot();
		final IPORoot po = root.getPORoot();
		
		addTypeParameters(root, makeSList("T1"));
		saveRodinFileOf(root);
		runBuilder();
		root.getTypeParameters()[0].setComment("foo", null);
		saveRodinFileOf(root);
		runBuilderNotChanged(sc, po);
	}
	
	/**
	 * Ensures that the statically-checked file of a theory is modified only
	 * when needed, when another theory (for instance an ancestor) has changed.
	 */
	public void testDeltaTheoryIndirect() throws Exception {
		final ITheoryRoot rootParent = createTheory("abs");
		final ISCTheoryRoot scParent = rootParent.getSCTheoryRoot();
		final IPORoot poParent = rootParent.getPORoot();
		addTypeParameters(rootParent, makeSList("T1"));
		saveRodinFileOf(rootParent);

		final ITheoryRoot childTheory = createTheory("thy");
		final ISCTheoryRoot scChildTheory = childTheory.getSCTheoryRoot();
		final IPORoot poChildTheory = childTheory.getPORoot();
		addImportTheory(childTheory, "abs");
		addTypeParameters(childTheory, makeSList("T11"));
		saveRodinFileOf(childTheory);

		runBuilder();

		rootParent.getTypeParameters()[0].setComment("foo", null);
		saveRodinFileOf(rootParent);
		runBuilderNotChanged(scParent, poParent, scChildTheory, poChildTheory);
	}

	
}
