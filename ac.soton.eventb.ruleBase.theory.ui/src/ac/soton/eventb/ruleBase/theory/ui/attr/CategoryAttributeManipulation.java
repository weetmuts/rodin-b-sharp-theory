package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.CATEGORY_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ICategoryElement;
import ac.soton.eventb.ruleBase.theory.ui.prefs.facade.PrefsRepresentative;

public class CategoryAttributeManipulation implements IAttributeManipulation {

	
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		return PrefsRepresentative.getCategories();
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		ICategoryElement cat = asCategory(element);
		return cat.getCategory();
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asCategory(element).hasCategory();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asCategory(element).removeAttribute(CATEGORY_ATTRIBUTE, monitor);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asCategory(element).setCategory(PrefsRepresentative.getMainCategory(), monitor);

	}

	
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		asCategory(element).setCategory(value, monitor);

	}

	ICategoryElement asCategory(IRodinElement e){
		assert e instanceof ICategoryElement;
		return (ICategoryElement) e;
	}
}
