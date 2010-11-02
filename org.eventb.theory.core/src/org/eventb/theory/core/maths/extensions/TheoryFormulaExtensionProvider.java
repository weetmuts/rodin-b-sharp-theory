package org.eventb.theory.core.maths.extensions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * A project scope formula provider.
 * 
 * @author maamria
 * 
 */
public class TheoryFormulaExtensionProvider implements
		IFormulaExtensionProvider {

	protected final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID
			+ ".theoryExtensionsProvider";

	protected static final Set<IFormulaExtension> EMPTY_SET = Collections
			.unmodifiableSet(new LinkedHashSet<IFormulaExtension>());

	private static final IFormulaExtensionsWorkspaceManager wsManager = WorkspaceManager.getDefault();;
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		IRodinProject project = root.getRodinProject();
		try {
			if (DB_TCFacade.originatedFromTheory(root.getRodinFile())) {
				if (root instanceof ITheoryRoot) {
					Set<IFormulaExtension> ext = wsManager
							.getNeededExtensions((ITheoryRoot) root);
					return ext;
				} else {
					ISCTheoryRoot scRoot = DB_TCFacade.getSCTheory(
							root.getComponentName(), project);
					if(scRoot.getDeployedTheoryRoot().exists()){
						return EMPTY_SET;
					}
					Set<IFormulaExtension> ext = wsManager
							.getNeededExtensions(scRoot);
					ext.addAll(wsManager.getDirtyExtensions(root,
							FormulaFactory.getInstance(ext)));
					return ext;
				}
			}
			return wsManager.getDeployedExtensions();

		} catch (CoreException e) {
			CoreUtilities.log(e, "Failed to load deployed theories.");
		}
		return EMPTY_SET;
	}

	@Override
	public void setFormulaFactory(IEventBRoot root, FormulaFactory ff) {
		// nothing to do
	}

	@Override
	public Set<IRodinFile> getCommonFiles(IEventBRoot root) {
		// nothing to supply
		return new LinkedHashSet<IRodinFile>();
	}

	@Override
	public Set<IRodinFile> getProjectFiles(IEventBRoot root) {
		// nothing to supply
		return new LinkedHashSet<IRodinFile>();
	}
	
	public static FormulaFactory getCurrentFormulaFactory() {
		// TODO Auto-generated method stub
		return FormulaFactory.getInstance(getCurrentlyDeployedExtensions());
	}
	
	protected static Set<IFormulaExtension> getCurrentlyDeployedExtensions(){
		try {
			return wsManager.getDeployedExtensions();
		} catch (CoreException e) {
			CoreUtilities.log(e, "Failed to retrieve deployed extensions.");
		}
		return EMPTY_SET;
	}
	

}
