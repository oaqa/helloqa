package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;

public abstract class RetrievalStrategist_ImplBase implements IComponent {
  
  public abstract void initialize();
  
  public abstract List<RetrievalResult> retrieveDocuments( List<String> keyterms );
  
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    try {
      JCas questionView = ViewManager.getQuestionView(jcas);
      List<String> keyterms = JCasManipulator.loadKeyterms(questionView);
      // Collection of retrieved documents from rs module's facade
      List<RetrievalResult> documents = retrieveDocuments(keyterms);
      JCas documentView = ViewManager.getDocumentView(jcas);
      JCasManipulator.storeDocuments(documentView, documents);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
  
}
