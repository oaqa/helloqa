package edu.cmu.lti.oaqa.openqa.dso.framework;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

public class ComponentPipeline extends AbstractCompositeComponent {

  private String componentId;
  
  public ComponentPipeline( String componentId, IComponent ... components ) {
    super(components);
    this.componentId = componentId;
  }
  
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    for (IComponent c : components) {
      if (c!=null) c.process(jcas);
    }   
  }

  @Override
  public String getComponentId() {
    return componentId;
  }

}
