package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IInfer;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class InferPrettyPrinter extends DefaultPrettyPrinter {

	private static final String I_PRED = "axiomPredicate"; 
	
	private static final String ONE_SPACES = " ";
	
	private static final String I_IDENT_SEPARATOR_BEGIN = null;
	private static final String I_IDENT_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent, 
			IPrettyPrintStream ps) {
		if(elt instanceof IInfer){
			IInfer i = (IInfer) elt;
			try {
				String pred = i.getPredicateString();
				ps.appendString(wrapString("\u25aa"+ONE_SPACES+pred), 
						getHTMLBeginForCSSClass(I_PRED, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(I_PRED, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								I_IDENT_SEPARATOR_BEGIN, 
								I_IDENT_SEPARATOR_END);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		ps.appendKeyword("&nbsp&nbsp&nbsp_________________");
		return true;
	}

}
