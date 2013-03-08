package edu.cmu.lti.oaqa.openqa.hello.gerp.passage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.Text;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorer;

public class DocumentPassageAnalyzer {

	private int totalMatches, totalKeyterms;
	private String docText;
	private List<Keyterm> keyterms;

	List<List<PassageSpan>> matchingSpans;
	List<PassageSpan> matchedSpans;

	public DocumentPassageAnalyzer(String text, List<Keyterm> keyterms) {
		this.docText = text;
		this.keyterms = keyterms;
		matchingSpans = new ArrayList<List<PassageSpan>>();
		matchedSpans = new ArrayList<PassageSpan>();

		// Find all keyterm matches.
		for (Keyterm keyterm : keyterms) {
			Pattern p = Pattern.compile(keyterm.getText());
			Matcher m = p.matcher(text);
			while (m.find()) {
				PassageSpan match = new PassageSpan(m.start(), m.end());
				matchedSpans.add(match);
				totalMatches++;
			}
			if (!matchedSpans.isEmpty()) {
				matchingSpans.add(matchedSpans);
				totalKeyterms++;
			}
		}
	}

	public void computeMatches(List<PassageCandidate> list,
			KeytermWindowScorer scorer) {
		for (PassageCandidate p : list) {
			int keytermsFound = 0;
			int matchesFound = 0;
			for (List<PassageSpan> keytermMatches : matchingSpans) {
				boolean thisKeytermFound = false;
				for (PassageSpan keytermMatch : keytermMatches) {
					if (keytermMatch.containedIn(p.getStart(), p.getEnd())) {
						matchesFound++;
						thisKeytermFound = true;
					}
				}
				if (thisKeytermFound)
					keytermsFound++;
			}
			double score = scorer.scoreWindow(p.getStart(), p.getEnd(),
					matchesFound, totalMatches, keytermsFound, totalKeyterms,
					this.docText.length());
			// PassageCandidate window = null;
			p.getProbabilities().add((float) score);
		}

	}

	public int getTotalMatches() {
		return totalMatches;
	}

	public int getTotalKeyterms() {
		return totalKeyterms;

	}

	class PassageSpan {
		private int begin, end;

		public PassageSpan(int begin, int end) {
			this.begin = begin;
			this.end = end;
		}

		public boolean containedIn(int begin, int end) {
			if (begin <= this.begin && end >= this.end) {
				return true;
			} else {
				return false;
			}
		}
	}

}
