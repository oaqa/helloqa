package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

public final class DSOEvaluationData {

	private final float ans_mrr;

	private final float ans_acc;

	private final float ans_recall;

	private final float passage_recall;

	private final float count;

	DSOEvaluationData(float ans_mrr, float ans_acc, float ans_recall, float passage_recall, int count) {
		this.ans_mrr = ans_mrr;
		this.ans_acc = ans_acc;
		this.ans_recall = ans_recall;
		this.passage_recall=passage_recall;
		this.count = count;
	}

	public float getAnsMRR() {
		return ans_mrr;
	}

	public float getAnsAcc() {
		return ans_acc;
	}

	public float getAnsRecall() {
		return ans_recall;
	}
	
	public float getPassageRecall() {
		return passage_recall;
	}

	public float getCount() {
		return count;
	}
}