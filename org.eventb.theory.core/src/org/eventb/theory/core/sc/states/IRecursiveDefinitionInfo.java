package org.eventb.theory.core.sc.states;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 *
 */
public interface IRecursiveDefinitionInfo extends ISCState {

	public final static IStateType<IRecursiveDefinitionInfo> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".recursiveDefinitionInfo");
	
	public void setInductiveArgument(FreeIdentifier ident, FormulaFactory factory) throws CoreException;
	
	public FreeIdentifier getInductiveArgument() throws CoreException;
	
	public void addEntry(IRecursiveDefinitionCase defCase, 
			ExtendedExpression exp, ITypeEnvironment typeEnvironment) throws CoreException;
	
	public Map<IRecursiveDefinitionCase, CaseEntry> getBaseEntries() throws CoreException;
	
	public Map<IRecursiveDefinitionCase, CaseEntry> getInductiveEntries() throws CoreException;
	
	public boolean isAccurate() throws CoreException;
	
	public void setNotAccurate() throws CoreException;
	
	public boolean isConstructor(IExpressionExtension extension) throws CoreException;
	
	public boolean isCoveredConstuctor(IExpressionExtension extension) throws CoreException;
	
	public boolean coveredAllConstructors() throws CoreException;
	
	public static class CaseEntry {
		ExtendedExpression caseExpression;
		ITypeEnvironment localTypeEnvironment;
		private boolean erroneous;

		// expression must be type checked
		public CaseEntry(ExtendedExpression caseExpression,
				ITypeEnvironment localTypeEnvironment) {
			this.caseExpression = caseExpression;
			this.localTypeEnvironment = localTypeEnvironment;
		}

		public ExtendedExpression getCaseExpression() {
			return caseExpression;
		}

		public ITypeEnvironment getLocalTypeEnvironment() {
			return localTypeEnvironment;
		}
		
		public boolean isErroneous() {
			return erroneous;
		}

		public void setErroneous() {
			this.erroneous = true;
		}

		public boolean isBaseCase(){
			boolean isBase = true;
			for (FreeIdentifier ident : caseExpression.getFreeIdentifiers()) {
				if (ident.getType().equals(caseExpression.getType())) {
					isBase = false;
					break;
				}
			}
			return isBase;
		}
	}
}
