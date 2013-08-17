package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.framework.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;

public class EphyraRetrievalStrategist implements IComponent {
	private double[] keytermsIDF;

	private LocalCorpusSearcher localSearcher;
	//private WebDocumentSearcher webSearcher;
	private RDFSearcher rdfSearcher;
	
	private static String answerType;

	public static final int DOC_RETURN_SIZE = 50;
	
	private List<Searcher> searchers = new ArrayList<Searcher>();
	private List<JSONObject> searchersConfig = new ArrayList<JSONObject>();
	private PassageReranker passageReranker = null;
	private JSONObject passageRerankerConfig = null;

	public EphyraRetrievalStrategist(JSONObject componentJSONConfig) {
		
		try{
		
			JSONObject passageRerankerJsonObject = componentJSONConfig.getJSONObject("passageReranker");
			
			String passageRerankerClassName = passageRerankerJsonObject.getString("className");
			
			passageReranker = (PassageReranker) Class.forName(passageRerankerClassName).newInstance();
			passageRerankerConfig = passageRerankerJsonObject.getJSONObject("configuration");
			
			JSONArray searchersJsonArray = componentJSONConfig.getJSONArray("searchers");
			
			for(int i = 0; i < searchersJsonArray.length(); i++){
				JSONObject searcherJson = searchersJsonArray.getJSONObject(i);
				if(searcherJson.getBoolean("enabled") == true){
					searchers.add((Searcher) Class.forName(searcherJson.getString("className")).newInstance());
					searchersConfig.add(searcherJson.getJSONObject("configuration"));
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
//		localSearcher = new LocalCorpusSearcher(serverMode, locations);
//		webSearcher = new WebDocumentSearcher();
//		rdfSearcher = new RDFSearcher();
	}

	@Override
	public void initialize() {
		try{
			passageReranker.initialize(passageRerankerConfig);
			for(int i = 0 ; i < searchers.size(); i++){
				searchers.get(i).initialize(searchersConfig.get(i));
			}
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	/**
	 * Retrieves text to be used for candidate extraction from sources.
	 * 
	 * @param jcas
	 */
	public synchronized void process(JCas jcas)
			throws AnalysisEngineProcessException {
		try {
			JCas questionView = ViewManager.getQuestionView(jcas);
			String questionText = questionView.getDocumentText();
			List<String> keyterms = JCasManipulator.loadKeyterms(questionView);
			List<String> keyphrases = JCasManipulator
					.loadKeyphrases(questionView);
			String answerType=JCasManipulator.loadAnswerType(questionView);
			List<RetrievalResult> documents = retrieveDocuments(keyterms,
					keyphrases, questionText, answerType);
			PassageReranker passageReranker = this.passageReranker;
			documents = passageReranker.rerank(questionText, keyterms,
					documents);
			JCas documentView = ViewManager.getDocumentView(jcas);
			JCasManipulator.storeDocuments(documentView, documents);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	public List<RetrievalResult> retrieveDocuments(List<String> keyterms,
			List<String> keyphrases, String question, String answerType) {
		List<RetrievalResult> mergedresults = new ArrayList<RetrievalResult>();
		keytermsIDF = new double[keyterms.size()];
		
		for(Searcher searcher : searchers){
			mergedresults.addAll(searcher.retrieveDocuments(keyterms, keyphrases, question, answerType));
			
			// TODO find a prettier way for integrating this
			if(searcher instanceof LocalCorpusSearcher){
				keytermsIDF = ((LocalCorpusSearcher) searcher).getKeytermIDF().get(0);
			}
		}
		
		
//		List<RetrievalResult> RDFpassages = rdfSearcher.retrieveDocuments(
//				keyterms, keyphrases, question, answerType);
//		mergedresults.addAll(RDFpassages);

//		List<RetrievalResult> localpassages = localSearcher.retrieveDocuments(
//				keyterms, keyphrases);
//		mergedresults.addAll(localpassages);
//		keytermsIDF = localSearcher.getKeytermIDF().get(0);

//		List<RetrievalResult> webpassages = webSearcher.retrieveDocuments(
//				keyterms, keyphrases, question);
//		mergedresults.addAll(webpassages);

		return mergedresults;
	}

	@Override
	public String getComponentId() {
		return "Ephyra Retrieval Strategist";
	}
		
}
