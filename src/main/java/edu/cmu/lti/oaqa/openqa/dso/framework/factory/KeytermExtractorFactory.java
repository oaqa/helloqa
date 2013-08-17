package edu.cmu.lti.oaqa.openqa.dso.framework.factory;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.decorator.MessageAndStopWatchDecorator;
import edu.cmu.lti.oaqa.openqa.dso.question.EphyraKeytermExtractor;

public class KeytermExtractorFactory extends AbstractComponentFactory {

  @Override
  public IComponent create(JSONObject config) throws JSONException {
    String id = config.getString("id");
    IComponent component = new EphyraKeytermExtractor();
    return new MessageAndStopWatchDecorator( component );
  }

}
