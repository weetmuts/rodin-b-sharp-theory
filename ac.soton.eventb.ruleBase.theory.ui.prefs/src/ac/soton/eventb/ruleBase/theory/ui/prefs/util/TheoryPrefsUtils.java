/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.prefs.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.ui.prefs.facade.PrefsRepresentative;
import ac.soton.eventb.ruleBase.theory.ui.prefs.plugin.TheoryPrefsPlugIn;

/**
 * @author maamria
 * 
 */
public class TheoryPrefsUtils {
	// the dtd file name
	public static final String DTD_NAME = "dTheory.dtd";
	// its location
	public static final String DTD_LOCATION = "dtd"+System.getProperty("file.separator")+DTD_NAME;
	/**
	 * <p>Makes sure that the deployment directory exists and if not creates it. It also copies the DTD file to it if necessary.</p>
	 * @param dir the deployment directory
	 * @return whether the operation was successful
	 */
	public static boolean checkAndCreateTheoryDirectory(String dir) {
		File directory = new File(dir);
		if (!directory.exists()) {
			directory.mkdir();
		}
		else if (directory.exists() && directory.isFile()) {
			return false;
		}
		if(!toArrayList(directory.list()).contains(DTD_NAME)){
			// copy the dtd file to it
			URL url = TheoryPrefsPlugIn.getDefault().getBundle().getEntry(DTD_LOCATION);
			String sourcePath = null;
			try {
				sourcePath = FileLocator.resolve(url).getPath();
			} catch (IOException e1) {
				log(e1, e1.getMessage());
			}
			if(sourcePath == null){
				return false;
			}
			IFileStore store = EFS.getLocalFileSystem().getStore(new Path(sourcePath));
			IFileStore destStore = EFS.getLocalFileSystem().
				getStore(new Path(makeFullPath(PrefsRepresentative.
						getTheoriesDirectory(), DTD_NAME)));
			try {
				store.copy(destStore, EFS.OVERWRITE , null);
			} catch (CoreException e) {
				log(e, e.getMessage());
			}
		}
		return true;
	}

	/**
	 * <p>Returns whether the given object <code>o</code> is contained in the array <code>objs</code>.</p>
	 * @param objs the array	
	 * @param o the object
	 * @return whether <code>objs</code> contains <code>o</code> 
	 */
	public static boolean contains(Object[] objs, Object o) {
		for (Object obj : objs) {
			if (obj.equals(o))
				return true;
		}
		return false;
	}
	/**
	 * <p>Returns a list of available categories from <code>strList</code> delimited by <code>delim</code>.</p>
	 * @param strList
	 * @param delim the delimiter
	 * @return array of categories names
	 */
	public static String[] getAvailableCategories(String strList, String delim){
		StringTokenizer tokeniser = new StringTokenizer(strList, 
				delim);
		int num = tokeniser.countTokens();
		String[] cats = new String[num];
		for(int i = 0 ; i < num; i++){
			cats[i] = tokeniser.nextToken();
		}
		return cats;
	}
	
	/**
	 * <p>Logs the given exception along the given message.</p>
	 * @param exc
	 * @param message
	 */
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
		IStatus status = new Status(IStatus.ERROR, TheoryPrefsPlugIn.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		TheoryPrefsPlugIn.getDefault().getLog().log(status);
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
		IStatus status = new Status(IStatus.ERROR, TheoryPrefsPlugIn.PLUGIN_ID,
				IStatus.OK, message, null);
		throw new CoreException(status);
	}
	
	public static String toSingleString(String[] strList, String delim){
		String str = "";
		int l = strList.length;
		for(int i = 0; i<l; i++){
			if(i == 0){
				str = strList[0];
			}
			else {
				str +=delim+strList[i];
			}
		}
		return str;
	}
	
	/**
	 * <p>Utility to make a full path from the directory path <code>dirPath</code> and the child file name <code>filePath</code>.</p>
	 * @param dirPath of parent directory
	 * @param filePath of child file
	 * @return the full path
	 */
	private static String makeFullPath(String dirPath, String filePath){
		return dirPath+System.getProperty("file.separator")+filePath;
	}
	
	private static ArrayList<String> toArrayList(String strs[]){
		return new ArrayList<String>(Arrays.asList(strs));
	}
}
