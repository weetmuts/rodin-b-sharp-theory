/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IInfer;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * @author maamria
 * 
 */
public class InferenceInferClauseModule extends
		InferenceClausesModule<IInfer, ISCInfer> {

	public static final IModuleType<InferenceInferClauseModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".inferenceInferClauseModule");

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected IInfer[] getClauses(IInferenceRule rule) throws CoreException {
		// TODO Auto-generated method stub
		return rule.getInfers();
	}

	@Override
	protected void addIdentifiers(Predicate predicate) {
		Collection<GivenType> types = predicate.getGivenTypes();
		FreeIdentifier iTypes[] = new FreeIdentifier[types.size()];
		int i = 0;
		for (GivenType type : types) {
			iTypes[i] = factory.makeFreeIdentifier(type.getName(), null,
					typeEnvironment.getType(type.getName()));
			i++;
		}
		inferenceIdentifiers.addInferIdentifiers(iTypes);
		inferenceIdentifiers
				.addInferIdentifiers(predicate.getFreeIdentifiers());

	}

	@Override
	protected ISCInfer getSCClause(ISCInferenceRule parent, String name) {
		ISCInfer scInfer = parent.getInfer(name);
		return scInfer;
	}

	@Override
	protected boolean checkPredicate(Predicate predicate, IInfer clause)
			throws CoreException {
		if (predicate.equals(MathExtensionsUtilities.BTRUE)) {
			createProblemMarker(clause, EventBAttributes.PREDICATE_ATTRIBUTE,
					TheoryGraphProblem.InferenceInferBTRUEPredErr);
			return false;
		}
		return true;
	}

}
