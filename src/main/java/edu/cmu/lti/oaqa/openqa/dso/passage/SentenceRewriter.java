package edu.cmu.lti.oaqa.openqa.dso.passage;

/*
 * Class to convert questions to sentence completion queries
 * 
 * @author - Koul
 * 
 */
public class SentenceRewriter {
	/*
	 * Generates query strings in Question completion format
	 */
	public static String questionCompleter(String str) {
		str = str.toLowerCase();
		str = str.replaceAll("\"", "");
		str = str.replaceAll("\\?", "");
		String originalStr = str;
		if (str.indexOf("what") > -1) {
			str = str.replaceAll("what are the ", "");
			str = str.replaceAll("what was the name of the ", "");
			str = str.replaceAll("what is the name of the ", "");
			str = str.replaceAll("what is the ", "");
			str = str.replaceAll("what is ", "");
			str = str.replaceAll("what was ", "");
			if (str != originalStr)
				str = "\"" + str + "\"";
		} else if (str.indexOf("when") > -1) {
			if(str.indexOf("when did the") > -1){
				str = str.replaceAll("when did the ", "");
				str = str.replaceAll("occur", "");
				str = str.replaceAll("take place", "");
				str = str.replaceAll("happen", "");				
			}
			str = str.replaceAll("when did ", "");
			str = str.replaceAll("when was the ", "");
			str = str.replaceAll("when was ", "");
			if (str != originalStr)
				str = "\"" + str + "\"" + " year";
		} else if (str.indexOf("how tall is") > -1) {
			str = str.replaceAll("how tall is ", "");
			str = "\"" + str + "\"" + " height";
		} else if (str.indexOf("who") > -1) {
			str = str.replaceAll("who was the ", "");
			str = str.replaceAll("who is the ", "");
			if (str != originalStr)
				str = "\"" + str + "\"";
		}else if (str.indexOf("how many ") > -1) {
			str = str.replaceAll("how many ", "");
		}else if (str.indexOf("how much ") > -1) {
			str = str.replaceAll("how much ", "");
		}
		return str;
	}	
}
