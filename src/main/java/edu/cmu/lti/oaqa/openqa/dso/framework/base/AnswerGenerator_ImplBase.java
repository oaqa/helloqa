package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;


public abstract class AnswerGenerator_ImplBase implements IComponent {

  public abstract void initialize();
  
  public abstract 
  List<AnswerCandidate> generateFinalAnswers(String answerType,
          List<String> keyterms, List<AnswerCandidate> answerCandidates);
  
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    try {
      JCas questionView = ViewManager.getQuestionView(jcas);
      JCas candidateView = ViewManager.getCandidateView(jcas);
      JCas finalAnswerView = ViewManager.getFinalAnswerView(jcas);

      String answerType = JCasManipulator.loadAnswerType(questionView);
      List<AnswerCandidate> answerCandidates = JCasManipulator
          .loadAnswerCandidates(candidateView);
      List<String> keyterms = JCasManipulator.loadKeyterms(questionView);
      List<AnswerCandidate> finalAnswers = generateFinalAnswers(
          answerType, keyterms, answerCandidates);
      JCasManipulator.storeFinalAnswers(finalAnswerView, finalAnswers);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
  
}
