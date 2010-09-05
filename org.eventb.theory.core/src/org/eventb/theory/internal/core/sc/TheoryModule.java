package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.sc.states.DatatypeTable;
import org.eventb.theory.internal.core.sc.states.OperatorLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

@SuppressWarnings("restriction")
public class TheoryModule extends SCProcessorModule {

	public static final IModuleType<TheoryModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryModule");

	private final static int LABEL_SYMTAB_SIZE = 2047;
	private final static int IDENT_SYMTAB_SIZE = 2047;

	private TheoryAccuracyInfo accuracyInfo;
	private ISCTheoryRoot theoryRoot;
	private IRodinElement source;

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		theoryRoot.setAccuracy(accuracyInfo.isAccurate(), monitor);
		theoryRoot.setSource(source, monitor);
		endProcessorModules(element, repository, monitor);
		removeStates(repository);
	}

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		accuracyInfo = new TheoryAccuracyInfo();
		final TheoryLabelSymbolTable labelSymbolTable = 
			new TheoryLabelSymbolTable(
				LABEL_SYMTAB_SIZE);
		final OperatorLabelSymbolTable opLabelSymbolTable =
			new OperatorLabelSymbolTable(
				LABEL_SYMTAB_SIZE);
		final IdentifierSymbolTable identSymbolTable = 
			new IdentifierSymbolTable(IDENT_SYMTAB_SIZE, 
					repository.getFormulaFactory());
		final DatatypeTable datatypeTable = new DatatypeTable(repository.getFormulaFactory());
	
		repository.setState(identSymbolTable);
		repository.setState(labelSymbolTable);
		repository.setState(opLabelSymbolTable);
		repository.setState(datatypeTable);
		
		repository.setState(accuracyInfo);

		initProcessorModules(element, repository, monitor);
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryRoot = (ISCTheoryRoot) target;
		source = element;
		processModules(element, target, repository, monitor);
	}

	/**
	 * @param repository
	 */
	private void removeStates(ISCStateRepository repository) throws CoreException{
		repository.removeState(TheoryLabelSymbolTable.STATE_TYPE);
		repository.removeState(IIdentifierSymbolTable.STATE_TYPE);
		repository.removeState(DatatypeTable.STATE_TYPE);
		repository.removeState(OperatorLabelSymbolTable.STATE_TYPE);
		repository.removeState(TheoryAccuracyInfo.STATE_TYPE);
	}

}
