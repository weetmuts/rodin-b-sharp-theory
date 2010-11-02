package org.eventb.theory.internal.core.sc;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IDatatypeTable.ERROR_CODE;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @since 1.0
 * @author maamria
 *
 */
public abstract class DatatypeModule extends SCProcessorModule{

	/**
	 * Makes a free identifier from the given name.
	 * <p>
	 * Returns <code>null</code> if one of the following happens:
	 * <li>the given name cannot be parsed as an expression</li>
	 * <li>the given name is not a valid free identifier name</li>
	 * <li>the given name is primed and prime is not allowed</li>
	 * <li>the given name contains leading or trailing spaces</li>
	 * For the first problem encountered, if any, a problem marker is added and
	 * <code>null</code> is returned immediately.
	 * </p>
	 * 
	 * @param name
	 *            a name to parse
	 * @param element
	 *            the element associated the the name
	 * @param attrType
	 *            the attribute type of the element where the name is located
	 * @param factory
	 *            a formula factory to parse the name
	 * @param display
	 *            a marker display for problem markers
	 * @param primeAllowed
	 *            <code>true</code> if primed names are allowed,
	 *            <code>false</code> otherwise
	 * @return a free identifier with the given name, or <code>null</code> if
	 *         there was a problem making the identifier
	 * @throws RodinDBException
	 *             if there is a problem accessing the Rodin database
	 */
	protected FreeIdentifier parseIdentifier(String name,
			IInternalElement element, IAttributeType.String attrType,
			FormulaFactory factory)
			throws RodinDBException {
	
		IParseResult pResult = factory.parseExpression(name, V2, element);
		Expression expr = pResult.getParsedExpression();
		if (pResult.hasProblem() || !(expr instanceof FreeIdentifier)) {
			createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		FreeIdentifier identifier = (FreeIdentifier) expr;
		if (identifier.isPrimed()) {
			createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		if (!name.equals(identifier.getName())) {
			createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierSpacesError, name);
			return null;
		}
		return identifier;
	}

	/**
	 * Creates a statically checked identifier element of the type
	 * <code>T</code>.
	 * 
	 * @param <T>
	 *            the type of the element
	 * @param type
	 *            intenal element type
	 * @param source
	 *            the source element
	 * @param parent
	 *            the parent rodin element
	 * @param monitor
	 *            the progress monitor
	 * @return the statically checked identifier element
	 * @throws CoreException
	 */
	protected <T extends IIdentifierElement> T createSCIdentifierElement(
			IInternalElementType<T> type, IIdentifierElement source,
			IInternalElement parent, IProgressMonitor monitor)
			throws CoreException {
		T scElement = parent.getInternalElement(type,
				source.getIdentifierString());
		scElement.create(null, monitor);
		return scElement;
	}

	/**
	 * Returns appropriate rodin problem for <code>code</code>. The error codes
	 * correspond to problems of name clashes of datatype related identifiers.
	 * 
	 * @param code
	 *            the error code
	 * @return the rodin problem
	 */
	protected IRodinProblem getAppropriateProblemForCode(ERROR_CODE code) {
		switch (code) {
		case NAME_IS_A_CONSTRUCTOR:
			return TheoryGraphProblem.IdenIsAConsNameError;
		case NAME_IS_A_DATATYPE:
			return TheoryGraphProblem.IdenIsADatatypeNameError;
		case NAME_IS_A_DESTRUCTOR:
			return TheoryGraphProblem.IdenIsADesNameError;
		}
		return null;
	}

}
