package ac.soton.eventb.ruleBase.theory.ui.prefs.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ac.soton.eventb.ruleBase.theory.ui.prefs.plugin.TheoryPrefsPlugIn;

public class TheoryPreferencesInitialiser extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = TheoryPrefsPlugIn.getDefault().getPreferenceStore();
		store.setDefault(TheoryPrefsPlugIn.THEORY_DIR_KEY, TheoryPrefsPlugIn.THEORY_DEFAULT_DIR);
		store.setDefault(TheoryPrefsPlugIn.THEORY_CAT_KEY, TheoryPrefsPlugIn.THEORY_DEFAULT_CAT);
	}

}
