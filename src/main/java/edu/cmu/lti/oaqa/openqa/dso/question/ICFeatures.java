package edu.cmu.lti.oaqa.openqa.dso.question;

import java.util.HashSet;

public class ICFeatures {
	private static ICFeatures instance;
	private static HashSet<String> ungrams= new HashSet<String>();
	
    private ICFeatures(){
		for (int i = 0; i < ICFeatures.UNIGRAMS.length; i++) {
			ungrams.add(ICFeatures.UNIGRAMS[i].toLowerCase());
		}
    }
    
    public static boolean contains(String word){
    	boolean containsKey=false;
    	if(ungrams.contains(word.trim().toLowerCase())){
    		containsKey=true;
    	}
    	return containsKey;
    }
    
	public static ICFeatures getInstance() {
		if (instance == null) {
			instance = new ICFeatures();
		}
		return instance;
	}
	
	private static final String[] UNIGRAMS={
		"incident",
		"incidents",
		"terrorist",
		"terrorists",
		"terrorism",
		"bomb",
		"bombs",
		"bombing",
		"bomber",
		"bombers",
		"target",
		"targets",
		"Qaeda",
		"Laden",
		"Osama",
		"bin",
		"mastermind",
		"masterminds",
		"perpetrator",
		"perpetrators",
		"attack",
		"attacks",
		"attacked",
		"attacking",
		"pirate",
		"pirates",
		"kill",
		"kills",
		"killed",
		"killing",
		"die",
		"died",
		"explosive",
		"explosives",
		"explosion"
	};

}
