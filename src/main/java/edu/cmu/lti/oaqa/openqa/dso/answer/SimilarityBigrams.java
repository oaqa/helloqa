package edu.cmu.lti.oaqa.openqa.dso.answer;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import info.ephyra.nlp.SnowballStemmer;
public class SimilarityBigrams implements IAnswerScorer{
	public double[] getScoreFromPassage(SupportingEvidenceArg arg) {

		SnowballStemmer.create();

		double[] scores = new double[arg.getNEs().length];
		double currentNEScore = 0;

		String stemedPassage = SnowballStemmer.stemAllTokens(arg.getPassages());
		for (String keyphrase : arg.getKeyphrases()) {
			keyphrase = SnowballStemmer.stemAllTokens(keyphrase);
			if (stemedPassage.contains(keyphrase)) {
				currentNEScore += 1;
			}
		}

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = currentNEScore;
		}

		return scores;
	}

	public double[] getScoreFromFixedWindow(SupportingEvidenceArg arg) {
		// Initialize stemmer
		SnowballStemmer.create();

		double[] scores = new double[arg.getNEs().length];
		double currentNEScore = 0;

		String stemmedSentence = SnowballStemmer.stemAllTokens(arg
				.getCurrentSentence());
		for (String keyphrase : arg.getKeyphrases()) {
			if (stemmedSentence.contains(keyphrase)) {
				currentNEScore += 2;
				continue;
			}
			stemmedSentence = SnowballStemmer.stemAllTokens(arg
					.getNextSentence());
			if (stemmedSentence.contains(keyphrase)) {
				currentNEScore += 1;
				continue;
			}
			stemmedSentence = SnowballStemmer.stemAllTokens(arg
					.getPreviousSentence());
			if (stemmedSentence.contains(keyphrase)) {
				currentNEScore += 1;
				continue;
			}
		}

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = currentNEScore;
		}

		return scores;
	}

	public double[] getScore(SupportingEvidenceArg arg) {

		return null;
	}
}
