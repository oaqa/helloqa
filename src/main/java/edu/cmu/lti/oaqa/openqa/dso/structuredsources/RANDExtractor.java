package edu.cmu.lti.oaqa.openqa.dso.structuredsources;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import au.com.bytecode.opencsv.CSVReader;


public class RANDExtractor {
	
	private List<RandEvent> randList;
	private final static String GTD_CORPUS = "res" + File.separator + "experimental" + File.separator + "events" + File.separator + "rand_events_clean.csv";
	
	//Params in RAND
	
//	private final static int DATE = 0;
//	private final static int CITY = 1;
//	private final static int COUNTRY = 2;
//	private final static int PERPETRATOR = 3;
//	private final static int WEAPON = 4;
//	private final static int INJURIES = 5;
//	private final static int FATALITIES = 6;
//	private final static int DESC = 7;
//	
	public RANDExtractor() {
		randList = new LinkedList<RandEvent>();
		try {
			parseCSV();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<RandEvent> getRandList() {
		return randList;
	}
	public void setRandList(List<RandEvent> randList) {
		this.randList = randList;
	}
	
	public RandEvent findRandWithId(int id) {
		
		for(RandEvent e : randList) {
			
			if(e.getId() == id) {
				return e;
			}
			
		}
		
		return null;
		
	}
	
	public void parseCSV() throws IOException {
		
		int cnt = 0;
		CSVReader reader = new CSVReader(new FileReader(GTD_CORPUS), '\t');
		List<String[]> randData = reader.readAll();
		
		System.out.println("Num rows in dataset=" + randData.size());
		
		for(int i = 1; i < randData.size(); i++) {
			
			String[] data = randData.get(i);
			String[] dataArr=new String[8];
			int id = cnt++;
			
			for(int j=0;j<data.length;j++){
				dataArr[j]=data[j];
			}
			
			RandEvent t_event = new RandEvent(id,dataArr);
			randList.add(t_event);
		}
		reader.close();
	}
	

}
