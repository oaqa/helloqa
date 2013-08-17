package edu.cmu.lti.oaqa.openqa.dso.question;

import java.util.ArrayList;
import java.util.List;

import info.ephyra.questionanalysis.atype.extractor.FeatureExtractor;
import info.ephyra.questionanalysis.atype.extractor.FeatureExtractorFactory;
import edu.cmu.lti.javelin.util.Language;
import edu.cmu.lti.oaqa.openqa.dso.util.StopWords;
import edu.cmu.lti.util.Pair;
import edu.cmu.minorthird.classify.Instance;
import edu.cmu.minorthird.classify.MutableInstance;

public class QuestionParser {
	private FeatureExtractor extractor;
	private boolean isInitialized;
	private Pair<Language, Language> languagePair;
	private String questionParseTree;
	public void initialize(String questionText) throws Exception {
		languagePair = new Pair<Language, Language>(Language.valueOf("en_US"),
				Language.valueOf("en_US"));
		if (languagePair == null)
			throw new Exception(
					"languagePair must be set before calling the parent initialize");
		extractor = FeatureExtractorFactory
				.getInstance(languagePair.getFirst());
		isInitialized = true;
		questionParseTree = setQuestionParseTree(questionText);
	}

	private String setQuestionParseTree(String questionText) throws Exception {
		if (!isInitialized)
			throw new Exception("getAnswerTypes called while not initialized");

		// create the instance
		Instance instance = new MutableInstance(questionText);
		if (extractor != null)
			instance = extractor.createInstance(questionText);

		System.out.println(instance.toString());
		return instance.toString();
	}

	public String getQuestionParseTree() {
		return questionParseTree;
	}

	public List<String> getBigrams() {
		// [instance/null: BIGRAM.American-general BIGRAM.What-American
		// BIGRAM.buried-in BIGRAM.general-is BIGRAM.in-Salzburg?
		// BIGRAM.is-buried FOCUS_TYPE.general MAIN_VERB.be UNIGRAM.American
		// UNIGRAM.Salzburg? UNIGRAM.What UNIGRAM.buried UNIGRAM.general
		// UNIGRAM.in UNIGRAM.is WH_WORD.what]
		
		StopWords.getInstance();
		
		List<String> bigrams = new ArrayList<String>();
		String[] tokens = questionParseTree.split(" ");
		for (String token : tokens) {
			if (token.contains("BIGRAM.")) {
				token = token.replace("BIGRAM.", "");
				token = token.replaceAll("\\p{Punct}", " ").trim();
				if (isBigram(token)) {
					bigrams.add(token);
				}
			}
		}

		return bigrams;
	}
	
	private static boolean isBigram(String token){
		if((token.contains("What ") || token.contains("When ")
				|| token.contains("How ") || token.contains("what ") || token
				.contains("Who "))){
			return false;
		}
		
		String[] subtokens=token.split(" ");
		for(String subtoken : subtokens){
			subtoken=subtoken.trim();
			if(subtoken.equals("")||StopWords.contains(subtoken)){
				return false;
			}
		}
		
		return true;
	}
}
