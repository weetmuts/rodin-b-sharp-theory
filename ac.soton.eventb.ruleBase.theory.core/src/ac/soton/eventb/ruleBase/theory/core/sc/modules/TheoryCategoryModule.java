package ac.soton.eventb.ruleBase.theory.core.sc.modules;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ICategory;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.TheoryAttributes;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
import ac.soton.eventb.ruleBase.theory.core.sc.Messages;
import ac.soton.eventb.ruleBase.theory.core.sc.TheoryGraphProblem;

/**
 * A module for processing theory categories.
 * 
 * <p>
 * The following checks are performed:
 * </p>
 * <ul>
 * <li>if a category is specified more than once, then a warning is issued and
 * duplicates are ignored.
 * <li>if a category is not pre-defined (in the theory preferences), an error is
 * issued.
 * </ul>
 * 
 * @author maamria
 * 
 */
public class TheoryCategoryModule extends SCProcessorModule {

	public static final IModuleType<TheoryCategoryModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".categoryModule");

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile theoryFile = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) theoryFile.getRoot();
		ISCTheoryRoot scRoot = (ISCTheoryRoot) target;
		ICategory[] categories = root.getCategories();
		if (categories.length != 0) {
			monitor.subTask(Messages.bind(Messages.progress_TheoryCategories));
			ArrayList<String> usedCategories = new ArrayList<String>();
			for (ICategory cat : categories) {
				// the name of category
				String catName = cat.getCategory();
				String elmntName = cat.getElementName();
				// check if the category has already been used
				if (!usedCategories.contains(catName)) {
					usedCategories.add(catName);
					createScCategory(catName, elmntName, scRoot, monitor);
				} else {
					// issue a warning and ignore cartegory
					createProblemMarker(cat,
							TheoryAttributes.CATEGORY_ATTRIBUTE,
							TheoryGraphProblem.DuplicateCategoryWarning,
							catName);
				}

			}
		}
	}

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private void createScCategory(String categoryName, String elementName,
			ISCTheoryRoot target, IProgressMonitor monitor)
			throws RodinDBException {
		ICategory scCat = target.getCategory(elementName);
		scCat.create(null, monitor);
		scCat.setAttributeValue(TheoryAttributes.CATEGORY_ATTRIBUTE,
				categoryName, monitor);
	}

}
