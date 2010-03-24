package ac.soton.eventb.prover.internal.base;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;

import ac.soton.eventb.prover.internal.base.retriever.DTheoryFile;
import ac.soton.eventb.prover.internal.base.retriever.Utilities;
import ac.soton.eventb.prover.prefs.PrefsRepresentative;

/**
 * An implementation of of a deployment manager.
 * 
 * @author maamria
 *
 */
public class DeployedTheoriesManager implements IDeployedTheoriesManager{
	
	private static DeployedTheoriesManager instance;
	private static final List<IDTheoryFile> NO_THEORIES = new ArrayList<IDTheoryFile>();

	private FormulaFactory factory;
	
	private DeployedTheoriesManager(){
		factory = FormulaFactory.getDefault();
	}
	
	
	public List<IDTheoryFile> getTheories() {
		File dir = new File(getDeploymentPath());
		if(!dir.exists() || !dir.isDirectory()){
			return NO_THEORIES;
		}
		String[] thyFiles = dir.list(new FilenameFilter(){
			
			public boolean accept(File dir, String name) {
				return name.endsWith(Utilities.DEPLOYED_THEORY_FILE_EXT);
			}
		});
		List<IDTheoryFile> theoryFiles = new ArrayList<IDTheoryFile>();
		for(String name: thyFiles){
			IDTheoryFile file = getTheory(name);
			if(file != null && !file.isEmpty()){
				theoryFiles.add(file);
			}
		}
		return Collections.unmodifiableList(theoryFiles);
	}
	
	
	public IDTheoryFile getTheory(String name) {
		DTheoryFile file = new DTheoryFile(name, factory);
		if(file.loadTheory(getDeploymentPath())){
			return file;
		}
		return null;
	}

	/**
	 * <p>Returns the singeleton instance.</p>
	 * @return instance
	 */
	public static IDeployedTheoriesManager getDefault(){
		if(instance == null) {
			instance = new DeployedTheoriesManager();
		}
		return instance;
	}

	/**
	 * <p>Returns the deployment path with which the manager works.</p>
	 * @return deployment path
	 */
	public String getDeploymentPath(){
		return PrefsRepresentative.getTheoriesDirectory();
	}

}
