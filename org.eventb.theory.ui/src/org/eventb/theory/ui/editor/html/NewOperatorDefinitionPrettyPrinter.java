package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class NewOperatorDefinitionPrettyPrinter extends DefaultPrettyPrinter {

	/**
	 * 
	 */
	private static final String ASSOC = "assoc";
	/**
	 * 
	 */
	private static final String COMMUT = "commut";
	// use an existing style...
	//TODO use a dedicated style...flag up extensibility issue
	private static final String OP_LABEL = "eventLabel";
	private static final String OP_SYNTAX = "variableIdentifier";
	private static final String OP_FORMTYPE = "extended";
	private static final String OP_PROP = "variantExpression";
	private static final String OP_IDENT_SEPARATOR_BEGIN = null;
	private static final String OP_IDENT_SEPARATOR_END = null;
	private static final String TWO_SPACES = "  ";
	private static final String NOP_SYMBOL = "\u2259";
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof INewOperatorDefinition) {
			final INewOperatorDefinition nod = (INewOperatorDefinition) elt;
			try {
				appendOPLabel(
						ps, 
						wrapString(nod.getLabel()+TWO_SPACES));
				appendOtherDetails(nod, ps);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get the details for datatype "
								+ nod.getElementName());
			}
		}
	}
	
	/**
	 * @param ps
	 * @param wrapString
	 */
	private static void appendOPLabel(IPrettyPrintStream ps, String ident) {
		ps.appendString(ident,
					getHTMLBeginForCSSClass(OP_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
					getHTMLEndForCSSClass(OP_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
						OP_IDENT_SEPARATOR_BEGIN, //
						OP_IDENT_SEPARATOR_END);
		ps.appendString(wrapString(NOP_SYMBOL+TWO_SPACES), 
				getHTMLBeginForCSSClass(OP_FORMTYPE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(OP_FORMTYPE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), 
				OP_IDENT_SEPARATOR_BEGIN, 
				OP_IDENT_SEPARATOR_END);
		
	}
	
	private static void appendOtherDetails(INewOperatorDefinition opDef, 
			IPrettyPrintStream ps)
	throws RodinDBException{
		ps.appendString(wrapString(opDef.getSyntaxSymbol()+TWO_SPACES),
				getHTMLBeginForCSSClass(OP_SYNTAX, //
					HorizontalAlignment.LEFT, //
					VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(OP_SYNTAX, //
					HorizontalAlignment.LEFT, //
					VerticalAlignement.MIDDLE), //
					OP_IDENT_SEPARATOR_BEGIN, //
					OP_IDENT_SEPARATOR_END);
		ps.appendString(wrapString(opDef.getFormulaType().toString()+TWO_SPACES),
				getHTMLBeginForCSSClass(OP_FORMTYPE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), 
				getHTMLEndForCSSClass(OP_FORMTYPE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
						OP_IDENT_SEPARATOR_BEGIN, //
						OP_IDENT_SEPARATOR_END);
		ps.appendString(wrapString(opDef.getNotationType().toString()+TWO_SPACES), 
				getHTMLBeginForCSSClass(OP_FORMTYPE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE),  
				getHTMLEndForCSSClass(OP_FORMTYPE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), // 
				OP_IDENT_SEPARATOR_BEGIN, 
				OP_IDENT_SEPARATOR_END);
		if(opDef.isAssociative()){
			ps.appendString(wrapString(ASSOC+TWO_SPACES), 
					getHTMLBeginForCSSClass(OP_PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE),  
					getHTMLEndForCSSClass(OP_PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), // 
					OP_IDENT_SEPARATOR_BEGIN, 
					OP_IDENT_SEPARATOR_END);
		}
		if(opDef.isCommutative()){
			ps.appendString(wrapString(COMMUT+TWO_SPACES), 
					getHTMLBeginForCSSClass(OP_PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE),  
					getHTMLEndForCSSClass(OP_PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), // 
					OP_IDENT_SEPARATOR_BEGIN, 
					OP_IDENT_SEPARATOR_END);
		}
	}

}
