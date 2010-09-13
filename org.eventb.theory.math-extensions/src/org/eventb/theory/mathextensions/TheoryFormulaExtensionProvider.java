package org.eventb.theory.mathextensions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.mathextensions.plugin.MathExtensionsPlugin;

public class TheoryFormulaExtensionProvider implements
		IFormulaExtensionProvider {

	protected final String PROVIDER_ID = MathExtensionsPlugin.PLUGIN_ID + ".theoryExtensionsProvider";
	
	public TheoryFormulaExtensionProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBProject project) {
		Set<IFormulaExtension> ext = new LinkedHashSet<IFormulaExtension>();
		try {
			ext.addAll(new FormulaExtensionsLoader(project).getFormulaExtensions());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return ext;
	}

}
