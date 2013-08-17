package edu.cmu.lti.oaqa.openqa.dso.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionFormater {
	static String QuestionFilePath="dataset\\questions\\";
	static String SourceFileName="New Text Document.txt";
	static String TargetFileName="dso.txt";
	
	public static List<String> readQuestions(String file) {
		List<String> lines=new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine().trim();
			while (line != null) {
				if((!line.trim().equals(""))&&line.contains("?")){
					lines.add(line.trim());
				}
				line = br.readLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public static void writeToFile(String filename, StringBuffer buffer) {
		BufferedWriter bufferedWriter = null;

		try {
			// Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(filename));

			// Start writing to the output stream
			bufferedWriter.write(buffer.toString());

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//process();
	}

}
