package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.util.StringUtil;

public class PassageReranker {

	public static List<RetrievalResult> rerank(String questionText,
			List<String> keyterms, double[] keytermsIDF,
			List<RetrievalResult> documents, int return_size) {
		List<RetrievalResult> resultList = new ArrayList<RetrievalResult>();

		for (int i = 0; i < keytermsIDF.length; i++) {
			if (keytermsIDF[i] == 0) {
				keytermsIDF[i] = 1;
			}
		}

		List<String> keytermExpansion = new ArrayList<String>();

		questionText = StringUtil.questionTextNormalization(questionText);
		List<String> questionItems = StringUtil.getQuestionItems(questionText,
				keyterms);
		keytermExpansion.addAll(questionItems);

		for (RetrievalResult document : documents) {
			String documentText = document.getText();

			double score = 0;
			// if keyterm >3 then determined by
			// termcoverage, else weighted
			// coverage
			if (keyterms.size() > 3) {
				score = RerankScorer.TERM_COVERAGE.getScore(documentText,
						keyterms, keytermsIDF, keytermExpansion);
			} else {
				score = RerankScorer.TF_IDF.getScore(documentText, keyterms,
						keytermsIDF, keytermExpansion);
			}

			score += RerankScorer.COMPLETE_KEYTERMS_IN_ONE_SENTENCE.getScore(
					documentText, keyterms, keytermsIDF, keytermExpansion);

			document.setRerankScore(score);
		}

		Collections.sort(documents, Collections.reverseOrder());
		int minDocNumber = Math.min(documents.size(), return_size);
		for (int i = 0; i < minDocNumber; i++) {
			resultList.add(documents.get(i));
		}

		return resultList;
	}
	
}