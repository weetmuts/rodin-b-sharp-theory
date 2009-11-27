package ac.soton.eventb.ruleBase.theory.ui.explorer;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class AntiSorter extends ViewerSorter {

	public AntiSorter() {
	}

	public AntiSorter(Collator collator) {
		super(collator);
	}

	/**
	 * No need for sorting.
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return -1;
	}

}
