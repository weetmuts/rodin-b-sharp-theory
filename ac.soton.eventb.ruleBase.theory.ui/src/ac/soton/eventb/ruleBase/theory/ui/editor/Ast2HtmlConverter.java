/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 *     Systerel - added guard theorem labels
 ******************************************************************************/
package ac.soton.eventb.ruleBase.theory.ui.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.internal.ui.BundledFileExtractor;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.framework.Bundle;


/**
 * @author Markus Gaisbauer
 */
@SuppressWarnings("restriction")
public class Ast2HtmlConverter extends AstConverter {
	
	public Ast2HtmlConverter() {
		Bundle bundle = EventBUIPlugin.getDefault().getBundle();
		IPath path = new Path("html/style.css");
		IPath absolutePath = BundledFileExtractor.extractFile(bundle, path);
		HEADER = "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
			"<head>"+
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"+
			"<link type=\"text/css\" rel=\"stylesheet\" href=\"" + absolutePath.toOSString() +"\" />"+
			"</head>" +
			"<body><div class=\"main\">";
		FOOTER = "</div></body></html>";
		BEGIN_MASTER_KEYWORD = "<div class=\"masterKeyword\">";
		BEGIN_KEYWORD_1 = "<div class=\"secondaryKeyword\">";
		END_MASTER_KEYWORD = "</div>";
		END_KEYWORD_1 = "</div>";
		BEGIN_LEVEL_0 = "";
		BEGIN_LEVEL_1 = "<table class=\"level1\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		BEGIN_LEVEL_2 = "<table class=\"level2\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		BEGIN_LEVEL_3 = "<table class=\"level3\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		END_LEVEL_0 = "";
		END_LEVEL_1 = "</tr></table>";
		END_LEVEL_2 = "</tr></table>";
		END_LEVEL_3 = "</tr></table>";
		EMPTY_LINE = "<br>";
		BEGIN_MULTILINE = "<td><table class=\"multiline\" cellspacing=\"0\" cellpadding=\"0\">";
		END_MULTILINE = "</table></td>";
		BEGIN_LINE = "<tr>";
		END_LINE = "</tr>";
		BEGIN_COMPONENT_NAME = "<td class=\"componentName\" align=\"left\" valign=\"center\">";
		END_COMPONENT_NAME = "</td>";
		BEGIN_COMMENT = "<td class=\"comment\" align=\"left\" valign=\"top\">";
		END_COMMENT = "</td>";
		BEGIN_VARIABLE_IDENTIFIER = "<td class=\"variableIdentifier\" align=\"left\" valign=\"center\">";
		END_VARIABLE_IDENTIFIER = "</td>";
		BEGIN_SET_IDENTIFIER = "<td class=\"setIdentifier\" align=\"left\" valign=\"center\">";
		END_SET_IDENTIFIER = "</td>";
		SPACE = "&nbsp;&nbsp;&nbsp;";
	}
	
	@Override
	protected String makeHyperlink(String hyperlink, String text) {
		return text;
	}

	@Override
	protected String wrapString(String text) {
		return UIUtils.HTMLWrapUp(text);
	}

}
