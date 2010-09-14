package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.plugin.TheoryPlugin;

public class TheoryFormulaExtensionProvider implements
		IFormulaExtensionProvider {

	protected final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID + ".theoryExtensionsProvider";
	
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
		// TODO if it is a modelling project get deployed theories
		// if otherwise get all
		try {
			ext.addAll(new FormulaExtensionsLoader(project).getFormulaExtensions());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return ext;
	}

}
