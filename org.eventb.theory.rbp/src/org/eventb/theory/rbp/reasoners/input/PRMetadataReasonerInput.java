/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.rbp.reasoners.input;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since
 */
public abstract class PRMetadataReasonerInput implements IReasonerInput {

	private static final String RULE_KEY = "Rule";
	private static final String THEORY_KEY = "Thy";
	private static final String PROJECT_KEY = "ThyProject";

	private IPRMetadata prMetadata;
	
	public PRMetadataReasonerInput(IPRMetadata prMetadata) {
		this.prMetadata = prMetadata;
	}
	
	public PRMetadataReasonerInput(IReasonerInputReader reader)
			throws SerializeException {
		String projectName = reader.getString(PROJECT_KEY);
		String theoryName = reader.getString(THEORY_KEY);
		String ruleName = reader.getString(RULE_KEY);

		this.prMetadata = new PRMetadata(projectName, theoryName, ruleName);
	}
	
	public IPRMetadata getPRMetadata() {
		return prMetadata;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasonerInput#hasError()
	 */
	@Override
	public final boolean hasError() {
		return this.getError() != null;
	}

	protected void serialise(IReasonerInputWriter writer)
			throws SerializeException {
		writer.putString(PROJECT_KEY, prMetadata.getProjectName());
		writer.putString(THEORY_KEY, prMetadata.getTheoryName());
		writer.putString(RULE_KEY, prMetadata.getRuleName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return prMetadata.toString();
	}
	
}
