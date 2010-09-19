/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.IOperatorExtension;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 * 
 */
public class TheoryOperatorExtensionPOGModule extends POGProcessorModule {

	public static final IModuleType<TheoryOperatorExtensionPOGModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryOperatorExtensionModule"); 
	
	protected FormulaFactory factory;
	protected ITypeEnvironment typeEnvironment;
	protected IPORoot target;

	protected static final String OPERATOR_WD_PO = "Operator Well-Definedness Preservation";
	protected static final String OPERATOR_WD_POSTFIX = "/Op-WD";
	protected static final String OPERATOR_COMMUT_PO = "Operator Commutativity";
	protected static final String OPERATOR_COMMUT_POSTFIX = "/Op-COMMUT";
	protected static final String OPERATOR_ASSOC_PO = "Operator Associativity";
	protected static final String OPERATOR_ASSOC_POSTFIX = "/Op-ASSOC";
	
	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		target = repository.getTarget();
		
		IRodinFile scTheoryFile = (IRodinFile) element;
		ISCTheoryRoot scTheoryRoot = (ISCTheoryRoot) scTheoryFile.getRoot();
		ISCNewOperatorDefinition[] scDefinitions = scTheoryRoot.getSCNewOperatorDefinitions();
		Set<IFormulaExtension> allExtensions = factory.getExtensions();
		Map<String, IFormulaExtension> map = new HashMap<String, IFormulaExtension>();
		for (IFormulaExtension extension : allExtensions){
			if(extension instanceof IOperatorExtension){
				IOperatorExtension opExtension = (IOperatorExtension) extension;
				if(opExtension.getOrigin()!=null && opExtension.getOrigin() instanceof ISCNewOperatorDefinition){
					ISCNewOperatorDefinition origin = (ISCNewOperatorDefinition) opExtension.getOrigin();
					String elementName = origin.getElementName();
					IRodinElement parent = origin.getParent();
					if(parent == null){
						continue;
					}
					String parentName = parent.getElementName();
					String key = parentName+"."+elementName;
					map.put(key, opExtension);
				}
			}
		}
		for (ISCNewOperatorDefinition definition : scDefinitions) {
			String elementName = definition.getElementName();
			String parentName = scTheoryRoot.getElementName();
			String key = parentName+"."+elementName;
			if(map.containsKey(key)){
				IFormulaExtension extension = map.get(key);
				generateCorrespondingPOs(extension, definition, monitor);
			}
			
		}
	}

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		factory = null;
		target = null;
		super.endModule(element, repository, monitor);
	}
	
	protected void generateCorrespondingPOs(IFormulaExtension extension,
			ISCNewOperatorDefinition definition, IProgressMonitor monitor) throws CoreException {
		if (extension instanceof IOperatorExtension) {
			IOperatorExtension operatorExtension = (IOperatorExtension) extension;

			// generate WD-PO
			Predicate wdPOPredicate = operatorExtension
					.getWellDefinednessChecker(factory, typeEnvironment);
			IPOPredicateSet hyp = target
					.getPredicateSet(TheoryTypeParametersPOGModule.ABS_HYP_NAME);
			IPOGSource[] sources = new IPOGSource[] { makeSource(
					IPOSource.DEFAULT_ROLE, definition.getSource()) };
			if (wdPOPredicate != null) {
				IPOGNature nature = makeNature(OPERATOR_WD_PO);
				createPO(
						target,
						operatorExtension.getId() + OPERATOR_WD_POSTFIX,
						nature,
						hyp,
						null,
						makePredicate(wdPOPredicate,
								definition.getSource()),
								sources, new IPOGHint[0], true, monitor);
			}

			// generate Commut-PO
			Predicate commutPOPredicate = operatorExtension
					.getCommutativityChecker(factory, typeEnvironment);
			if (commutPOPredicate != null) {
				IPOGNature nature = makeNature(OPERATOR_COMMUT_PO);
				createPO(
						target,
						operatorExtension.getId() + OPERATOR_COMMUT_POSTFIX,
						nature,
						hyp,
						null,
						makePredicate(commutPOPredicate,
								definition.getSource()),
								sources, new IPOGHint[0], true, monitor);
			}
			// generate Assos-PO
			Predicate assosPOPredicate = operatorExtension
					.getAssociativityChecker(factory, typeEnvironment);
			if (assosPOPredicate != null) {
				IPOGNature nature = makeNature(OPERATOR_ASSOC_PO);
				
				createPO(
						target,
						operatorExtension.getId() + OPERATOR_ASSOC_POSTFIX,
						nature,
						hyp,
						null,
						makePredicate(assosPOPredicate,
								definition.getSource()),
								sources, new IPOGHint[0], true, monitor);
			}
		}
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
