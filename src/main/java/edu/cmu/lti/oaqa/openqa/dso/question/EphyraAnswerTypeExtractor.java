package edu.cmu.lti.oaqa.openqa.dso.question;

import org.apache.log4j.Logger;

import edu.cmu.lti.oaqa.openqa.dso.framework.base.AnswerTypeExtractor_ImplBase;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

public class EphyraAnswerTypeExtractor extends AnswerTypeExtractor_ImplBase {

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	@Override
	public void initialize() {
	}

	@Override
	public String extractAnswerType(String question) {
		String answerType="NElocation";
		LOGGER.info("  Answer type: " + answerType);
		return answerType;
	}

	@Override
	public String getComponentId() {
		return "Answer Type Extractor";
	}
}
