package edu.cmu.lti.oaqa.openqa.dso.structuredsources;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import au.com.bytecode.opencsv.CSVReader;

/**
 * This class extracts out information from GTD and writes them to a trectext
 * file
 * 
 * @author Junyang Ng
 * 
 */
public class GTDExtractor {

	// Dataset had to be pre-processed to remove all apostrophe and commas
	private final static String GTD_CORPUS = "res" + File.separator + "experimental" + File.separator + "events" + File.separator + "91_2010_gtd_4.csv";

	// List fields we care about
	private final static int ID = 0; // GTD ID
	private final static int YEAR = 1;
	private final static int MONTH = 2;
	private final static int DAY = 3;
	private final static int COUNTRY = 8; // Country text
	private final static int REGION = 10; // region text
	private final static int PROVINCE = 11;
	private final static int CITY = 12;
	private final static int LOCATION_DESC = 14; // Additional info about the
													// location
	private final static int SUMMARY = 15; // Summary of incident
	private final static int MULTIPLE_INCIDENT = 22;
	private final static int ATTACK_TYPE = 27;
	private final static int TARGET = 34;
	private final static int WEAPON_TYPE = 76;
	private final static int PERPETRATORS = 50;
	private final static int NUMKILLED = 92;
	private final static int NUMWOUNDED = 95;
	
	List<GTDEvent> gtdList;
	
	public GTDExtractor() {
		gtdList = new LinkedList<GTDEvent>();
		try {
			readGTD();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public GTDEvent findGTDByID(String id) {
		
		for(GTDEvent e : gtdList) {
			
			if(e.getId().equalsIgnoreCase(id)) {
				return e;
			}
			
		}
		
		return null;
		
	}

	// Read the CSV file manually
	public List<GTDEvent> readGTD() throws IOException {

		CSVReader reader = new CSVReader(new FileReader(GTD_CORPUS));
		List<String[]> gtdData = reader.readAll();

		String[] hdr = gtdData.get(0);

		System.err.println("Num Columns in Dataset=" + hdr.length);
		System.err.println("Num rows in Dataset=" + gtdData.size());

		for (int i = 1; i < gtdData.size(); i++) {

			String[] temp = gtdData.get(i);

			String t_id = temp[ID];
			int t_year = Integer.parseInt(temp[YEAR]);
			int t_month = Integer.parseInt(temp[MONTH]);
			int t_day = Integer.parseInt(temp[DAY]);
			String t_country = temp[COUNTRY];
			String t_region = temp[REGION];
			String t_province = temp[PROVINCE];
			String t_city = temp[CITY];
			String t_location_desc = temp[LOCATION_DESC];
			String t_summary = temp[SUMMARY];
			int t_multiple = Integer.parseInt(temp[MULTIPLE_INCIDENT]);
			String t_attack_type = temp[ATTACK_TYPE];
			String t_target = temp[TARGET];
			String t_weapon_type = temp[WEAPON_TYPE];
			String t_perpetrators = temp[PERPETRATORS];
			
			int t_numkilled = 0;
			int t_numwounded = 0;
			try {
				if(temp[NUMKILLED].isEmpty()) {
					t_numkilled = -1;
				} else {
					t_numkilled = Integer.parseInt(temp[NUMKILLED]);
				}
			} catch (Exception e) {
				t_numkilled = -1;
			}
			
			try {
				if(temp[NUMWOUNDED].isEmpty()) {
					t_numwounded = -1;
				} else {
					t_numwounded = Integer.parseInt(temp[NUMWOUNDED]);
				}
			} catch (Exception e) {
				t_numwounded = -1;
			}
			

			GTDEvent tempEvent = new GTDEvent(t_id, t_year, t_month, t_day,
					t_country, t_region, t_province, t_city, t_location_desc,
					t_summary, t_multiple, t_attack_type, t_target,
					t_weapon_type, t_perpetrators, t_numkilled, t_numwounded);

			gtdList.add(tempEvent);

		}

		return gtdList;

	}

	public List<GTDEvent> getGtdList() {
		return gtdList;
	}

	public void setGtdList(List<GTDEvent> gtdList) {
		this.gtdList = gtdList;
	}
	
	public static void main(String[] args) {

	}

}
