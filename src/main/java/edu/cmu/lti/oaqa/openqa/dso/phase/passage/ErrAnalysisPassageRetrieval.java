package edu.cmu.lti.oaqa.openqa.dso.phase.passage;

import java.util.ArrayList;
import java.util.HashMap;
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
import edu.cmu.lti.oaqa.openqa.dso.util.FileUtil;

public class ErrAnalysisPassageRetrieval extends AbstractPassageRetrieval {

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		initialize();
	}

	private LocalCorpusSearcher localWikiSearcher;
	private LocalCorpusSearcher localDSOSearcher;

	public static final int DOC_RETURN_SIZE = 50;

	private List<Searcher> searchers = new ArrayList<Searcher>();

	private HashMap<String, RetrievalResult> gsRetrievalResult = new HashMap<String, RetrievalResult>();

	@Override
	public void initialize() {
		String wikiIndex = "/home/ruil/Downloads/Indexes/wikipedia";
		if (new File(wikiIndex).isFile()) {
			localWikiSearcher = new LocalCorpusSearcher();
			WikiLocalRetrievalCache wiki_cache = new WikiLocalRetrievalCache();
			localWikiSearcher.initialize(wiki_cache.getInstance(), wikiIndex);
			searchers.add(localWikiSearcher);
		}

		String DSOIndex = "xmirepo/dso/index";
		if (new File(DSOIndex).isFile()) {
			localDSOSearcher = new LocalCorpusSearcher();
			DSOLocalRetrievalCache dso_cache = new DSOLocalRetrievalCache();
			localDSOSearcher.initialize(dso_cache.getInstance(), DSOIndex);
			searchers.add(localDSOSearcher);
		}

		String filePathName = "src/main/resources/gs/dso-extension-psg.txt";
		List<String> lines = FileUtil.readFile(filePathName);
		for (int i = 0; i < lines.size(); i++) {
			String psggs = lines.get(i);
			int index = psggs.indexOf(" ");
			String id = "";
			for(int j=0;j<psggs.indexOf(" ");j++){
				if(psggs.charAt(j)<='9'&&psggs.charAt(j)>='0'){
					id+=String.valueOf(psggs.charAt(j));
				}
			}
			gsRetrievalResult.put(id,
					new RetrievalResult("GS", 1.0, psggs.substring(index + 1),
							1, ""));
		}
	}

	public List<RetrievalResult> retrieveDocuments(String qid,
			List<String> keyterms, List<String> keyphrases, String question,
			String answerType) {
		List<RetrievalResult> mergedresults = new ArrayList<RetrievalResult>();

		if (localWikiSearcher != null) {
			List<RetrievalResult> localWikipassages = localWikiSearcher
					.retrieveDocuments(keyterms, keyphrases, question, answerType);
			mergedresults.addAll(localWikipassages);
		}

		if (localDSOSearcher != null) {
			List<RetrievalResult> localDSOpassages = localDSOSearcher
					.retrieveDocuments(keyterms, keyphrases, question, answerType);
			mergedresults.addAll(localDSOpassages);
		}

		RetrievalResult gspassage = readGsPsgs(qid);
		mergedresults.add(gspassage);

		return mergedresults;
	}

	private RetrievalResult readGsPsgs(String qid) {
		if (gsRetrievalResult.containsKey(qid)) {
			return gsRetrievalResult.get(qid);
		}else{
			return new RetrievalResult("GS", 0.0, "", 1, "");
		}
	}

}
