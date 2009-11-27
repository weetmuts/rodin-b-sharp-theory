package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.pog.states.ITheoryHypothesesManager;

/**
 * @author maamria
 *
 */
public class TheoryCommitHypothesesModule extends CommitHypothesesModule {


	public static final IModuleType<TheoryCommitHypothesesModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryCommitHypothesesModule");
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IHypothesisManager getHypothesisManager(IPOGStateRepository repository) 
	throws CoreException {
		return (IHypothesisManager) repository.getState(ITheoryHypothesesManager.STATE_TYPE);
	}

}
