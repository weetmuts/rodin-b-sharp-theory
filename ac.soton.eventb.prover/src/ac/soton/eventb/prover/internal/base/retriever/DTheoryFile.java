package ac.soton.eventb.prover.internal.base.retriever;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ac.soton.eventb.prover.internal.base.IDRewriteRule;
import ac.soton.eventb.prover.internal.base.IDRuleRightHandSide;
import ac.soton.eventb.prover.internal.base.IDTheoryFile;
import ac.soton.eventb.prover.internal.base.retriever.DTheoryValidator.ValidationResult;
import ac.soton.eventb.prover.utils.ProverUtilities;
/**
 * DONE FIXME TODO Cache
 * @author maamria
 *
 */
public class DTheoryFile implements IDTheoryFile{
	
	/**
	 * <p> Utility enumeration used to distinguish the type of information XML attributes hold.</p>
	 * @author maamria
	 *
	 */
	protected static enum InfoType{BooleanType, 
		FormulaType, 
		PredicateType, 
		StringType, 
		TypeOfExpType}
	private List<IDRewriteRule> expRewriteRules;
	private FormulaFactory factory;
	private List<IDRewriteRule> predRewriteRules;
	
	private String theoryName;
	
	private ITypeEnvironment typeEnvironment;
	
	/**
	 * <p>Constructs a deployed theory object with the given name (including extension).</p>
	 * @param theoryName with extension .thy
	 * @param factory
	 */
	public DTheoryFile(String theoryName, FormulaFactory factory){
		this.theoryName = theoryName;
		this.factory = factory;
	}
	
	
	public List<IDRewriteRule> getExpressionRewriteRules() {
		return expRewriteRules;
	}

	
	public ITypeEnvironment getGloablTypeEnvironment() {
		return typeEnvironment.clone();
	};
	
	
	public List<IDRewriteRule> getPredicateRewriteRules() {
		return predRewriteRules;
	}
	
	
	public String getTheoryName() {
		return theoryName;
	}
	/**
	 * <p>With best-effort semantics, this method gets as much valid data as possible.</p>
	 * <p>This method is not intended to be called by clients.</p>
	 * @param path the deployment path where theories reside
	 * @return whether the theory has been successfully loaded.
	 */
	public boolean loadTheory(String path) 
	{
		File file = new File(
				path+
				System.getProperty("file.separator")+
				theoryName);
		if(!file.exists()){
			return false;
		}
		ValidationResult validationReport = DTheoryValidator.validateTheoryFile(file);
		if(validationReport.getDocument() == null){
			return false;
		}
		if(validationReport.getDocument() != null && 
				validationReport.hasSeriousProblems()){
			return false;
		}
		Document theoryDoc = validationReport.getDocument();
		Element theoryDocElmnt = theoryDoc.getDocumentElement();
		if(theoryDocElmnt == null){
			return false;
		}
		
		NodeList setsList = theoryDoc.getElementsByTagName(Utilities.META_SET);
		NodeList varsList = theoryDoc.getElementsByTagName(Utilities.META_VARIABLE);
		NodeList rewRulesList = theoryDoc.getElementsByTagName(Utilities.REWRITE_RULE);
		constructTypeEnv(setsList, varsList);
		constructRewriteRules(rewRulesList);
		return true;
	}
	
