package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.util.FilterUtils;

import info.ephyra.nlp.NETagger;
import info.ephyra.nlp.OpenNLP;

public abstract class CandidateExtractorByAnswerType extends CandidateExtractorBase {

	public CandidateExtractorByAnswerType(String answerType, String[] sentences) {
		if (NETypePatterns == null) {
			initialize();
		}
	}

	protected String[][] extractCandidatesUsingPatterns(int[] nePatternIds,
			String[] sentences) {
		// Extract NEs of the types in the 'enhanced' list of patterns
		String[][] nes=new String[sentences.length][];
		for (int i = 0; i < sentences.length; i++) {
			HashSet<String> neSet=new HashSet<String>();
			List<String> neList=new ArrayList<String>();
			for (int neId : nePatternIds) {
				String[][] currentNEs = NETagger.extractNes(tokens, neId);

				// untokenize NEs
				for (int j = 0; j < currentNEs[i].length; j++) {
					currentNEs[i][j] = OpenNLP.untokenize(currentNEs[i][j], sentences[i]);
				}
				for (int j = 0; j < currentNEs[i].length; j++) {
					String candidateStr = currentNEs[i][j].trim();
					// ignores illogical candidates
					if ((FilterUtils.isSourceName(candidateStr)
							|| candidateStr.length() <= 1)) {
						continue;
					}else{
						if(!neSet.contains(candidateStr)){
							neList.add(candidateStr);
						}
					}
				}
				
				neSet.addAll(neList);
			}
			nes[i]=neList.toArray(new String[neList.size()]);
		}
		return nes;
	}

	@Override
	public String[][] getAnswerCandidates() {
		return nes;
	}

	protected List<int[]> getNEType(String answerType) {
		String[] neTypes = answerType.split("->");
		List<int[]> nePatterns = new ArrayList<int[]>();
		int neIds[] = new int[0];
		for (String neType : neTypes) {
			int[] thisIds = NETagger.getNeIds(neType);
			if (thisIds.length > 0)
				neIds = thisIds;
		}
		nePatterns.add(neIds);

		// adds extraction patterns from backoff NEtype for given answer type
		/*if (CandidateExtractorByAnswerType.NETypePatterns.containsKey(answerType)) {
			String[] backoffNETypes = CandidateExtractorByAnswerType.NETypePatterns.get(answerType)
					.split("->");
			int[] thisIds = NETagger
					.getNeIds(backoffNETypes[backoffNETypes.length - 1]);
			if (thisIds.length > 0)
				nePatterns.add(thisIds);
		}
		*/
		return nePatterns;
	}
}
