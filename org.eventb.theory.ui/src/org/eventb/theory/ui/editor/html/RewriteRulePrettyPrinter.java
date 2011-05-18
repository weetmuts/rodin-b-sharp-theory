package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class RewriteRulePrettyPrinter extends DefaultPrettyPrinter {

	private static final String REW_LABEL = "actionLabel"; 
	private static final String REW_LHS = "guardPredicate";
	private static final String REW_PROP= "variantExpression";
	
	private static final String REW_IDENT_SEPARATOR_BEGIN = null;
	private static final String REW_IDENT_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent, 
			IPrettyPrintStream ps) {
		if(elt instanceof IRewriteRule){
			IRewriteRule rule = (IRewriteRule) elt;
			try {
				String label = rule.getLabel();
				String lhs = rule.getFormula();
				String cc = rule.isComplete()? 
						Messages.rewriteRule_isComplete: Messages.rewriteRule_isIncomplete;
				String auto = rule.isAutomatic()?
						Messages.rule_isAutomatic: Messages.rule_isNotAutomatic;
				String inter = rule.isInteractive()?
						Messages.rule_isInteractive: Messages.rule_isUnInteractive;
				String desc = rule.getDescription();
				
				ps.appendString(wrapString("\u2022"+label), 
						getHTMLBeginForCSSClass(REW_LABEL, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(REW_LABEL, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
						REW_IDENT_SEPARATOR_BEGIN, 
						":");
				ps.appendString(lhs, 
						getHTMLBeginForCSSClass(REW_LHS, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(REW_LHS, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								REW_IDENT_SEPARATOR_BEGIN, 
								"");
				ps.appendString("("+cc+", "+auto+", "+inter+")", 
						getHTMLBeginForCSSClass(REW_PROP, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(REW_PROP, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								REW_IDENT_SEPARATOR_BEGIN, 
								"");
				ps.appendString(desc, 
						getHTMLBeginForCSSClass(REW_LHS, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(REW_PROP, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								REW_IDENT_SEPARATOR_BEGIN, 
								REW_IDENT_SEPARATOR_END);
				
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(
						e,
						"Cannot get the details for rewrite rule "
								+ rule.getElementName());
			}
		}
	}
}
