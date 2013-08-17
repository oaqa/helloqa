package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;

public abstract class InformationExtractor_ImplBase implements IComponent {

  public abstract void initialize();
  
  public abstract List<AnswerCandidate> extractAnswerCandidates(String questionText,
			String answerType, List<String> keyterms, List<String> keyphrases,
			List<RetrievalResult> documents);
  public abstract List<RetrievalResult> extractStructuredCandidates();
  
  protected String icEvent;
  
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    try {
        JCas questionView = ViewManager.getQuestionView(jcas);
        JCas documentView = ViewManager.getDocumentView(jcas);
        String questionText = questionView.getDocumentText();
        String answerType = JCasManipulator.loadAnswerType(questionView);
        this.icEvent = JCasManipulator.loadIcEvent(questionView);
        List<String> keyterms = JCasManipulator.loadKeyterms(questionView);
        List<String> keyphrases = JCasManipulator.loadKeyphrases(questionView);
        List<RetrievalResult> documents = JCasManipulator.loadDocuments(documentView);
        
        List<AnswerCandidate> answers = extractAnswerCandidates(
      		  questionText, answerType, keyterms, keyphrases, documents);
        JCas candidateView = ViewManager.getCandidateView(jcas);
        JCasManipulator.storeCandidates(candidateView, answers);
        
        
        //Store updated documents here, got problem
        
        List<RetrievalResult> structuredAns = extractStructuredCandidates();
        
        List<RetrievalResult> newDoc = new ArrayList<RetrievalResult>(documents);
        newDoc.addAll(structuredAns);
        
        JCasManipulator.storeDocuments(documentView, newDoc);
        
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
  
}
