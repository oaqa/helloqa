package edu.cmu.lti.oaqa.openqa.dso.framework.factory;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.framework.ComponentPipeline;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.decorator.MessageAndStopWatchDecorator;

/**
 * Factory for creating the end-to-end pipeline
 * @author Hideki Shima
 *
 */
public class EndToEndPipelineFactory {

  public enum Component {AnswerTypeExtractor, ICEventExtractor, KeytermExtractor, 
    RetrievalStrategist, InformationExtractor, AnswerGenerator }
    
  /**
   * Creates the end-to-end system pipeline given configuration
   * 
   * @param jsonText
   *        jsonText is a configuration written in json,
   *        which looks like <code>{"AnswerTypeExtractor": {id: "stable"}, ... }</code>
   * @return pipeline component
   */
  public static IComponent create( String jsonText ) {
    ComponentPipeline pipeline = new ComponentPipeline("Overall System");
    try {
      JSONObject json = new JSONObject(jsonText);
      pipeline.add(create( json, Component.AnswerTypeExtractor ));
      pipeline.add(create( json, Component.KeytermExtractor ));
      pipeline.add(create( json, Component.ICEventExtractor ));
      pipeline.add(create( json, Component.RetrievalStrategist ));
      pipeline.add(create( json, Component.InformationExtractor ));
      pipeline.add(create( json, Component.AnswerGenerator ));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new MessageAndStopWatchDecorator(pipeline);
  }
  
  private static IComponent create( JSONObject json, Component c ) throws JSONException {
    AbstractComponentFactory factory = getFactory(c);
    IComponent component = factory.create( json.getJSONObject(c.toString()) );
    return component;
  }
  
  private static AbstractComponentFactory getFactory( Component c ) {
    switch (c) {
      case AnswerTypeExtractor:
        return new AnswerTypeExtractorFactory();
      case KeytermExtractor:
        return new KeytermExtractorFactory();
      case RetrievalStrategist:
        return new RetrievalStrategistFactory();
      case InformationExtractor:
        return new InformationExtractorFactory();
      case AnswerGenerator:
        return new AnswerGeneratorFactory();
      case ICEventExtractor:
    	  return new ICEventExtractorFactory();
      default:
        return null;
    }
  }
}
