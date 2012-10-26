package edu.cmu.lti.oaqa.openqa.hello.passage;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.mergeqa.ie.AbstractPassageExtractor;

public class SimplePassageExtractor extends AbstractPassageExtractor {
	
	SolrWrapper wrapper;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
	    super.initialize(aContext);
	    String serverUrl = (String) aContext.getConfigParameterValue("server");
	    Integer serverPort = (Integer) aContext.getConfigParameterValue("port");
	    Boolean embedded = (Boolean) aContext.getConfigParameterValue("embedded");
	    String core = (String) aContext.getConfigParameterValue("core");
	    // String keytermWindowScorer = (String)aContext.getConfigParameterValue( "keytermWindowScorer" );
	    // System.out.println( "initialize() : keytermWindowScorer: " + keytermWindowScorer );
	    try {
	      this.wrapper = new SolrWrapper(serverUrl, serverPort, embedded, core);
	    } catch (Exception e) {
	      throw new ResourceInitializationException(e);
	    }
	  }


	@Override
	protected List<PassageCandidate> extractPassages( String question, List<Keyterm> keyterms, List<RetrievalResult> documents ) {
		List<PassageCandidate> result = new ArrayList<PassageCandidate>();
		for ( RetrievalResult document : documents ) {
			System.out.println( "RetrievalResult: " + document.toString() );
			String id = document.getDocID();
			// @Alkesh: can you add this call to the SolrWrapper API?
			// String text = wrapper.getDocText( id );
			String text = null;
			PassageCandidateFinder finder = new PassageCandidateFinder( id , text , new KeytermWindowScorerSum() );
			List<PassageCandidate> passageSpans = finder.extractPassages( (String[])keyterms.toArray() );
			for ( PassageCandidate passageSpan : passageSpans )
				result.add( passageSpan );
		}
		return result;
	}


}
