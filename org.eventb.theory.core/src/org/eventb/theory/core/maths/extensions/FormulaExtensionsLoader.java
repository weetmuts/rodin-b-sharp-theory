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
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.Definitions;
import org.eventb.core.ast.extensions.maths.IOperatorExtension;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ISCTypeParameter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * An implementation of formula extensions loader from an extensions source.
 * 
 * <p>
 * Currently, possible sources of extensions include:
 * <li> {@link ISCTheoryRoot}</li>
 * <li> {@link IDeployedTheoryRoot}</li>
 * 
 * </p>
 * 
 * @author maamria
 * 
 */
public class FormulaExtensionsLoader {

	public static final Set<IFormulaExtension> EMPTY_EXT = new LinkedHashSet<IFormulaExtension>();

	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private IFormulaExtensionsSource source;

	public FormulaExtensionsLoader(IFormulaExtensionsSource source, FormulaFactory factory) {
		this.source = source;
		this.factory = factory;
		this.typeEnvironment = factory.makeTypeEnvironment();
	}

	public Set<IFormulaExtension> load() throws CoreException {

		if (source == null || !source.exists()) {
			return EMPTY_EXT;
		}
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		initialise();

		ISCDatatypeDefinition datatypeDefinitions[] = getDatatypes(source);

		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			DatatypeTransformer transformer = new DatatypeTransformer();
			Set<IFormulaExtension> addedExtensions = transformer.transform(definition, factory, typeEnvironment);
			if (addedExtensions != null) {
				extensions.addAll(addedExtensions);
			}
			factory = factory.withExtensions(extensions);
			typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		ISCNewOperatorDefinition operatorDefinitions[] = getOperators(source);
		for (ISCNewOperatorDefinition definition : operatorDefinitions) {
			OperatorTransformer transformer = new OperatorTransformer();
			ITypeEnvironment localTypeEnvironment = typeEnvironment.clone();
			IOperatorExtension addedExtensions = transformer.transform(definition, factory, localTypeEnvironment);
			if (addedExtensions == null) {
				continue;
			}
			extensions.add(addedExtensions);
			factory = factory.withExtensions(extensions);
			typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
			localTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(localTypeEnvironment, factory);
			
			if (definition.getDirectOperatorDefinitions().length == 1){
				Formula<?> scFormula = definition.getDirectOperatorDefinitions()[0].getSCFormula(factory, localTypeEnvironment);
				addedExtensions.setDefinition(new Definitions.DirectDefintion(scFormula));
			}
			else if (definition.getRecursiveOperatorDefinitions().length == 1){
				ISCRecursiveOperatorDefinition recDef = definition.getRecursiveOperatorDefinitions()[0];
				Map<Expression, Formula<?>> recursiveCases = new LinkedHashMap<Expression, Formula<?>>();
				FreeIdentifier inductiveArg = factory.makeFreeIdentifier(recDef.getInductiveArgument(),
						null, localTypeEnvironment.getType(recDef.getInductiveArgument()));
				for (ISCRecursiveDefinitionCase recCase : recDef.getRecursiveDefinitionCases()){
					String expressionString = recCase.getExpressionString();
					IParseResult parseRes = factory.parseExpression(expressionString, LanguageVersion.V2, null);
					if (!parseRes.hasProblem()){
						Expression caseExp = parseRes.getParsedExpression();
						RelationalPredicate predicate = factory.makeRelationalPredicate(Formula.EQUAL, inductiveArg, caseExp, null);
						ITypeCheckResult typeCheckRes = predicate.typeCheck(localTypeEnvironment);
						if(!typeCheckRes.hasProblem()){
							ITypeEnvironment inferredEnvironment = typeCheckRes.getInferredEnvironment();
							inferredEnvironment.addAll(localTypeEnvironment);
							Formula<?> caseDef = recCase.getSCFormula(factory, inferredEnvironment);
							recursiveCases.put(predicate.getRight(), caseDef);
						}
					}
					
				}
				
				addedExtensions.setDefinition(new Definitions.RecursiveDefinition(inductiveArg, recursiveCases));
			}

		}
		ISCAxiomaticDefinitionsBlock blocks[] = getBlocks(source);
		for(ISCAxiomaticDefinitionsBlock block : blocks){
			for (ISCAxiomaticTypeDefinition def : block.getAxiomaticTypeDefinitions()){
				AxiomaticTypeTransformer trans = new AxiomaticTypeTransformer();
				IFormulaExtension addedExtensions = trans.transform(def, factory, typeEnvironment);
				if (addedExtensions != null) {
					extensions.add(addedExtensions);
				}
				factory = factory.withExtensions(extensions);
				typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
				
			}
			List<IOperatorExtension> axiomExts = new ArrayList<IOperatorExtension>();
			for (ISCAxiomaticOperatorDefinition def : block.getAxiomaticOperatorDefinitions()){
				AxiomaticOperatorTransformer trans = new AxiomaticOperatorTransformer();
				IOperatorExtension addedExtensions = trans.transform(def, factory, typeEnvironment);
				if (addedExtensions != null) {
					extensions.add(addedExtensions);
					axiomExts.add(addedExtensions);
				}
				factory = factory.withExtensions(extensions);
				typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
			}
			List<Predicate> axiomPredicates = new ArrayList<Predicate>();
			for (ISCAxiomaticDefinitionAxiom axiom : block.getAxiomaticDefinitionAxioms()){
				axiomPredicates.add(axiom.getPredicate(factory, typeEnvironment));
			}
			for (IOperatorExtension opExt : axiomExts){
				opExt.setDefinition(new Definitions.AxiomaticDefinition(axiomPredicates));
			}
		}
		return extensions;
	}

