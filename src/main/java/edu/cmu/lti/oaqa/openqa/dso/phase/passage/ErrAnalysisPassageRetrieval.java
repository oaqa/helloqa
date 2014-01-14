package edu.cmu.lti.oaqa.openqa.dso.phase.passage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.AbstractPassageRetrieval;
import edu.cmu.lti.oaqa.openqa.dso.passage.DSOLocalRetrievalCache;
import edu.cmu.lti.oaqa.openqa.dso.passage.LocalCorpusSearcher;
import edu.cmu.lti.oaqa.openqa.dso.passage.PassageReranker;
import edu.cmu.lti.oaqa.openqa.dso.passage.Searcher;
import edu.cmu.lti.oaqa.openqa.dso.passage.TFIDFPassageReranker;
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
	private LocalCorpusSearcher backgroundCorpusSearcher;
	
	public static final int DOC_PRUNING_RETURN_SIZE = 100;

	private List<Searcher> searchers = new ArrayList<Searcher>();

	private HashMap<String, RetrievalResult> gsRetrievalResult = new HashMap<String, RetrievalResult>();

	@Override
	public void initialize() {
		localWikiSearcher = new LocalCorpusSearcher();
		localDSOSearcher = new LocalCorpusSearcher();
		backgroundCorpusSearcher = localWikiSearcher;

		DSOLocalRetrievalCache dso_cache = new DSOLocalRetrievalCache();
		WikiLocalRetrievalCache wiki_cache = new WikiLocalRetrievalCache();

		searchers.add(localWikiSearcher);
		searchers.add(localDSOSearcher);

		localWikiSearcher.initialize(wiki_cache.getInstance(),
				"/home/ruil/Downloads/Indexes/wikipedia");
		localDSOSearcher.initialize(dso_cache.getInstance(),
				"xmirepo/dso/index");

		// String filePathName =
		// "/home/ruil/workspace/git/helloqa/src/main/resources/gs/dso-extension-psg.txt";
		String filePathName = "/home/ruil/workspace/git/helloqa/src/main/resources/gs/LOC-Questions-ps.txt";

		List<String> lines = FileUtil.readFile(filePathName);
		for (int i = 0; i < lines.size(); i++) {
			String psggs = lines.get(i);
			int index = psggs.indexOf(" ");
			String id = "";
			for (int j = 0; j < psggs.indexOf(" "); j++) {
				if (psggs.charAt(j) <= '9' && psggs.charAt(j) >= '0') {
					id += String.valueOf(psggs.charAt(j));
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

		List<RetrievalResult> localWikipassages = localWikiSearcher
				.retrieveDocuments(keyterms, keyphrases, question, answerType);

		for (int i = 0; i < Math.min(localWikipassages.size(),
				DOC_PRUNING_RETURN_SIZE); i++) {
			mergedresults.add(localWikipassages.get(i));
		}

		List<RetrievalResult> localDSOpassages = localDSOSearcher
				.retrieveDocuments(keyterms, keyphrases, question, answerType);
		//mergedresults.addAll(localDSOpassages);

		RetrievalResult gspassage = readGsPsgs(qid);
		// mergedresults.add(gspassage);
		
//		return TFIDFPassageReranker
//				.rerank(question, keyterms, backgroundCorpusSearcher
//						.getKeytermIDF().get(0), localWikipassages,
//						DOC_PRUNING_RETURN_SIZE);
		return mergedresults;
	}

	private RetrievalResult readGsPsgs(String qid) {
		if (gsRetrievalResult.containsKey(qid)) {
			return gsRetrievalResult.get(qid);
		} else {
			return new RetrievalResult("GS", 0.0, "", 1, "");
		}
	}

}
