package edu.cmu.lti.oaqa.openqa.dso.structuredsources;

public class StructuredQuestionAnalyzer {
	
	/**
	 * Returns the field to look for
	 * @param question
	 * @return
	 */
	public static String getField(String question, String answerType) {
		
		String line = question;
		
//		if(line.contains("Where")) { //where did massacre happen
//			return "location";
//		} else if (line.contains("When")) {
//			return "date";
//		} else if(line.contains("perpetrators")) {
//			return "perpetrator";
//		} else if(line.contains("responsible")) {
//			return "perpetrator";
//		} else if(line.contains("mastermind")) {
//			return "perpetrator";
//		} else if(line.contains("injured") || line.contains("wounded")) {
//			return "injured";
//		}else if(line.contains("attack type")) {
//			return "attack_type";
//		} else if(line.contains("target(s)")) {
//			return "target";
//		}
		
		String type = FieldAnalyser.classify(question);
		
		if(type.equals("IC_TYPES.PERPETRATOR"))
			return "perpetrator";
		else if(type.equals("IC_TYPES.NUMBER_INJURED"))
			return "injured";
		else if(type.equals("IC_TYPES.DATE"))
			return "date";
		else if(type.equals("IC_TYPES.LOCATION"))
			return "location";
		else if(type.equals("IC_TYPES.ATTACK_TYPE"))
			return "attack_type";
		else if(type.equals("IC_TYPES.TARGET"))
			return "target";
		
		return "";
		
	}

}
