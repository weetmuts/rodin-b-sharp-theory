package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class DirectOperatorDefinitionPrettyPrinter extends DefaultPrettyPrinter {

	private static final String STYLE = "convergence";
	private static final String WDC_IDENTIFIER_SEPARATOR_BEGIN = null;
	private static final String WDC_IDENTIFIER_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if(elt instanceof IDirectOperatorDefinition){
			IDirectOperatorDefinition def = (IDirectOperatorDefinition) elt;
			try {
				ps.appendString(
						def.getFormula(), 
						getHTMLBeginForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						WDC_IDENTIFIER_SEPARATOR_BEGIN, 
						WDC_IDENTIFIER_SEPARATOR_END);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(
						e,
						"Cannot get the details for direct def "
								+ def.getElementName());
			}
		}
	}

}
