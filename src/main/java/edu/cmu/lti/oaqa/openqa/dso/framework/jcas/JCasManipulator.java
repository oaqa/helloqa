package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.oaqa.dso.model.AbstractQuery;
import org.oaqa.dso.model.Answer;
import org.oaqa.dso.model.AnswerList;
import org.oaqa.dso.model.AnswerType;
import org.oaqa.dso.model.ICEvent;
import org.oaqa.dso.model.QueryConcept;
import org.oaqa.dso.model.QueryOperator;
import org.oaqa.dso.model.Question;
import org.oaqa.dso.model.Search;
import org.oaqa.dso.model.SearchResult;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;

public class JCasManipulator {

	private final static String TERM = "TIE";
	private final static String PHRASE = "PHRASE";
	private final static String NER = "NER";

	/**
	 * Helper method to add an item to a stupid FS list. UIMA's FSList seems to
	 * be a classic linked-list implementation
	 */
	public static NonEmptyFSList addToFSList(JCas aJCas, FSList list, TOP item) {
		NonEmptyFSList result = new NonEmptyFSList(aJCas);
		result.setHead(item);
		result.setTail(list);
		return result;
	}

	/**
	 * Convert UIMA data model
	 * 
	 * @param questionView
	 * @return answerType
	 */
	public static String loadAnswerType(JCas questionView) {
		String result = null;
		AnnotationIndex<?> index = questionView
				.getAnnotationIndex(AnswerType.type);
		Iterator<?> it = index.iterator();

		if (it.hasNext()) {
			AnswerType atype = (AnswerType) it.next();
			result = atype.getLabel();
		}
		return result;
	}
	
	public static String loadIcEvent(JCas questionView) {
		
		String result = null;
		AnnotationIndex<?> index = questionView
		.getAnnotationIndex(ICEvent.type);
		
		Iterator<?> it = index.iterator();
		
		if(it.hasNext()) {
			ICEvent ic = (ICEvent)it.next();
			result = ic.getLabel();
		}
		
		return result;
		
	}

	/**
	 * Convert UIMA data model
	 * 
	 * @param questionView
	 * @return keyterms
	 */
	private static List<String> loadConcepts(JCas questionView, String type) {
		List<String> reversedResult = new ArrayList<String>();
		Iterator<?> it = questionView.getJFSIndexRepository().getAllIndexedFS(
				AbstractQuery.type);

		if (it.hasNext()) {
			AbstractQuery query = (AbstractQuery) it.next();
			FSList concepts = query.getConcepts();

			// According to a UIMA forum, there isn't any simpler way to iterate
			// FSList.
			while (concepts instanceof NonEmptyFSList) {
				QueryConcept head = (QueryConcept) ((NonEmptyFSList) concepts)
						.getHead();
				if (type.equals(head.getOperator().getName())) {
					reversedResult.add(head.getText());
				}
				concepts = ((NonEmptyFSList) concepts).getTail();
			}
		}
		List<String> result = new ArrayList<String>(reversedResult.size());
		for (int i = 0; i < reversedResult.size(); i++) {
			result.add(reversedResult.get(reversedResult.size() - (i + 1)));
		}
		return result;
	}

	public static List<String> loadKeyterms(JCas questionView) {
		return loadConcepts(questionView, TERM);
	}

	public static List<String> loadKeyphrases(JCas questionView) {
		return loadConcepts(questionView, PHRASE);
	}

	public static List<RetrievalResult> loadDocuments(JCas documentView) {
		List<RetrievalResult> result = new ArrayList<RetrievalResult>();
		Iterator<?> it = documentView.getJFSIndexRepository().getAllIndexedFS(
				Search.type);

		if (it.hasNext()) {
			Search retrievalResult = (Search) it.next();
			FSArray hitList = retrievalResult.getHitList();
			for (int i = 0; i < hitList.size(); i++) {
				SearchResult sr = (SearchResult) hitList.get(i);
				result.add(new RetrievalResult(sr));
			}
		}
		return result;
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

	public static Question loadQuestion(JCas questionView) {
		AnnotationIndex<?> index = questionView
				.getAnnotationIndex(Question.type);
		Iterator<?> it = index.iterator();

		Question question = null;
		if (it.hasNext()) {
			question = (Question) it.next();
		}
		return question;
	}

	/**
	 * Stores (overwrite) answer type in a view
	 * 
	 * @param questionView
	 * @param type
	 */
	public static void storeAnswerType(JCas questionView, String type) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = questionView.getJFSIndexRepository().getAllIndexedFS(
				AnswerType.type);
		while (it.hasNext()) {
			AnswerType oaqaType = (AnswerType) it.next();
			oaqaType.removeFromIndexes();
		}

		AnswerType oaqaType = new AnswerType(questionView);
		oaqaType.setLabel(type);
		oaqaType.addToIndexes();
	}
	
	public static void storeICEvent(JCas questionView, String type) {
		
		Iterator<?> it = questionView.getJFSIndexRepository().getAllIndexedFS(ICEvent.type);
		
		while(it.hasNext()) {
			ICEvent icEvent = (ICEvent)it.next();
			icEvent.removeFromIndexes();
		}
		
		ICEvent icEvent = new ICEvent(questionView);
		icEvent.setLabel(type);
		icEvent.addToIndexes();
		
	}

