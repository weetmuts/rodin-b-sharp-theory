package ac.soton.eventb.prover.internal.base.retriever;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.prover.plugin.ProverPlugIn;

public class Utilities {

	public static enum StatusInfo{Sound, Unsound}

	public static final String DEPLOYED_THEORY_FILE_EXT = ".thy";
	public static final String FILE_ENCODING = "UTF-8";
	public static final String TEMP_THEORY = "48911a2b5e1a6f3c1a6060.thy";
	
	public static final String AUTOMATIC = "automatic";
	public static final String CATEGORY = "category";
	public static final String COMPLETE = "complete";
	public static final String DESC = "desc";
	public static final String IDENTIFIER = "identifier";
	public static final String INTERACTIVE = "interactive";
	public static final String LHS = "lhs";
	public static final String META_SET = "metaSet";
	public static final String META_VARIABLE = "metaVariable";
	public static final String NAME = "name";
	public static final String PREDICATE = "predicate";
	public static final String REWRITE_RULE = "rewriteRule";
	public static final String RHS = "rhs";
	public static final String RULE_R_H_S = "ruleRHS";
	public static final String SOUND = "sound";
	public static final String THEORY = "theory";
	public static final String TOOL_TIP = "toolTip";
	public static final String TYPE = "type";
	
	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, ProverPlugIn.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		ProverPlugIn.getDefault().getLog().log(status);
	}
	
	/**
	 * Throw a Core exception.
	 * <p>
	 * 
	 * @param message
	 *            The message for displaying
	 * @throws CoreException
	 *             a Core exception with the status contains the input message
	 */
	public static void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, ProverPlugIn.PLUGIN_ID,
				IStatus.OK, message, null);
		throw new CoreException(status);
	}

	public static boolean validateBooleanString(String str){
		return str.equals("true") || str.equals("false");
	}
}
