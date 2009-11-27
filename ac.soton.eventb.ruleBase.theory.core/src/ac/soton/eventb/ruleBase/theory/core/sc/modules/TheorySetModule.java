package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

import ac.soton.eventb.ruleBase.theory.core.ISet;
import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.Messages;
import ac.soton.eventb.ruleBase.theory.core.sc.modules.base.IdentifierModule;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IGivenSets;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.symbolTable.TheorySymbolFactory;

/**
 * A processor module for theory sets. It performs the usual identifier checks.
 * @author maamria
 *
 */
public class TheorySetModule extends IdentifierModule {

	public static final IModuleType<TheorySetModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theorySetModule");

	private IGivenSets givenSets;
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		givenSets = (IGivenSets) repository.getState(IGivenSets.STATE_TYPE);
		super.initModule(element, repository, monitor);
	}
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile theoryFile = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) theoryFile.getRoot();
		ISet[] sets = root.getSets();
		if (sets.length != 0){
			monitor.subTask(Messages.bind(Messages.progress_TheorySets));
			fetchSymbols(sets, target, repository, monitor);
		}
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		givenSets = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		IEventBRoot theory = (IEventBRoot) element.getParent();
		return TheorySymbolFactory.getInstance().makeTheorySet(name, true, element,
				theory.getComponentName());
	}

	@Override
	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			ITypeEnvironment environment) throws CoreException {
		environment.addGivenSet(newSymbolInfo.getSymbol());
		newSymbolInfo.setType(environment.getType(newSymbolInfo.getSymbol()));
		// add it to the given sets
		givenSets.addGivenSet(newSymbolInfo.getSymbol());
	}
}
