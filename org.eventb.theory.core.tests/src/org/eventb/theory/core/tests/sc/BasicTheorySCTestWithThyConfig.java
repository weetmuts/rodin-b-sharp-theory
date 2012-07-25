package org.eventb.theory.core.tests.sc;

import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public abstract class BasicTheorySCTestWithThyConfig extends BasicTheorySCTest {

	public static final String THEORY_NAME = "thy";
	public static final String BLOCK_LABEL = "DummyBlock";
	public static final String REWRITE_LABEL = "DummyRule";
	public static final String RHS_LABEL = "DummyRHS";
	public static final String INFERENCE_LABEL = "DummyInference";
	public static final String THEOREM_LABEL = "DummyThm";
	public static final String DATATYPE_NAME = "dt";
	public static final String CONS_NAME = "c";
	public static final String DEST_NAME = "d";
	
	@Override
	protected ITheoryRoot createTheory(String bareName) throws RodinDBException {
		ITheoryRoot theory = super.createTheory(bareName);
		theory.setConfiguration(DatabaseUtilities.THEORY_CONFIGURATION, null);
		return theory;
	}
	
}
