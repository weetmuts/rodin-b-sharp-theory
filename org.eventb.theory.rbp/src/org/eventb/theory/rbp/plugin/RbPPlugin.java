/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.plugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 * @author maamria
 *
 */
public class RbPPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.eventb.theory.rbp";

	private static RbPPlugin plugin;
	
	private static final String R_IMG_PATH = "icons/infer.gif";
	private static final String R_IMG_KEY= "INFER";
	
	public RbPPlugin(){}
	
	public static RbPPlugin getDefault(){
		return plugin;
	}
	
	@Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);

        ImageDescriptor myImage = ImageDescriptor.createFromURL(
              FileLocator.find(bundle,
                               new Path(R_IMG_PATH),
                                        null));
        registry.put(R_IMG_KEY, myImage);
    }

	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.start(bundleContext);
	}
	
	public Image getInferImage(){
		Image image = getImageRegistry().get(R_IMG_KEY);
		return image;
	}

}
