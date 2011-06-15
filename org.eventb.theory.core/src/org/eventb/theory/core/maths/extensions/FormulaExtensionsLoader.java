/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.core.maths.MathExtensionsFactory;
import org.eventb.theory.core.maths.OperatorExtensionProperties;
import org.eventb.theory.internal.core.maths.OperatorArgument;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;

/**
 * An implementation of formula extensions loader from an extensions source.
 * 
 * <p>Currently, possible sources of extensions include:
 * <li> {@link ISCTheoryRoot}</li>
 * <li> {@link IDeployedTheoryRoot}</li>
 * 
 * </p>
 * 
 * @author maamria
 * 
 */
public class FormulaExtensionsLoader {

	private static final Set<IFormulaExtension> EMPTY_EXT = new LinkedHashSet<IFormulaExtension>();
	
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private IFormulaExtensionsSource source;
	
	public FormulaExtensionsLoader(IFormulaExtensionsSource source, FormulaFactory factory){
		this.source = source;
		this.factory = factory;
		this.typeEnvironment = factory.makeTypeEnvironment();
	}
	
	public Set<IFormulaExtension> load() {

		if (source == null || !source.exists()) {
			return EMPTY_EXT;
		}
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		try {
			initialise();

			ISCDatatypeDefinition datatypeDefinitions[] = getDatatypes(source);

			for (ISCDatatypeDefinition definition : datatypeDefinitions) {
				DatatypeTransformer transformer = new DatatypeTransformer();
				Set<IFormulaExtension> addedExtensions = transformer.transform(
						definition, factory, typeEnvironment);
				if (addedExtensions != null) {
					extensions.addAll(addedExtensions);
				}
				factory = factory.withExtensions(extensions);
				typeEnvironment = MathExtensionsUtilities
						.getTypeEnvironmentForFactory(typeEnvironment, factory);
			}
			ISCNewOperatorDefinition operatorDefinitions[] = getOperators(source);
			for (ISCNewOperatorDefinition definition : operatorDefinitions) {
				OperatorTransformer transformer = new OperatorTransformer();
				Set<IFormulaExtension> addedExtensions = transformer.transform(
						definition, factory, typeEnvironment);
				if (addedExtensions != null) {
					extensions.addAll(addedExtensions);
				}
				factory = factory.withExtensions(extensions);
				typeEnvironment = MathExtensionsUtilities
						.getTypeEnvironmentForFactory(typeEnvironment, factory);

			}
			return extensions;
		} catch (CoreException exception) {
			return EMPTY_EXT;
		}
	}

	private void initialise() throws CoreException {
		ISCTypeParameter[] typeParameters = getTypeParameters(source);
		for (ISCTypeParameter typeParameter : typeParameters) {
			typeEnvironment.addGivenSet(typeParameter.getIdentifierString());
		}
	}

	private ISCNewOperatorDefinition[] getOperators(
			IFormulaExtensionsSource source) throws CoreException {
		return source.getSCNewOperatorDefinitions();
	}

	private ISCDatatypeDefinition[] getDatatypes(
			IFormulaExtensionsSource source) throws CoreException {
		return source.getSCDatatypeDefinitions();
	}

	private ISCTypeParameter[] getTypeParameters(
			IFormulaExtensionsSource source) throws CoreException {
		return source.getSCTypeParameters();
	}
}

/**
 * An implementation of a transformer of a statically checked datatype
 * definition.
 * 
 * @see ISCDatatypeDefinition
 * 
 * @author maamria
 * 
 */
class DatatypeTransformer extends DefinitionTransformer<ISCDatatypeDefinition> {

	@Override
	public Set<IFormulaExtension> transform(
			final ISCDatatypeDefinition definition,
			final FormulaFactory factory, ITypeEnvironment typeEnvironment) {
		if (definition == null || !definition.exists()) {
			return EMPTY_EXT;
		}
		try {
			if (definition.hasHasErrorAttribute() && definition.hasError()) {
				return EMPTY_EXT;
			}

			final String typeName = definition.getIdentifierString();
			ISCTypeArgument[] scTypeArguments = definition.getTypeArguments();
			final String[] typeArguments = new String[scTypeArguments.length];
			for (int i = 0; i < typeArguments.length; i++) {
				typeArguments[i] = scTypeArguments[i].getSCGivenType(factory)
						.toString();
			}
			FormulaFactory tempFactory = factory
					.withExtensions(extensionsFactory
							.getSimpleDatatypeExtensions(typeName,
									typeArguments, factory));

			ISCDatatypeConstructor[] constructors = definition
					.getConstructors();
			final Map<String, Map<String, Type>> datatypeCons = new LinkedHashMap<String, Map<String, Type>>();
			for (ISCDatatypeConstructor cons : constructors) {
				String consIdent = cons.getIdentifierString();
				ISCConstructorArgument[] destructors = cons
						.getConstructorArguments();
				if (destructors.length == 0) {
					datatypeCons.put(consIdent,
							new LinkedHashMap<String, Type>());
				} else {
					Map<String, Type> datatypeDes = new LinkedHashMap<String, Type>();
					for (ISCConstructorArgument dest : destructors) {
						datatypeDes.put(dest.getIdentifierString(),
								dest.getType(tempFactory));
					}
					datatypeCons.put(consIdent, datatypeDes);
				}

			}
			return extensionsFactory.getCompleteDatatypeExtensions(typeName,
					typeArguments, datatypeCons, factory);
		} catch (CoreException exception) {
			return EMPTY_EXT;
		}

	}

}

