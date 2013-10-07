package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

public final class DSOMeasureCounts {

	private float ans_mrr;

	private float ans_acc;

	private float ans_recall;

	private float passage_recall;

	private float psg_err;

	private float ie_err;

	private float ans_err;

	private int count;

	DSOMeasureCounts() {
		this(0, 0, 0, 0, 0, 0, 0, 0);
	}

	public DSOMeasureCounts(float ans_mrr, float ans_acc, float ans_recall,
			float passage_recall, float ans_err, float ie_err,
			float psg_err, int num) {
		this.ans_mrr = ans_mrr;
		this.ans_acc = ans_acc;
		this.ans_recall = ans_recall;
		this.passage_recall = passage_recall;
		this.ans_err=ans_err;
		this.ie_err=ie_err;
		this.psg_err=psg_err;
		this.count = num;
	}

	void update(DSOMeasureCounts other) {
		ans_mrr += other.ans_mrr;
		ans_acc += other.ans_acc;
		ans_recall += other.ans_recall;
		passage_recall += other.passage_recall;

		this.ans_err+=other.ans_err;
		this.ie_err+=other.ie_err;
		this.psg_err+=other.psg_err;
		
		count += other.count;
	}

	public float getAnsMRR() {
		return ans_mrr;
	}

	public float getAnsAcc() {
		return ans_acc;
	}

	public float getsAnsRecall() {
		return ans_recall;
	}

	public float getsPassageRecall() {
		return passage_recall;
	}

	public float getsPsgErr() {
		return psg_err;
	}

	public float getsIEErr() {
		return ie_err;
	}

	public float getsAnsErr() {
		return ans_err;
	}

	public int getCount() {
		return count;
	}
}