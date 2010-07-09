package ac.soton.eventb.ruleBase.theory.core.basis;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.basis.SCIdentifierElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISCVariable;
import ac.soton.eventb.ruleBase.theory.core.utils.Messages;
import ac.soton.eventb.ruleBase.theory.core.utils.TheoryUtils;

public class SCVariable extends SCIdentifierElement implements ISCVariable {

	public SCVariable(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCVariable> getElementType() {
		return ISCVariable.ELEMENT_TYPE;
	}

	// could not extend SCPredicateElement (Java multi inheritance)
	public Predicate getPredicate(FormulaFactory factory) throws RodinDBException {
		String contents = getPredicateString();
		// no predicate pattern here
		IParseResult parserResult = factory.parsePredicate(contents, V2, null);
		if (parserResult.getProblems().size() != 0) {
			throw TheoryUtils.newRodinDBException(
					Messages.database_SCPredicateParseFailure,
					this
			);
		}
		Predicate result = parserResult.getParsedPredicate();
		return result;
	}

	public Predicate getPredicate(
			FormulaFactory factory, ITypeEnvironment typenv) throws RodinDBException {
		
		Predicate result = getPredicate(factory);
		ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (! tcResult.isSuccess())  {
			throw TheoryUtils.newRodinDBException(
					Messages.database_SCPredicateTCFailure,
					this
			);
		}
		assert result.isTypeChecked();
		return result;
	}
	
	public void setPredicate(Predicate predicate, IProgressMonitor monitor) throws RodinDBException {
		setPredicateString(predicate.toStringWithTypes(), monitor);
	}

	public void setPredicate(Predicate predicate) throws RodinDBException {
		// TODO Auto-generated method stub
		
	}

}
