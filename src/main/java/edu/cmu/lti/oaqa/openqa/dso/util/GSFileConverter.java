package edu.cmu.lti.oaqa.openqa.dso.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GSFileConverter {
	
	static void TREC2CSE(){
		HashMap<String, String> questionMap=new HashMap<String, String>();
		ArrayList<String> lines=FileUtil.readFile("./src/main/resources/input/TREC-dso-extension.txt");
		
		for(String line:lines){
			String id=line.substring(0, line.indexOf("	")).trim();
			questionMap.put(id, line.substring(line.indexOf("	")+1));
		}
		
		HashMap<String, String> answerkeyMap=new HashMap<String, String>();
		lines=FileUtil.readFile("./src/main/resources/gs/TREC-dso-extension-answerkey.txt");
		for(String line:lines){
			String id=line.substring(0, line.indexOf(" ")).trim();
			String answerkey=line.substring(line.indexOf(" ")+1);
			answerkeyMap.put(id, answerkey);
		}

		int i=1;
		Iterator<String> iter=questionMap.keySet().iterator();
		while(iter.hasNext()){
			String id=iter.next().trim();
			if(!answerkeyMap.containsKey(id)){
				int a=0;
			}
			String answerkey=answerkeyMap.get(id);
			String converted=i+" "+answerkey;
			i++;
			System.out.println(converted);
		}
	}
	
	public static void main(String[] args) {
		TREC2CSE();
	}

}
