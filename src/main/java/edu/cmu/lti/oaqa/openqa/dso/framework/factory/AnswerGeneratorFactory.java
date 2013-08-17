package edu.cmu.lti.oaqa.openqa.dso.framework.factory;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.answer.EphyraAnswerGenerator;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.decorator.MessageAndStopWatchDecorator;

public class AnswerGeneratorFactory extends AbstractComponentFactory {

	@Override
	public IComponent create(JSONObject config) throws JSONException {
		String id = config.getString("id");
		IComponent component = new EphyraAnswerGenerator();
		if (component == null)
			LOGGER.error("Factory failed to create Answer Generator.");
		return new MessageAndStopWatchDecorator(component);
	}

}
