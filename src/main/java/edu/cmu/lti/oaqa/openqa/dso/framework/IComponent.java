package edu.cmu.lti.oaqa.openqa.dso.framework;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

/**
 * All components must implement this interface
 * @author Hideki Shima
 *
 */
public interface IComponent {

  void initialize();
  
  void process( JCas jcas ) throws AnalysisEngineProcessException;
  
  /**
   * Get the unique identifier of the component
   * @return component id
   */
  String getComponentId();
  
}