	private void initialise() throws CoreException {
		ISCTypeParameter[] typeParameters = getTypeParameters(source);
		for (ISCTypeParameter typeParameter : typeParameters) {
			typeEnvironment.addGivenSet(typeParameter.getIdentifierString());
		}
	}

	private ISCNewOperatorDefinition[] getOperators(IFormulaExtensionsSource source) throws CoreException {
		return source.getSCNewOperatorDefinitions();
	}

	private ISCDatatypeDefinition[] getDatatypes(IFormulaExtensionsSource source) throws CoreException {
		return source.getSCDatatypeDefinitions();
	}

	private ISCTypeParameter[] getTypeParameters(IFormulaExtensionsSource source) throws CoreException {
		return source.getSCTypeParameters();
	}
	
	private ISCAxiomaticDefinitionsBlock[] getBlocks(IFormulaExtensionsSource source) throws CoreException{
		return source.getSCAxiomaticDefinitionsBlocks();
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
class DatatypeTransformer{

	public Set<IFormulaExtension> transform(final ISCDatatypeDefinition definition, final FormulaFactory factory,
			ITypeEnvironment typeEnvironment) throws RodinDBException {
		if (definition == null || !definition.exists()) {
			return null;
		}
		if (definition.hasHasErrorAttribute() && definition.hasError()) {
			return FormulaExtensionsLoader.EMPTY_EXT;
		}

		final String typeName = definition.getIdentifierString();
		ISCTypeArgument[] scTypeArguments = definition.getTypeArguments();
		final String[] typeArguments = new String[scTypeArguments.length];
		for (int i = 0; i < typeArguments.length; i++) {
			typeArguments[i] = scTypeArguments[i].getSCGivenType(factory).toString();
		}
		FormulaFactory tempFactory = factory.withExtensions(MathExtensionsFactory.getSimpleDatatypeExtensions(
				typeName, typeArguments, factory));

		ISCDatatypeConstructor[] constructors = definition.getConstructors();
		final Map<String, Map<String, String>> datatypeCons = new LinkedHashMap<String, Map<String, String>>();
		for (ISCDatatypeConstructor cons : constructors) {
			String consIdent = cons.getIdentifierString();
			ISCConstructorArgument[] destructors = cons.getConstructorArguments();
			if (destructors.length == 0) {
				datatypeCons.put(consIdent, new LinkedHashMap<String, String>());
			} else {
				Map<String, String> datatypeDes = new LinkedHashMap<String, String>();
				for (ISCConstructorArgument dest : destructors) {
					datatypeDes.put(dest.getIdentifierString(), dest.getType(tempFactory).toString());
				}
				datatypeCons.put(consIdent, datatypeDes);
			}
			
		}
		return MathExtensionsFactory.getCompleteDatatypeExtensions(typeName, typeArguments, datatypeCons, factory);

	}

}

class AxiomaticTypeTransformer{

	public IFormulaExtension transform(ISCAxiomaticTypeDefinition definition, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		try{
		return (IFormulaExtension)
				MathExtensionsFactory.getAxiomaticTypeExtension(definition.getIdentifierString(), 
						definition.getIdentifierString() + " Axiomatic Type", definition);
		} catch(CoreException exception){
			return null;
		}
	}
	
}

class AxiomaticOperatorTransformer extends DefinitionTransformer<ISCAxiomaticOperatorDefinition>{
	
	@Override
	public IOperatorExtension transform(ISCAxiomaticOperatorDefinition definitionElmnt, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) throws RodinDBException {
		if (definitionElmnt == null || !definitionElmnt.exists()) {
			return null;
		}
		if (definitionElmnt.hasHasErrorAttribute() && definitionElmnt.hasError()) {
			return null;
		}
		String theoryName = definitionElmnt.getParent().getParent().getElementName();
		String syntax = definitionElmnt.getLabel();
		String operatorID = AstUtilities.makeOperatorID(theoryName, syntax);
		FormulaType formulaType = definitionElmnt.getFormulaType();
		Notation notation = definitionElmnt.getNotationType();
		String groupID = definitionElmnt.getOperatorGroup();
		boolean isAssociative = definitionElmnt.isAssociative();
		boolean isCommutative = definitionElmnt.isCommutative();

		ISCOperatorArgument[] scOperatorArguments = definitionElmnt.getOperatorArguments();
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		List<GivenType> typeParameters = new ArrayList<GivenType>();
		for (ISCOperatorArgument arg : scOperatorArguments) {
			Type type = arg.getType(factory);
			operatorArguments.put(arg.getIdentifierString(), type);
			for (GivenType t : AstUtilities.getGivenTypes(type)) {
				if (!typeParameters.contains(t)) {
					typeParameters.add(t);
				}
			}
		}
		ITypeEnvironment tempTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(
				typeEnvironment, factory);
		for (String arg : operatorArguments.keySet()) {
			tempTypeEnvironment.addName(arg, operatorArguments.get(arg));
		}
		Predicate wdCondition = definitionElmnt.getPredicate(factory, tempTypeEnvironment);
		Predicate dWdCondition = definitionElmnt.getWDCondition(factory, tempTypeEnvironment);
		IOperatorExtension extension = null;
		OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType,
				notation, groupID);
		if (AstUtilities.isExpressionOperator(definitionElmnt.getFormulaType())) {
			extension = (IOperatorExtension)MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
					operatorArguments, definitionElmnt.getType(factory), wdCondition, dWdCondition, null,definitionElmnt);
		} else {
			extension = (IOperatorExtension)MathExtensionsFactory.getPredicateExtension(properties, isCommutative, operatorArguments,
					wdCondition, dWdCondition, null, definitionElmnt);
		}
		return extension;
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
class OperatorTransformer extends DefinitionTransformer<ISCNewOperatorDefinition> {

	@Override
	public IOperatorExtension transform(ISCNewOperatorDefinition definitionElmnt, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) throws RodinDBException {
		if (definitionElmnt == null || !definitionElmnt.exists()) {
			return null;
		}
		if (definitionElmnt.hasHasErrorAttribute() && definitionElmnt.hasError()) {
			return null;
		}
		String theoryName = definitionElmnt.getParent().getElementName();
		String syntax = definitionElmnt.getLabel();
		String operatorID = AstUtilities.makeOperatorID(theoryName, syntax);
		FormulaType formulaType = definitionElmnt.getFormulaType();
		Notation notation = definitionElmnt.getNotationType();
		String groupID = definitionElmnt.getOperatorGroup();
		boolean isAssociative = definitionElmnt.isAssociative();
		boolean isCommutative = definitionElmnt.isCommutative();

		ISCOperatorArgument[] scOperatorArguments = definitionElmnt.getOperatorArguments();
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		List<GivenType> typeParameters = new ArrayList<GivenType>();
		for (ISCOperatorArgument arg : scOperatorArguments) {
			Type type = arg.getType(factory);
			operatorArguments.put(arg.getIdentifierString(), type);
			for (GivenType t : AstUtilities.getGivenTypes(type)) {
				if (!typeParameters.contains(t)) {
					typeParameters.add(t);
				}
			}
		}
		for (String arg : operatorArguments.keySet()) {
			typeEnvironment.addName(arg, operatorArguments.get(arg));
		}
		Predicate wdCondition = definitionElmnt.getPredicate(factory, typeEnvironment);
		Predicate dWdCondition = definitionElmnt.getWDCondition(factory, typeEnvironment);
		final IOperatorExtension extension;
		OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType,
				notation, groupID);
		if (AstUtilities.isExpressionOperator(definitionElmnt.getFormulaType())) {
			extension = (IOperatorExtension) MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
					operatorArguments, definitionElmnt.getType(factory), wdCondition, dWdCondition, null, definitionElmnt);
		} else {
			extension = (IOperatorExtension)MathExtensionsFactory.getPredicateExtension(properties, isCommutative, operatorArguments,
					wdCondition, dWdCondition, null,definitionElmnt);
		}
		return extension;
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
 * <code> {@link ISCAxiomaticOperatorDefinition} </code></li>
 * </p>
 * 
 * @author maamria
 * 
 */
abstract class DefinitionTransformer<E extends IInternalElement> {

	/**
	 * Returns the mathematical extension contained in the definition
	 * element.
	 * 
	 * @param definition
	 *            the definitional element
	 * @param factory
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @return the formula extension
	 * @throws RodinDBException 
	 * @throws CoreException
	 */
	public abstract IOperatorExtension transform(E definition, final FormulaFactory factory,
			ITypeEnvironment typeEnvironment) throws RodinDBException;

}