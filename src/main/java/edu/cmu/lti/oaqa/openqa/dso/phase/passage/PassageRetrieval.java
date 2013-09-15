package edu.cmu.lti.oaqa.openqa.dso.phase.passage;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONException;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.AbstractPassageRetrieval;
import edu.cmu.lti.oaqa.openqa.dso.passage.DSOLocalRetrievalCache;
import edu.cmu.lti.oaqa.openqa.dso.passage.LocalCorpusSearcher;
import edu.cmu.lti.oaqa.openqa.dso.passage.RDFSearcher;
import edu.cmu.lti.oaqa.openqa.dso.passage.Searcher;
import edu.cmu.lti.oaqa.openqa.dso.passage.WikiLocalRetrievalCache;

public class PassageRetrieval extends AbstractPassageRetrieval{

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		initialize();
	}
	
	
	private double[] keytermsIDF;

	private LocalCorpusSearcher localWikiSearcher;
	private LocalCorpusSearcher localDSOSearcher;
	//private WebDocumentSearcher webSearcher;
	private RDFSearcher rdfSearcher;
	
	public static final int DOC_RETURN_SIZE = 50;
	
	private List<Searcher> searchers = new ArrayList<Searcher>();

	@Override
	public void initialize() {
		localWikiSearcher = new LocalCorpusSearcher();
		localDSOSearcher=new LocalCorpusSearcher();
		//webSearcher = new WebDocumentSearcher();
		rdfSearcher = new RDFSearcher();
		
		DSOLocalRetrievalCache dso_cache=new DSOLocalRetrievalCache();
		WikiLocalRetrievalCache wiki_cache=new WikiLocalRetrievalCache();
		
		searchers.add(localWikiSearcher);
		searchers.add(localDSOSearcher);
		searchers.add(rdfSearcher);
		
		localWikiSearcher.initialize(wiki_cache.getInstance(), "/home/ruil/Downloads/Indexes/wikipedia");
		localDSOSearcher.initialize(dso_cache.getInstance(), "xmirepo/dso/index");
		try {
			rdfSearcher.initialize(null, "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public List<RetrievalResult> retrieveDocuments(List<String> keyterms,
			List<String> keyphrases, String question, String answerType) {
		List<RetrievalResult> mergedresults = new ArrayList<RetrievalResult>();
		keytermsIDF = new double[keyterms.size()];
		
		List<RetrievalResult> RDFpassages = rdfSearcher.retrieveDocuments(
				keyterms, keyphrases, question, answerType);
		mergedresults.addAll(RDFpassages);

		List<RetrievalResult> localWikipassages = localWikiSearcher.retrieveDocuments(
				keyterms, keyphrases, question, answerType);
		mergedresults.addAll(localWikipassages);
		keytermsIDF = localWikiSearcher.getKeytermIDF().get(0);
		
		List<RetrievalResult> localDSOpassages = localDSOSearcher.retrieveDocuments(
				keyterms, keyphrases, question, answerType);
		mergedresults.addAll(localDSOpassages);

//		List<RetrievalResult> webpassages = webSearcher.retrieveDocuments(
//				keyterms, keyphrases, question);
//		mergedresults.addAll(webpassages);

		return mergedresults;
	}

}
