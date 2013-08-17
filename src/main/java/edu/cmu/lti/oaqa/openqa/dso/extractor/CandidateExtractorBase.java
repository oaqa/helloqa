package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.cmu.lti.oaqa.openqa.dso.util.FileUtil;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;


import info.ephyra.nlp.NETagger;

public abstract class CandidateExtractorBase {
	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	protected String[][] nes;
	protected String[][] tokens;
	protected static Map<String, String> NETypePatterns;

	protected static HashMap<String, String> TerroristOrgOntologyHash;
	protected static HashMap<String, String> IncidentOntologyHash;

	protected static List<String> TerroristOrgOntologyList;
	protected static List<String> NumberOntologyList;
	protected static List<String> IncidentOntologyList;
	protected static List<String> TerroristOntologyList;

	public abstract String[][] getAnswerCandidates();

	public static void initialize() {
		NETypePatterns = new HashMap<String, String>();
		TerroristOrgOntologyHash = new HashMap<String, String>();
		IncidentOntologyHash=new HashMap<String, String>();
		TerroristOrgOntologyList = new ArrayList<String>();
		NumberOntologyList = new ArrayList<String>();
		IncidentOntologyList = new ArrayList<String>();
		TerroristOntologyList = new ArrayList<String>();

		try {
			List<String> lines = FileUtil
					.readFile("res/experimental/candidate_extractor/NETypeRules.txt");

			for (String line : lines) {
				String[] lineSeg = line.trim().split(":");
				NETypePatterns.put(lineSeg[0], lineSeg[1]);
			}

			lines = FileUtil
					.readFile("res/ephyra/ontologies/terrorist organizations.txt");

			for (String line : lines) {
				String[] lineSeg = line.trim().toLowerCase().split(",");
				if (lineSeg.length > 0) {
					TerroristOrgOntologyHash.put(lineSeg[0].trim(),
							lineSeg[0].trim());
					for (int i = 1; i < lineSeg.length; i++) {
						TerroristOrgOntologyHash.put(lineSeg[i].trim(),
								lineSeg[0].trim());
					}
				}
				for (String onto : lineSeg) {
					TerroristOrgOntologyList.add(onto.trim());
				}
			}

			lines = FileUtil.readFile("res/ephyra/ontologies/number.txt");

			for (String line : lines) {
				line = line.trim().toLowerCase();
				NumberOntologyList.add(line);
			}

			lines = FileUtil
					.readFile("res/ephyra/ontologies/terrorist incidents.txt");
			for (String line : lines) {
				String[] lineSeg = line.trim().split(",");
				if (lineSeg.length > 0) {
					IncidentOntologyHash.put(lineSeg[0].trim(),
							lineSeg[0].trim());
					for (int i = 1; i < lineSeg.length; i++) {
						IncidentOntologyHash.put(lineSeg[i].trim(),
								lineSeg[0].trim());
					}
				}
				for (String onto : lineSeg) {
					IncidentOntologyList.add(onto.trim());
				}
			}

			lines = FileUtil.readFile("res/ephyra/ontologies/terrorists.txt");
			for (String line : lines) {
				line = line.trim().toLowerCase();
				TerroristOntologyList.add(line);
			}

		} catch (Exception e) {
			LOGGER.error("      Error while loading Backoff NETagger data", e);
		}

	}

	protected String[][] getTokens(String[] sentences) {
		String[][] tokens = new String[sentences.length][];
		for (int i = 0; i < sentences.length; i++)
			tokens[i] = NETagger.tokenize(sentences[i]);

		return tokens;
	}

	protected String[][] getMatchOntology(String[] sentences,
			String[][] tokens, List<String> ontologyList) {

		for (int i = 0; i < sentences.length; i++) {
			List<String> currentTokens = new ArrayList<String>();
			for (int j = 0; j < tokens[i].length; j++) {
				currentTokens.add(tokens[i][j]);
			}

			String sentence = sentences[i].toLowerCase();
			if (sentence != null && sentence.length() > 0) {
				for (String token : ontologyList) {
					if (sentence.contains(token)) {
						boolean flag = false;
						// tokens do not have the ontology
						for (String NEtoken : currentTokens) {
							if (token.equals(NEtoken.toLowerCase())) {
								flag = true;
								break;
							}
						}

						if (!flag) {
							currentTokens.add(token);
						}
					}
				}
			}

			tokens[i] = currentTokens.toArray(new String[currentTokens.size()]);
		}
		return tokens;
	}

	public static String getNEMatchOntology(String ne, String answerType) {
		String lowercaseNE = ne.toLowerCase();
		if (answerType.toLowerCase().contains(
				"NETerroristOrganization".toLowerCase())) {
			if (TerroristOrgOntologyHash.containsKey(lowercaseNE)) {
				String result = TerroristOrgOntologyHash.get(lowercaseNE);
				return result;
			}
		}
		return ne;
	}
}
