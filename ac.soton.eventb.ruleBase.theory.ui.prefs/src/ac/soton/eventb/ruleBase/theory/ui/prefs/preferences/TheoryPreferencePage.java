package ac.soton.eventb.ruleBase.theory.ui.prefs.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ac.soton.eventb.ruleBase.theory.ui.prefs.plugin.TheoryPrefsPlugIn;
import ac.soton.eventb.ruleBase.theory.ui.prefs.util.TheoryPrefsUtils;

public class TheoryPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private CustomisedListEditor categoriesEditor;
	private DirectoryFieldEditor directoryEditor;

	public TheoryPreferencePage() {
		super(GRID);
		setPreferenceStore(TheoryPrefsPlugIn.getDefault().getPreferenceStore());
		setDescription("Preferences for theory deployment");
	}

	public void init(IWorkbench workbench) {
		// nothing
	}

	@Override
	public boolean performOk() {
		// check the new directory and possibly copy the DTD file to it.
		if(TheoryPrefsUtils.checkAndCreateTheoryDirectory(directoryEditor.getStringValue()))
			TheoryPrefsPlugIn.THEORY_DIRECTORY =
				directoryEditor.getStringValue();
		TheoryPrefsPlugIn.THEORY_CATEGORIES =
			categoriesEditor.getStringValue();
		directoryEditor.store();
		categoriesEditor.store();
		// TODO FIXME in case there are theories that have a removed category, set to default!
		return super.performOk();
	}

	@Override
	protected void createFieldEditors() {
		directoryEditor = new DirectoryFieldEditor(
				TheoryPrefsPlugIn.THEORY_DIR_KEY, "&Theories Directory: ",
				getFieldEditorParent());
		addField(directoryEditor);
		categoriesEditor = new CustomisedListEditor(
				TheoryPrefsPlugIn.THEORY_CAT_KEY, "&Available Categories: ",
				getFieldEditorParent());
		addField(categoriesEditor);

	}

	protected void performDefaults() {
		directoryEditor.loadDefault();
		categoriesEditor.loadDefault();
	}
}
