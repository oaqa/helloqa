package edu.cmu.lti.oaqa.openqa.dso.phase.ie;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.extractor.ICandidateExtractor;
import edu.cmu.lti.oaqa.openqa.dso.extractor.LeafErrAnalysis;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.AbstractInformationExtractor;
import edu.cmu.lti.oaqa.openqa.dso.util.ClassUtil;
import edu.cmu.lti.oaqa.openqa.dso.util.FileUtil;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

public class ErrAnalysisInformationExtractor extends AbstractInformationExtractor {

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	private String extractClassName;
	private String leafClassNames;
	
	private HashMap<String, String[]> answerKeyMap=new HashMap<String, String[]>();

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		initialize();
		this.extractClassName = (String) aContext
				.getConfigParameterValue("extractor");
		if (aContext.getConfigParameterValue("leaves") != null)
			this.leafClassNames = (String) aContext
					.getConfigParameterValue("leaves");
	}

	@Override
	public void initialize() {
		String filePathName = "src/main/resources/gs/dso-extension-answerkey.txt";
		List<String> lines = FileUtil.readFile(filePathName);
		for (int i = 0; i < lines.size(); i++) {
			String psggs = lines.get(i);
			int index = psggs.indexOf(" ");
			String id = "";
			for(int j=0;j<psggs.indexOf(" ");j++){
				if(psggs.charAt(j)<='9'&&psggs.charAt(j)>='0'){
					id+=String.valueOf(psggs.charAt(j));
				}
			}
			String[] temp=psggs.substring(index + 1).split("\\|");
			answerKeyMap.put(id,temp);
		}
	}

	@Override
	public List<AnswerCandidate> extractAnswerCandidates(String qid, String icEvent, String questionText,
			String answerType, List<String> keyterms, List<String> keyphrases,
			List<RetrievalResult> documents) {

		// build arg
		SupportingEvidenceArg arg = new SupportingEvidenceArg(icEvent, questionText,
				answerType, keyterms, keyphrases, documents,
				this.leafClassNames);

		ICandidateExtractor candidateExtractor = ClassUtil.factory(
				extractClassName, arg);
		List<AnswerCandidate> candidates = candidateExtractor
				.getAnswerCandidates(arg);
		
		arg.setGsCandidates(answerKeyMap.get(qid));
		LeafErrAnalysis errAnalyissExtractor=new LeafErrAnalysis(arg);
		candidates.addAll(errAnalyissExtractor.getAnswerCandidates(arg));

		LOGGER.info("  Extracted " + candidates.size() + " candidates.");
		return candidates;
	}

}
