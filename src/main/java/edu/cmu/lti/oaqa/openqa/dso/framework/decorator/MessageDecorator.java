package edu.cmu.lti.oaqa.openqa.dso.framework.decorator;

import java.util.Stack;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

/**
 * Decorator that adds message showing capability to a component
 * @author Hideki Shima
 *
 */
public class MessageDecorator extends ComponentDecorator {

  private static final Logger LOGGER = Logger.getLogger(LogUtil.getInvokingClassName());

  private static Stack<String> stack = new Stack<String>();
  
  public MessageDecorator( IComponent component ) {
    super(component);
  }
  
  @Override
  public String getComponentId() {
    return component.getComponentId();
  }

  @Override
  public void initialize() {
    LOGGER.info(getIndent()+"Initializing "+getComponentId()+".");
    stack.push(getComponentId());
    component.initialize();
    stack.pop();
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    LOGGER.info("> Running "+getComponentId());
    try {
      component.process(jcas);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
  
  private static String getIndent() {
    StringBuilder sb = new StringBuilder();
    for ( int i=0; i<stack.size(); i++ ) {
      sb.append( "  " );
    }
    return sb.toString();
  }

}
