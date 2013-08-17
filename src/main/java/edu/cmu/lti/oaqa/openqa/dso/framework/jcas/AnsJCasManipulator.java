package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.oaqa.dso.model.Answer;
import org.oaqa.dso.model.AnswerList;
import org.oaqa.dso.model.SearchResult;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;

public class AnsJCasManipulator {
	public static void storeCandidates(JCas candidateView,
			List<AnswerCandidate> answers) {
		storeAnswers(candidateView, answers);
	}

	public static List<AnswerCandidate> loadAnswerCandidates(JCas candidateView)
			throws CASException {
		// if (!candidateView.getViewName().equals(ViewManager.CANDIDATE) && !
		// candidateView.getViewName().equals(ViewManager.FINAL_ANSWER)) {
		// throw new CASException();
		// }
		List<AnswerCandidate> result = new ArrayList<AnswerCandidate>();
		Iterator<?> it = candidateView.getJFSIndexRepository().getAllIndexedFS(
				AnswerList.type);

		if (it.hasNext()) {
			AnswerList answerCandidates = (AnswerList) it.next();
			String[] featureLabels = null;
			if (answerCandidates.getFeatureLabels() != null) {
				featureLabels = new String[answerCandidates.getFeatureLabels()
						.size()];
				for (int i = 0; i < answerCandidates.getFeatureLabels().size(); i++) {
					featureLabels[i] = answerCandidates.getFeatureLabels(i);
				}
			}
			FSArray answerList = answerCandidates.getAnswerList();
			for (int i = 0; i < answerList.size(); i++) {
				Answer a = (Answer) answerList.get(i);
				AnswerCandidate candidate = new AnswerCandidate(a);
				candidate.setFeatureLables(featureLabels);
				result.add(candidate);
			}
		}
		return result;
	}
	
	/**
	 * Store (overwrite) answers into a view
	 * 
	 * @param view
	 *            either candidate view or final answer view
	 * @param answers
	 */
	private static void storeAnswers(JCas view, List<AnswerCandidate> answers) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = view.getJFSIndexRepository().getAllIndexedFS(
				AnswerList.type);
		while (it.hasNext()) {
			AnswerList answerCandidates = (AnswerList) it.next();
			answerCandidates.removeFromIndexes();
		}

		FSArray answerArray = new FSArray(view, answers.size());

		// Erase currently registered items
		// candidateView.reset();
		answerArray.addToIndexes();
		for (int i = 0; i < answers.size(); i++) {
			AnswerCandidate candidate = answers.get(i);
			Answer a = new Answer(view);
			// a.addToIndexes();
			a.setText(candidate.getText());

			int featureNum = 0;
			double[] features = candidate.getFeatures();
			DoubleArray featureList = new DoubleArray(view, features.length);
			featureList.addToIndexes();
			for (int j = 0; j < features.length; j++) {
				featureList.set(featureNum, features[j]);
			}
			a.setFeatureVector(featureList);

			int resultCount = 0;
			FSArray hitList = new FSArray(view, candidate.getRetrievalResults()
					.size());
			hitList.addToIndexes();
			for (RetrievalResult result : candidate.getRetrievalResults()) {
				SearchResult sr = new SearchResult(view);
				sr.addToIndexes();
				sr.setRank((resultCount + 1));
				sr.setText(result.getText());
				sr.setScore(result.getScore());
				sr.setUri(result.getDocID());
				sr.setQueryString(result.getQueryString());
				hitList.set(resultCount, sr);
				resultCount++;
			}
			a.setSearchResultList(hitList);
			a.setScore(candidate.getScore());
			a.addToIndexes();

			answerArray.set(i, a);
		}

		AnswerList answerList = new AnswerList(view);
		answerList.setAnswerList(answerArray);
		if (answers.size() > 0) {
			if (answers.get(0).getFeatureLabels() != null) {
				String[] featureLabels = answers.get(0).getFeatureLabels();
				StringArray featureLabelList = new StringArray(view,
						featureLabels.length);
				featureLabelList.addToIndexes();
				for (int j = 0; j < featureLabels.length; j++) {
					featureLabelList.set(j, featureLabels[j]);
				}
				answerList.setFeatureLabels(featureLabelList);
			}
		}
		answerList.addToIndexes();
	}
}
