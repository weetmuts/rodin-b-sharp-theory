/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.sc.base.LabeledElementModule;
import org.eventb.theory.internal.core.sc.states.AbstractTheoryLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.sc.states.OperatorInformation;
import org.eventb.theory.internal.core.sc.states.OperatorLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class NewOperatorDefinitionModule extends LabeledElementModule{

	private static final int OP_IDENT_SYMTAB_SIZE = 2047;

	IModuleType<NewOperatorDefinitionModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".newOperatorDefinitionModule");
	
	private FormulaFactory factory;
	private ITypeEnvironment globalTypeEnvironment;
	private IdentifierSymbolTable identifierSymbolTable;
	
	private INewOperatorDefinition newOpDefs[];
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		monitor.subTask(Messages.progress_TheoryOperators);
		monitor.worked(1);
		ILabelSymbolInfo[] operators = fetchOperators(file, repository, monitor);
		ISCNewOperatorDefinition scNewOpDefs[] = new ISCNewOperatorDefinition[newOpDefs.length];
		commitOperators(root, targetRoot, scNewOpDefs,operators, monitor);
		processOperators(scNewOpDefs, repository, operators, monitor);
		monitor.worked(2);
		
	}

	/**
	 * @param scNewOpDefs
	 * @param repository
	 * @param operators
	 * @param monitor
	 */
	private void processOperators(ISCNewOperatorDefinition[] scNewOpDefs,
			ISCStateRepository repository, ILabelSymbolInfo[] operators,
			IProgressMonitor monitor) throws CoreException{
		for (int i = 0; i < newOpDefs.length; i++) {

			if (operators[i] != null && !operators[i].hasError()) {

				repository.setState(new StackedIdentifierSymbolTable(
						identifierSymbolTable, OP_IDENT_SYMTAB_SIZE,
						factory));
				factory = repository.getFormulaFactory();
				globalTypeEnvironment = repository.getTypeEnvironment();

				ITypeEnvironment opTypeEnvironment = factory.makeTypeEnvironment();
				opTypeEnvironment.addAll(globalTypeEnvironment);
				repository.setTypeEnvironment(opTypeEnvironment);
				IOperatorInformation operatorInformation = new OperatorInformation(operators[i].getSymbol(), factory);
				repository.setState(operatorInformation);
				populateOperatorInformation(operatorInformation, operators[i], newOpDefs[i]);
				initProcessorModules(newOpDefs[i], repository, null);

				processModules(newOpDefs[i], scNewOpDefs[i], repository, monitor);

				endProcessorModules(newOpDefs[i], repository, null);

				// update the factory
				factory = repository.getFormulaFactory();
				globalTypeEnvironment = CoreUtilities.getTypeEnvironmentForFactory(globalTypeEnvironment, factory);
			}

			monitor.worked(1);
		}

		
	}

	/**
	 * @param operatorInformation
	 * @param iLabelSymbolInfo
	 * @param newOpDefs2 
	 */
	private void populateOperatorInformation(
			IOperatorInformation operatorInformation,
			ILabelSymbolInfo symbolInfo, 
			INewOperatorDefinition newOpDef) throws CoreException{
		assert symbolInfo.getSymbol().equals(newOpDef.getLabel());
		if(!symbolInfo.hasError()){
			operatorInformation.setFormulaType(newOpDef.getFormulaType());
			operatorInformation.setNotation(newOpDef.getNotationType());
			operatorInformation.setSyntax(newOpDef.getSyntaxSymbol());
		}
	}

	/**
	 * @param root
	 * @param targetRoot
	 * @param scNewOpDefs
	 * @param operators
	 * @param monitor
	 */
	private void commitOperators(ITheoryRoot root, ISCTheoryRoot targetRoot,
			ISCNewOperatorDefinition[] scNewOpDefs,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) 
	throws CoreException{
		int index = 0;

		for (int i = 0; i < newOpDefs.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scNewOpDefs[i] = createSCNewOpDef(targetRoot, index++, labelSymbolInfos[i],
						newOpDefs[i], monitor);
			}
		}
		
	}

	private static final String OP_NAME_PREFIX = "NewOP";
	
	/**
	 * @param targetRoot
	 * @param i
	 * @param iLabelSymbolInfo
	 * @param iNewOperatorDefinition
	 * @param monitor
	 * @return
	 */
	private ISCNewOperatorDefinition createSCNewOpDef(ISCTheoryRoot targetRoot,
			int index, ILabelSymbolInfo symbolInfo,
			INewOperatorDefinition iNewOperatorDefinition,
			IProgressMonitor monitor) throws CoreException {
		
		ILabeledElement scNewOpDef = symbolInfo.createSCElement(targetRoot,
				OP_NAME_PREFIX + index, monitor);
		return (ISCNewOperatorDefinition) scNewOpDef;
	}

	protected ILabelSymbolInfo[] fetchOperators(IRodinFile theoryFile,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		String theoryName = theoryFile.getElementName();
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
	

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		newOpDefs = root.getNewOperatorDefinitions();
		factory = repository.getFormulaFactory();
		globalTypeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IdentifierSymbolTable) repository.getState(IdentifierSymbolTable.STATE_TYPE);
		
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		repository.setTypeEnvironment(globalTypeEnvironment);
		identifierSymbolTable = null;
		factory = null;
		globalTypeEnvironment = null;
		newOpDefs = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected AbstractTheoryLabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (AbstractTheoryLabelSymbolTable) 
					repository.getState(OperatorLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalOperator(symbol, true, element,
				component);
	}

}
