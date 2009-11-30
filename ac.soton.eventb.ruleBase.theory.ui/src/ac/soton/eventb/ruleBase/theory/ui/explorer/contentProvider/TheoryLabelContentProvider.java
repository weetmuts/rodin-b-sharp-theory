package ac.soton.eventb.ruleBase.theory.ui.explorer.contentProvider;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.OverlayIcon;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IRewriteRule;
import ac.soton.eventb.ruleBase.theory.ui.editor.images.ITheoryImages;
import ac.soton.eventb.ruleBase.theory.ui.editor.images.TheoryImage;
import ac.soton.eventb.ruleBase.theory.ui.explorer.model.ModelPOContainer;
import ac.soton.eventb.ruleBase.theory.ui.explorer.model.TheoryModelController;
import ac.soton.eventb.ruleBase.theory.ui.explorer.model.TheoryModelElementNode;
import ac.soton.eventb.ruleBase.theory.ui.util.TheoryUIUtils;
import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.statistics.IStatistics;
import fr.systerel.internal.explorer.statistics.Statistics;

@SuppressWarnings("restriction")
public class TheoryLabelContentProvider implements ILabelProvider {

	
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public Image getImage(Object element) {
		final Image image = getIcon(element);
		if (image == null)
			return null;

		final String ovrName = getOverlayIcon(element);
		if (ovrName == null)
			return image;

		final ImageDescriptor desc = ImageDescriptor.createFromImage(image);
		final OverlayIcon ovrIcon = new OverlayIcon(desc);
		ovrIcon.addBottomRight(TheoryImage.getImageDescriptor(ovrName));
		return ovrIcon.createImage();
	}

	public String getText(Object obj) {
		if (obj instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) obj).getLabel();
			} catch (RodinDBException e) {
				TheoryUIUtils.log(e, "when getting label for " +obj);
			}
		} else if (obj instanceof IIdentifierElement) {
			try {
				return ((IIdentifierElement) obj).getIdentifierString();
			} catch (RodinDBException e) {
				TheoryUIUtils.log(e, "when getting identifier for " +obj);
			}
		} else if (obj instanceof IRodinElement) {
			return ((IRodinElement) obj).getElementName();

		} else if (obj instanceof ModelPOContainer) {
			return ModelPOContainer.DISPLAY_NAME;

		} else if (obj instanceof IElementNode) {
			return ((IElementNode) obj).getLabel();

		} else if (obj instanceof IContainer) {
			return ((IContainer) obj).getName();
		}
		return obj.toString();
	}

	
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	private IElementType<?> getElementType(Object obj) {
		if (obj instanceof IRodinElement) {
			return ((IRodinElement) obj).getElementType();
		}
		if (obj instanceof IElementNode) {
			return ((IElementNode) obj).getChildrenType();
		}
		return null;
	}

	private Image getIcon(Object element) {

		if (element instanceof IPSStatus) {
			IPSStatus status = ((IPSStatus) element);
			return TheoryImage.getPRSequentImage(status);
		}
		if (element instanceof IRodinElement) {
			return TheoryImage.getRodinImage((IRodinElement) element);

		} else if (element instanceof IElementNode) {
			IElementNode node = (IElementNode) element;

			if (node.getChildrenType().equals(IRewriteRule.ELEMENT_TYPE)) {
				return TheoryImage.getImage(ITheoryImages.IMG_REWRITE_RULE);
			}
			if (node.getChildrenType().equals(IPSStatus.ELEMENT_TYPE)) {
				ModelPOContainer parent = ((TheoryModelElementNode) node)
						.getModelParent();
				boolean discharged = parent.getMinConfidence() > IConfidence.REVIEWED_MAX;
				boolean reviewed = parent.getMinConfidence() > IConfidence.PENDING;

				if (discharged) {
					return TheoryImage
							.getImage(ITheoryImages.IMG_DISCHARGED);
				} else if (reviewed) {
					return TheoryImage
							.getImage(ITheoryImages.IMG_REVIEWED);
				} else {
					return TheoryImage
							.getImage(ITheoryImages.IMG_PENDING);
				}
			}

		} else if (element instanceof IContainer) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJS_INFO_TSK);
		}
		return null;
	}

	private String getOverlayIcon(Object obj) {
		if (!hasStatistics(obj))
			return null;
        // Proof status doesn't need overlay icon
		if (obj instanceof IElementNode) {
			final IElementNode node = (IElementNode) obj;
			if (node.getChildrenType().equals(IPSStatus.ELEMENT_TYPE))
				return null;
		}

		final IModelElement model = TheoryModelController.getModelElement(obj);
		if (model == null)
			return null;

		final IStatistics s = new Statistics(model);
		if (s.getUndischargedRest() > 0) {
			return ITheoryImages.IMG_PENDING_OVERLAY_PATH;
		} else if (s.getReviewed() > 0) {
			return ITheoryImages.IMG_REVIEWED_OVERLAY_PATH;
		} else {
			return null;
		}
	}

	private boolean hasStatistics(Object obj) {
		final IElementType<?> type = getElementType(obj);
		return type != IRewriteRule.ELEMENT_TYPE;
	}

}
