package edu.cmu.lti.oaqa.openqa.dso.passage;

import info.ephyra.search.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import edu.cmu.lti.oaqa.openqa.dso.util.MultiMap;

public class WebRetrievalCache {

	private Hashtable<String, Object> tableInMemory = new Hashtable<String, Object>();

	public MultiMap<String, Result> loadCache(String RetrievalEngine) {
		// If already in memory, then return back
		if (tableInMemory.containsKey(RetrievalEngine))
			return (MultiMap<String, Result>) tableInMemory.get(RetrievalEngine);
		if (tableInMemory.isEmpty())
			System.out.println("tableInMemory is still empty");

		String fileName = "res" + File.separator +"cache" + File.separator +"" + RetrievalEngine + "Cache.txt";
		System.out.println("Reading " + RetrievalEngine + " Cache File");
		ObjectInputStream inputStream = null;
		MultiMap<String, Result> table = null;

		try {
			inputStream = new ObjectInputStream(new FileInputStream(fileName));
			try {
				table = (MultiMap<String, Result>) inputStream.readObject();
			} catch (ClassNotFoundException e) {
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("File doesn't exist!");

		} catch (IOException e) {
		} finally {
		}

		if (table != null)
			tableInMemory.put(RetrievalEngine, table);
		if (tableInMemory.isEmpty())
			System.out
					.println("tableInMemory after the put command is still empty");
		return (MultiMap<String, Result>) table;
	}

	public void saveCache(Object table, String RetrievalEngine) {

		if (table == null) {
			System.out.println("Save cache : Table is null");
			return;
		}

		if (tableInMemory.isEmpty()) {
			System.out
					.println("tableInMemory is still empty at the save cache stage");
		}

		tableInMemory.put(RetrievalEngine, table);

		System.out.println("Writing " + RetrievalEngine + " Cache File");

		String fileName = "res" + File.separator +"cache" + File.separator +"" + RetrievalEngine + "Cache.txt";
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(
					new FileOutputStream(fileName));
			outputStream.writeObject(table);
			outputStream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
		}
	}
}
