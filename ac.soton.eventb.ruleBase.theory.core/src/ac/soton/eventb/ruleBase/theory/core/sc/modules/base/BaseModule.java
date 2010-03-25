package ac.soton.eventb.ruleBase.theory.core.sc.modules.base;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;


/**
 * Base SC processor module for theory.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class BaseModule extends SCProcessorModule {

	private final static int IDENT_SYMTAB_SIZE = 2047;

	
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		endProcessorModules(element, repository, monitor);
	}

	
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		final IdentifierSymbolTable identifierSymbolTable = new IdentifierSymbolTable(
				IDENT_SYMTAB_SIZE);
		repository.setState(identifierSymbolTable);
		initProcessorModules(element, repository, monitor);
	}

	
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		processModules(element, target, repository, monitor);
	}
}