	/**
	 * Store (overwrite) concepts (i.e. key terms) into a view
	 * 
	 * @param questionView
	 * @param keyterms
	 */
	public static void storeKeyterms(JCas questionView, List<String> keyterms) {
		storeConcepts(questionView, keyterms, new ArrayList<String>(),
				new ArrayList<String>());
	}

	/**
	 * Store (overwrite) concepts (i.e. key phrases) into a view
	 * 
	 * @param questionView
	 * @param keyterms
	 */
	public static void storeKeyTermsAndPhrases(JCas questionView,
			List<String> keyterms, List<String> keyphrases) {
		storeConcepts(questionView, keyterms, keyphrases,
				new ArrayList<String>());
	}

	/**
	 * Store (overwrite) concepts (i.e. key terms / phrases in our
	 * implementation) into a view
	 * 
	 * @param questionView
	 * @param keyterms
	 * @param type
	 */
	private static void storeConcepts(JCas questionView, List<String> keyterms,
			List<String> keyphrases, List<String> keyNERs) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = questionView.getJFSIndexRepository().getAllIndexedFS(
				AbstractQuery.type);
		while (it.hasNext()) {
			AbstractQuery query = (AbstractQuery) it.next();
			query.removeFromIndexes();
		}

		AbstractQuery query = new AbstractQuery(questionView);
		FSList concepts = new FSList(questionView);

		for (String keyterm : keyterms) {
			QueryConcept concept = new QueryConcept(questionView);
			concept.setText(keyterm);
			QueryOperator operator = new QueryOperator(questionView);
			operator.addToIndexes();
			operator.setName(TERM);
			concept.setOperator(operator);
			concept.addToIndexes();

			// add a QueryConcept into FSList
			concepts = JCasManipulator.addToFSList(questionView, concepts,
					concept);
		}
		for (String keyterm : keyphrases) {
			QueryConcept concept = new QueryConcept(questionView);
			concept.setText(keyterm);
			QueryOperator operator = new QueryOperator(questionView);
			operator.addToIndexes();
			operator.setName(PHRASE);
			concept.setOperator(operator);
			concept.addToIndexes();

			// add a QueryConcept into FSList
			concepts = JCasManipulator.addToFSList(questionView, concepts,
					concept);
		}
		for (String keyterm : keyNERs) {
			QueryConcept concept = new QueryConcept(questionView);
			concept.setText(keyterm);
			QueryOperator operator = new QueryOperator(questionView);
			operator.addToIndexes();
			//operator.setName(NER);
			concept.setOperator(operator);
			concept.addToIndexes();

			// add a QueryConcept into FSList
			concepts = JCasManipulator.addToFSList(questionView, concepts,
					concept);
		}
		query.setConcepts(concepts);
		concepts.addToIndexes();
		query.addToIndexes();
	}
	
	/**
	 * Store documents into a view for structured evaluation
	 */
	public static void storeStructuredWithoutOverwrite(JCas documentView, List<RetrievalResult> documents) {
		
		FSArray hitList = new FSArray(documentView, documents.size());
		hitList.addToIndexes();
		for (int i = 0; i < documents.size(); i++) {
			SearchResult sr = new SearchResult(documentView);
			sr.addToIndexes();
			sr.setRank((i + 1));
			sr.setText(documents.get(i).getText());
			sr.setScore(documents.get(i).getScore());
			sr.setUri(documents.get(i).getDocID());
			sr.setQueryString(documents.get(i).getQueryString());
			hitList.set(i, sr);
		}

		Search search = new Search(documentView);
		search.setHitList(hitList);
		search.addToIndexes();
		
	}

	/**
	 * Store (overwrite) documents in a view
	 * 
	 * @param documentView
	 * @param documents
	 */
	public static void storeDocuments(JCas documentView,
			List<RetrievalResult> documents) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = documentView.getJFSIndexRepository().getAllIndexedFS(
				Search.type);
		while (it.hasNext()) {
			Search search = (Search) it.next();
			search.removeFromIndexes();
		}

		FSArray hitList = new FSArray(documentView, documents.size());
		hitList.addToIndexes();
		for (int i = 0; i < documents.size(); i++) {
			SearchResult sr = new SearchResult(documentView);
			sr.addToIndexes();
			sr.setRank((i + 1));
			sr.setText(documents.get(i).getText());
			sr.setScore(documents.get(i).getScore());
			sr.setUri(documents.get(i).getDocID());
			sr.setQueryString(documents.get(i).getQueryString());
			hitList.set(i, sr);
		}

		Search search = new Search(documentView);
		search.setHitList(hitList);
		search.addToIndexes();
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

	public static void storeCandidates(JCas candidateView,
			List<AnswerCandidate> answers) {
		storeAnswers(candidateView, answers);
	}

	public static void storeFinalAnswers(JCas finalAnswerView,
			List<AnswerCandidate> answers) {
		storeAnswers(finalAnswerView, answers);
	}

}
