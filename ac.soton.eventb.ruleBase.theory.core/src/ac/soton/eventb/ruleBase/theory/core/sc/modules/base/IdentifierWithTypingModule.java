package ac.soton.eventb.ruleBase.theory.core.sc.modules.base;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ITypingElement;
import ac.soton.eventb.ruleBase.theory.core.TheoryAttributes;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IGivenSets;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolTable;
import ac.soton.eventb.ruleBase.theory.core.utils.TheoryUtils;

/**
 * <p> This class should be subclassed by SC processor modules
 * acting on internal elements that are instances of both 
 * <code>ITypingElement</code> and 
 * <code>IIdentifierElement</code>.</p>
 * 
 * @author maamria
 *
 */

public abstract class IdentifierWithTypingModule extends SCProcessorModule{

	protected FormulaFactory factory;

	protected IGivenSets givenSets;

	protected IIdentifierSymbolTable identifierSymbolTable;
	
	protected ITypeEnvironment typeEnvironment;
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		identifierSymbolTable = null;
		typeEnvironment = null;
		givenSets = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = FormulaFactory.getDefault();
		typeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		givenSets = (IGivenSets) repository.getState(IGivenSets.STATE_TYPE);
	}
	
	protected abstract IIdentifierSymbolInfo createIdentifierSymbolInfo(
			String name, IIdentifierElement element);
	
	/**
	 * Fetch identifiers from component, parse them and add them to the symbol
	 * table.
	 * 
	 * @param elements
	 *            the identifier elements to fetch
	 * @param target
	 *            the target static checked container
	 * @param repository
	 *            the state repository
	 * @throws CoreException
	 *             if there was a problem accessing the symbol table
	 */
	protected void fetchSymbolsWithTheirTypes(IIdentifierElement[] elements,
			IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		initFilterModules(repository, null);

		for (IIdentifierElement element : elements) {
		
			FreeIdentifier identifier = parseIdentifier(element, monitor);

			if (identifier == null)
				continue;
			String name = identifier.getName();

			IIdentifierSymbolInfo newSymbolInfo = createIdentifierSymbolInfo(
					name, element);
			newSymbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE,
					element);
			assert element instanceof ITypingElement;
			boolean ok = insertIdentifierSymbol(element, newSymbolInfo) && 
				typeIdentifier(newSymbolInfo, (ITypingElement) element, identifier);
			
			if (!ok || !filterModules(element, repository, null)){
				continue;
			}

			monitor.worked(1);

		}

		endFilterModules(repository, null);
		
	}
	
	
	
	private boolean insertIdentifierSymbol(IIdentifierElement element,
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {

		try {

			identifierSymbolTable.putSymbolInfo(newSymbolInfo);

		} catch (CoreException e) {

			IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
					.getSymbolInfo(newSymbolInfo.getSymbol());

			newSymbolInfo.createConflictMarker(this);

			if (symbolInfo.hasError())
				return false; // do not produce too many error messages

			symbolInfo.createConflictMarker(this);

			if (symbolInfo.isMutable())
				symbolInfo.setError();

			return false;
		}
		return true;
	}

	/**
	 * Parse the identifier element
	 * 
	 * @param element
	 *            the element to be parsed
	 * @return a <code>FreeIdentifier</code> in case of success,
	 *         <code>null</code> otherwise
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	private FreeIdentifier parseIdentifier(IIdentifierElement element,
			IProgressMonitor monitor) throws RodinDBException {

		if (element.hasIdentifierString()) {

			return parseIdentifier(element.getIdentifierString(), element,
					EventBAttributes.IDENTIFIER_ATTRIBUTE);
		} else {

			createProblemMarker(element, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					GraphProblem.IdentifierUndefError);
			return null;
		}
	}

	private FreeIdentifier parseIdentifier(String name,
			IInternalElement element, IAttributeType.String attrType)
			throws RodinDBException {

		IParseResult pResult = factory.parseExpression(name, V2, element);
		Expression expr = pResult.getParsedExpression();
		if (pResult.hasProblem() || !(expr instanceof FreeIdentifier)) {
			createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		FreeIdentifier identifier = (FreeIdentifier) expr;
		if (!name.equals(identifier.getName())) {
			createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierSpacesError, name);
			return null;
		}
		return identifier;
	}
	
	private Type parseTypeExpression(ITypingElement typingElmnt,
			FormulaFactory factory2) 
	throws CoreException{
		IAttributeType.String attributeType = TheoryAttributes.TYPING_ATTRIBUTE;

		if (!typingElmnt.hasTypingString()) {
			createProblemMarker(typingElmnt,
					attributeType,
					GraphProblem.ExpressionUndefError);
			return null;
		}
		String expString = typingElmnt.getTypingString();

		IParseResult parseResult = factory.parseType(expString, V2);

		if (TheoryUtils.issueASTProblemMarkers(typingElmnt, attributeType,
				parseResult, this)) {
			return null;
		}
		Type type = parseResult.getParsedType();
		return type;
	}
	
	private boolean typeIdentifier(IIdentifierSymbolInfo newSymbolInfo,
			ITypingElement typingElmnt , FreeIdentifier identifier)
	throws CoreException{
		Type typeExp =  parseTypeExpression(typingElmnt, factory);
		if(typeExp == null){
			return false;
		}
		FreeIdentifier[] idents = typeExp.toExpression(factory).getFreeIdentifiers();
		for(FreeIdentifier ident : idents){
			boolean allowed = false;
			for(String set: givenSets.getGivenSets()){
				if(ident.getName().equals(set)){
					allowed = true;
					break;
				}
			}
			// just issue that the ident has not been declared
			if(!allowed){
				createProblemMarker(typingElmnt,
						TheoryAttributes.TYPING_ATTRIBUTE, 
						GraphProblem.UndeclaredFreeIdentifierError, 
						ident.getName(), ident.getName());
				return false;
			}
		}
		newSymbolInfo.setType(typeExp);
		// we now augment the type env
		typeEnvironment.addName(newSymbolInfo.getSymbol(), typeExp);
		// add a typing predicate to the variable
		Predicate typinPred = factory.makeRelationalPredicate(Formula.IN, 
				identifier,
				typeExp.toExpression(factory), null);
		newSymbolInfo.setAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE, typinPred.toString());
		return true;
	}
}
