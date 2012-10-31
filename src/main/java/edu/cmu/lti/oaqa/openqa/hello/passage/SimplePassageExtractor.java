package edu.cmu.lti.oaqa.openqa.hello.passage;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.cse.basephase.ie.AbstractPassageExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;

/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public class SimplePassageExtractor extends AbstractPassageExtractor {

  protected SolrWrapper wrapper;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    String serverUrl = (String) aContext.getConfigParameterValue("server");
    Integer serverPort = (Integer) aContext.getConfigParameterValue("port");
    Boolean embedded = (Boolean) aContext.getConfigParameterValue("embedded");
    String core = (String) aContext.getConfigParameterValue("core");
    // String keytermWindowScorer = (String)aContext.getConfigParameterValue( "keytermWindowScorer"
    // );
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
      try {
        // @Alkesh: can you add this call to the SolrWrapper API? - Now work with solr-provider 1.0.5-SNAPSHOT
        String text = wrapper.getDocText( id );
        System.out.println(text);
        PassageCandidateFinder finder = new PassageCandidateFinder( id , text , new KeytermWindowScorerSum() );
        // @EHN: to avoid ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String;
        List<String> keytermStrings = Lists.transform(keyterms, new Function<Keyterm, String>() {
          public String apply(Keyterm keyterm) { return keyterm.getText(); }
        });
        List<PassageCandidate> passageSpans = finder.extractPassages( keytermStrings.toArray(new String[0]) );
        for ( PassageCandidate passageSpan : passageSpans )
          result.add( passageSpan );
      } catch (SolrServerException e) {
        e.printStackTrace();
      }
		}
		return result;
	}

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    wrapper.close();
  }
  
}
