package edu.cmu.lti.oaqa.openqa.dso.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GSFileConverter {
	
//	static void TREC2CSE_Question(){
//		ArrayList<String> lines=FileUtil.readFile("/home/ruil/Downloads/dataset/questions/question_InfoBox0.txt");
//		
//		int i=1;
//		for(String line:lines){
//			System.out.println(i+"|"+line.substring(line.indexOf("	")+1));
//			i++;
//		}
//	}
	
	static void TREC2CSE_AnsKey(){
		HashMap<String, String> questionMap=new HashMap<String, String>();
		ArrayList<String> lines=FileUtil.readFile("/home/ruil/Downloads/dataset/questions/question_InfoBox0.txt");
		
		for(String line:lines){
			String id=line.substring(0, line.indexOf("	")).trim();
			questionMap.put(id, line.substring(line.indexOf("	")+1));
		}
		
		HashMap<String, String> answerkeyMap=new HashMap<String, String>();
		lines=FileUtil.readFile("/home/ruil/Downloads/dataset/goldstandard/answer_InfoBox.txt");
		for(String line:lines){
			String id=line.substring(0, line.indexOf("	")).trim();
			String answerkey=line.substring(line.indexOf("	")+1);
			answerkeyMap.put(id, answerkey);
		}

		int i=1;
		Iterator<String> iter=questionMap.keySet().iterator();
		while(iter.hasNext()){
			String id=iter.next().trim();
			String answerkey=answerkeyMap.get(id);
			String converted=i+" "+answerkey;
			i++;
			System.out.println(converted);
		}
	}
	
	static void TREC2CSE_QUestion(){
		HashMap<String, String> questionMap=new HashMap<String, String>();
		ArrayList<String> lines=FileUtil.readFile("/home/ruil/Downloads/dataset/questions/question_InfoBox0.txt");
		
		for(String line:lines){
			String id=line.substring(0, line.indexOf("	")).trim();
			questionMap.put(id, line.substring(line.indexOf("	")+1));
		}
		
		HashMap<String, String> answerkeyMap=new HashMap<String, String>();
		lines=FileUtil.readFile("/home/ruil/Downloads/dataset/goldstandard/answer_InfoBox.txt");
		for(String line:lines){
			String id=line.substring(0, line.indexOf("	")).trim();
			String answerkey=line.substring(line.indexOf("	")+1);
			answerkeyMap.put(id, answerkey);
		}

		int i=0;
		Iterator<String> iter=questionMap.keySet().iterator();
		while(iter.hasNext()){
			String id=iter.next().trim();
			String answerkey=answerkeyMap.get(id);
			String converted=i+" "+answerkey;
			i++;
			//System.out.println(converted);
			System.out.println(i+"|"+questionMap.get(id));
		}
	}
	
	static void Question(){
		HashMap<String, String> questionMap=new HashMap<String, String>();
		ArrayList<String> lines=FileUtil.readFile("src/main/resources/gs/dso-extension-psg.txt");
		
		int index=1;
		for(String line:lines){
			String[] temp=line.split("	");
			if(temp.length==2){
				System.out.println(index+" "+temp[temp.length-1]);
			}
			index++;
		}
	}
	
	
	
	public static void main(String[] args) {
		Question();
	}

}
