package edu.cmu.lti.oaqa.openqa.dso.scorer;

import java.util.ArrayList;

public class Score {
	double score;
	String psgID;
	ArrayList<String> distInfo;

	public Score(int keywordSize) {
		this.distInfo = new ArrayList<String>(keywordSize);
	}

	public void setPsgID(String psgID) {
		this.psgID = psgID;
	}

	public String getPsgID() {
		return this.psgID;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return this.score;
	}

	public void addDistInfo(String distInfo) {
		this.distInfo.add(distInfo);
	}

	public String[] getDistInfo() {
		return this.distInfo.toArray(new String[this.distInfo.size()]);
	}
}
