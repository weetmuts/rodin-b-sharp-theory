package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.maths.extensions.graph.ITheoryGraph;
import org.eventb.theory.internal.core.maths.extensions.graph.ProjectGraph;
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

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		if(root instanceof ITheoryRoot){
			return new LinkedHashSet<IFormulaExtension>();
		}
		IRodinProject project = root.getRodinProject();
		String rootName = root.getComponentName();
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		try {
			FormulaExtensionsLoader loader = null;
			// in case of theory related request
			if(TheoryCoreFacade.originatedFromTheory(root.getRodinFile())){
				ISCTheoryRoot scRoot = TheoryCoreFacade.getSCTheory(rootName, project);
				ITheoryGraph<ISCTheoryRoot> graph = new 
					ProjectGraph<ISCTheoryRoot>(new ISCTheoryRoot[]{scRoot}).getGraph();
				List<String> toExeclude = new ArrayList<String>(graph.getClosureNames(scRoot));
				toExeclude.add(rootName);
				loader = new FormulaExtensionsLoader(project, toExeclude);
				extensions.addAll(loader.getFormulaExtensions());
				
				Set<ISCTheoryRoot> scRootsSet = graph.getElements();
				ISCTheoryRoot[] scRoots = scRootsSet.toArray(new ISCTheoryRoot[scRootsSet.size()]);
				extensions.addAll(loader.getAdditionalExtensions(scRoots));
			}
			// in case of a model request
			else {
				loader = new FormulaExtensionsLoader(project, new ArrayList<String>());
				extensions.addAll(loader.getFormulaExtensions());
			}
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return extensions;
	}

	@Override
	public void setFormulaFactory(IEventBRoot root, FormulaFactory ff) {
		// consider proof files
		if(root instanceof IPRRoot){
			
		}
	}

	@Override
	public Set<IRodinFile> getCommonFiles(IEventBRoot root) {
		try {
			IDeployedTheoryRoot[] deployed = TheoryCoreFacade.getDeployedTheories(root.getRodinProject());
			Set<IRodinFile> files = new LinkedHashSet<IRodinFile>();
			for(IDeployedTheoryRoot dep : deployed){
				files.add(dep.getRodinFile());
			}
			return files;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return new LinkedHashSet<IRodinFile>();
	}

	@Override
	public Set<IRodinFile> getProjectFiles(IEventBRoot root) {
		// nothing to supply
		return new LinkedHashSet<IRodinFile>();
	}

}
