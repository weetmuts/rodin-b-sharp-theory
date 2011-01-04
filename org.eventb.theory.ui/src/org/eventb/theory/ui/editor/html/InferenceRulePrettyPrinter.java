package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class InferenceRulePrettyPrinter extends DefaultPrettyPrinter {

	private static final String INF_LABEL = "actionLabel"; 
	private static final String INF_PROP= "variantExpression";
	
	private static final String INF_IDENT_SEPARATOR_BEGIN = null;
	private static final String INF_IDENT_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent, 
			IPrettyPrintStream ps) {
		if(elt instanceof IInferenceRule){
			IInferenceRule rule = (IInferenceRule) elt;
			try {
				String label = rule.getLabel();
				String auto = rule.isAutomatic()?
						Messages.rule_isAutomatic: Messages.rule_isNotAutomatic;
				String inter = rule.isInteractive()?
						Messages.rule_isInteractive: Messages.rule_isUnInteractive;
				
				ps.appendString(wrapString("\u2022"+label), 
						getHTMLBeginForCSSClass(INF_LABEL, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(INF_LABEL, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
						INF_IDENT_SEPARATOR_BEGIN, 
						":");
				ps.appendString("("+auto+", "+inter+")", 
						getHTMLBeginForCSSClass(INF_PROP, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(INF_PROP, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								INF_IDENT_SEPARATOR_BEGIN, 
								INF_IDENT_SEPARATOR_END);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
