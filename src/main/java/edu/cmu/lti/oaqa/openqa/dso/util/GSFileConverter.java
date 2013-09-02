package edu.cmu.lti.oaqa.openqa.dso.util;

import java.util.ArrayList;

public class GSFileConverter {
	
	static void TREC2CSE(){
		ArrayList<String> lines=FileUtil.readFile("./src/main/resources/input/dso-extension.txt");
		for(String line:lines){
			String converted=line.substring(0, line.indexOf("	"))+"|"+line.substring(line.indexOf("	")+1);
			
			System.out.println(converted);
		}
	}
	
	public static void main(String[] args) {
		TREC2CSE();
	}

}
