package edu.cmu.lti.oaqa.openqa.dso.scorer;

import java.util.ArrayList;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public class LeafProximityAVG implements IAnswerScorer {
	public Score[] getScore(SupportingEvidenceArg arg) {
		Score[] scores = new Score[arg.getNEs().length];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = new Score(arg.getKeywords().size());
		}

		int nesLength = arg.getNEs().length;
		String sentenceText = arg.getCurrentSentence().toLowerCase();

		// ne positions
		int[] nesPosition = new int[nesLength];
		int currentNEPosition = 0;
		for (int i = 0; i < nesLength; i++) {
			if (arg.getNEs() == null || arg.getNEs()[i] == null) {
				continue;
			}
			nesPosition[i] = sentenceText.indexOf(
					arg.getNEs()[i].toLowerCase(), currentNEPosition);
			if (nesPosition[i] == -1) {
				nesPosition[i] = 0;
			}
			currentNEPosition = nesPosition[i] + arg.getNEs()[i].length();
		}

		// keyterm positions
		ArrayList<Double> keytermPositions = new ArrayList<Double>();
		for (int i = 0; i < arg.getKeywords().size(); i++) {
			String[] temp = sentenceText.split(arg.getKeywords().get(i)
					.toLowerCase());
			double sumpos = 0;
			for (int j = 0; j < temp.length - 1; j++) {
				sumpos += temp[j].length();
			}
			if (temp.length > 1) {
				keytermPositions.add(new Double(sumpos / (temp.length - 1)));
			}
		}

		if (keytermPositions.size() < 1 || nesPosition.length < 1) {
			return new Score[nesLength];
		}

		// compute proximity
		double sumproximity = 0;
		Score[] proximity = new Score[nesLength];
		for (int i = 0; i < nesPosition.length; i++) {
			double sumpos = 0;
			for (int j = 0; j < keytermPositions.size(); j++) {
				sumpos += Math.abs(((Double) keytermPositions.get(j))
						.doubleValue() - nesPosition[i]);
			}
			proximity[i].setScore(sumpos);
			sumproximity += sumpos;
		}

		// normalize proximity
		double norm = 0;
		for (int i = 0; i < proximity.length; i++) {
			if (sumproximity == 0) {
				proximity[i].setScore(0);
			} else {
				proximity[i].setScore(arg.getCombinedScore()
						* (1 - proximity[i].getScore() / sumproximity));
			}
			norm += proximity[i].getScore();
		}

		if (norm == 0) {
			return new Score[nesLength];
		}

		for (int i = 0; i < proximity.length; i++) {
			proximity[i].setScore(arg.getCombinedScore()
					* proximity[i].getScore() / norm);
		}

		return proximity;
	}
}
