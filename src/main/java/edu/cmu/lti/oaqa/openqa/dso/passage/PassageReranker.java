package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.List;

import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;


public interface PassageReranker {
	
	public abstract void initialize(JSONObject config);

	public abstract List<RetrievalResult> rerank(String questionText,
			List<String> keyterms, List<RetrievalResult> documents);

}