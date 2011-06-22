/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.pm;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.core.pm.basis.MatchingFactory;

/**
 * @author maamria
 *
 */
public class AssociativityHandler {

	public static boolean match(Expression formChild1, Expression patternChild1,
			Expression formChild2,Expression patternChild2, boolean isAC, 
			IBinding existingBinding, MatchingFactory matchingFactory){
		IBinding b1 = matchingFactory.createBinding(false, existingBinding.getFormulaFactory());
		IBinding b2 = matchingFactory.createBinding(false, existingBinding.getFormulaFactory());
		if(isAC){
			// mix and match
			// first
			if (match(formChild1, patternChild1, formChild2, patternChild2, false, b1, matchingFactory)){
				b1.makeImmutable();
				if(existingBinding.isBindingInsertable(b1)){
					
					existingBinding.insertBinding(b1);
					return true;
				}
				return false;
			}
			// second
			else if(match(formChild1, patternChild2, formChild2, patternChild1, false, b2, matchingFactory)){
				b2.makeImmutable();
				if(existingBinding.isBindingInsertable(b2)){
					
					existingBinding.insertBinding(b2);
					return true;
				}
				return false;
			}
			else {
				return false;
			}
		}
		else {
			if(patternChild1 instanceof FreeIdentifier){
				if(!existingBinding.putExpressionMapping((FreeIdentifier) patternChild1, formChild1)) {
					return false;
				}
			}
			else {
				if(!matchingFactory.match(formChild1, patternChild1, existingBinding)){
					return false;
				}
			}
			if(patternChild2 instanceof FreeIdentifier){
				if(!existingBinding.putExpressionMapping((FreeIdentifier) patternChild2, formChild2)) {
					return false;
				}
			}
			else {
				if(!matchingFactory.match(formChild2, patternChild2, existingBinding)){
					return false;
				}
			}
		}
		return true;
	}
	
}
