package edu.cmu.lti.oaqa.openqa.dso.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	public static ArrayList<String> readFile(String filePathName) {
		ArrayList<String> lines = new ArrayList<String>();

		File file = new File(filePathName);
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) {
				String line = in.readLine();
				if (line != null && !line.equals("")) {
					lines.add(line);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public static ArrayList<String> readQuestionMapFile(String filePathName) {
		ArrayList<String> lines = new ArrayList<String>();

		File file = new File(filePathName);
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) {
				String line = in.readLine();
				if (line != null) {
					lines.add(line);
				}
			}
			in.close();
		} catch (Exception e) {
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
}
