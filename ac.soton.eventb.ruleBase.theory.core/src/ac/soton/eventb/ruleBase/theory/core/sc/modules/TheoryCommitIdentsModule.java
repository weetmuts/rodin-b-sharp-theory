package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * A module for commiting all free identifiers (sets and variables).
 * <p>It checks that all identifiers are typed.</p>
 * @author maamria
 *
 */
public class TheoryCommitIdentsModule extends SCProcessorModule {

	public static final IModuleType<TheoryCommitIdentsModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryCommitIdentsModule");


	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}


	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IIdentifierSymbolTable identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		for (IIdentifierSymbolInfo symbolInfo : identifierSymbolTable
				.getSymbolInfosFromTop()) {
			if (symbolInfo.isPersistent()) {
				Type type = symbolInfo.getType();
				if (type == null) { // identifier could not be typed
					symbolInfo.createUntypedErrorMarker(this);
					symbolInfo.setError();
				} else if (!symbolInfo.hasError()) {
					symbolInfo.createSCElement(target, null);
				}
				symbolInfo.makeImmutable();
			}
		}
	}

}