/**
 * An implementation of a transformer for statically checked operator
 * definitions.
 * 
 * @see ISCNewOperatorDefinition
 * 
 * @author maamria
 * 
 */
class OperatorTransformer extends
		DefinitionTransformer<ISCNewOperatorDefinition> {

	@Override
	public Set<IFormulaExtension> transform(
			ISCNewOperatorDefinition definition, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		if (definition == null || !definition.exists()) {
			return EMPTY_EXT;
		}
		try {
			if (definition.hasHasErrorAttribute() && definition.hasError()) {
				return EMPTY_EXT;
			}
			String theoryName = definition.getParent().getElementName();
			String operatorID = theoryName + "." + definition.getLabel();
			String syntax = definition.getLabel();
			FormulaType formulaType = definition.getFormulaType();
			Notation notation = definition.getNotationType();
			String groupID = definition.getOperatorGroup();
			boolean isAssociative = definition.isAssociative();
			boolean isCommutative = definition.isCommutative();

			ISCOperatorArgument[] scOperatorArguments = definition
					.getOperatorArguments();
			List<IOperatorArgument> operatorArguments = new ArrayList<IOperatorArgument>();
			List<GivenType> typeParameters = new ArrayList<GivenType>();
			int index = 0;
			for (ISCOperatorArgument arg : scOperatorArguments) {
				OperatorArgument opArg = new OperatorArgument(index++,
						arg.getIdentifierString(), arg.getType(factory));
				operatorArguments.add(opArg);
				for (GivenType t : opArg.getGivenTypes()) {
					if (!typeParameters.contains(t)) {
						typeParameters.add(t);
					}
				}
			}
			ITypeEnvironment tempTypeEnvironment = MathExtensionsUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
			for (IOperatorArgument arg : operatorArguments) {
				tempTypeEnvironment.addName(arg.getArgumentName(),
						arg.getArgumentType());
			}
			Predicate wdCondition = definition.getPredicate(factory,
					tempTypeEnvironment);
			IFormulaExtension extension = null;
			OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, groupID);
			if (MathExtensionsUtilities.isExpressionOperator(definition.getFormulaType())) {
				extension = extensionsFactory.getFormulaExtension(properties, isCommutative, isAssociative, 
						extensionsFactory.getTypingRule(
								operatorArguments, 
								definition.getType(factory), 
								wdCondition, isAssociative), 
							definition);
			} else {
				extension = extensionsFactory.getFormulaExtension(properties, isCommutative, 
						extensionsFactory.getTypingRule(operatorArguments, wdCondition), 
						definition);
			}
			return MathExtensionsUtilities.singletonExtension(extension);
		} catch (CoreException exception) {
			return EMPTY_EXT;
		}
	}

}

/**
 * Common protocol for a definition element transformer.
 * 
 * <p>
 * Typically, a definition element contains information to construct a
 * mathematical formula extension as per AST requirements.
 * 
 * <p>
 * Currently, the available definition elements are :
 * 
 * <li>
 * <code> {@link ISCNewOperatorDefinition} </code></li>
 * <li>
 * <code> {@link ISCDatatypeDefinition} </code></li>
 * </p>
 * 
 * @author maamria
 * 
 */
abstract class DefinitionTransformer<E extends IInternalElement> {

	protected MathExtensionsFactory extensionsFactory;

	protected final Set<IFormulaExtension> EMPTY_EXT = new LinkedHashSet<IFormulaExtension>();

	protected DefinitionTransformer() {
		extensionsFactory = MathExtensionsFactory.getDefault();
	}

	/**
	 * Returns the set of mathematical extensions contained in the definition
	 * element.
	 * 
	 * @param definition
	 *            the definitional element
	 * @param factory
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @return the formula extensions, should not be <code>null</code>
	 * @throws CoreException
	 */
	public abstract Set<IFormulaExtension> transform(E definition,
			final FormulaFactory factory, ITypeEnvironment typeEnvironment);

}