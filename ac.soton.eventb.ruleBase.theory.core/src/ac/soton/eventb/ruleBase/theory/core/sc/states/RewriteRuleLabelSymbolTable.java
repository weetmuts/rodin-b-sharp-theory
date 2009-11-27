package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ILabeledElement;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.IInternalElementType;

import ac.soton.eventb.ruleBase.theory.core.sc.symbolTable.SymbolTable;

public class RewriteRuleLabelSymbolTable
		extends
		SymbolTable<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo>
		implements IRewriteRuleLabelSymbolTable {

	public RewriteRuleLabelSymbolTable(int size) {
		super(size);
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
