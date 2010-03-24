package ac.soton.eventb.prover.internal.prefs;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ac.soton.eventb.prover.plugin.ProverPlugIn;

public class TheoryPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private CustomisedListEditor categoriesEditor;
	private DirectoryFieldEditor directoryEditor;

	public TheoryPreferencePage() {
		super(GRID);
		setPreferenceStore(ProverPlugIn.getDefault().getPreferenceStore());
		setDescription("Preferences for theory deployment");
	}

	public void init(IWorkbench workbench) {
		// nothing
	}

	@Override
	public boolean performOk() {
		// check the new directory and possibly copy the DTD file to it.
		if(TheoryPrefsUtils.checkAndCreateTheoryDirectory(directoryEditor.getStringValue()))
			ProverPlugIn.THEORY_DIRECTORY =
				directoryEditor.getStringValue();
		ProverPlugIn.THEORY_CATEGORIES =
			categoriesEditor.getStringValue();
		directoryEditor.store();
		categoriesEditor.store();
		// TODO FIXME in case there are theories that have a removed category, set to default!
		return super.performOk();
	}

	@Override
	protected void createFieldEditors() {
		directoryEditor = new DirectoryFieldEditor(
				ProverPlugIn.THEORY_DIR_KEY, "&Theories Directory: ",
				getFieldEditorParent());
		directoryEditor.getTextControl(getFieldEditorParent()).setEditable(false);
		addField(directoryEditor);
		categoriesEditor = new CustomisedListEditor(
				ProverPlugIn.THEORY_CAT_KEY, "&Available Categories: ",
				getFieldEditorParent());
		addField(categoriesEditor);

	}

	protected void performDefaults() {
		directoryEditor.loadDefault();
		categoriesEditor.loadDefault();
	}
}
