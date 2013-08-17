package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.oaqa.dso.model.Search;
import org.oaqa.dso.model.SearchResult;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;

public class DocumentJCasManipulator {
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
}
