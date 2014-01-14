package edu.cmu.lti.oaqa.openqa.dso.phase.answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.AbstractAnswerGenerator;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

public class NonRedundancyAnswerGenerator extends AbstractAnswerGenerator {

	private static int CANDIDATE_NUM = 100;

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		initialize();
	}

	/**
	 * Generates a final list of answers by simply sorting the candidates by
	 * their frequencies in descending order.
	 * 
	 * @param answerType
	 *            answer type
	 * @param keyterms
	 *            list of key terms used to generate answer candidates
	 * @param answerCandidates
	 *            list of answer candidates
	 * @return list of final answers
	 */
	public List<AnswerCandidate> generateFinalAnswers(String answerType,
			List<String> keyterms, List<RetrievalResult> documents, List<AnswerCandidate> answerCandidates) {

		AnswerCandidate[] sorted = answerCandidates
				.toArray(new AnswerCandidate[answerCandidates.size()]);
		Arrays.sort(sorted, Collections.reverseOrder());
		List<AnswerCandidate> finalAnswers = new ArrayList<AnswerCandidate>();
		for (int i = 0; i < Math.min(sorted.length, CANDIDATE_NUM); i++) {
			finalAnswers.add(sorted[i]);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < Math.min(5, finalAnswers.size()); i++) {
			sb.append("\"" + finalAnswers.get(i) + "\"");
			sb.append("\"" + finalAnswers.get(i).getScore() + "\"\n");
			sb.append("Distance to normalized keywords: ");
			for(int j=0;j<finalAnswers.get(i).getKeytermDistances().length;j++){
				sb.append("\""+finalAnswers.get(i).getKeytermDistances()[j]+ "\"");
			}
			sb.append("\n");
			sb.append(finalAnswers.get(i).getRetrievalResults().get(0).getText()+"\n");
		}
		LOGGER.info("  Final top answers: "
				+ (sb.length() > 0 ? sb : "No answers found."));

		return finalAnswers;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

}
