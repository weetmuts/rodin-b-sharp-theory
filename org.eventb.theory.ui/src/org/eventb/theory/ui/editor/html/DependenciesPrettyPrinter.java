package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class DependenciesPrettyPrinter extends ComponentPrettyPrinter implements
		IElementPrettyPrinter {

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream sb) {
		if (elt instanceof IImportTheory) {
			try {
				final String name;
				name = ((IImportTheory) elt).getImportedTheoryName();
				super.appendComponentName(sb, wrapString(name));
			} catch (RodinDBException e) {
				TheoryUIUtils.log(e, "Cannot get imports of theory "+ elt.getRodinFile().getElementName());
			}
		} 
	}

}
