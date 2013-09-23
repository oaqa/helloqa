package edu.cmu.lti.oaqa.openqa.dso.phase.ie;

import java.util.ArrayList;
import java.util.List;

import info.ephyra.nlp.OpenNLP;
import info.ephyra.questionanalysis.TermExtractor;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.extractor.ICandidateExtractor;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.AbstractInformationExtractor;
import edu.cmu.lti.oaqa.openqa.dso.util.ClassUtil;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

public class InformationExtractor extends AbstractInformationExtractor {
	
	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	private String extractClassName;
	private String leafClassNames;
	
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		initialize();
		this.extractClassName = (String)aContext.getConfigParameterValue("extractor");
		if(aContext.getConfigParameterValue("leaves")!=null)
			this.leafClassNames = (String)aContext.getConfigParameterValue("leaves");
	}

	@Override
	public void initialize() {
		
	}
	
	@Override
	public List<AnswerCandidate> extractAnswerCandidates(String questionText,
			String answerType, List<String> keyterms, List<String> keyphrases,
			List<RetrievalResult> documents) {
		String[][] questionNEs = TermExtractor.getNes(questionText);
		List<String> questionNELs = new ArrayList<String>();

		for (int i = 0; i < questionNEs.length; i++) {
			for (int k = 0; k < questionNEs[i].length; k++) {
				if (questionNEs[i][k] != null
						&& (!questionNEs[i][k].equals(""))
						&& (!questionNELs.contains(questionNEs[i][k]))) {
					questionNELs.add(questionNEs[i][k]);
				}
			}
		}

		int rank = 1;
		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();
		for (RetrievalResult document : documents) {
			// split search result into sentences and tokenize sentences
			String documentText = document.getText().replace(" ... ", " ! ");

			// get refined sentence
			String[] sentences = detectSentences(documentText);

			// No filtering/merging of candidates being done in this function,
			// so each candidate will have a list with only one retrieval result
			List<RetrievalResult> documentList = new ArrayList<RetrievalResult>();
			documentList.add(document);
			
			// build arg
			SupportingEvidenceArg arg = new SupportingEvidenceArg(questionText,
					answerType, keyterms, keyphrases, document.getDocID(), documentText, sentences, rank, this.leafClassNames);

			ICandidateExtractor candidateExtractor = ClassUtil.factory(extractClassName, arg);
			candidates.addAll(candidateExtractor.getAnswerCandidates(arg));
			
			rank++;
		}

		LOGGER.info("  Extracted " + candidates.size() + " candidates.");
		return candidates;
	}

	private static final int MIN_SENTENCE_LENGTH = 3;
	private String[] detectSentences(String documentText) {
		String[] sentences = OpenNLP.sentDetect(documentText);
		List<String> refinedSentences = new ArrayList<String>();
		for (String sentence : sentences) {
			if (sentence.length() > MIN_SENTENCE_LENGTH) {
				refinedSentences.add(sentence);
			}
		}
		return refinedSentences.toArray(new String[refinedSentences.size()]);
	}

}
