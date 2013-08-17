package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.structuredsources.GTDEvent;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.GTDExtractor;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.RANDExtractor;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.RandEvent;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.StructuredQuestionAnalyzer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
public class CandidateExtractorByStructuredSources extends
		CandidateExtractorBase {

	@Override
	public String[][] getAnswerCandidates() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getAnswerCandidates(String questionText,
			String answerType, String icEvent, GTDExtractor gtd,
			RANDExtractor rand) {
		
		if(icEvent == null) {
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

				System.err.println("Year=" + event.getYear() + ",Month=" + event.getMonth() + ",Day=" + event.getDay());
				
				DateTime dt = new DateTime(); //immutable
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
				//no field
			}

		}

		return candidateAns;

	}	

}
