package ac.soton.eventb.ruleBase.theory.deploy.retriever.basis;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p>A simple XML validator for deployed theory files.</p>
 * 
 * <p>This is internal API.</p>
 * @author maamria
 *
 */
class DTheoryValidator {

	private static class BooleanWrapper{
		private boolean value ;
		
		public BooleanWrapper(boolean value){
			this.value = value;
		}
		
		public void setValue(boolean newVal){
			value = newVal;
		}
		
		public boolean getValue(){
			return value;
		}
	}
	
	public static class ValidationResult{
		Document document;
		boolean hasProblems;
		
		public ValidationResult(Document document, boolean  hasProblems){
			this.document = document;
			this.hasProblems = hasProblems;
		}
		
		public Document getDocument() {
			return document;
		}

		
		public boolean hasSeriousProblems(){
			return hasProblems;
		}	
	}
	/**
	 * <p>Validates the given theory file with respect to theories DTD.</p>
	 * @param theoryFile
	 * @return the validation result
	 */
	public static ValidationResult validateTheoryFile(File theoryFile){
		Document theoryDoc = null;
		final BooleanWrapper hasProblems = new BooleanWrapper(false);
		try {
			DocumentBuilderFactory theoryFactory = DocumentBuilderFactory
					.newInstance();
			theoryFactory.setValidating(true);
			DocumentBuilder builder = theoryFactory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
				
				public void error(SAXParseException exception)
						throws SAXException {
					hasProblems.setValue(true);
				}

				
				public void fatalError(SAXParseException exception)
						throws SAXException {
					hasProblems.setValue(true);
				}

				
				public void warning(SAXParseException exception)
						throws SAXException {
					// do nothing
				}

			});
			theoryDoc = builder.parse(theoryFile);
		}  catch (ParserConfigurationException e) {
			theoryDoc = null;
		} catch (SAXException e) {
			theoryDoc = null;
		} catch (IOException e) {
			theoryDoc = null;
		}
		 return new ValidationResult(theoryDoc, hasProblems.getValue());
	}
}
