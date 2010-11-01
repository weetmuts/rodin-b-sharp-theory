package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation;

import org.eclipse.core.runtime.CoreException;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;

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
	private static final String OP_DETAILS = ".extended";
	private static final String OP_FORMTYPE = "extended";
	private static final String OP_PROP = "variantExpression";
	private static final String OP_IDENT_SEPARATOR_BEGIN = null;
	private static final String OP_IDENT_SEPARATOR_END = null;
	private static final String TWO_SPACES = "  ";
	private static final String NOP_SYMBOL = "\u2259";
	private static final String ONE_SPACES = " ";
	
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
			} catch (CoreException e) {
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
		ps.appendString(wrapString(":"+TWO_SPACES), 
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
	throws CoreException{
		ps.appendString(getOperatorText(opDef),
				getHTMLBeginForCSSClass(OP_DETAILS, //
					HorizontalAlignment.LEFT, //
					VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(OP_DETAILS, //
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
	
	protected static String getOperatorText(INewOperatorDefinition opDef){
		StringBuilder builder = new StringBuilder();
		try {
			if(opDef.getNotationType().equals(Notation.INFIX)){
				IOperatorArgument args[] = opDef.getOperatorArguments();
				if(args.length == 0){
					return opDef.getSyntaxSymbol() + TWO_SPACES;
				}
				else {
					int i = 0;
					for (IOperatorArgument arg : args){
						builder.append("("+arg.getIdentifierString()+ 
								ONE_SPACES +":"+ONE_SPACES+ 
								arg.getType()+ ")");
						if(i < args.length - 1){
							builder.append(ONE_SPACES + opDef.getSyntaxSymbol() + ONE_SPACES);
						}
						i++;
					}
					return wrapString(builder.toString()+TWO_SPACES);
				}
			}
			builder.append(opDef.getSyntaxSymbol());
			IOperatorArgument args[] = opDef.getOperatorArguments();
			if(args.length > 0){
				builder.append("(");
				int i = 0;
				for (IOperatorArgument arg : args){
					builder.append(arg.getIdentifierString()+ 
						ONE_SPACES +":"+ONE_SPACES+ 
						arg.getType());
					if(i < args.length - 1){
						builder.append(", ");
					}
					i++;
				}
				builder.append(") " + NOP_SYMBOL+ " ");
			}
			IDirectOperatorDefinition[] defs = opDef.getDirectOperatorDefinitions();
			if(defs.length > 0){
				builder.append(defs[0].getFormula());
			}
			return wrapString(builder.toString()+TWO_SPACES);
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "Unable to retrive operator details.");
		}
		return "ERROR";
	}

}
