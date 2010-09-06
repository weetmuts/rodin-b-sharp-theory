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
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.sc.states.AbstractTheoryLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class ProofRulesBlockModule extends LabeledElementModule{

	IModuleType<ProofRulesBlockModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".proofRulesBlockModule");
	
	private static final int PRB_IDENT_SYMTAB_SIZE = 2047;
	
	private IProofRulesBlock[] rulesBlocks;
	private ITypeEnvironment globalTypeEnvironment;
	private FormulaFactory factory;
	private IIdentifierSymbolTable identifierSymbolTable;
	private TheoryAccuracyInfo theoryAccuracyInfo;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		monitor.subTask(Messages.progress_TheoryProofRules);
		monitor.worked(1);
		ILabelSymbolInfo[] blocks = fetchBlocks(file, repository, monitor);
		ISCProofRulesBlock[] scProofRulesBlocks = new ISCProofRulesBlock[blocks.length];
		commitBlocks(root, targetRoot, scProofRulesBlocks, blocks, monitor);
		processBlocks(scProofRulesBlocks, repository, blocks, monitor);
	}
	
	private void processBlocks(ISCProofRulesBlock[] scProofRulesBlocks,
			ISCStateRepository repository, ILabelSymbolInfo[] blocks,
			IProgressMonitor monitor) throws CoreException{
		factory = repository.getFormulaFactory();
		globalTypeEnvironment = repository.getTypeEnvironment();
		for (int i = 0; i < rulesBlocks.length; i++) {

			if (blocks[i] != null && !blocks[i].hasError()) {
				
				IIdentifierSymbolTable stackedIdentSymbolTable =
					new StackedIdentifierSymbolTable(
							identifierSymbolTable, PRB_IDENT_SYMTAB_SIZE,
							factory);
				
				repository.setState(stackedIdentSymbolTable);
				
				ITypeEnvironment opTypeEnvironment = factory.makeTypeEnvironment();
				opTypeEnvironment.addAll(globalTypeEnvironment);
				repository.setTypeEnvironment(opTypeEnvironment);
				
				initProcessorModules(rulesBlocks[i], repository, null);
				processModules(rulesBlocks[i], scProofRulesBlocks[i], repository, monitor);
				endProcessorModules(rulesBlocks[i], repository, null);
			}
			else {
				theoryAccuracyInfo.setNotAccurate();
			}
			repository.setTypeEnvironment(globalTypeEnvironment);
			monitor.worked(1);
		}

		
	}
	
	/**
	 * @param root
	 * @param targetRoot
	 * @param scNewOpDefs
	 * @param operators
	 * @param monitor
	 */
	private void commitBlocks(ITheoryRoot root, ISCTheoryRoot targetRoot,
			ISCProofRulesBlock[] scProofRulesBlocks,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) 
	throws CoreException{
		int index = 0;

		for (int i = 0; i < rulesBlocks.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scProofRulesBlocks[i] = createSCBlock(targetRoot, index++, labelSymbolInfos[i],
						rulesBlocks[i], monitor);
			}
		}
		
	}
	
	private static final String PRB_NAME_PREFIX = "PRB";
	
	private ISCProofRulesBlock createSCBlock(ISCTheoryRoot targetRoot,
			int index, ILabelSymbolInfo symbolInfo,
			IProofRulesBlock proofRulesBlock,
			IProgressMonitor monitor) throws CoreException {
		
		ILabeledElement scProofRulesBlock = symbolInfo.createSCElement(targetRoot,
				PRB_NAME_PREFIX + index, monitor);
		return (ISCProofRulesBlock) scProofRulesBlock;
	}

	protected ILabelSymbolInfo[] fetchBlocks(IRodinFile theoryFile,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		String theoryName = theoryFile.getElementName();
		initFilterModules(repository, monitor);
		ILabelSymbolInfo[] labelSymbolInfos = new ILabelSymbolInfo[rulesBlocks.length];
		for(int i = 0 ; i < rulesBlocks.length; i++){
			IProofRulesBlock block = rulesBlocks[i];
			labelSymbolInfos[i] = fetchLabel(block, theoryName, monitor);
			if(labelSymbolInfos[i] == null){
				continue;
			}
			if (!filterModules(block, repository, null)) {

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
		rulesBlocks = root.getProofRulesBlocks();
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
		rulesBlocks = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	protected AbstractTheoryLabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (AbstractTheoryLabelSymbolTable) 
				repository.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRulesBlock(symbol, 
				true, element, component);
	}

}
