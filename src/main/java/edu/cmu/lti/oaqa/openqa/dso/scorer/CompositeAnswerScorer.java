package edu.cmu.lti.oaqa.openqa.dso.scorer;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.extractor.ICandidateExtractor;
import edu.cmu.lti.oaqa.openqa.dso.util.ClassUtil;

public class CompositeAnswerScorer implements IAnswerScorer {
	private static double[] scores;

	private List<IAnswerScorer> candidateScorerLs = new ArrayList<IAnswerScorer>();

	private String[] classNames;

	public CompositeAnswerScorer(SupportingEvidenceArg arg) {
		this.classNames = arg.getClassNames().split(",");
		for (String className : this.classNames) {
			candidateScorerLs.add(ClassUtil.scorerFactory(className, arg));
		}
	}

	@Override
	public Score[] getScore(SupportingEvidenceArg arg) {
		Score[] scores = new Score[arg.getNEs().length];
		for (int i = 0; i < candidateScorerLs.size(); i++) {
			for (int j = 0; j < scores.length; j++) {
				scores[j].setScore(candidateScorerLs.get(i).getScore(arg)[j]
						.getScore() + scores[j].getScore());
			}
		}
		return scores;
		
//		LeafAnswerValidation av = new LeafAnswerValidation();
//		return av.getScore(arg);
	}

	public static double[] getDefaultScore(SupportingEvidenceArg arg) {
		int length = arg.getNEs().length;
		double[] scores = new double[length];
		for (int i = 0; i < length; i++) {
			scores[i] = 1;
		}
		return scores;
	}

	public static double[] getUnigramScore(SupportingEvidenceArg arg) {
		LeafUnigram unigram = new LeafUnigram();

		for (int i = 0; i < scores.length; i++) {
			scores[i] = unigram.getScore(arg)[i].getScore();
		}

		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);
		return scores;
	}

	public static double[] getBigramScore(SupportingEvidenceArg arg,
			String passage) {
		LeafBigrams bigrams = new LeafBigrams();
		scores = bigrams.getScoreFromPassage(arg, passage);
		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);
		return scores;
	}

	public static double[] getFusionUnigramBigramScore(
			SupportingEvidenceArg arg, String passage) {
		scores = new double[arg.getNEs().length];

		LeafUnigram unigram = new LeafUnigram();
		Score[] unigramScores = unigram.getScore(arg);

		LeafBigrams bigrams = new LeafBigrams();
		double[] bigramScores = bigrams.getScoreFromPassage(arg, passage);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = unigramScores[i].getScore() + bigramScores[i];
		}

		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);

		return scores;
	}

	public static Score[] getProximityAVGScore(SupportingEvidenceArg arg) {
		LeafProximityAVG proximity = new LeafProximityAVG();
		return proximity.getScore(arg);
	}

	public static double[] getFusionUnigramProximityScore(
			SupportingEvidenceArg arg) {
		scores = new double[arg.getNEs().length];

		LeafUnigram unigram = new LeafUnigram();
		Score[] unigramScores = unigram.getScore(arg);

		if (scores.length > 0)
			arg.setCombinedScore(unigramScores[0].getScore());

		LeafProximityAVG proximity = new LeafProximityAVG();
		Score[] proximityScores = proximity.getScore(arg);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = normalizedByKeywords(unigramScores[i].getScore(), arg
					.getKeywords().size())
					+ 0.1 * proximityScores[i].getScore();
		}

		return scores;
	}

	public static Score[] getAnswerValidationScore(SupportingEvidenceArg arg) {
		LeafAnswerValidation av = new LeafAnswerValidation();
		return av.getScore(arg);
	}

	public static double[] getFusionUnigramBigramPrximityScore(
			SupportingEvidenceArg arg, String passage) {

		LeafUnigram unigram = new LeafUnigram();
		Score[] unigramScores = unigram.getScore(arg);

		LeafBigrams bigrams = new LeafBigrams();
		double[] bigramScores = bigrams.getScoreFromPassage(arg, passage);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = unigramScores[i].getScore() + bigramScores[i];
		}

		if (scores.length > 0)
			arg.setCombinedScore(scores[0]);

		LeafProximityAVG proximity = new LeafProximityAVG();
		Score[] proximityScores = proximity.getScore(arg);

		for (int i = 0; i < arg.getNEs().length; i++) {
			scores[i] = normalizedByKeywords(scores[i], arg.getKeywords()
					.size())
					+ 0.1 * proximityScores[i].getScore();
		}

		return scores;
	}

	private static double normalizedByKeywords(double evidenceScore,
			double keytermSize) {
		double score = (Math.pow(evidenceScore / ((double) keytermSize), 2.0));
		return score;
	}

//	public static double normalization(double evidenceScore,
//			double keytermSize, int rank) {
//		double passageRankNormScore = Math.pow(rank + LAMADA, BETA);
//		double score = evidenceScore / passageRankNormScore;
//		return score;
//	}

}