	private void constructRewriteRules(NodeList rewRulesList) {
		if(rewRulesList == null || rewRulesList.getLength()==0){
			return;
		}
		List<IDRewriteRule> expRules =  new ArrayList<IDRewriteRule>();
		List<IDRewriteRule> predRules =  new ArrayList<IDRewriteRule>();
		for(int i = 0; i < rewRulesList.getLength(); i++){
			Node rule = rewRulesList.item(i);
			if(!rule.hasAttributes()){
				continue;
			}
			NamedNodeMap map = rule.getAttributes();
			InfoHolder holder = getAttributeValue(InfoType.StringType, Utilities.NAME, map, factory);
			if(holder == null){
				continue;
			}
			String nameAttr = holder.getString();
			
			holder = getAttributeValue(InfoType.BooleanType, Utilities.AUTOMATIC, map, factory);
			if(holder == null){
				continue;
			}
			boolean isAuto = holder.getBoolean();
			
			holder = getAttributeValue(InfoType.BooleanType, Utilities.INTERACTIVE, map, factory);
			if(holder == null){
				continue;
			}
			boolean isInter = holder.getBoolean();
			
			holder = getAttributeValue(InfoType.BooleanType, Utilities.COMPLETE, map, factory);
			if(holder == null){
				continue;
			}
			boolean isComplete = holder.getBoolean();
			
			holder = getAttributeValue(InfoType.BooleanType, Utilities.SOUND, map, factory);
			if(holder == null){
				continue;
			}
			boolean isSound = holder.getBoolean();
			// load sound rules only
			if(!isSound)
				continue;
			//
			holder = getAttributeValue(InfoType.StringType, Utilities.TOOL_TIP, map, factory);
			if(holder == null){
				continue;
			}
			String toolTip = holder.getString();
			//
			
			//
			holder = getAttributeValue(InfoType.StringType, Utilities.DESC, map, factory);
			if(holder == null){
				continue;
			}
			String desc = holder.getString();
			//
			holder = getAttributeValue(InfoType.FormulaType, Utilities.LHS, map, factory);
			if(holder == null){
				continue;
			}
			Formula<?> lhsFormula = holder.getFormula();
			// TODO check here
			lhsFormula.typeCheck(typeEnvironment);
			
			List<IDRuleRightHandSide> ruleRHSs = constructRuleRHSs(rule);
			
			DRewriteRule dRule = new DRewriteRule(nameAttr, theoryName,
					lhsFormula, 
					Collections.unmodifiableList(ruleRHSs), 
					isAuto, isInter, isComplete, isSound, toolTip, desc,
					typeEnvironment);
			if(dRule.isExpression()){
				expRules.add(dRule);
			}
			else {
				predRules.add(dRule);
			}
			
		}
		this.expRewriteRules = Collections.unmodifiableList(expRules);
		this.predRewriteRules = Collections.unmodifiableList(predRules);
	}
	
