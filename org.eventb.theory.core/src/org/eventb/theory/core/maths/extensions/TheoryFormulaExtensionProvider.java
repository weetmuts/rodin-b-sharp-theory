/*******************************************************************************
 * Copyright (c) 2011, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - dependency on checked theory path
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.eventb.theory.internal.core.util.CoreUtilities.log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.IOperatorExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public class TheoryFormulaExtensionProvider implements IFormulaExtensionProvider {

	private static final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID
		+ ".theoryExtensionsProvider";
	private FormulaFactory factory;
	private ITypeEnvironmentBuilder typeEnvironment;
	
	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		try {
			return WorkspaceExtensionsManager.getInstance()
					.getFormulaExtensions(root);
			// else ignore paths
		} catch (CoreException e) {
			CoreUtilities
					.log(e, "Error while computing math extensions for "
							+ root.getPath());
		}
		return FormulaExtensionsLoader.EMPTY_EXT;
	}

	/**
	 * Returns the checked theory path of the given project. We do not search
	 * directly the checked file, as it might not exist yet (e.g., just after a
	 * project clean), but rather the unchecked file that we convert eventually.
	 * 
	 * @param prj
	 *            some Rodin project
	 * @return the checked theory path of the given project
	 */
	private ISCTheoryPathRoot findSCTheoryPathRoot(IRodinProject prj) {
		final ITheoryPathRoot[] paths;
		try {
			paths = prj.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			log(e, "error while getting theory path for project " + prj);
			return null;
		}
		if (paths.length == 0) {
			return null;
		}
		if (paths.length > 1) {
			log(null,
					"Several theory paths in project " + prj + ": "
							+ Arrays.asList(paths));
			return null;
		}
		return paths[0].getSCTheoryPathRoot();
	}

	@Override
	public Set<IRodinFile> getFactoryFiles(IEventBRoot root) {
		final IRodinProject prj = root.getRodinProject();
		final ISCTheoryPathRoot pathRoot = findSCTheoryPathRoot(prj);
		if (pathRoot == null) {
			return emptySet();
		}
		return singleton(pathRoot.getRodinFile());
	}

	@Override
	public FormulaFactory loadFormulaFactory(ILanguage element,
			IProgressMonitor monitor) throws CoreException {
		// optimisation: if all of extensions of element is syntax and semantic same as the extensions of current ff (getFormulaExtensions), dont need to create new ff
		
 		factory = FormulaFactory.getDefault();
 		typeEnvironment = factory.makeTypeEnvironment();
		for (IRodinElement extensionElement : element.getChildren()) {
			
			if (extensionElement instanceof ISCNewOperatorDefinition) {
				loadSCNewOperatorDefinition((ISCNewOperatorDefinition) extensionElement);
			}
			else if (extensionElement instanceof ISCAxiomaticTypeDefinition) {
				loadSCAxiomaticTypeDefinition((ISCAxiomaticTypeDefinition) extensionElement);
			}
			else if (extensionElement instanceof ISCAxiomaticOperatorDefinition) {
				loadSCAxiomaticOperatorDefinition((ISCAxiomaticOperatorDefinition) extensionElement);
			}
			else if (extensionElement instanceof ISCDatatypeDefinition) {
				loadSCDatatypeDefinition((ISCDatatypeDefinition) extensionElement);
			}
			else {
				log(null, "Extension is not supported: " + extensionElement);
			}	
		}
	
		return factory;
	}


	private void loadSCDatatypeDefinition(ISCDatatypeDefinition extensionElement) throws CoreException {
		
		Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();
		DatatypeTransformer transformer = new DatatypeTransformer();
		IDatatype datatype = transformer.transform(extensionElement, factory);
		if (datatype != null) {
			extensions.addAll(datatype.getExtensions());
		}
		factory = factory.withExtensions(extensions);
		typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
	}

	private void loadSCAxiomaticOperatorDefinition(ISCAxiomaticOperatorDefinition extensionElement) throws CoreException {

		Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();
		AxiomaticOperatorTransformer trans = new AxiomaticOperatorTransformer();
		IOperatorExtension addedExtensions = trans.transform(extensionElement, factory, typeEnvironment);
		if (addedExtensions != null) {
			extensions.add(addedExtensions);
		}
		factory = factory.withExtensions(extensions);
		typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
	}

	private void loadSCAxiomaticTypeDefinition(ISCAxiomaticTypeDefinition extensionElement) throws CoreException {
		
		Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();
		AxiomaticTypeTransformer trans = new AxiomaticTypeTransformer();
		IFormulaExtension addedExtensions = trans.transform(extensionElement, factory, typeEnvironment);
		if (addedExtensions != null) {
			extensions.add(addedExtensions);
		}
		factory = factory.withExtensions(extensions);
		typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
		
	}

	private void loadSCNewOperatorDefinition(ISCNewOperatorDefinition extensionElement) throws CoreException {
	
		Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();
		OperatorTransformer transformer = new OperatorTransformer();
		ITypeEnvironmentBuilder localTypeEnvironment = typeEnvironment.makeBuilder();
		IOperatorExtension addedExtensions = transformer.transform(extensionElement, factory, localTypeEnvironment);
		if (addedExtensions != null) {
			extensions.add(addedExtensions);
		}
		factory = factory.withExtensions(extensions);
		typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
		localTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(localTypeEnvironment, factory);
	}

	@Override
	public void saveFormulaFactory(ILanguage element, FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException {
		
		final Set<IFormulaExtension> extensions = factory.getExtensions();
		for (IFormulaExtension extension : extensions) {
			Object extensionElement = extension.getOrigin();
			if (extensionElement instanceof ISCNewOperatorDefinition) {
				ISCNewOperatorDefinition operator = (ISCNewOperatorDefinition) extensionElement;
				if (!contains(element.getChildren(), operator))
					operator.copy(element, null, null, false, monitor);
			}
			else if (extensionElement instanceof ISCAxiomaticTypeDefinition) {
				((ISCAxiomaticTypeDefinition) extensionElement).copy(element, null, null, false, monitor);
			}
			else if (extensionElement instanceof ISCAxiomaticOperatorDefinition) {
				((ISCAxiomaticOperatorDefinition) extensionElement).copy(element, null, null, false, monitor);
			}
			else if (extensionElement instanceof IDatatype) {
				ISCDatatypeDefinition datatype = (ISCDatatypeDefinition) ((IDatatype) extensionElement).getOrigin();
				if (!contains(element.getChildren(), datatype))
					datatype.copy(element, null, null, false, monitor);
			}
//			else {
//				log(null, "Extension is not supported: " + extensionElement);
//			}
		}
		
	}
	
	private boolean contains(IRodinElement[] elementChildren, IRodinElement datatype) {
		for (final IRodinElement e : elementChildren)
			if (datatype != null && datatype.getElementName().equals(e.getElementName()))
				return true;
		return false;
	}
	
}
