package ac.soton.eventb.ruleBase.theory.core.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.ProverLib;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ac.soton.eventb.ruleBase.theory.core.ICategory;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRuleRightHandSide;
import ac.soton.eventb.ruleBase.theory.core.ISCSet;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ISCVariable;
import ac.soton.eventb.ruleBase.theory.core.deploy.basis.IDeployedRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.deploy.basis.IDeployedRuleRHS;
import ac.soton.eventb.ruleBase.theory.core.deploy.basis.IDeployedTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.deploy.basis.IMetaSet;
import ac.soton.eventb.ruleBase.theory.core.deploy.basis.IMetaVariable;
import ac.soton.eventb.ruleBase.theory.core.deploy.basis.DeployUtilities;

/**
 * <p>
 * The theory deployment manager is assigned the task of converting a statically
 * checked theory file to a deployed theory.
 * </p>
 * <p>
 * The manager uses proof-related files of the theory in question to calculate
 * the soundness of each rule.
 * </p>
 * <p>The deployment process works by creating a temporary Rodin file 
 * with root {@link IDeployedTheoryRoot} populated with all needed information
 * including soundness of rules. The temp file will reside in the SC file
 * directory (i.e., still in workspace). The process concludes by copying the temp file
 * to the deployment directory after some housekeeping and processing 
 * (to strip off unnecessary data) and add important validation information (i.e., DTD). </p>
 * @author maamria
 * 
 */
public class DeployManager {

	private static class Patterns {

		public static final HashMap<String, String> PATTERNS = new HashMap<String, String>();
		
