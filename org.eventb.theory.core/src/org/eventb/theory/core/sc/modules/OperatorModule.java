/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.OperatorLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class OperatorModule extends LabeledElementModule{

	private final IModuleType<OperatorModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorModule");
	
	private TheoryAccuracyInfo theoryAccuracyInfo;
	private ITypeEnvironment globalTypeEnvironment;
	private FormulaFactory factory;
	private IdentifierSymbolTable identifierSymbolTable;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		monitor.subTask(Messages.progress_TheoryOperators);
		monitor.worked(1);
		INewOperatorDefinition[] newOpDefs = root.getNewOperatorDefinitions();
		ILabelSymbolInfo[] operators = fetchOperators(newOpDefs, root.getComponentName(), repository, monitor);
		ISCNewOperatorDefinition scNewOpDefs[] = new ISCNewOperatorDefinition[newOpDefs.length];
		commitOperators(newOpDefs, targetRoot, scNewOpDefs,operators, monitor);
		processOperators(newOpDefs, root.getComponentName(), scNewOpDefs, repository, operators, monitor);
		monitor.worked(2);
		
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		factory = repository.getFormulaFactory();
		globalTypeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IdentifierSymbolTable) repository.getState(IdentifierSymbolTable.STATE_TYPE);
		
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		repository.setTypeEnvironment(globalTypeEnvironment);
		theoryAccuracyInfo = null;
		identifierSymbolTable = null;
		factory = null;
		globalTypeEnvironment = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (OperatorLabelSymbolTable) repository.getState(OperatorLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalOperator(symbol, true, element,
				component);
	}
	
	/**
	 * Fetches the operators provided.
	 * @param newOpDefs the operator definitions
	 * @param theoryName the theory name
	 * @param repository the state repository
	 * @param monitor the progress monitor
	 * @return the symbol infos
	 * @throws CoreException
	 */
	protected ILabelSymbolInfo[] fetchOperators(INewOperatorDefinition[] newOpDefs, String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		initFilterModules(repository, monitor);
		ILabelSymbolInfo[] labelSymbolInfos = new ILabelSymbolInfo[newOpDefs.length];
		for(int i = 0 ; i < newOpDefs.length; i++){
			INewOperatorDefinition opDef = newOpDefs[i];
			labelSymbolInfos[i] = fetchLabel(opDef, theoryName, monitor);
			if(labelSymbolInfos[i] == null){
				continue;
			}
			if (!filterModules(opDef, repository, null)) {
				labelSymbolInfos[i].setError();

			}
		}
		endFilterModules(repository, null);
			
		return labelSymbolInfos;
	}
	
	/**
	 * Creates the statically checked counterparts of the given operator definitions
	 * @param newOpDefs the operator definitions
	 * @param targetRoot the target root
	 * @param scNewOpDefs the SC operator definitions
	 * @param labelSymbolInfos the symbol info
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected void commitOperators(INewOperatorDefinition[] newOpDefs, ISCTheoryRoot targetRoot,
			ISCNewOperatorDefinition[] scNewOpDefs,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) 
	throws CoreException{
		for (int i = 0; i < newOpDefs.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scNewOpDefs[i] = createSCNewOperatorDefinition(targetRoot, labelSymbolInfos[i],
						newOpDefs[i], monitor);
			}
			else {
				theoryAccuracyInfo.setNotAccurate();
			}
		}
	}
	
	/**
	 * Create a statically checked operator definition corresponding to the given operator definition.
	 * @param targetRoot the target root
	 * @param symbolInfo the symbol info
	 * @param newOpDef the operator definition
	 * @param monitor the progress monitor
	 * @return the SC new operator definition
	 * @throws CoreException
	 */
	protected ISCNewOperatorDefinition createSCNewOperatorDefinition(ISCTheoryRoot targetRoot,
			ILabelSymbolInfo symbolInfo,
			INewOperatorDefinition newOpDef,
			IProgressMonitor monitor) throws CoreException {
		
		ILabeledElement scNewOpDef = symbolInfo.createSCElement(targetRoot,
				null, monitor);
		return (ISCNewOperatorDefinition) scNewOpDef;
	}
	
	/**
	 * Processes the given operator definitions and accumulates information on the operator being defined.
	 * @param newOpDefs the operator definitions
	 * @param theoryName the name of the theory
	 * @param scNewOpDefs the SC operator definitions
	 * @param repository the state repository
	 * @param operators the symbol infos
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected void processOperators(INewOperatorDefinition[] newOpDefs, String theoryName, ISCNewOperatorDefinition[] scNewOpDefs,
			ISCStateRepository repository, ILabelSymbolInfo[] operators,
			IProgressMonitor monitor) throws CoreException{
		for (int i = 0; i < newOpDefs.length; i++) {

			if (operators[i] != null && !operators[i].hasError()) {
				// get latest factory and environment
				factory = repository.getFormulaFactory();
				ITypeEnvironment opTypeEnvironment = factory.makeTypeEnvironment();
				opTypeEnvironment.addAll(globalTypeEnvironment);
				repository.setTypeEnvironment(opTypeEnvironment);
				// needed states
				repository.setState(new StackedIdentifierSymbolTable(
						identifierSymbolTable, ModulesUtils.IDENT_SYMTAB_SIZE,
						factory));
				String opID = theoryName + "." + operators[i].getSymbol();
				IOperatorInformation operatorInformation = new OperatorInformation(opID, factory);
				repository.setState(operatorInformation);
				// copying of information
				operatorInformation.setFormulaType(newOpDefs[i].getFormulaType());
				operatorInformation.setNotation(newOpDefs[i].getNotationType());
				operatorInformation.setSyntax(newOpDefs[i].getSyntaxSymbol());
				operatorInformation.setAssociative(newOpDefs[i].isAssociative());
				operatorInformation.setCommutative(newOpDefs[i].isCommutative());
				
				// children processors
				{
					initProcessorModules(newOpDefs[i], repository, null);
					processModules(newOpDefs[i], scNewOpDefs[i], repository, monitor);
					endProcessorModules(newOpDefs[i], repository, null);
				}
				// update the factory
				if(!operatorInformation.hasError()){
					factory = repository.getFormulaFactory();
					globalTypeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(globalTypeEnvironment, factory);
					repository.setTypeEnvironment(globalTypeEnvironment);
				}
			}
			monitor.worked(1);
		}
		// get the new type environment corresponding to the factory
		// TODO test if another update of variable factory is needed
		globalTypeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(globalTypeEnvironment, factory);
		repository.setTypeEnvironment(globalTypeEnvironment);
	}
	
}
