package edu.cmu.lti.oaqa.openqa.hello.gerp.passage;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import edu.cmu.lti.oaqa.cse.basephase.gerp.GerpComponent;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import org.apache.uima.resource.ResourceInitializationException;

public class PassageGerpComponent extends GerpComponent<PassageCandidate> {

	  @Override
	  public void initialize(UimaContext c) throws ResourceInitializationException {
	    super.initialize(c);
	  }

	  @Override
	  public void process(JCas jcas) throws AnalysisEngineProcessException {
	    super.process(jcas);
	  }
	  
}
