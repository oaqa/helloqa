package edu.cmu.lti.oaqa.openqa.dso.extractor;

import info.ephyra.nlp.StanfordParser;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.rdfanswergenerator.RDFAnswerGenerator;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.FieldAnalyser;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.tregex.TreeMatcher.TRegexTreeReaderFactory;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class CandidateExtractorByRDF {
	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());
	
	private String sparqlServer;

	private String indexLocation;

	private boolean isServer;
	
	private static TRegexTreeReaderFactory trf;

	static {
		initialize();
	}
	
	public CandidateExtractorByRDF(JSONObject config) {
		try {
			sparqlServer = config.getString("sparqlServer");
			indexLocation = config.getString("indexLocation");
			isServer = config.getBoolean("isServer");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getAnswerType(String line){
//		if(line.contains("Where")) { //where did massacre happen
//			  return "location";
//			 } else if (line.contains("When")) {
//			  return "when-happened-date";
//			 } else if(line.contains("perpetrators")) {
//			  return "perpetrators";
//			 } else if(line.contains("responsible")) {
//			  return "perpetrators";
//			 } else if(line.contains("mastermind")) {
//			  return "perpetrators";
//			 } else if(line.contains("injured") || line.contains("wounded")) {
//			  return "injuries";
//			 }else if(line.contains("attack type")) {
//			  return "type";
//			 } else if(line.contains("target(s)")) {
//			  return "target";
//			 } else if(line.contains("dead") || line.contains("killed")){
//				 return "fatalities";
//			 }
//		return null;
		
		String type = FieldAnalyser.classify(line);
		
		if(type.equals("IC_TYPES.PERPETRATOR"))
			return "perpetrators";
		else if(type.equals("IC_TYPES.NUMBER_INJURED"))
			return "injuries";
		else if(type.equals("IC_TYPES.DATE"))
			return "when-happened-date";
		else if(type.equals("IC_TYPES.LOCATION"))
			return "location";
		else if(type.equals("IC_TYPES.ATTACK_TYPE"))
			return "type";
		else if(type.equals("IC_TYPES.TARGET"))
			return "target";
		
		return null;
	}
	
	public String getAnswerForQuestion(String question){
		String semanticTarget = getAnswerType(question);
		String targetEvent = (getTargetEvent(question));
		if(semanticTarget == null || targetEvent.trim().equals(""))
			return null;
		else{
			LOGGER.info("Target Event: " + targetEvent);
			LOGGER.info("Semantic Target: " + semanticTarget );
			RDFAnswerGenerator rdfAG = new RDFAnswerGenerator(sparqlServer, isServer, indexLocation, targetEvent);
			LOGGER.info("Mapping: " + Arrays.toString(rdfAG.semanticTargetMap.get(semanticTarget)));
			List<String> propertiesForTarget = rdfAG.getPropertiesForTarget(semanticTarget);
			if(propertiesForTarget != null && !propertiesForTarget.isEmpty()){
				LOGGER.info("RDF Answer: " + propertiesForTarget.get(0));
				return propertiesForTarget.get(0);
			}else{
				return null;
			}
		}
	}

	public static void initialize(){
		try {
			StanfordParser.initialize();
			trf = new TRegexTreeReaderFactory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getTargetEvent(String question){
		StringBuffer buffer = new StringBuffer();
		try{
			String parse = StanfordParser.parse(question);
			TreeReader newTreeReader = trf.newTreeReader(new StringReader(parse));
			Tree tree = newTreeReader.readTree();
			TregexPattern pattern = TregexPattern.compile("NP < NNP");
			TregexMatcher matcher = pattern.matcher(tree);
			while(matcher.findNextMatchingNode()){
				Tree t = matcher.getMatch();
				List<Tree> leaves = t.getLeaves();
				for(Tree l : leaves){
					buffer.append(l.nodeString() + " ");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return buffer.toString().trim();
	}
	
	
	public static void main(String args[]) throws Exception{
//		String question = "When did Guildford pub bombings happen?";
//		CandidateExtractorByRDF ceRDF = new CandidateExtractorByRDF();
//		System.out.println(ceRDF.getAnswerForQuestion(question));
	}
}
