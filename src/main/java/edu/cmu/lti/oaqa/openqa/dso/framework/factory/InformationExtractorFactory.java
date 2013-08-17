package edu.cmu.lti.oaqa.openqa.dso.framework.factory;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.extractor.EphyraInformationExtractor;
import edu.cmu.lti.oaqa.openqa.dso.framework.ComboCandidateFilter;
import edu.cmu.lti.oaqa.openqa.dso.framework.ComponentPipeline;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.decorator.MessageAndStopWatchDecorator;
import edu.cmu.lti.oaqa.openqa.dso.framework.decorator.MessageDecorator;

public class InformationExtractorFactory extends AbstractComponentFactory {

	@Override
	public IComponent create(JSONObject config) throws JSONException {
		String id = config.getString("id");
		BackOffInformationExtractor extractors = new BackOffInformationExtractor(
				"Back-off Information Extractor",
				new MessageAndStopWatchDecorator(
						new EphyraInformationExtractor(config)));
		ComponentPipeline pipeline = new ComponentPipeline(
				"Component and Filter Information Extractor",
				new MessageDecorator(extractors),
				new MessageAndStopWatchDecorator(new ComboCandidateFilter()));
		IComponent component = pipeline;
		return new MessageDecorator(component);
	}

}
