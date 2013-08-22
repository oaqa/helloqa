package edu.cmu.lti.oaqa.openqa.dso.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;

/**
 * @author Rui Liu
 * 
 */
public class Evaluation {
	private double AnswerRecall;
	private double[] TopK;
	private double MMR;
	private final int K = 10;
	private int count;

	/**
* 
*/
	public Evaluation(HashMap<String, List<AnswerCandidate>> finalAnswers,
			HashMap<String, Pattern> answeKeyHashMap) {
		this.TopK = new double[this.K];
		int[] correctCountMat = new int[this.K + 1];
		HashMap<String, boolean[]> judgements = new HashMap<String, boolean[]>();
		Iterator<String> iter = answeKeyHashMap.keySet().iterator();
		while (iter.hasNext()) {
			String qid = iter.next();
			Pattern answerKey = answeKeyHashMap.get(qid);
			List<AnswerCandidate> finalAnswer = finalAnswers.get(qid);
			boolean[] judgement = new boolean[finalAnswer.size()];
			judgements.put(qid, judgement);
			for (int i = 0; i < finalAnswer.size(); i++) {
				String candidate = finalAnswer.get(i).getText().toLowerCase();
				if (answerKey.matcher(candidate).matches() && i == 0) {
					count++;
					System.out.println(count + " **********");
				}
				judgement[i] = judgeCandidateCorrectness(candidate, answerKey);
			}
			// 0.0 counting for answer recall
			correctCountMat[this.K] += TopK(judgement.length, judgement);
			// 0.1 counting for Top1-10
			for (int i = 0; i < this.K; i++) {
				correctCountMat[i] += TopK(i + 1, judgement);
			}
			// 0.2 counting for MMR
			this.MMR += MMR(judgement);
		}
		// 1. answer recall
		this.AnswerRecall = (1.0 * correctCountMat[this.K])
				/ (1.0 * answeKeyHashMap.size());
		// 2. Top1-10
		for (int i = 0; i < this.K; i++) {
			this.TopK[i] = (1.0 * correctCountMat[i])
					/ (1.0 * answeKeyHashMap.size());
		}
		// 3. MMR
		this.MMR = this.MMR / (1.0 * answeKeyHashMap.size());
	}

	private double MMR(boolean[] judgement) {
		double score = 0;
		for (int i = 0; i < Math.min(judgement.length, 5); i++) {
			if (judgement[i]) {
				score += 1.0 / (1.0 * (i + 1));
				break;
			}
		}
		return score;
	}

	private int TopK(int k, boolean[] judgement) {
		int correctCount = 0;
		for (int i = 0; i < Math.min(judgement.length, k); i++) {
			if (judgement[i]) {
				correctCount++;
				break;
			}
		}
		return correctCount;
	}

	private boolean judgeCandidateCorrectness(String candidate,
			Pattern answerKeyPattern) {
		if (answerKeyPattern.matcher(candidate).matches()) {
			return true;
		}
		return false;
	}

	public double[] getTop1to10() {
		return this.TopK;
	}

	public double getAnswerRecall() {
		return this.AnswerRecall;
	}

	public double getMRR() {
		return this.MMR;
	}
}
