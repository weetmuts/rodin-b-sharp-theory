package ac.soton.eventb.ruleBase.theory.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.ui.eventbeditor.EventBEditorPage;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.ui.plugin.TheoryUIPlugIn;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;

/**
 * The theory editor. This contains an edit page.
 * <p>
 * TODO Add HTML page.
 * </p>
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryEditor extends EventBEditor<ITheoryRoot> {

	public static final String EDITOR_ID = TheoryUIPlugIn.PLUGIN_ID
			+ ".editors.theory";

	public TheoryEditor() {
		super();
	}

	// @Override
	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}

	public void setSelection(ISelection selection) {
		// Pass the selection to Theory Edit Page
		setActivePage(TheoryEditPage.PAGE_ID);
		IFormPage page = getActivePageInstance();
		if (page instanceof TheoryEditPage) {
			((TheoryEditPage) page).setSelection(selection);
		}
	}

	// @Override
	@Override
	protected void addPages() {
		EventBEditorPage editPage = new TheoryEditPage();
		EventBEditorPage htmlPage = new TheoryHTMLPage();
		htmlPage.initialize(this);
		editPage.initialize(this);
		try {
			addPage(htmlPage);
			addPage(editPage);
		} catch (PartInitException e) {
			TheoryUIUtils.log(e, 
					"Failed to initialise page " + editPage.getId() +
					" or page "+ htmlPage.getId());
		}
	}

}
