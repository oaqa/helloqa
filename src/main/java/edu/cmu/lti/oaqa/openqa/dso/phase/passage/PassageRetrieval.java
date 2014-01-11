package edu.cmu.lti.oaqa.openqa.dso.phase.passage;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

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

	private LocalCorpusSearcher localWikiSearcher;
	private LocalCorpusSearcher localDSOSearcher;
	//private WebDocumentSearcher webSearcher;
	//private RDFSearcher rdfSearcher;
	
	public static final int DOC_RETURN_SIZE = 50;
	
	private List<Searcher> searchers = new ArrayList<Searcher>();

	@Override
	public void initialize() {
		//webSearcher = new WebDocumentSearcher();
		//rdfSearcher = new RDFSearcher();
		
		//searchers.add(rdfSearcher);
		
		String wikiIndex = "/home/ruil/Downloads/Indexes/wikipedia";
		if (new File(wikiIndex).isFile()) {
			localWikiSearcher = new LocalCorpusSearcher();
			WikiLocalRetrievalCache wiki_cache=new WikiLocalRetrievalCache();
			localWikiSearcher.initialize(wiki_cache.getInstance(), wikiIndex);
			searchers.add(localWikiSearcher);
		}

		String DSOIndex = "xmirepo/dso/index";
		if (new File(DSOIndex).isFile()) {
			localDSOSearcher=new LocalCorpusSearcher();
			DSOLocalRetrievalCache dso_cache=new DSOLocalRetrievalCache();
			localDSOSearcher.initialize(dso_cache.getInstance(), DSOIndex);
			searchers.add(localDSOSearcher);
		}

//		try {
//			rdfSearcher.initialize(null, "");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	public List<RetrievalResult> retrieveDocuments(String qid, List<String> keyterms,
			List<String> keyphrases, String question, String answerType) {
		List<RetrievalResult> mergedresults = new ArrayList<RetrievalResult>();
		
//		List<RetrievalResult> RDFpassages = rdfSearcher.retrieveDocuments(
//				keyterms, keyphrases, question, answerType);
//		mergedresults.addAll(RDFpassages);

		if (localWikiSearcher != null) {
			List<RetrievalResult> localWikipassages = localWikiSearcher.retrieveDocuments(
					keyterms, keyphrases, question, answerType);
			mergedresults.addAll(localWikipassages);
		}
		
		if (localDSOSearcher != null) {
			List<RetrievalResult> localDSOpassages = localDSOSearcher.retrieveDocuments(
					keyterms, keyphrases, question, answerType);
			mergedresults.addAll(localDSOpassages);
		}

//		List<RetrievalResult> webpassages = webSearcher.retrieveDocuments(
//				keyterms, keyphrases, question);
//		mergedresults.addAll(webpassages);

		return mergedresults;
	}

}
