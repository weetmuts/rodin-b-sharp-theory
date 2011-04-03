package org.eventb.theory.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.eventb.internal.ui.eventbeditor.htmlpage.HTMLPage;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * The theory editor. This contains an edit page.
 * <p>
 * TODO Add HTML page.
 * </p>
 * 
 * @author maamria
 * 
 */

@SuppressWarnings("restriction")
public class TheoryEditor extends EventBEditor<ITheoryRoot> {

	public static final String EDITOR_ID = TheoryUIPlugIn.PLUGIN_ID
			+ ".theoryEditor";

	public TheoryEditor() {
		super();

	}

	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}

	public void setSelection(ISelection selection) {
		// Pass the selection to Theory Edit Page
		IFormPage page = getActivePageInstance();
		if (page instanceof EditPage) {
			setActivePage(EditPage.PAGE_ID);
			((EditPage) page).setSelection(selection);
		}
	}

	@Override
	protected void addPages() {
		EventBEditorPage htmlPage = new HTMLPage();
		try {
			htmlPage.initialize(this);
			addPage(htmlPage);
			// only create the edit page if deployed counterpart does not exist
			if(!DatabaseUtilities.hasDeployedVersion(getRodinInput().getSCTheoryRoot())){
				EventBEditorPage editPage = new EditPage();
				editPage.initialize(this);
				addPage(editPage);
			}
		} catch (PartInitException e) {
			TheoryUIUtils.log(e,
					"Failed to initialise page for "+ getRodinInput().getRodinFile().getElementName()+".");
		}
	}

	@Override
	protected void createPages() {
		super.createPages();
	}

}
