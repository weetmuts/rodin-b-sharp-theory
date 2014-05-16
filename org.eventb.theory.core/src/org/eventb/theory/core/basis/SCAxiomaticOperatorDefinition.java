package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.HAS_ERROR_ATTRIBUTE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class SCAxiomaticOperatorDefinition extends TheoryElement implements ISCAxiomaticOperatorDefinition {

	public SCAxiomaticOperatorDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasHasErrorAttribute() throws RodinDBException {
		return hasAttribute(HAS_ERROR_ATTRIBUTE);
	}

	@Override
	public boolean hasError() throws RodinDBException {
		return getAttributeValue(HAS_ERROR_ATTRIBUTE);
	}

	@Override
	public void setHasError(boolean hasError, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(HAS_ERROR_ATTRIBUTE, hasError, monitor);
	}
	
	private Predicate getPredicate(FormulaFactory factory) throws CoreException {
		String contents = getPredicateString();
		final IRodinElement source;
		if (hasAttribute(EventBAttributes.SOURCE_ATTRIBUTE)) {
			source = getAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE);
		} else {
			source = null;
		}
		IParseResult parserResult = factory.parsePredicate(contents, source);
		if (parserResult.getProblems().size() != 0) {
			throw Util.newCoreException(
					Messages.database_SCPredicateParseFailure,
					this
			);
		}
		Predicate result = parserResult.getParsedPredicate();
		return result;
	}

	@Override
	public Predicate getPredicate(ITypeEnvironment typenv) throws CoreException {
		Predicate result = getPredicate(typenv.getFormulaFactory());
		ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (! tcResult.isSuccess())  {
			throw Util.newCoreException(
					Messages.database_SCPredicateTCFailure,
					this
			);
		}
		assert result.isTypeChecked();
		return result;
	}
	
	@Override
	public void setPredicate(Predicate predicate, IProgressMonitor monitor) throws RodinDBException {
		setPredicateString(predicate.toStringWithTypes(), monitor);
	}
	
	@Override
	public ISCOperatorArgument getOperatorArgument(String name) {
		return getInternalElement(ISCOperatorArgument.ELEMENT_TYPE, name);
	}

	@Override
	public ISCOperatorArgument[] getOperatorArguments() throws RodinDBException {
		return getChildrenOfType(ISCOperatorArgument.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
