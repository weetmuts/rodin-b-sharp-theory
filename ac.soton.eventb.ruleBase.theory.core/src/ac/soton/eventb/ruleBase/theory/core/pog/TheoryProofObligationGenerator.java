package ac.soton.eventb.ruleBase.theory.core.pog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.ProofObligationGenerator;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;

/**
 * 
 * @author maamria
 *
 */
public class TheoryProofObligationGenerator extends ProofObligationGenerator {

	@Override
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask(Messages.bind(Messages.build_extracting, file
					.getName()), 1);
			IRodinFile source = RodinCore.valueOf(file);
			ISCTheoryRoot root = (ISCTheoryRoot) source.getRoot();
			IRodinFile target = root.getPORoot().getRodinFile();
			graph.addTarget(target.getResource());
			graph.addToolDependency(source.getResource(), target.getResource(),
					true);
		} finally {
			monitor.done();
		}
		
	}



}
