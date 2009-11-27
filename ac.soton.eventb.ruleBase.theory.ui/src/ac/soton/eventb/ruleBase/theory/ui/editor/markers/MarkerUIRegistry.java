package ac.soton.eventb.ruleBase.theory.ui.editor.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;

public class MarkerUIRegistry implements IMarkerUIRegistry {

	private static IMarkerUIRegistry instance;

	private MarkerUIRegistry() {
		// Singleton: Private constructor.
	}

	public IMarker[] getAttributeMarkers(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		return MarkerRegistry.getDefault().getAttributeMarkers(element,
				attributeType);
	}

	public IMarker[] getMarkers(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMarkers(element);
	}

	public int getMaxMarkerSeverity(IParent parent, IElementType<?> childType)
			throws CoreException {
		IRodinElement[] elements = parent.getChildrenOfType(childType);
		int severity = -1;
		for (IRodinElement element : elements) {
			int newSeverity = MarkerRegistry.getDefault().getMaxMarkerSeverity(
					element);
			if (severity < newSeverity)
				severity = newSeverity;
		}
		return severity;
	}

	public int getMaxMarkerSeverity(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMaxMarkerSeverity(element);
	}

	public int getMaxMarkerSeverity(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		return MarkerRegistry.getDefault().getMaxMarkerSeverity(element,
				attributeType);
	}

	public static IMarkerUIRegistry getDefault() {
		if (instance == null) {
			instance = new MarkerUIRegistry();
		}
		return instance;
	}

}
