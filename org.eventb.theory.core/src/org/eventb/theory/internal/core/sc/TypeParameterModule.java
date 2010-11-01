package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.IdentifierModule;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

@SuppressWarnings("restriction")
public class TypeParameterModule extends IdentifierModule {

	public static final IModuleType<TypeParameterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".typeParameterModule"); //$NON-NLS-1$

	private TheoryAccuracyInfo theoryAccuracyInfo;
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();

		ITypeParameter[] typePars = root.getTypeParameters();

		if (typePars.length == 0)
			return;

		monitor.subTask(Messages.bind(Messages.progress_TheoryTypeParameters));

		fetchSymbols(typePars, target, repository, monitor);
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
			else {
				theoryAccuracyInfo.setNotAccurate();
			}
		}
	}

	@Override
	public void initModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
	}
	
	@Override
	public void endModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			ITypeEnvironment environment) throws CoreException {
		environment.addGivenSet(newSymbolInfo.getSymbol());

		newSymbolInfo.setType(environment.getType(newSymbolInfo.getSymbol()));
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		return TheorySymbolFactory.getInstance().makeLocalTypeParameter(name, true,
				element, element.getAncestor(ITheoryRoot.ELEMENT_TYPE).getComponentName());
	}

}
