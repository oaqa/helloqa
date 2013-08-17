package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.oaqa.dso.model.AbstractQuery;
import org.oaqa.dso.model.QueryConcept;
import org.oaqa.dso.model.QueryOperator;

public class KeytermJCasManipulator {
	private final static String TERM = "TIE";
	private final static String PHRASE = "PHRASE";

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
			// operator.setName(NER);
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
}
