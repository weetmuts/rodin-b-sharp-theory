/*******************************************************************************
 * Copyright (c) 2015 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.tests.rbp.reasoners;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;

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
public class AutoInferenceReasonerTests extends AbstractReasonerTests {

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests#getReasonerID()
	 */
	@Override
	public String getReasonerID() {
		return "org.eventb.theory.rbp.autoInferenceReasoner";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests#getSuccessfulReasonerApplications()
	 */
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		
		
		return new SuccessfullReasonerApplication[] {

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests #getUnsuccessfullReasonerApplications()
	 */
	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				
		};
	}

}
