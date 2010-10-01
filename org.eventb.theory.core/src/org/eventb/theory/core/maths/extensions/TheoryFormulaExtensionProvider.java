package org.eventb.theory.core.maths.extensions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.plugin.TheoryPlugin;
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

	protected final Set<IFormulaExtension> EMPTY_SET = Collections
			.unmodifiableSet(new LinkedHashSet<IFormulaExtension>());

	protected Map<IRodinProject, IFormulaExtensionsProjectManager> managers;

	public TheoryFormulaExtensionProvider() {
		managers = new LinkedHashMap<IRodinProject, IFormulaExtensionsProjectManager>();
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {

		if (root instanceof ITheoryRoot) {
			return EMPTY_SET;
		}
		IRodinProject project = root.getRodinProject();
		try {
			IFormulaExtensionsProjectManager manager = managers.get(project);
			if (manager == null) {
				manager = new ProjectManager(project);
				managers.put(project, manager);
			}
			if (root instanceof IPRRoot) {
				return manager.getProofFileExtensions((IPRRoot) root);
			}
			if (TheoryCoreFacade.originatedFromTheory(root.getRodinFile())) {
				ISCTheoryRoot concernedTheory = TheoryCoreFacade.getSCTheory(
						root.getComponentName(), project);
				Set<IFormulaExtension> exts = new LinkedHashSet<IFormulaExtension>();
				exts.addAll(manager.getDirtyExtensions(concernedTheory));
				return exts;
			} else {
				return manager.getDeployedExtensions();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return EMPTY_SET;
	}

	@Override
	public void setFormulaFactory(IEventBRoot root, FormulaFactory ff) {
		IRodinProject project = root.getRodinProject();
		IFormulaExtensionsProjectManager manager = managers.get(project);
		try {
			if (manager == null) {
				manager = new ProjectManager(project);
				managers.put(project, manager);

			}
			
			IPRRoot proofRoot = (IPRRoot) root;
			TheoryCoreFacade.removeInternalTheories(proofRoot);
			manager.setProofFileExtensions(proofRoot);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<IRodinFile> getCommonFiles(IEventBRoot root) {
		IRodinProject project = root.getRodinProject();
		IFormulaExtensionsProjectManager manager = managers.get(project);
		if (manager == null) {
			try {
				manager = new ProjectManager(project);
				managers.put(project, manager);
				return manager.getCommonFiles(root);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return new LinkedHashSet<IRodinFile>();
	}

	@Override
	public Set<IRodinFile> getProjectFiles(IEventBRoot root) {
		// nothing to supply
		return new LinkedHashSet<IRodinFile>();
	}

}
