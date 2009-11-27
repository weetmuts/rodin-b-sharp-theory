package ac.soton.eventb.ruleBase.theory.ui.attr;

import static ac.soton.eventb.ruleBase.theory.core.TheoryAttributes.CATEGORY_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ICategoryElement;
import ac.soton.eventb.ruleBase.theory.ui.prefs.facade.PrefsRepresentative;

public class CategoryAttributeManipulation implements IAttributeManipulation {

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		return PrefsRepresentative.getCategories();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		ICategoryElement cat = asCategory(element);
		return cat.getCategory();
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asCategory(element).hasCategory();
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asCategory(element).removeAttribute(CATEGORY_ATTRIBUTE, monitor);

	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asCategory(element).setCategory(PrefsRepresentative.getMainCategory(), monitor);

	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		asCategory(element).setCategory(value, monitor);

	}

	ICategoryElement asCategory(IRodinElement e){
		assert e instanceof ICategoryElement;
		return (ICategoryElement) e;
	}
}
