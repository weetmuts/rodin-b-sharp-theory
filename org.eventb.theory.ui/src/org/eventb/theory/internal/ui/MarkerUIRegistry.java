package org.eventb.theory.internal.ui;

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
	
	public static IMarkerUIRegistry getDefault() {
		if (instance == null) {
			instance = new MarkerUIRegistry();
		}
		return instance;
	}
	
	@Override
	public IMarker[] getMarkers(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMarkers(element);
	}

	@Override
	public int getMaxMarkerSeverity(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMaxMarkerSeverity(element);
	}

	@Override
	public int getMaxMarkerSeverity(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		return MarkerRegistry.getDefault().getMaxMarkerSeverity(element,
				attributeType);
	}

	@Override
	public IMarker[] getAttributeMarkers(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		return MarkerRegistry.getDefault().getAttributeMarkers(element,
				attributeType);
	}

	@Override
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

}