		static {
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.deployedTheory",
					DeployUtilities.THEORY);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.category",
					DeployUtilities.CATEGORY);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.metaVariable",
					DeployUtilities.META_VARIABLE);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.type", 
					DeployUtilities.TYPE);
			PATTERNS.put("org.eventb.core.identifier", 
					DeployUtilities.IDENTIFIER);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.metaSet",
					DeployUtilities.META_SET);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.deployedRewRule",
					DeployUtilities.REWRITE_RULE);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.auto", 
					DeployUtilities.AUTOMATIC);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.complete",
					DeployUtilities.COMPLETE);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.interactive",
					DeployUtilities.INTERACTIVE);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.lhs", 
					DeployUtilities.LHS);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.toolTip",
					DeployUtilities.TOOL_TIP);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.sound", 
					DeployUtilities.SOUND);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.deployedRuleRHS",
					DeployUtilities.RULE_R_H_S);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.rhs", 
					DeployUtilities.RHS);
			PATTERNS.put("org.eventb.core.predicate", 
					DeployUtilities.PREDICATE);
			PATTERNS.put("ac.soton.eventb.ruleBase.theory.core.desc", 
					DeployUtilities.DESC);
		}
	}
	private static DeployManager instance;
	
	private static String theoriesDirectory;
	
	private IRodinFile tempFile;
	//private String theoriesDirectory;
	
	private FormulaFactory factory;

	private HashMap<String, DeployUtilities.StatusInfo> rulesSoundness;

	private DeployManager() {
		factory = FormulaFactory.getDefault();
		rulesSoundness =  new HashMap<String, DeployUtilities.StatusInfo>();
		instance = this;
	}
	
	/**
	 * <p>
	 * Deploys the statically checked theory to the given file name
	 * <code>dName</code> in the deployment directory.
	 * </p>
	 * <p>
	 * Only accurate rules (from a static checker point of view) are included in
	 * the deployment.
	 * </p>
	 * 
	 * @param root
	 *            the statically checked theory file
	 * @param dName
	 *            with extension
	 * @return the resultant rodin file
	 * @throws RodinDBException if a problem occurred
	 */
	public void deployTheory(final ISCTheoryRoot root, final String dName, final boolean force, final String deployTheoriesDir,
			IProgressMonitor monitor) throws RodinDBException {
		monitor.subTask("Creating deployed theory ...");
		theoriesDirectory = deployTheoriesDir;
		RodinCore.run(new IWorkspaceRunnable() {

			
			public void run(IProgressMonitor monitor) throws CoreException {
				tempFile = root.getRodinProject().getRodinFile(
						DeployUtilities.TEMP_THEORY);
				if (tempFile.exists()) {
					tempFile.makeConsistent(null);
					tempFile.getRoot().clear(force, null);
				}
				else
					tempFile.create(true, null);
				
				// getting the deployed root
				IDeployedTheoryRoot target = (IDeployedTheoryRoot) tempFile
						.getRoot();
				// assigning the categories
				makeCategories(root, target);
				ISCRewriteRule[] scRules = root.getSCRewriteRules();
				monitor.worked(2);
				// calculating soundness infos
				monitor.subTask("Calculating soundness ...");
				calculateSoundness(root, scRules);
				monitor.worked(1);
				// constructing meta fields
				constructMetaFields(root, target);
				monitor.subTask("Populating accurate rules ...");
				// only copy accurate rewrite rules
				for (ISCRewriteRule scRule : scRules) {
					if (!scRule.isAccurate()) {
						continue;
					}
					makeDeployedRule(scRule, target, monitor);
				}
				monitor.worked(5);
				tempFile.save(monitor, true);
				monitor.subTask("Generating and saving theory ...");
				try {
					convertToManageableThyFile(tempFile, dName, force);
				} catch (IOException e) {
					DeployUtilities.throwCoreException(e.getMessage());
				} catch (TransformerException e) {
					DeployUtilities.throwCoreException(e.getMessage());
				} catch (SAXException e) {
					DeployUtilities.throwCoreException(e.getMessage());
				} catch (ParserConfigurationException e) {
					DeployUtilities.throwCoreException(e.getMessage());
				}
				monitor.worked(3);
			}

		}, monitor);
	}

	
	private void calculateSoundness(ISCTheoryRoot root, ISCRewriteRule[] rules)
			throws RodinDBException {
		rulesSoundness.clear();
		IPSRoot psRoot = root.getPSRoot();
		IPSStatus[] sts = psRoot.getStatuses();
		for (ISCRewriteRule rule : rules) {
			boolean isSound = true;
			for (IPSStatus s : sts) {
				if (s.getElementName().startsWith(rule.getLabel())) {
					if (!ProverLib.isDischarged(s.getConfidence())
							&& !ProverLib.isReviewed(s.getConfidence())){
						isSound = false;
					}
				}
			}
			rulesSoundness.put(rule.getLabel(), 
					(isSound?DeployUtilities.StatusInfo.Sound:DeployUtilities.StatusInfo.Unsound));
		}

	}
	
	private void constructMetaFields(ISCTheoryRoot root,
			IDeployedTheoryRoot target) throws RodinDBException {
		ISCSet sets[] = root.getSCSets();
		for (ISCSet set : sets) {
			IMetaSet metaSet = target.getMetaSet(set.getIdentifierString());
			metaSet.create(null, null);
			metaSet.setIdentifierString(set.getIdentifierString(), null);
		}

		ISCVariable vars[] = root.getSCVariables();
		for (ISCVariable var : vars) {
			IMetaVariable metaVar = target.getMetaVariable(var.getIdentifierString());
			metaVar.create(null, null);
			metaVar.setIdentifierString(var.getIdentifierString(), null);
			metaVar.setTypingString(var.getType(factory).toString(), null);
		}
	}

	private boolean isSound(ISCRewriteRule rule) throws RodinDBException {
		return rulesSoundness.get(rule.getLabel()).equals(DeployUtilities.StatusInfo.Sound);
	}

	/**
	 * ":" is used as a delimeter
	 * 
	 * @param scTheory
	 * @param target
	 * @throws RodinDBException
	 */
	private void makeCategories(ISCTheoryRoot scTheory,
			IDeployedTheoryRoot target) throws RodinDBException {
		ICategory[] cats = scTheory.getCategories();
		String categories = "";
		for (ICategory cat : cats) {
			categories += cat.getCategory() + ":";
		}
		if (cats.length > 0) {
			categories = categories.substring(0, categories.length() - 1);
		}
		target.setCategory(categories, null);
	}

	private void makeDeployedRule(ISCRewriteRule rule,
			IDeployedTheoryRoot target, IProgressMonitor monitor)
			throws RodinDBException {
		IDeployedRewriteRule dRule = target.getRewriteRule(rule.getLabel());
		dRule.create(null, null);

		dRule.setSound(isSound(rule), null);
		dRule.setInteractive(rule.isInteractive(), null);
		dRule.setAutomatic(rule.isAutomatic(), null);
		dRule.setComplete(rule.isComplete(), null);
		dRule.setLHSString(rule.getLHSString(), null);
		dRule.setToolTip(rule.getToolTip(), null);
		dRule.setDescription(rule.getDescription(), null);
		ISCRewriteRuleRightHandSide[] scRHSs = rule.getSCRuleRHSs();
		for (ISCRewriteRuleRightHandSide rhs : scRHSs) {
			makeDeployedRuleRHS(rhs, dRule, monitor);
		}
	}

	private void makeDeployedRuleRHS(ISCRewriteRuleRightHandSide scRuleRhs,
			IDeployedRewriteRule target, IProgressMonitor monitor)
			throws RodinDBException {
		IDeployedRuleRHS dRHS = target.getRHS(scRuleRhs.getLabel());
		dRHS.create(null, null);
		dRHS.setPredicateString(scRuleRhs.getPredicateString(), null);
		dRHS.setRHSString(scRuleRhs.getRHSString(), null);
	}


	/**
	 * <p> Removes the temp theory file from the specified project. 
	 * This method should be called from within a <code>IWorkspaceRunnable</code>.</p>
	 * @param projectName
	 * @throws RodinDBException if problem occurred
	 */
	public void cleanUp(String projectName) throws RodinDBException{
		IRodinProject proj = RodinCore.getRodinDB().getRodinProject(projectName);
		if(proj.exists()){
			IRodinFile temp =  proj.getRodinFile(DeployUtilities.TEMP_THEORY);
			if(temp.exists()){
				temp.close();
				temp.delete(true, null);
			}
		}
	}
	
	/**
	 * <p>Returns the default instance that has the ability to deploy theories to the deployment directory.</p>
	 * @return the default instance
	 */
	public static DeployManager getInstance() {
		if (instance == null)
			instance = new DeployManager();
		return instance;
	}
	
	private void convertToManageableThyFile(IRodinFile file, String destName,
			boolean force) throws 
			IOException, TransformerException, SAXException, ParserConfigurationException {
		assert file.getRoot() instanceof IDeployedTheoryRoot;
		transformFile(file, destName, force);
	}

	private void transformFile(IRodinFile dTheoryfile, String destName,
			boolean force) throws FileNotFoundException, IOException,
			TransformerException, SAXException, ParserConfigurationException {
		CharSequence seq = fromFile(dTheoryfile);
		for (String key : Patterns.PATTERNS.keySet()) {
			Pattern pattern = Pattern.compile(key);
			Matcher matcher = pattern.matcher(seq);
			seq = matcher.replaceAll(Patterns.PATTERNS.get(key));
		}
		File file = new File(theoriesDirectory
				+ System.getProperty("file.separator") + destName);
		if (!file.exists()) {
			file.createNewFile();
		} else {
			if (force) {
				// recreate an empty file
				file.delete();
				file.createNewFile();
			} else {
				throw new IOException("File " + file.getAbsolutePath()
						+ " already exists in "
						+ theoriesDirectory + ".");
			}
		}
		
		FileOutputStream stream = new FileOutputStream(file);
		OutputStreamWriter sWriter = new OutputStreamWriter(stream, Charset.forName(DeployUtilities.FILE_ENCODING));
		sWriter.write(seq.toString());
		sWriter.flush();
		sWriter.close();
		generateDtd(file);
	}
	
	// Converts the contents of a file into a CharSequence
	private CharSequence fromFile(IRodinFile file) throws IOException {

		FileInputStream input = new FileInputStream(file
				.getCorrespondingResource().getLocation().toFile());
		FileChannel channel = input.getChannel();

		// Create a read-only CharBuffer on the file
		ByteBuffer bbuf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
				(int) channel.size());
		CharBuffer cbuf = Charset.forName(DeployUtilities.FILE_ENCODING).newDecoder().decode(bbuf);
		return cbuf;
	}

	private void generateDtd(File file) 
	throws TransformerException, SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setValidating(true); 
	    factory.setExpandEntityReferences(false);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    builder.setErrorHandler(new ErrorHandler(){

			
			public void error(SAXParseException exception) throws SAXException {
				// ignore
			}

			
			public void fatalError(SAXParseException exception)
					throws SAXException {
			   throw exception;
			}

			
			public void warning(SAXParseException exception)
					throws SAXException {
				// ignore
			}
	    	
	    });
	    Document doc = builder.parse(file);
	    Transformer xformer = TransformerFactory.newInstance().newTransformer();
	    xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "dTheory.dtd");
	    Source source = new DOMSource(doc);
	    Result result = new StreamResult(file);
	    xformer.transform(source, result);
	}
}
