package edu.cmu.lti.oaqa.openqa.dso.data;

import java.util.List;

public class SupportingEvidenceArg {

	private String questionText;
	private String answerType;
	private String[] nes;
	private List<String> keywords;
	private List<String> keyphrases;

	private String passages;
	private String previousSentence;
	private String currentSentence;
	private String nextSentence;

	private double combinedScore;

	public SupportingEvidenceArg(String questionText, String answerType,
			String[] nes, List<String> keywords, List<String> keyphrases,
			String passages, String previousSentence, String currentSentence,
			String nextSentence) {
		this.questionText = questionText;
		this.answerType = answerType;
		this.nes = nes;
		this.keywords = keywords;
		this.keyphrases = keyphrases;
		this.passages = passages;
		this.previousSentence = previousSentence;
		this.currentSentence = currentSentence;
		this.nextSentence = nextSentence;
	}

	public String getAnswerType() {
		return answerType;
	}

	public void setNEs(String answerType) {
		this.answerType = answerType;
	}

	public String[] getNEs() {
		return nes;
	}

	public void setNEs(String[] nes) {
		this.nes = nes;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setQuestionText(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getKeyphrases() {
		return keyphrases;
	}

	public void setKeyphrases(List<String> keyphrases) {
		this.keyphrases = keyphrases;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getPassages() {
		return passages;
	}

	public String getPreviousSentence() {
		return previousSentence;
	}

	public String getCurrentSentence() {
		return currentSentence;
	}

	public String getNextSentence() {
		return nextSentence;
	}

	public double getCombinedScore() {
		return combinedScore;
	}

	public void setCombinedScore(double score) {
		combinedScore = score;
	}
}
