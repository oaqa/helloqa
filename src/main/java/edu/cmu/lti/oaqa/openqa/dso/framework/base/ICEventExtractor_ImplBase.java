package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;

public abstract class ICEventExtractor_ImplBase implements IComponent {
	
	  public abstract void initialize();
	  
	  public abstract String extractICEvent(String question, List<String> keyterms);
	  
	  public void process(JCas jcas) throws AnalysisEngineProcessException {
	    try {
	      JCas questionView = ViewManager.getQuestionView(jcas);
	      String questionText = questionView.getDocumentText();
	      
	      List<String> keyterms = JCasManipulator.loadKeyterms(questionView);
	      String ICevent = extractICEvent(questionText, keyterms);
	      
	      JCasManipulator.storeICEvent(questionView, ICevent);
	      
	    } catch (Exception e) {
	      throw new AnalysisEngineProcessException(e);
	    }
	  }

}
