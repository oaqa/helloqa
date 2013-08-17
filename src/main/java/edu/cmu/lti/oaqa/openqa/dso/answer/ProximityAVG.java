package edu.cmu.lti.oaqa.openqa.dso.answer;

import java.util.ArrayList;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public class ProximityAVG implements IAnswerScorer{
	public double[] getScore(SupportingEvidenceArg arg) {
		int nesLength=arg.getNEs().length;
		String sentenceText = arg.getCurrentSentence().toLowerCase();

		// ne positions
		int[] nesPosition = new int[nesLength];
		int currentNEPosition = 0;
		for (int i = 0; i < nesLength; i++) {
			nesPosition[i] = sentenceText.indexOf(arg.getNEs()[i].toLowerCase(),
					currentNEPosition);
			if (nesPosition[i] == -1) {
				nesPosition[i] = 0;
			}
			currentNEPosition = nesPosition[i] + arg.getNEs()[i].length();
		}

		// keyterm positions
		ArrayList<Double> keytermPositions = new ArrayList<Double>();
		for (int i = 0; i < arg.getKeywords().size(); i++) {
			String[] temp = sentenceText.split(arg.getKeywords().get(i).toLowerCase());
			double sumpos = 0;
			for (int j = 0; j < temp.length - 1; j++) {
				sumpos += temp[j].length();
			}
			if (temp.length > 1) {
				keytermPositions.add(new Double(sumpos / (temp.length - 1)));
			}
		}

		if (keytermPositions.size() < 1 || nesPosition.length < 1) {
			return new double[nesLength];
		}

		// compute proximity
		double sumproximity = 0;
		double[] proximity = new double[nesLength];
		for (int i = 0; i < nesPosition.length; i++) {
			double sumpos = 0;
			for (int j = 0; j < keytermPositions.size(); j++) {
				sumpos += Math.abs(((Double) keytermPositions.get(j))
						.doubleValue()
						- nesPosition[i]);
			}
			proximity[i] = sumpos;
			sumproximity += sumpos;
		}

		// normalize proximity
		double norm = 0;
		for (int i = 0; i < proximity.length; i++) {
			if (sumproximity == 0) {
				proximity[i] = 0;
			} else {
				proximity[i] = arg.getCombinedScore()
						* (1 - proximity[i] / sumproximity);
			}
			norm += proximity[i];
		}

		if (norm == 0) {
			return new double[nesLength];
		}

		for (int i = 0; i < proximity.length; i++) {
			proximity[i] = arg.getCombinedScore()*proximity[i] / norm;
		}

		return proximity;
	}
}
