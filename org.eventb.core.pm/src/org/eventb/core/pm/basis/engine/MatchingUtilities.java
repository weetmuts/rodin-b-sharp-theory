/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.basis.engine;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.pm.IBinding;

/**
 * 
 * Matching utilities. Despite this being exposed API, do not use.
 * 
 * @author maamria
 *
 */
public class MatchingUtilities {

	/**
	 * Make sure tag is for an associative expression.
	 * <p>
	 * This method checks whether the operator is AC.
	 * 
	 * @param tag
	 * @return
	 */
	public static boolean isAssociativeCommutative(int tag) {
		if (tag == AssociativeExpression.BCOMP
				|| tag == AssociativeExpression.FCOMP) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns whether the two arrays of declarations match (simple implementation).
	 * @param formulaDecs the formula declarations
	 * @param patternDecs the pattern declarations
	 * @param existingBinding the existing binding
	 * @return whether the declarations match
	 */
	public static boolean boundIdentDecsMatch(BoundIdentDecl[] formulaDecs, 
			BoundIdentDecl[] patternDecs, IBinding existingBinding){
		if(formulaDecs.length == patternDecs.length){
			int index = 0;
			for(BoundIdentDecl pDec: patternDecs){
				BoundIdentDecl fDec = formulaDecs[index];
				if(!existingBinding.canUnifyTypes(fDec.getType(), pDec.getType())){
					return false;
				}
				index++;
			}
			return true;
		}
		else 
			return false;
	}

}
