package edu.cmu.lti.oaqa.openqa.dso.framework.decorator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;

public abstract class ComponentDecorator implements IComponent {

	protected IComponent component;

	public ComponentDecorator(IComponent component) {
		this.component = component;
	}

	@Override
	public String getComponentId() {
		return component.getComponentId();
	}

	@Override
	public void initialize() {
		component.initialize();
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		component.process(jcas);
	}

}
