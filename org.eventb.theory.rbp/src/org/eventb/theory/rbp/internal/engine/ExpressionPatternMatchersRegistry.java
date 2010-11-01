/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.theory.rbp.internal.engine;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ast.Expression;
import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IExpressionMatcher;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.utils.ProverUtilities;


class ExpressionPatternMatchersRegistry {

	private final static String MATCHER_ID = RbPPlugin.PLUGIN_ID
			+ ".expressionPatternMatchers";

	private static ExpressionPatternMatchersRegistry SINGLETON_INSTANCE;

	public static boolean DEBUG;

	private Map<Class<? extends Expression>, ExpressionMatcher<? extends Expression>> matchers;

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private ExpressionPatternMatchersRegistry() {
		// Singleton implementation
		if (matchers == null) {
			loadMatchers();
		}
	}

	static ExpressionPatternMatchersRegistry getMatchersRegistry() {
		if(SINGLETON_INSTANCE == null){
			SINGLETON_INSTANCE = new ExpressionPatternMatchersRegistry();
		}
		return SINGLETON_INSTANCE;
	}

	/**
	 * Initializes the provider using extensions to the formula extension
	 * provider extension point. It shall be only one extension provider.
	 */
	private synchronized void loadMatchers() {
		if (matchers != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		matchers = new LinkedHashMap<Class<? extends Expression>, ExpressionMatcher<? extends Expression>>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(MATCHER_ID);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			try {
				final int priority = 
					ProverUtilities.parsePositiveInteger(element.getAttribute("priority"));
				final ExpressionMatcher<? extends Expression> matcher = 
					(ExpressionMatcher<?>) element.createExecutableExtension("class");
				if(matcher != null){
					matcher.setPriority(priority);
					ExpressionMatcher<? extends Expression> m = matchers.get(matcher.getType());
					if(m == null){
						matchers.put(matcher.getType(), matcher);
					}
					else {
						// replace if new has higher priority
						if(m.compareTo(matcher) < 0){
							matchers.put(matcher.getType(), matcher);
						}
					}
				}
				
			} catch (CoreException e) {
				System.out.println("while loading expression matcher");
			}
		}
	}

	synchronized IExpressionMatcher getMatcher(Class<? extends Expression> clazz){
		
		return matchers.get(clazz);
	}
	
}
