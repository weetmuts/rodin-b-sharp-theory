package ac.soton.eventb.ruleBase.theory.deploy.plugin;

import org.eclipse.core.runtime.Plugin;

public class TheoryDeployPlugIn extends Plugin {

	public static final String PLUGIN_ID= "ac.soton.eventb.ruleBase.theory.deploy";
	
	private static TheoryDeployPlugIn plugin;
	
	public TheoryDeployPlugIn() {
		plugin = this;
	}

	public static TheoryDeployPlugIn getPlugin(){
		return plugin;
	}
	
}
