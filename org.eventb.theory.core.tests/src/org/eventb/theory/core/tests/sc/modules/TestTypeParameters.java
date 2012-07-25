package org.eventb.theory.core.tests.sc.modules;

import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.sc.modules.TypeParameterModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;

/**
 * @see TypeParameterModule
 * @author maamria
 *
 */
public class TestTypeParameters extends BasicTheorySCTestWithThyConfig{

	/**
	 * No error
	 */
	public void testTypeParameters_001_NoError() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("T"));
		saveRodinFileOf(root);
		runBuilder();
		containsTypeParameters(root.getSCTheoryRoot(), makeSList("T"));
		containsMarkers(root, false);
		isAccurate(root.getSCTheoryRoot());
	}
	public void testTypeParameters_002_NoError() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("T1", "T2"));
		saveRodinFileOf(root);
		runBuilder();
		containsTypeParameters(root.getSCTheoryRoot(), makeSList("T1", "T2"));
		containsMarkers(root, false);
		isAccurate(root.getSCTheoryRoot());
	}
	
	/**
	 * Conflict between two type parameters
	 */
	public void testTypeParameters_003_NameConf() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("T1", "T1"));
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		containsTypeParameters(scTheoryRoot);
		hasMarker(root.getTypeParameters()[0]);
		hasMarker(root.getTypeParameters()[1]);
		isNotAccurate(scTheoryRoot);
	}
	
	/**
	 * Faulty names
	 */
	public void testTypeParameters_004_FaultyNames() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("S>", "k-1", "#"));
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		containsTypeParameters(scTheoryRoot);
		hasMarker(root.getTypeParameters()[0]);
		hasMarker(root.getTypeParameters()[1]);
		hasMarker(root.getTypeParameters()[2]);
		isNotAccurate(scTheoryRoot);
	}
}
