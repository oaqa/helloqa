package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.util.StopWords;
import edu.cmu.lti.oaqa.openqa.dso.util.StringUtil;

public class TFIDFPassageReranker {

	public static List<RetrievalResult> rerank(String questionText,
			List<String> keyterms, double[] keytermsIDF,
			List<RetrievalResult> documents, int return_size) {
		List<RetrievalResult> resultList = new ArrayList<RetrievalResult>();

		List<String> keytermExpansion = new ArrayList<String>();
		List<String> keytermStopWordExpansion = new ArrayList<String>();

		questionText = StringUtil.questionTextNormalization(questionText);
		List<String> questionItems = StringUtil.getQuestionItems(questionText,
				keyterms);
		for(int i=0;i<questionItems.size();i++){
			if(!StopWords.contains(questionItems.get(i))){
				keytermStopWordExpansion.add(questionItems.get(i));
			}
		}
		
		keytermExpansion.addAll(questionItems);

		for (RetrievalResult document : documents) {
			String documentText = document.getText();

			double score  = RerankScorer.TF_IDF.getScore(documentText, keyterms,
					keytermsIDF, keytermStopWordExpansion);

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
