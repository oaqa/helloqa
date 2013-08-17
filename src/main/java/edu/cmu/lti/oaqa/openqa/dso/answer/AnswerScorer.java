package edu.cmu.lti.oaqa.openqa.dso.answer;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public class AnswerScorer {
	private static double[] scores;
	private static final int LAMADA = 1;
	private static final double BETA = 0.4;

	public static double[] getDefaultScore(SupportingEvidenceArg arg) {
		int length = arg.getNEs().length;
		double[] scores = new double[length];
		for (int i = 0; i < length; i++) {
			scores[i] = 1;
		}
		return scores;
	}

	public static double[] getUnigramScore(SupportingEvidenceArg arg) {
		SimilarityUnigram unigram = new SimilarityUnigram();
		scores = unigram.getScore(arg);
		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);
		return scores;
	}

	public static double[] getBigramScore(SupportingEvidenceArg arg) {
		SimilarityBigrams bigrams = new SimilarityBigrams();
		scores = bigrams.getScoreFromPassage(arg);
		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);
		return scores;
	}

	public static double[] getFusionUnigramBigramScore(SupportingEvidenceArg arg) {
		scores = new double[arg.getNEs().length];

		SimilarityUnigram unigram = new SimilarityUnigram();
		double[] unigramScores = unigram.getScore(arg);

		SimilarityBigrams bigrams = new SimilarityBigrams();
		double[] bigramScores = bigrams.getScoreFromPassage(arg);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = unigramScores[i] + bigramScores[i];
		}

		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);

		return scores;
	}

	public static double[] getProximityAVGScore(SupportingEvidenceArg arg) {
		ProximityAVG proximity = new ProximityAVG();
		scores = proximity.getScore(arg);
		return scores;
	}

	public static double[] getFusionUnigramProximityScore(
			SupportingEvidenceArg arg) {
		scores = new double[arg.getNEs().length];

		SimilarityUnigram unigram = new SimilarityUnigram();
		double[] unigramScores = unigram.getScore(arg);

		if (scores.length > 0)
			arg.setCombinedScore(unigramScores[0]);

		ProximityAVG proximity = new ProximityAVG();
		double[] proximityScores = proximity.getScore(arg);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = normalizedByKeywords(unigramScores[i], arg
					.getKeywords().size())
					+ 0.1 * proximityScores[i];
		}

		return scores;
	}

	public static double[] getFusionUnigramBigramPrximityScore(
			SupportingEvidenceArg arg) {
		scores = new double[arg.getNEs().length];

		SimilarityUnigram unigram = new SimilarityUnigram();
		double[] unigramScores = unigram.getScore(arg);

		SimilarityBigrams bigrams = new SimilarityBigrams();
		double[] bigramScores = bigrams.getScoreFromPassage(arg);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = unigramScores[i] + bigramScores[i];
		}

		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);

		ProximityAVG proximity = new ProximityAVG();
		double[] proximityScores = proximity.getScore(arg);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = normalizedByKeywords(scores[i], arg.getKeywords()
					.size())
					+ 0.1 * proximityScores[i];
		}

		return scores;
	}

	private static double normalizedByKeywords(double evidenceScore,
			double keytermSize) {
		double score = (Math.pow(evidenceScore / ((double) keytermSize), 2.0));
		return score;
	}

	public static double normalization(double evidenceScore,
			double keytermSize, int rank) {
		double passageRankNormScore = Math.pow(rank + LAMADA, BETA);
		double score = evidenceScore / passageRankNormScore;
		return score;
	}
}
