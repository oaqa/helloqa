package edu.cmu.lti.oaqa.openqa.dso.util;

import java.util.ArrayList;
import java.util.List;

import info.ephyra.nlp.SnowballStemmer;

public class StringUtil {
	public static String textNormalization(String str){
		SnowballStemmer.create();
		String stemmedString=SnowballStemmer.stemAllTokens(str);
		stemmedString = stemmedString.replaceAll("\\p{Punct}", " ");
		String[] tokens=stemmedString.split(" ");
		
		StringBuffer buffer=new StringBuffer(); 
		for(String token : tokens){
			if(!token.trim().equals("")){
				buffer.append(token+" ");
			}
		}
		
		return buffer.toString().trim();
	}
	
	public static String questionTextNormalization(String questionText) {

		questionText = questionText.trim();
		questionText = questionText.replace("?", "");
		questionText = questionText.replaceAll("what", " ");
		questionText = questionText.replaceAll("how", " ");
		questionText = questionText.replaceAll("when", " ");
		questionText = questionText.replaceAll("who", " ");
		
		String[] tokens=questionText.split(" ");

		StringBuffer buffer=new StringBuffer();
		for(String token : tokens){
			if(!token.equals("")){
				buffer.append(token+" ");
			}
		}
		
		return questionText.trim();
	}
	
	public static ArrayList<String> getQuestionItems(String question, List<String> keyterms) {
		ArrayList<String> questionKeyterms = new ArrayList<String>();
		questionKeyterms.addAll(keyterms);
		question = question.toLowerCase();
		question = question.replaceAll("\\p{Punct}", " ");

		String[] tokens = question.split(" ");
		for (int j = 0; j < tokens.length; j++) {
			if (!tokens[j].equals("") && (tokens[j].length() > 1)) {
				boolean containedinkeyterms = false;
				for (int k = 0; k < keyterms.size(); k++) {
					if (keyterms.get(k).toLowerCase().equals(tokens[j])) {
						containedinkeyterms = true;
						break;
					}
				}
				if (!containedinkeyterms) {
					questionKeyterms.add(tokens[j]);
				}
			}
		}
		return questionKeyterms;
	}
	
	public static List<String> getTextItems(String text){
		List<String> tokens=new ArrayList<String>();
		
		String[] items = text.split(" ");
		for (int j = 0; j < items.length; j++) {
			if (!items[j].equals("")) {
				tokens.add(items[j]);
			}
		}
		return tokens;
	}
	
	public static boolean isInitcap(String item){
		if(item.length()==0){
			return false;
		}
		
		char initchar = item.charAt(0);
		if ((initchar >= 'A' && initchar <= 'Z')) {
			return true;
		}
		return false;
	}
	
	public static boolean isInitnum(String item){
		if(item.length()==0){
			return false;
		}
		
		char initchar = item.charAt(0);
		if ((initchar >= '0' && initchar <= '9')) {
			return true;
		}
		return false;
	}
}
