package ac.soton.eventb.ruleBase.theory.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.StaticChecker;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;

/**
 * @author maamria
 * 
 */
public class TheoryStaticChecker extends StaticChecker {

	@Override
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask(Messages.bind(Messages.build_extracting, file
					.getName()), 1);
			IRodinFile source = RodinCore.valueOf(file);
			ITheoryRoot root = (ITheoryRoot) source.getRoot();
			IRodinFile target = root.getSCTheoryRoot().getRodinFile();
			graph.addTarget(target.getResource());
			graph.addToolDependency(source.getResource(), target.getResource(),
					true);
		} finally {
			monitor.done();
		}

	}

}