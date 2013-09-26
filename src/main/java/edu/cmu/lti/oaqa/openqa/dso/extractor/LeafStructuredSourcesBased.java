package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.GTDEvent;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.GTDExtractor;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.RANDExtractor;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.RandEvent;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.StructuredQuestionAnalyzer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LeafStructuredSourcesBased extends CandidateExtractorBase {
	private Map<String, String> structuredMap = null;
	private static final String EVENT_MAP = "res" + File.separator
			+ "experimental" + File.separator + "events" + File.separator
			+ "event-map.txt";
	private GTDExtractor gtdExtractor = null;
	private RANDExtractor randExtractor = null;

	public LeafStructuredSourcesBased(SupportingEvidenceArg arg) {
		super(arg);
		structuredMap = new HashMap<String, String>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(EVENT_MAP));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String t_line = scanner.nextLine();
			String[] splits = t_line.split(",");
			structuredMap.put(splits[0], splits[1]);
		}
		gtdExtractor = new GTDExtractor();
		randExtractor = new RANDExtractor();
	}

	public List<String> getAnswerCandidates(String questionText,
			String answerType, String icEvent, GTDExtractor gtd,
			RANDExtractor rand) {

		if (icEvent == null) {
			return new ArrayList<String>();
		}

		List<String> candidateAns = new ArrayList<String>();

		String[] splits = icEvent.split("-");
		String dataset = splits[1];
		String docId = splits[0];

		// determine which field
		String field = StructuredQuestionAnalyzer.getField(questionText,
				answerType);

		if (dataset.equals("gtd")) {
			// gtd
			GTDEvent event = gtd.findGTDByID(docId);

			if (field.equalsIgnoreCase("location")) {
				candidateAns.add(event.getCity() + ", " + event.getCountry());
			} else if (field.equalsIgnoreCase("date")) {

				DateTimeFormatter fmt = DateTimeFormat
						.forPattern("MMMM dd, YYYY");

				System.err.println("Year=" + event.getYear() + ",Month="
						+ event.getMonth() + ",Day=" + event.getDay());

				DateTime dt = new DateTime(); // immutable
				dt = dt.withYear(event.getYear());
				dt = dt.withMonthOfYear(event.getMonth());
				dt = dt.withDayOfMonth(event.getDay());

				candidateAns.add(dt.toString(fmt));

			} else if (field.equalsIgnoreCase("perpetrator")) {
				candidateAns.add(event.getPerpetrators());
			} else if (field.equalsIgnoreCase("injured")) {
				candidateAns.add(event.getNumwounded() + "");
			} else if (field.equalsIgnoreCase("attack_type")) {
				candidateAns.add(event.getAttack_type());
			} else if (field.equalsIgnoreCase("target")) {
				candidateAns.add(event.getTarget());
			}

		} else if (dataset.equals("rand")) {
			// rand
			RandEvent event = rand.findRandWithId(Integer.parseInt(docId));

			if (field.equalsIgnoreCase("location")) {
				candidateAns.add(event.getCity() + ", " + event.getCountry());
			} else if (field.equalsIgnoreCase("date")) {
				candidateAns.add(event.getDate());
			} else if (field.equalsIgnoreCase("perpetrator")) {
				candidateAns.add(event.getPerpetrator());
			} else if (field.equalsIgnoreCase("injured")) {
				candidateAns.add(event.getInjuries() + "");
			} else if (field.equalsIgnoreCase("attack_type")) {
				candidateAns.add(event.getWeapon());
			} else if (field.equalsIgnoreCase("target")) {
				// no field
			}
		}
		return candidateAns;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "structure";
	}

	@Override
	public List<AnswerCandidate> getAnswerCandidates(SupportingEvidenceArg arg) {
		String datasetEvent = structuredMap.get(arg.getICEvent());
		List<String> gtdrandCandidates = getAnswerCandidates(
				arg.getQuestionText(), arg.getAnswerType(), datasetEvent,
				gtdExtractor, randExtractor);
		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();
		for (String ne : gtdrandCandidates) {
			if (ne == null || ne.isEmpty() || ne.equals("")) {
				continue;
			}
			AnswerCandidate candidate = new AnswerCandidate(ne.trim(),
					new ArrayList<RetrievalResult>());
			candidate.setScore(10);
			candidates.add(candidate);
		}
		return candidates;
	}

}
