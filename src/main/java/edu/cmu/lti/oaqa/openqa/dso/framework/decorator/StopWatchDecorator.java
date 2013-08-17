package edu.cmu.lti.oaqa.openqa.dso.framework.decorator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.oaqa.dso.model.Question;

import edu.cmu.lti.oaqa.framework.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.util.TimeKeeper;
import edu.cmu.lti.oaqa.openqa.dso.util.TimeKeeper.StopWatch;

/**
 * Decorator that adds process-time measuring capability to a component
 * @author Hideki Shima
 *
 */
public class StopWatchDecorator extends ComponentDecorator {

  public StopWatchDecorator( IComponent component ) {
    super(component);
  }
  
  @Override
  public String getComponentId() {
    return component.getComponentId();
  }

  @Override
  public void initialize() {
    StopWatch s = TimeKeeper.createStopWatch("init", getComponentId());
    s.start();
    component.initialize();
    s.stop();
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    try {
      JCas questionView = ViewManager.getQuestionView(jcas);
      Question q = JCasManipulator.loadQuestion(questionView);      
      StopWatch s = TimeKeeper.createStopWatch(q.getId(), getComponentId());
      s.start();
      component.process(jcas);
      s.stop();
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
  
}
