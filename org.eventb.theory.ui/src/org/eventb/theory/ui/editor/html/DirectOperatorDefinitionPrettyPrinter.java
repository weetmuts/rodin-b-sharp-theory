package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;

import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class DirectOperatorDefinitionPrettyPrinter extends DefaultPrettyPrinter {

	/**
	 * 
	 */
	private static final String STYLE = "convergence";
	private static final String WDC_IDENTIFIER_SEPARATOR_BEGIN = null;
	private static final String WDC_IDENTIFIER_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if(elt instanceof IDirectOperatorDefinition){
			IDirectOperatorDefinition cond = (IDirectOperatorDefinition) elt;
			try {
				ps.appendString(
						cond.getFormula(), 
						getHTMLBeginForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						WDC_IDENTIFIER_SEPARATOR_BEGIN, 
						WDC_IDENTIFIER_SEPARATOR_END);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
