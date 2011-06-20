/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.pm.basis.PredicateMatcher;

/**
 * 
 * @author maamria
 *
 */
public abstract class ExtendedPredicateMatcher<P extends IPredicateExtension> extends PredicateMatcher<ExtendedPredicate> {

	private Class<P> extensionClass;
	
	public ExtendedPredicateMatcher(Class<P> extensionClass) {
		super(ExtendedPredicate.class);
		this.extensionClass = extensionClass;
	}

	@Override
	protected ExtendedPredicate getPredicate(Predicate p) {
		return (ExtendedPredicate) p;
	}
	
	public Class<P> getExtensionClass(){
		return extensionClass;
	}
}
