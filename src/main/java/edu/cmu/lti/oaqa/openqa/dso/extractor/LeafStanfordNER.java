package edu.cmu.lti.oaqa.openqa.dso.extractor;

import info.ephyra.io.MsgPrinter;
import info.ephyra.nlp.NETagger;
import info.ephyra.nlp.OpenNLP;
import info.ephyra.nlp.StanfordNeTagger;
import info.ephyra.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.InfoBoxResult;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.scorer.AnswerCandidateScorer;
import edu.cmu.lti.oaqa.openqa.dso.util.FilterUtils;
import edu.stanford.nlp.ling.CoreLabel;

public class LeafStanfordNER extends CandidateExtractorBase {

	public LeafStanfordNER(SupportingEvidenceArg arg) {
		super(arg);
	}

	@Override
	public List<AnswerCandidate> getAnswerCandidates(SupportingEvidenceArg arg) {
		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();

		int rank = 1;
		for (RetrievalResult document : arg.getPassages()) {
			// split search result into sentences and tokenize sentences
			String documentText = document.getText().replace(" ... ", " ! ");

			// get refined sentence
			String[] sentences = detectSentences(documentText);

			arg.setPsg(document.getDocID(), sentences);

			tokens = getTokens(sentences);
			nes = extractLOC(sentences);

			List<AnswerCandidate> currentCandidates = AnswerCandidateScorer
					.getAnswerCandidates(arg, getTypeName(), nes, rank);
			for (AnswerCandidate candidate : currentCandidates) {
				candidate.addRetrievalResult(document);
			}

			candidates.addAll(currentCandidates);
			rank++;
		}

		return candidates;
	}

	private String[][] extractLOC(String[] sentences) {
		String[][] nes = new String[sentences.length][];
		for (int i = 0; i < sentences.length; i++) {
			HashSet<String> neSet = new HashSet<String>();
			List<String> neList = new ArrayList<String>();

			String[][] currentNEs = new String[tokens.length][];

			for (int s = 0; s < tokens.length; s++) {
				HashMap<String, String[]> allStanfordNEs = StanfordNER
						.extractNEs(StringUtils.concatWithSpaces(tokens[s]));

				String[] stanfordNEs = allStanfordNEs.get("NElocation");
				if (stanfordNEs == null)
					stanfordNEs = new String[0];
				currentNEs[s] = stanfordNEs;
			}

			// untokenize NEs
			for (int j = 0; j < currentNEs[i].length; j++) {
				currentNEs[i][j] = OpenNLP.untokenize(currentNEs[i][j],
						sentences[i]);
			}

			for (int j = 0; j < currentNEs[i].length; j++) {
				String candidateStr = currentNEs[i][j].trim();
				// ignores illogical candidates
				if ((FilterUtils.isSourceName(candidateStr) || candidateStr
						.length() <= 1)) {
					continue;
				} else {
					if (!neSet.contains(candidateStr)) {
						neList.add(candidateStr);
					}
				}
			}

			neSet.addAll(neList);

			nes[i] = neList.toArray(new String[neList.size()]);
		}
		return nes;
	}

	@Override
	public String getTypeName() {
		return "StanfordNLP-v3.3.0";
	}
}
