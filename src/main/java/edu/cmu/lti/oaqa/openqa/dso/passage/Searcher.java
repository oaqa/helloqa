package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;

public interface Searcher {

	public abstract List<RetrievalResult> retrieveDocuments(
			List<String> keyterms, List<String> keyphrases, String questionText, String answerType);

	public abstract void initialize(JSONObject config) throws JSONException;

}