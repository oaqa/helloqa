package edu.cmu.lti.oaqa.openqa.dso.passage;

import info.ephyra.querygeneration.Query;
import info.ephyra.search.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.extractor.CandidateExtractorByInfoBox;


public class RDFSearcher implements Searcher{
	public List<RetrievalResult> retrieveDocuments(List<String> keyterms,
			List<String> keyphrases, String questionText, String answerType) {

		List<RetrievalResult> documents = new ArrayList<RetrievalResult>();
		
		CandidateExtractorByInfoBox extractorbyInfoBox = new CandidateExtractorByInfoBox();
		List<String> infoBoxCandidates = extractorbyInfoBox
				.getAnswerCandidates(keyterms, answerType);
		
		StringBuffer queryStr=new StringBuffer();
		for (String keyterm : keyterms) {
			if (queryStr.length() > 0)
				queryStr.append(" ");
			queryStr.append(keyterm);
		}
		Query query=new Query(queryStr.toString());
		
		Random generator = new Random();
		for (String ne : infoBoxCandidates) {
			if(ne==null){
				continue;
			}
			ne=queryStr+", "+ne;
			Result result = new Result(ne, query,"" + generator.nextInt(1000000), 0); // Document Rank
			
			//List<RetrievalResult> documentList = new ArrayList<RetrievalResult>();
			documents.add(new RetrievalResult("INFO"+result.getDocID(),
					-1, ne, 
					1,
					null));
		}
		return documents;
	}

	@Override
	public void initialize(JSONObject config) throws JSONException {
		// no special initialization configs
	}
}
