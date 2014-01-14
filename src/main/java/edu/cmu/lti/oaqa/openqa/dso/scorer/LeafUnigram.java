package edu.cmu.lti.oaqa.openqa.dso.scorer;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import info.ephyra.nlp.SnowballStemmer;

public class LeafUnigram implements IAnswerScorer {

	public Score[] getScore(SupportingEvidenceArg arg) {
		SnowballStemmer.create();

		Score[] scores = new Score[arg.getNEs().length];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = new Score(arg.getKeywords().size());
		}

		double currentNEScore = 0;
		String currentSentence = SnowballStemmer.stemAllTokens(arg
				.getCurrentSentence().toLowerCase());
		String previousSentence = SnowballStemmer.stemAllTokens(arg
				.getPreviousSentence().toLowerCase());
		String nextSentence = SnowballStemmer.stemAllTokens(arg
				.getNextSentence().toLowerCase());

		for (String keyword : arg.getKeywords()) {
			keyword = SnowballStemmer.stem(keyword).toLowerCase();
			if (currentSentence.contains(keyword)) {
				currentNEScore += 2;
				continue;
			}
			if (nextSentence.contains(keyword)) {
				currentNEScore += 1;
				continue;
			}
			if (previousSentence.contains(keyword)) {
				currentNEScore += 1;
				continue;
			}
		}

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i].setScore(currentNEScore);
		}

		return scores;
	}
	
}
