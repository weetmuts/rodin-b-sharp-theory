package org.eventb.theory.ui.editor.html;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IOperatorArgument;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


public class OperatorArgumentPrettyPrinter extends DefaultPrettyPrinter {

	/**
	 * 
	 */
	private static final String STYLE = "constantIdentifier";
	private static final String OPARG_IDENT_SEPARATOR_BEGIN = null;
	private static final String OPARG_IDENT_SEPARATOR_END = null;
	private static final String ONE_SPACES = " ";
	private static final String MEMB_SYMB = "\u2208";
	
	public void prettyPrint(IInternalElement elt, IInternalElement parent, IPrettyPrintStream ps) {
		if(elt instanceof IOperatorArgument){
			IOperatorArgument opArg = (IOperatorArgument) elt;
			try {
				ps.appendString(
						wrapString(opArg.getIdentifierString()+ 
						ONE_SPACES +MEMB_SYMB+ONE_SPACES+ 
						opArg.getType()), 
						getHTMLBeginForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE),  
						OPARG_IDENT_SEPARATOR_BEGIN, 
						OPARG_IDENT_SEPARATOR_END);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
	}

}
