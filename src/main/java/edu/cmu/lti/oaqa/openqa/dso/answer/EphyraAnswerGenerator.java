package edu.cmu.lti.oaqa.openqa.dso.answer;

import info.ephyra.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.AnswerGenerator_ImplBase;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;


public class EphyraAnswerGenerator extends AnswerGenerator_ImplBase {

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	@Override
	public void initialize() {
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
			List<String> keyterms, List<AnswerCandidate> answerCandidates) {

		// sort answer candidates by sum of scores
		// (use original order for tie-breaking)
		Map<String, AnswerCandidate> results = new LinkedHashMap<String, AnswerCandidate>();
		for (AnswerCandidate candidate : answerCandidates) {
			String norm = StringUtils.normalize(candidate.toString());
			AnswerCandidate result = results.get(norm);
			if (result == null) {
				results.put(norm, candidate);
			} else {
				// Add all documents from this instance of candidate to stored
				// instance
				List<RetrievalResult> cResults = candidate
						.getRetrievalResults();
				for (RetrievalResult c : cResults) {
					result.addRetrievalResult(c);
				}
				// Increase score
				result.setScore(result.getScore() + candidate.getScore());
			}
		}
		AnswerCandidate[] sorted = results.values().toArray(
				new AnswerCandidate[results.size()]);
		Arrays.sort(sorted, Collections.reverseOrder());
		List<AnswerCandidate> finalAnswers = new ArrayList<AnswerCandidate>();
		for (AnswerCandidate result : sorted) {
			finalAnswers.add(result);
		}

		if (answerType.contains("NEterrorist")||answerType.contains("NElocation")||answerType.contains("NEdate")) {
			if (finalAnswers.size() > 0) {
				String firstanswer = finalAnswers.get(0).getText();
				for (int i = 1; i < Math.min(5, finalAnswers.size()); i++) {
					if (finalAnswers.get(i).getText()
							.contains(" " + firstanswer)
							|| finalAnswers.get(i).getText()
									.contains(firstanswer + " ")) {
						finalAnswers.set(0, finalAnswers.get(i));
						break;
					}
				}
			}
			
			List<AnswerCandidate> refinedfinalAnswers = new ArrayList<AnswerCandidate>();
			for(int k=0;k<finalAnswers.size();k++){
				String name=finalAnswers.get(k).getText();
				String[] nameSeg=name.split(" ");
				if(nameSeg.length>1){
					refinedfinalAnswers.add(finalAnswers.get(k));
				}
			}
			finalAnswers=refinedfinalAnswers;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Math.min(5, finalAnswers.size()); i++) {
			sb.append((i > 0 ? ", " : ""));
			sb.append("\"" + finalAnswers.get(i) + "\"");
			sb.append("\"" + finalAnswers.get(i).getScore() + "\"");
		}
		LOGGER.info("  Final top answers: "
				+ (sb.length() > 0 ? sb : "No answers found."));
		
		return finalAnswers;
	}

	@Override
	public String getComponentId() {
		return "Ephyra Answer Generator";
	}
}
