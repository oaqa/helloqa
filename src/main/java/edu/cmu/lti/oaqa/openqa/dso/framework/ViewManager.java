package edu.cmu.lti.oaqa.openqa.dso.framework;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

//Responsible for view (nearly equals to sofa) related jobs 
public class ViewManager {

  public final static String QUESTION = "question";
  public final static String DOCUMENT = "document";
  public final static String CANDIDATE = "candidate";
  public final static String FINAL_ANSWER = "final_answer";
  
  public static JCas getQuestionView( JCas jcas ) throws CASException {
    return jcas.getView(QUESTION);
  }
  
  public static void createQuestionView( JCas jcas ) throws CASException {
    jcas.createView(QUESTION);
  }

  public static JCas getDocumentView( JCas jcas ) throws CASException {
    return jcas.getView(DOCUMENT);
  }
  
  public static void createDocumentView( JCas jcas ) throws CASException {
    jcas.createView(DOCUMENT);
  }
  
  public static JCas getCandidateView( JCas jcas ) throws CASException {
    return jcas.getView(CANDIDATE);
  }
  
  public static void createCandidateView( JCas jcas ) throws CASException {
    jcas.createView(CANDIDATE);
  }
  
  public static JCas getFinalAnswerView( JCas jcas ) throws CASException {
    return jcas.getView(FINAL_ANSWER);
  }
  
  public static void createFinalAnswerView( JCas jcas ) throws CASException {
    jcas.createView(FINAL_ANSWER);
  }
  
}
