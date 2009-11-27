/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.editor.images;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.OverlayIcon;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.ui.editor.markers.IMarkerRegistry;
import ac.soton.eventb.ruleBase.theory.ui.editor.markers.MarkerRegistry;
import ac.soton.eventb.ruleBase.theory.ui.plugin.TheoryUIPlugIn;

/**
 * <p>A class providing some static methods for manipulating and accessing images and theory-specific images.</p>
 * This class mirrors {@link EventBImage}.
 * <p>
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryImage {

	public static Image getImage(ImageDescriptor desc, int overlay) {
		int F_COMMENT = 0x00001;

		int F_ERROR = 0x00002;

		int F_WARNING = 0x00004;

		String key = "desc:" + desc;
		key += ":" + overlay;

		ImageRegistry imageRegistry = TheoryUIPlugIn.getDefault()
				.getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) {
			if (UIUtils.DEBUG)
				System.out.println("Create a new image: " + key);
			OverlayIcon icon = new OverlayIcon(desc);
			if ((overlay & F_COMMENT) != 0)
				icon
						.addTopLeft(getImageDescriptor(ITheoryImages.IMG_COMMENT_OVERLAY_PATH));
			if ((overlay & F_ERROR) != 0)
				icon
						.addBottomLeft(getImageDescriptor(ITheoryImages.IMG_ERROR_OVERLAY_PATH));
			else if ((overlay & F_WARNING) != 0)
				icon
						.addBottomLeft(getImageDescriptor(ITheoryImages.IMG_WARNING_OVERLAY_PATH));
			image = icon.createImage();
			imageRegistry.put(key, image);
		}
		return image;

	}

	/**
	 * Get an image from the image registry with a given key
	 * <p>
	 * 
	 * @param key
	 *            a key (String)
	 * @return an image associated with the input key or null if it does not
	 *         exist
	 */
	public static Image getImage(String key) {
		ImageRegistry registry = TheoryUIPlugIn.getDefault().getImageRegistry();
		return registry.get(key);
	}

	public static ImageDescriptor getImageDescriptor(IRodinElement element) {
		final IElementDesc elementDesc = ElementDescRegistry.getInstance()
				.getElementDesc(element.getElementType());
		return elementDesc.getImageProvider().getImageDescriptor(element);
	}

	/**
	 * Returns an image descriptor for the image file within the Event-B UI
	 * Plugin at the given plug-in relative path
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image within this Event-B UI Plugin
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return getImageDescriptor(TheoryUIPlugIn.PLUGIN_ID, path);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in and
	 * the relative path within the plugin
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String pluginID,
			String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path);
	}

	public static Image getPRSequentImage(IPSStatus status) {
		String base_path = "";

		int F_AUTO = 0x00001;
		
		int F_INACCURATE = 0x00002;
		
		int F_REVIEWED_BROKEN = 0x00004;
		
		int F_DISCHARGED_BROKEN = 0x00008;

		int confidence;
		
		try {
			confidence = status.getConfidence();
		} catch (RodinDBException e) {
			String message = "Cannot get the confidence from the status of"
					+ status.getElementName();
			if (UIUtils.DEBUG) {
				System.out.println(message);
				e.printStackTrace();
			}
			UIUtils.log(e, message);
			return null;
		}

		int overlay = 0;

		boolean isAttempted = confidence > IConfidence.UNATTEMPTED;
		if (!isAttempted)
			base_path = ITheoryImages.IMG_PENDING_PATH;
		else {
			boolean isProofBroken = false;
			try {
				isProofBroken = status.isBroken();
			} catch (RodinDBException e) {
				String message = "Cannot check if the proof tree of the sequent "
						+ status.getElementName() + " is brocken or not";
				if (UIUtils.DEBUG) {
					System.out.println(message);
					e.printStackTrace();
				}
				UIUtils.log(e, message);
				return null;
			}
			if (isProofBroken) {
				if (confidence == IConfidence.PENDING) {
					// Do nothing
				}
				else if (confidence <= IConfidence.REVIEWED_MAX)
					overlay = overlay | F_REVIEWED_BROKEN;
				else if (confidence <= IConfidence.DISCHARGED_MAX)
					overlay = overlay | F_DISCHARGED_BROKEN;
				base_path = ITheoryImages.IMG_PENDING_PATH;
			} else {
				if (confidence == IConfidence.PENDING)
					base_path = ITheoryImages.IMG_PENDING_PATH;
				else if (confidence <= IConfidence.REVIEWED_MAX)
					base_path = ITheoryImages.IMG_REVIEWED_PATH;
				else if (confidence <= IConfidence.DISCHARGED_MAX)
					base_path = ITheoryImages.IMG_DISCHARGED_PATH;
			}
		}

		boolean isAutomatic = false;
		try {
			isAutomatic = ! status.getHasManualProof();
		} catch (RodinDBException e) {
			String message = "Cannot check if the proof tree of the sequent "
				+ status.getElementName()
				+ " is automatically generated or not";
			if (UIUtils.DEBUG) {
				System.out.println(message);
				e.printStackTrace();
			}
		}
		if (isAutomatic && isAttempted) {
			overlay = overlay | F_AUTO;
		}

		boolean isAccurate = false;
		try {
			isAccurate = status.getPOSequent().isAccurate();
		} catch (RodinDBException e) {
			// Do nothing
		}
		if (!isAccurate) {
			overlay = overlay | F_INACCURATE;
		}
		// Compute the key
		// key = "prsequent":pluginID:base_path:overlay
		// overlay = auto
		String key = "prsequent:" + base_path + ":" + overlay;

		// Return the image if it exists, otherwise create a new image and
		// register with the registry.
		ImageRegistry registry = TheoryUIPlugIn.getDefault().getImageRegistry();
		Image image = registry.get(key);
		if (image == null) {
			if (UIUtils.DEBUG)
				System.out.println("Create a new image: " + key);
			OverlayIcon icon = new OverlayIcon(getImageDescriptor(base_path));
			if ((overlay & F_AUTO) != 0)
				icon
						.addTopRight(getImageDescriptor(ITheoryImages.IMG_AUTO_OVERLAY_PATH));
			if ((overlay & F_INACCURATE) != 0)
				icon
						.addBottomLeft(getImageDescriptor(ITheoryImages.IMG_WARNING_OVERLAY_PATH));
			if ((overlay & F_REVIEWED_BROKEN) != 0) {
				icon
						.addBottomRight(getImageDescriptor(ITheoryImages.IMG_REVIEWED_OVERLAY_PATH));
			}
			if ((overlay & F_DISCHARGED_BROKEN) != 0) {
				icon
						.addBottomRight(getImageDescriptor(ITheoryImages.IMG_DISCHARGED_OVERLAY_PATH));
			}
			image = icon.createImage();
			registry.put(key, image);
		}

		return image;
	}

	/**
	 * Getting an image corresponding to a Rodin element.
	 * <p>
	 * 
	 * @param element
	 *            A Rodin element
	 * @return The image for displaying corresponding to the input element
	 */
	public static Image getRodinImage(IRodinElement element) {
		final ImageDescriptor desc = getImageDescriptor(element);
		if (desc == null)
			return null;

		int F_COMMENT = 0x00001;
		int F_ERROR = 0x00002;
		int F_WARNING = 0x00004;
		int F_INFO = 0x00008;
		
		int overlay = 0;
		if (element instanceof ICommentedElement) {
			ICommentedElement ce = (ICommentedElement) element;
			try {
				if (ce.hasComment() && ce.getComment().length() != 0)
					overlay = overlay | F_COMMENT;
			} catch (RodinDBException e) {
				// Do nothing
				if (UIUtils.DEBUG)
					e.printStackTrace();
			}
		}
		IMarkerRegistry registry = MarkerRegistry.getDefault();
		try {
			int severity = registry.getMaxMarkerSeverity(element);
			if (severity == IMarker.SEVERITY_ERROR) {
				overlay = overlay | F_ERROR;
			} else if (severity == IMarker.SEVERITY_WARNING) {
				overlay = overlay | F_WARNING;
			} else if (severity == IMarker.SEVERITY_INFO) {
				overlay = overlay | F_INFO;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getImage(desc, overlay);
	}


	/**
	 * Initialialise the image registry. Additional image should be added here
	 * <p>
	 * 
	 * @param registry
	 *            The image registry
	 */
	public static void initializeImageRegistry(ImageRegistry registry) {
		initializeRegistry(registry);
	}

	/**
	 * Initialialise the image registry. Additional image should be added here
	 * <p>
	 * 
	 * @param registry
	 *            The image registry
	 */
	public static void initializeRegistry(ImageRegistry registry) {
		registerImage(registry, ITheoryImages.IMG_REWRITE_RULE,
				ITheoryImages.IMG_REWRITE_RULE_PATH);
		registerImage(registry, ITheoryImages.IMG_PARAMETER,
				ITheoryImages.IMG_PARAMETER_PATH);
		registerImage(registry, ITheoryImages.IMG_RULE_RHS,
				ITheoryImages.IMG_RULE_RHS_PATH);
		registerImage(registry, ITheoryImages.IMG_SET,
				ITheoryImages.IMG_SET_PATH);
		registerImage(registry, ITheoryImages.IMG_THEORY,
				ITheoryImages.IMG_THEORY_PATH);
		registerImage(registry, ITheoryImages.IMG_TYPE_AXIOM,
				ITheoryImages.IMG_TYPE_AXIOM_PATH);
		// Other images
		registerImage(registry, ITheoryImages.IMG_PENDING,
				"icons/pending.gif");
		registerImage(registry, ITheoryImages.IMG_PENDING_BROKEN,
				"icons/pending_broken.gif");
		registerImage(registry, ITheoryImages.IMG_APPLIED,
				"icons/applied.gif");
		registerImage(registry, ITheoryImages.IMG_DISCHARGED,
				"icons/discharged.gif");
		registerImage(registry, ITheoryImages.IMG_DISCHARGED_BROKEN,
				"icons/discharged_broken.gif");
		registerImage(registry, ITheoryImages.IMG_REVIEWED,
				"icons/reviewed.gif");
		registerImage(registry, ITheoryImages.IMG_REVIEWED_BROKEN,
				"icons/reviewed_broken.gif");
		registerImage(registry, ITheoryImages.IMG_DEFAULT,
				"icons/sample.gif");
		registerImage(registry, ITheoryImages.IMG_REFINES,
				"icons/full/ctool16/refines.gif");
		registerImage(registry, ITheoryImages.IMG_NULL,
				"icons/full/ctool16/null.gif");
		registerImage(registry, ITheoryImages.IMG_ADD,
				"icons/full/ctool16/add.gif");
		registerImage(registry, ITheoryImages.IMG_REMOVE,
				"icons/full/ctool16/remove.gif");
		registerImage(registry, ITheoryImages.IMG_ADD,
				"icons/full/ctool16/add.gif");
		registerImage(registry, ITheoryImages.IMG_REMOVE,
				"icons/full/ctool16/remove.gif");
		registerImage(registry, ITheoryImages.IMG_COLLAPSED,
				"icons/full/elcl16/collapsed.gif");
		registerImage(registry, ITheoryImages.IMG_COLLAPSED_HOVER,
				"icons/full/elcl16/collapsedHover.gif");
		registerImage(registry, ITheoryImages.IMG_EXPANDED,
				"icons/full/elcl16/expanded.gif");
		registerImage(registry, ITheoryImages.IMG_EXPANDED_HOVER,
				"icons/full/elcl16/expandedHover.gif");
		registerImage(registry, ITheoryImages.IMG_EXPAND_ALL,
				"icons/full/elcl16/expandall.gif");
		registerImage(registry, ITheoryImages.IMG_COLLAPSED,
				"icons/full/elcl16/collapsed.gif");
		registerImage(registry, ITheoryImages.IMG_COLLAPSED_HOVER,
				"icons/full/elcl16/collapsedHover.gif");
		registerImage(registry, ITheoryImages.IMG_COLLAPSE_ALL,
				"icons/full/elcl16/collapseall.gif");
		registerImage(registry, ITheoryImages.IMG_EXPANDED,
				"icons/full/elcl16/expanded.gif");
		registerImage(registry, ITheoryImages.IMG_EXPANDED_HOVER,
				"icons/full/elcl16/expandedHover.gif");
		registerImage(registry, ITheoryImages.IMG_DISCHARGED_SMILEY,
				"icons/full/ctool16/wink-green.gif");
		registerImage(registry, ITheoryImages.IMG_PENDING_SMILEY,
				"icons/full/ctool16/sad.gif");
		registerImage(registry, ITheoryImages.IMG_REVIEW_SMILEY,
				"icons/full/ctool16/wink-blue.gif");
		registerImage(registry, ITheoryImages.IMG_INVERSE,
				"icons/full/ctool16/inv_prover.gif");
		registerImage(registry, ITheoryImages.IMG_SELECT_ALL,
				"icons/full/ctool16/select_all_prover.gif");
		registerImage(registry, ITheoryImages.IMG_SELECT_NONE,
				"icons/full/ctool16/select_none_prover.gif");
		registerImage(registry, ITheoryImages.IMG_UP,
				"icons/full/ctool16/up_edit.gif");
		registerImage(registry, ITheoryImages.IMG_DOWN,
				"icons/full/ctool16/down_edit.gif");	
	}
	
	/**
	 * Register an image with the image registry
	 * <p>
	 * 
	 * @param registry
	 *            the image registry
	 * @param key
	 *            the key to retrieve the image later
	 * @param path
	 *            the path to the location of the image file within this Event-B
	 *            UI plugin
	 */
	public static void registerImage(ImageRegistry registry, String key,
			String path) {
		ImageDescriptor desc = getImageDescriptor(path);
		registry.put(key, desc);
	}
	
	/**
	 * Register an image with the image registry
	 * <p>
	 * 
	 * @param registry
	 *            the image reigstry
	 * @param key
	 *            the key to retrieve the image later
	 * @param pluginID
	 *            the id of the plugin where the image can be found
	 * @param path
	 *            the path to the location of the image file within the plugin
	 */
	public static void registerImage(ImageRegistry registry, String key,
			String pluginID, String path) {
		ImageDescriptor desc = getImageDescriptor(pluginID, path);
		registry.put(key, desc);
	}

}
