package ac.soton.eventb.ruleBase.theory.core.utils;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.sc.ParseProblem;
import org.eventb.core.sc.SCModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class TheoryUtils {
	
	private final static Object[] NO_OBJECT = new Object[0];

	/**
	 * <p>A utility to check if an object is present in an array of objects. This method uses <code>Object.equals(Object)</code></p>
	 * @param objs the container array of objects
	 * @param o the object to check
	 * @return whether <code>o</code> is in <code>objs</code>
	 */
	public static boolean contains(Object[] objs, Object o) {
		for (Object obj : objs) {
			if (obj.equals(o))
				return true;
		}
		return false;
	}
	/**
	 * <p> Utility to check whether <code>objs</code> contains all of <code>os</code>.
	 * </p>
	 * @param objs the container array of objects
	 * @param os the array of objects
	 * @return whether <code>objs</code> contains all of <code>os</code>.
	 */
	public static boolean subset(Object[] objs, Object[] os) {
		for (Object o : os) {
			if (!contains(objs, o))
				return false;
		}
		return true;
	}

	/**
	 * <p> A utility to issue problem markers related to AST problems.</p>
	 * @param element
	 * @param attributeType
	 * @param result
	 * @param module
	 * @return whether an error has been issued
	 * @throws RodinDBException
	 */
	public static boolean issueASTProblemMarkers(IInternalElement element,
			IAttributeType.String attributeType, IResult result, SCModule module)
			throws RodinDBException {
		boolean errorIssued = false;
		for (ASTProblem parserProblem : result.getProblems()) {
			SourceLocation location = parserProblem.getSourceLocation();
			ProblemKind problemKind = parserProblem.getMessage();
			Object[] args = parserProblem.getArgs();

			IRodinProblem problem;
			Object[] objects; // parameters for the marker

			switch (problemKind) {

			case FreeIdentifierHasBoundOccurences:
				problem = ParseProblem.FreeIdentifierHasBoundOccurencesWarning;
				objects = new Object[] { args[0] };
				break;

			case BoundIdentifierHasFreeOccurences:
				// ignore
				// this is just the symmetric message to
				// FreeIdentifierHasBoundOccurences
				continue;

			case BoundIdentifierIsAlreadyBound:
				problem = ParseProblem.BoundIdentifierIsAlreadyBoundWarning;
				objects = new Object[] { args[0] };
				break;

			case BoundIdentifierIndexOutOfBounds:
				// internal error
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
				break;

			case Circularity:
				problem = ParseProblem.CircularityError;
				objects = NO_OBJECT;
				break;

			case InvalidTypeExpression:
				// internal error
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
				break;

			case LexerError:
				problem = ParseProblem.LexerError;
				objects = new Object[] { args[0] };
				break;

			case LexerException:
				// internal error
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
				break;

			case ParserException:
				// internal error
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
				break;

			case SyntaxError:

				// TODO: prepare detailed error messages "args[0]" obtained from
				// the parser for
				// internationalisation

				problem = ParseProblem.SyntaxError;
				objects = new Object[] { args[0] };
				break;

			case TypeCheckFailure:
				problem = ParseProblem.TypeCheckError;
				objects = NO_OBJECT;
				break;

			case TypesDoNotMatch:
				problem = ParseProblem.TypesDoNotMatchError;
				objects = new Object[] { args[0], args[1] };
				break;

			case TypeUnknown:
				problem = ParseProblem.TypeUnknownError;
				objects = NO_OBJECT;
				break;

			case MinusAppliedToSet:
				problem = ParseProblem.MinusAppliedToSetError;
				objects = NO_OBJECT;
				break;

			case MulAppliedToSet:
				problem = ParseProblem.MulAppliedToSetError;
				objects = NO_OBJECT;
				break;

			default:

				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;

				break;
			}
			if (location == null) {
				module.createProblemMarker(element, attributeType, problem, objects);
			} else {
				module.createProblemMarker(element, attributeType,
						location.getStart(), location.getEnd(), problem,
						objects);
			}

			errorIssued |= problem.getSeverity() == IMarker.SEVERITY_ERROR;
		}

		return errorIssued;
	}
	
	/**
	 * Creates a new Rodin database exception with the given message and message arguments.
	 * <p>
	 * The created database exception just wraps up a core exception created
	 * with {@link #newCoreException(String)}.
	 * </p>
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 *            Should be one of the messages defined in the {@link Messages}
	 *            class
	 *            
	 * @param args
	 * 			  parameters to bind with the message
	 *            
	 *  @see #newCoreException(String)
	 */
	public static RodinDBException newRodinDBException(String message,
			Object... args) {
		
		return newRodinDBException(Messages.bind(message, args));
	}
	
	/**
	 * <p>Calculates the difference between <code>main</code> and <code>toCheck</code>.</p>
	 * @param main
	 * @param toCheck
	 * @return <code>main \ toCheck</code>
	 */
	public static Object[] diff(Object[] main, Object[] toCheck){
		ArrayList<Object> diff = new ArrayList<Object>(); 
		for(Object o : toCheck){
			if (!contains(main, o)){
				diff.add(o);
			}
		}
		return diff.toArray();
	}
	/**
	 * 
	 * @param <T>
	 * @param origin
	 * @return the cast array list of <code>T</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> castArray(Object[] origin){
		ArrayList<T> cast = new ArrayList<T>();
		for(Object o : origin){
			cast.add((T)o);
		}
		return cast;
	}
	
	/**
	 * <p> Facility to merge two arrays of objects. No effort is made to check for duplicate objects by equality.</p>
	 * @param os1
	 * @param os2
	 * @return the merged array
	 */
	public static Object[] merge(Object[] os1, Object[] os2){
		Object os[] = new Object[os1.length + os2.length];
		int i = 0;
		for (Object o : os1){
			os[i] = o;
			i++;
		}
		for (Object o : os2){
			os[i]= o;
			i++;
		}
		return os;
	}

	
	/**
	 * <p>Facility to parse theory formulas <code>Expression</code> or <code>Predicate</code></p>
	 * @param element
	 * @param toParse
	 * @param factory
	 * @param typeEnvironment
	 * @param attributeType
	 * @param repository
	 * @param module
	 * @return the parsed formula
	 * @throws CoreException
	 */
	public static Formula<?> parseFormula(IInternalElement element, 
			String toParse,
			FormulaFactory factory,
			ITypeEnvironment typeEnvironment,
			IAttributeType attributeType,
			ISCStateRepository repository,
			SCModule module)
	throws CoreException{
		assert attributeType instanceof IAttributeType.String;
		IAttributeType.String attrType = (IAttributeType.String) attributeType;
		Formula<?> finalForm = null;
		IParseResult expResult = null;
		IParseResult predResult = null;
		boolean isExpression = true;
		expResult = factory
				.parseExpression(toParse, LanguageVersion.V2, element);
		if (expResult.hasProblem()) {
			isExpression = false;
			predResult = factory.parsePredicate(toParse, LanguageVersion.V2,
					element);
			if (TheoryUtils.issueASTProblemMarkers(element, attrType,
					predResult, module)) {
				// TODO only error messages of predicate are issued.
				return null;
			}
		}
		if (isExpression) {
			finalForm = expResult.getParsedExpression();
		} else {
			finalForm = predResult.getParsedPredicate();
		}
		ITypeCheckResult tcResult = finalForm.typeCheck(typeEnvironment);
		if (TheoryUtils.issueASTProblemMarkers(element, attrType,
				tcResult, module)) {
			return null;
		}
		return finalForm;
	}
}
