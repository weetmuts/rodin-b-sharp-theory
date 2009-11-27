package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ITypingElement;
import ac.soton.eventb.ruleBase.theory.core.IVariable;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.Messages;
import ac.soton.eventb.ruleBase.theory.core.sc.modules.base.IdentifierWithTypingModule;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.symbolTable.TheorySymbolFactory;

/**
 * A processor module for theory variables.
 * <p> It performs the usual identifier checks. It also performs checks on the typing
 * attribute of variables (see {@link ITypingElement}) to ensure it holds
 * a type expression that refers only to the theory sets (plus other built-in sets of course).
 * </p>
 * @author maamria
 *
 */
public class TheoryVariableModule extends IdentifierWithTypingModule {

	public static final IModuleType<TheoryVariableModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryVariableModule");

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile theoryFile = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) theoryFile.getRoot();
		IVariable[] vars = root.getVariables();
		if (vars.length == 0)
			return;
		monitor.subTask(Messages.bind(Messages.progress_TheoryVariables));
		fetchSymbolsWithTheirTypes(vars, target, repository, monitor);
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		IEventBRoot theory = (IEventBRoot) element.getParent();
		return TheorySymbolFactory.getInstance().makeLocalVariable(name, true,
				element, theory.getComponentName());
	}
}
