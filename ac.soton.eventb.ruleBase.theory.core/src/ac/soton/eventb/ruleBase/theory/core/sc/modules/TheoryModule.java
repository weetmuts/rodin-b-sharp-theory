package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.modules.base.BaseModule;
import ac.soton.eventb.ruleBase.theory.core.sc.states.GivenSets;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IGivenSets;
import ac.soton.eventb.ruleBase.theory.core.sc.states.ITheoryAccuracyInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.TheoryAccuracyInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.TheoryLabelSymbolTable;

/**
 * The root theory module. It sets the overall accuracy of the theory.
 * @author maamria
 *
 */
public class TheoryModule extends BaseModule {

	public static final IModuleType<TheoryModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryModule");

	private final static int LABEL_SYMTAB_SIZE = 2047;

	private ITheoryAccuracyInfo accuracyInfo;
	private ISCTheoryRoot theoryRoot;

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		theoryRoot.setAccuracy(accuracyInfo.isAccurate(), monitor);
		super.endModule(element, repository, monitor);
	}

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		accuracyInfo = new TheoryAccuracyInfo();
		final TheoryLabelSymbolTable labelSymbolTable = new TheoryLabelSymbolTable(
				LABEL_SYMTAB_SIZE);
		final IGivenSets givenSets = new GivenSets();
		
		repository.setState(labelSymbolTable);
		repository.setState(accuracyInfo);
		repository.setState(givenSets);
		
		super.initModule(element, repository, monitor);
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryRoot = (ISCTheoryRoot) target;
		super.processModules(element, target, repository, monitor);
	}
}
