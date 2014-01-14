package edu.cmu.lti.oaqa.openqa.dso.data;

import java.util.List;

public class SupportingEvidenceArg {

	private String icEvent;
	private String questionText;
	private String answerType;
	private String[] nes;
	private List<String> keywords;
	private List<String> keyphrases;

	private List<RetrievalResult> passages;
	
	private String psgID;
	private String[] currentPsgSentences;
	
	private String previousSentence;
	private String currentSentence;
	private String nextSentence;

	private double combinedScore;
	private String extractorClassNames;
	private String scorerClassNames;

	private String[] gsCandidates;

	public SupportingEvidenceArg(String icEvent, String questionText,
			String answerType, List<String> keywords, List<String> keyphrases,
			List<RetrievalResult> passages, String classNames) {
		this.icEvent = icEvent;
		this.questionText = questionText;
		this.answerType = answerType;

		this.keywords = keywords;
		this.keyphrases = keyphrases;

		this.passages = passages;
		this.extractorClassNames = classNames;
	}

	private void setSentences(String previousSentence, String currentSentence,
			String nextSentence) {
		this.previousSentence = previousSentence;
		this.currentSentence = currentSentence;
		this.nextSentence = nextSentence;
	}

	public void updateSupportingEvidenceArg(int index, String[][] nes) {
		// window -1, +1
		String previousSentence = "", currentSentence = "", nextSentence = "";
		if (index - 1 >= 0) {
			previousSentence = this.currentPsgSentences[index - 1];
		}
		currentSentence = this.currentPsgSentences[index];
		if (index + 1 < this.currentPsgSentences.length) {
			nextSentence = this.currentPsgSentences[index + 1];
		}

		setSentences(previousSentence, currentSentence, nextSentence);
		setNEs(nes[index]);
	}

	public String getAnswerType() {
		return answerType;
	}

	public void setPsg(String psgID, String[] sentences){
		this.psgID=psgID;
		this.currentPsgSentences=sentences;
	}
	
	public String getPsgID(){
		return this.psgID;
	}
	
	public void setAnsType(String answerType) {
		this.answerType = answerType;
	}

	public void setGsCandidates(String[] gsCandidates) {
		this.gsCandidates = gsCandidates;
	}

	public String[] getGsCandidates() {
		return this.gsCandidates;
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

	public List<RetrievalResult> getPassages() {
		return passages;
	}
	
	public String[] getPassageSentences(){
		return this.currentPsgSentences;
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

	public String getClassNames() {
		return this.extractorClassNames;
	}

	public String getICEvent() {
		return icEvent;
	}
}
