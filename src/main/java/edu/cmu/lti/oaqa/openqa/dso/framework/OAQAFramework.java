package edu.cmu.lti.oaqa.openqa.dso.framework;

import info.ephyra.io.MsgPrinter;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.oaqa.dso.model.Question;

import edu.cmu.lti.oaqa.openqa.dso.framework.factory.EndToEndPipelineFactory;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;
import edu.cmu.lti.oaqa.openqa.dso.util.TimeKeeper;

public class OAQAFramework extends JCasAnnotator_ImplBase {

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	private static final String PARAM_SYSTEM_CONFIG = "SystemConfiguration";

	private IComponent pipeline;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			String configFilePath = ((String) aContext
					.getConfigParameterValue(PARAM_SYSTEM_CONFIG)).trim();
			String configText = FileUtils.file2String(new File(configFilePath));
			pipeline = EndToEndPipelineFactory.create(configText);
			LOGGER.info("\n------------------ Initialization ------------------");
			pipeline.initialize();
			MsgPrinter.enableErrorMsgs(true);
			int time = TimeKeeper.getInstance().getProcessTime("init",
					pipeline.getComponentId());
			LOGGER.info("Initialization done in " + (time / 1000D) + " sec.");
			System.out.println();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			Question q = JCasManipulator.loadQuestion(ViewManager
					.getQuestionView(jcas));
			LOGGER.info("\n------------------ QID: " + q.getId()
					+ " ------------------");
			LOGGER.info("Input question: \"" + q.getCoveredText() + "\"\n");

			pipeline.process(jcas);
			int time = TimeKeeper.getInstance().getProcessTime(q.getId(),
					pipeline.getComponentId());
			LOGGER.info("> Done in " + (time / 1000D) + " sec.");
			System.out.println();
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
