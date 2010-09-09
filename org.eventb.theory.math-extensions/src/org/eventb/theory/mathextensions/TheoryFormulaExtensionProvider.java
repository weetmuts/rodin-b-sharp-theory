package org.eventb.theory.mathextensions;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IEventBProject;
import org.eventb.core.IFormulaExtensionProvider;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.theory.mathextensions.plugin.MathExtensionsPlugin;

@SuppressWarnings("restriction")
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
		Set<IFormulaExtension> ext = new HashSet<IFormulaExtension>();
		ext.add(Cond.getCond());
		return ext;
	}

}
