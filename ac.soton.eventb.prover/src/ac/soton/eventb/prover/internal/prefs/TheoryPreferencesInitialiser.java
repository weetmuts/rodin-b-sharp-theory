package ac.soton.eventb.prover.internal.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ac.soton.eventb.prover.plugin.ProverPlugIn;

public class TheoryPreferencesInitialiser extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = ProverPlugIn.getDefault().getPreferenceStore();
		store.setDefault(ProverPlugIn.THEORY_DIR_KEY, ProverPlugIn.THEORY_DEFAULT_DIR);
		store.setDefault(ProverPlugIn.THEORY_CAT_KEY, ProverPlugIn.THEORY_DEFAULT_CAT);
	}

}
