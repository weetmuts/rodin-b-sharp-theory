/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import java.util.ArrayList;

import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 * 
 */
public class TheoryPathDeltaProcessor {
	
	public TheoryPathDeltaProcessor(IRodinElementDelta delta) {
		processDelta(delta);
	}

	/**
	 * Process the delta recursively depending on the kind of the delta.
	 * <p>
	 * 
	 * @param delta
	 *            The Delta from the Rodin Database
	 */
	public void processDelta(final IRodinElementDelta delta) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			if (element instanceof IRodinProject) {
//				the content provider refreshes the model
				addToRefresh(element.getRodinDB());
			} else {
				addToRefresh(element.getParent());
			}
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinProject) {
				addToRemove(element);
				// This will update everything.
				addToRefresh(element.getRodinDB());
			} else {
				if (element instanceof IRodinFile)  {
					IRodinFile file = (IRodinFile) element;
					//remove the context from the model
					if (file.getRoot() instanceof ITheoryPathRoot) {
						addToRemove(file.getRoot());
						addToRefresh(element.getRodinProject());
					}
					
				}
				//remove the context from the model
				if (element instanceof ITheoryPathRoot) {
					addToRemove(element);
				}

				
				//add the containing project to refresh.
				// if it is a root 
				if (element instanceof IEventBRoot) {
					addToRefresh(element.getRodinProject());
				//otherwise add the parent to refresh
				} else {
					addToRefresh(element.getParent());
				}
			}
			return;
		}

		if (kind == IRodinElementDelta.CHANGED) {
			int flags = delta.getFlags();

			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (IRodinElementDelta element2 : deltas) {
					processDelta(element2);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}
			if ((flags & IRodinElementDelta.F_OPENED) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}
			if ((flags & IRodinElementDelta.F_CLOSED) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}
			
		}

	}
	
	private void addToRefresh(IRodinElement o) {
		if (!toRefresh.contains(o)) {
			//add the root and not the file
			if (o instanceof IRodinFile) {
				o = ((IRodinFile) o).getRoot();
			}
			toRefresh.add(o);
		}
	}

	private void addToRemove(IRodinElement o) {
		if (!toRemove.contains(o)) {
			//add the root and not the file
			if (o instanceof IRodinFile) {
				o = ((IRodinFile) o).getRoot();
			}
			toRemove.add(o);
		}
	}
	
	// List of elements that need to be refreshed in the viewer and the model.
	private ArrayList<IRodinElement> toRefresh =new ArrayList<IRodinElement>();

	// List of elements that need to be removed from the model
	private ArrayList<IRodinElement> toRemove =new ArrayList<IRodinElement>();
	
	public ArrayList<IRodinElement> getToRefresh() {
		return toRefresh;
	}

	public ArrayList<IRodinElement> getToRemove() {
		return toRemove;
	}

}
