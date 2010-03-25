package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.rodinp.core.IInternalElementType;

@SuppressWarnings("restriction")
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