	// TODO Log
	private List<IDRuleRightHandSide> constructRuleRHSs(Node ruleNode){
		NodeList rhsNodeList = ruleNode.getChildNodes();
		List<IDRuleRightHandSide> ruleRHSs =  new ArrayList<IDRuleRightHandSide>();
		for(int i= 0 ; i < rhsNodeList.getLength(); i++){
			Node rhsNode = rhsNodeList.item(i);
			boolean hasAttr = rhsNode.hasAttributes();
			if(!hasAttr){
				continue;
			}
			NamedNodeMap map = rhsNode.getAttributes();
			
			InfoHolder holder = getAttributeValue(InfoType.StringType, Utilities.NAME, map, factory);
			if(holder == null){
				continue;
			}
			String nameAttr = holder.getString();
			
			holder = getAttributeValue(InfoType.PredicateType, Utilities.PREDICATE, map, factory);
			if(holder == null){
				continue;
			}
			Predicate predAttr = holder.getPredicate();
			// TODO check here
			predAttr.typeCheck(typeEnvironment);
			
			holder = getAttributeValue(InfoType.FormulaType, Utilities.RHS, map, factory);
			if(holder == null){
				continue;
			}
			Formula<?> rhsAttr = holder.getFormula();
			// TODO check here
			rhsAttr.typeCheck(typeEnvironment);
			
			ruleRHSs.add(new DRuleRightHandSide(nameAttr, predAttr, rhsAttr));
			
		}
		return ruleRHSs;
	}
	private void constructTypeEnv(NodeList setsList, NodeList varsList){
		typeEnvironment = factory.makeTypeEnvironment();
		if (setsList != null) {
			for (int i = 0; i < setsList.getLength(); i++) {
				Node set = setsList.item(i);
				NamedNodeMap map = set.getAttributes();
				InfoHolder holder = getAttributeValue(InfoType.StringType, 
						Utilities.IDENTIFIER, 
						map, factory);
				if(holder == null)
					continue;
				String setName = holder.getString();
				
				typeEnvironment.addGivenSet(setName);
			}
		}
		if (varsList != null) {
			for (int k = 0; k < varsList.getLength(); k++) {
				Node node = varsList.item(k);
				NamedNodeMap map = node.getAttributes();
				
				InfoHolder holder = getAttributeValue(InfoType.StringType, 
						Utilities.IDENTIFIER, 
						map, factory);
				if(holder == null)
					continue;
				String varName = holder.getString();
				
				holder = getAttributeValue(InfoType.TypeOfExpType, 
						Utilities.TYPE, 
						map, factory);
				if(holder == null)
					continue;
				Type parType = holder.getType();
				
				typeEnvironment.addName(varName, parType);
			}
		}
	}
	/**
	 * <p>Returns an <code>InfoHolder</code> objects with the value of the given attribute name in <code>attributesMap</code>.</p>
	 * @param type
	 * @param attribute
	 * @param attributesMap
	 * @param factory
	 * @return
	 */
	private static InfoHolder getAttributeValue(InfoType type, String attribute, 
			NamedNodeMap attributesMap, FormulaFactory factory){
		InfoHolder holder = null;
		if(type == InfoType.StringType){
			Node node = attributesMap.getNamedItem(attribute);
			if (node == null) {
				return null;
			}
			String str = node.getNodeValue();
			holder= new InfoHolder(str);
		}
		else if(type == InfoType.PredicateType){
			Node node = attributesMap.getNamedItem(attribute);
			if(node ==  null){
				return null;
			}
			String predAttr = node.getNodeValue();
			IParseResult predParseRes = factory.parsePredicate(predAttr, V2, null);
			if(predParseRes.hasProblem()){
				return null;
			}
			Predicate p = predParseRes.getParsedPredicate();
			holder = new InfoHolder(p);
		}
		else if(type == InfoType.TypeOfExpType){
			Node node = attributesMap.getNamedItem(attribute);
			if (node == null) {
				return null;
			}
			String parStrType = node.getNodeValue();
			IParseResult typeParseRes = factory.parseType(parStrType, V2);
			Type expType = null;
			if (!typeParseRes.hasProblem()) {
				expType = typeParseRes.getParsedType();
			} else {
				return null;
			}
			holder = new InfoHolder(expType);
		}
		else if(type == InfoType.BooleanType){
			Node node = attributesMap.getNamedItem(attribute);
			if(node == null){
				return null;
			}
			String attr = node.getNodeValue();
			boolean bValue = attr.equals("true");
			holder = new InfoHolder(bValue);
		}
		else {
			Node node = attributesMap.getNamedItem(attribute);
			if(node == null){
				return null;
			}
			String attr =node.getNodeValue();
			Formula<?> form = ProverUtilities.parseFormula(attr, factory);
			if(form ==  null){
				return null;
			}	
			holder = new InfoHolder(form);
		}
		return holder;
	}

	public boolean isEmpty() {
		return expRewriteRules==null && predRewriteRules==null;
	}

}
/**
 * <p>Utility class whose objects are used to store information retrieved from XML elements in a generic fashion.</p>
 * @author maamria
 *
 */
class InfoHolder{
	boolean bValue = false;
	Formula<?> fValue = null;
	Predicate pValue = null;
	String sValue = null;
	Type tValue = null;
	
	public InfoHolder(boolean bValue){
		this.bValue = bValue;
	}
	
	public InfoHolder(Formula<?> fValue){
		this.fValue = fValue;
	}
	
	public InfoHolder(Predicate pValue){
		this.pValue = pValue;
	}
	
	public InfoHolder(String sValue){
		this.sValue = sValue;
	}
	
	public InfoHolder(Type tValue){
		this.tValue = tValue;
	}
	
	boolean getBoolean(){
		return bValue;
	}
	Formula<?> getFormula(){
		return fValue;
	}
	Predicate getPredicate(){
		return pValue;
	}
	String getString(){
		return sValue;
	}
	Type getType(){
		return tValue;
	}
}