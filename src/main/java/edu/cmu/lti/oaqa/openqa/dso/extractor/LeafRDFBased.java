package edu.cmu.lti.oaqa.openqa.dso.extractor;

import info.ephyra.nlp.StanfordParser;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.rdfanswergenerator.RDFAnswerGenerator;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.FieldAnalyser;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPattern.TRegexTreeReaderFactory;

public class LeafRDFBased implements ICandidateExtractor{
	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());
	private static String sparqlServer="http://gold.lti.cs.cmu.edu:8891/sparql";
	private String indexLocation= "res/rdf_dbpedia_labels_indri_index";
	private boolean isServer=false;
	private static TRegexTreeReaderFactory trf;

	public LeafRDFBased(SupportingEvidenceArg arg) {
		initialize();
	}

	@Override
	public String getTypeName() {
		return "RDF";
	}

	@Override
	public void initialize() {
		if(trf!=null){
			return;
		}
		try {
			StanfordParser.initialize();
			trf = new TRegexTreeReaderFactory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<AnswerCandidate> getAnswerCandidates(SupportingEvidenceArg arg) {
		String ans=getAnswerForQuestion(arg.getQuestionText());
		List<AnswerCandidate> candidates=new ArrayList<AnswerCandidate>();
		if(ans==null){
			return candidates;
		}
		candidates.add(new AnswerCandidate(ans, new ArrayList<RetrievalResult>()));
		return candidates;
	}
	
	private String getAnswerType(String line) {
		// if(line.contains("Where")) { //where did massacre happen
		// return "location";
		// } else if (line.contains("When")) {
		// return "when-happened-date";
		// } else if(line.contains("perpetrators")) {
		// return "perpetrators";
		// } else if(line.contains("responsible")) {
		// return "perpetrators";
		// } else if(line.contains("mastermind")) {
		// return "perpetrators";
		// } else if(line.contains("injured") || line.contains("wounded")) {
		// return "injuries";
		// }else if(line.contains("attack type")) {
		// return "type";
		// } else if(line.contains("target(s)")) {
		// return "target";
		// } else if(line.contains("dead") || line.contains("killed")){
		// return "fatalities";
		// }LogUtil
		// return null;
		String type = FieldAnalyser.classify(line);
		if (type.equals("IC_TYPES.PERPETRATOR"))
			return "perpetrators";
		else if (type.equals("IC_TYPES.NUMBER_INJURED"))
			return "injuries";
		else if (type.equals("IC_TYPES.DATE"))
			return "when-happened-date";
		else if (type.equals("IC_TYPES.LOCATION"))
			return "location";
		else if (type.equals("IC_TYPES.ATTACK_TYPE"))
			return "type";
		else if (type.equals("IC_TYPES.TARGET"))
			return "target";
		return null;
	}

	public String getAnswerForQuestion(String question) {
		String semanticTarget = getAnswerType(question);
		String targetEvent = (getTargetEvent(question));
		if (semanticTarget == null || targetEvent.trim().equals(""))
			return null;
		else {
			LOGGER.info("Target Event: " + targetEvent);
			LOGGER.info("Semantic Target: " + semanticTarget);
			RDFAnswerGenerator rdfAG = new RDFAnswerGenerator(sparqlServer,
					isServer, indexLocation, targetEvent);
			LOGGER.info("Mapping: "
					+ Arrays.toString(RDFAnswerGenerator.semanticTargetMap
							.get(semanticTarget)));
			List<String> propertiesForTarget = rdfAG
					.getPropertiesForTarget(semanticTarget);
			if (propertiesForTarget != null && !propertiesForTarget.isEmpty()) {
				LOGGER.info("RDF Answer: " + propertiesForTarget.get(0));
				return propertiesForTarget.get(0);
			} else {
				return null;
			}
		}
	}

	public String getTargetEvent(String question) {
		StringBuffer buffer = new StringBuffer();
		try {
			String parse = StanfordParser.parse(question);
			TreeReader newTreeReader = trf
					.newTreeReader(new StringReader(parse));
			Tree tree = newTreeReader.readTree();
			TregexPattern pattern = TregexPattern.compile("NP < NNP");
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree t = matcher.getMatch();
				List<Tree> leaves = t.getLeaves();
				for (Tree l : leaves) {
					buffer.append(l.nodeString() + " ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return buffer.toString().trim();
	}

	public static void main(String args[]) throws Exception {
//		 String question = "When did Guildford pub bombings happen?";
//		 LeafRDFBased ceRDF = new LeafRDFBased();
//		 System.out.println(ceRDF.getAnswerForQuestion(question));
	}
}
