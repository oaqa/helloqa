package edu.cmu.lti.oaqa.openqa.dso.passage;

import info.ephyra.querygeneration.Query;
import info.ephyra.search.Result;
import info.ephyra.search.Search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.util.JSONUtil;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;


public class LocalCorpusSearcher implements Searcher {
	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	private List<IndriSentencesKM> kms;

	private String[] locations;
	
	private List<double[]> keytermIDFList = new ArrayList<double[]>();
	
	public LocalCorpusSearcher() {
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.lti.oaqa.experimental_impl.passage.searcher.Searcher#initialize()
	 */
	@Override
	public void initialize(JSONObject config) throws JSONException {
		this.locations = JSONUtil.convertJSONArray(config.getJSONArray("locations"));
		kms = new ArrayList<IndriSentencesKM>();
		kms.add(new IndriSentencesKM(locations));
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.lti.oaqa.experimental_impl.passage.searcher.Searcher#retrieveDocuments(java.util.List, java.util.List)
	 */
	@Override
	public List<RetrievalResult> retrieveDocuments(List<String> keyterms,
			List<String> keyphrases, String questionText, String answerType) {
		
		List<RetrievalResult> documents = new ArrayList<RetrievalResult>(0);
		if (keyterms.size() == 0)
			return documents;

		// construct queries
		List<Query> queryL = new ArrayList<Query>();
		Set<String> queryS = new HashSet<String>();
		StringBuilder b;
		// - keyword queries
		b = new StringBuilder();
		for (String keyterm : keyterms) {
			if (b.length() > 0)
				b.append(" ");
			b.append(keyterm);
		}
		String keywordQuery = b.toString();
		if (queryS.add(keywordQuery)) {
			queryL.add(new Query(keywordQuery));
		}

		// run search
		Search.clearKnowledgeMiners();
		for (IndriSentencesKM km : kms) {
			Search.addKnowledgeMiner(km);
			for (int i=0;i<queryL.size();i++) {
				keytermIDFList.add(km.getKeytermIDF(keyterms));
			}
			km.setKeyPhrases(keyphrases);
		}

		List<Result> resultL = new ArrayList<Result>();
		for (Query query : queryL) {
			Result[] results = Search.doSearch(new Query[] { query });
			for (Result result : results) {
				resultL.add(result);
			}
		}

		// remove characters that are not supported by UIMA
		for (Result result : resultL) {
			String s = result.getAnswer();
			StringBuilder sb = new StringBuilder();
			for (char c : s.toCharArray())
				if (c > 0x1f) {
					sb.append(c);
				} else {
					sb.append(' ');
				}
			// documents.add(sb.toString());
			documents.add(new RetrievalResult(result.getDocID(), result
					.getScore(), sb.toString(), result.getHitPos(), result
					.getQuery().getQueryString()));
		}
		LOGGER.info("  Retrieved " + documents.size() + " docs.");
		return documents;
	}
	
	public List<double[]> getKeytermIDF(){
		return this.keytermIDFList;
	}

}
