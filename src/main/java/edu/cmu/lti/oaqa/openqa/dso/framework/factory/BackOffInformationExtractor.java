package edu.cmu.lti.oaqa.openqa.dso.framework.factory;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.framework.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.AbstractCompositeComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;

/**
 * Composite information extractor which runs a series of sub components.
 * If a result comes out from a component, it halts.
 * It is recommended that higher-precision approach is applied first.
 * 
 * @author Hideki Shima
 *
 */
public class BackOffInformationExtractor extends AbstractCompositeComponent {

  private String componentId;
  
  public BackOffInformationExtractor( String componentId, IComponent ... components ) {
    super(components);
    this.componentId = componentId;
  }
  
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {    
    if ( components.size()==0 ) return;

    try {
      JCas candidateView = ViewManager.getCandidateView(jcas);
      for (IComponent c : components) {
        c.process(jcas);
        
        if (JCasManipulator.loadAnswerCandidates(candidateView).size() > 0) {
          break;
        }
      }
      
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  @Override
  public String getComponentId() {
    return componentId;
  }
}
