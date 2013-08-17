package edu.cmu.lti.oaqa.openqa.dso.passage;

import info.ephyra.search.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Cache for document retrieval component.
 * 
 */
public class LocalRetrievalCache {

	private HashMap<String, Result[]> map = null;
	private final String name = "IndriResult_dso4.cache";// file name
	private static LocalRetrievalCache instance;

	private LocalRetrievalCache() {
		Read();
	}

	/**
	 * Singleton pattern
	 * 
	 * @return cache instance
	 */
	public static LocalRetrievalCache getInstance() {
		if (instance == null) {
			instance = new LocalRetrievalCache();
		}
		return instance;
	}

	/**
	 * get result from cache
	 * 
	 * @param query
	 * @return
	 */
	public Result[] getResults(String query) {
		return map.get(query);
	}

	public boolean isInCache(String query) {
		return map.containsKey(query);
	}

	/**
	 * add new result to cache
	 * 
	 * @param key
	 * @param Results
	 * @return whether operation succeeded or not
	 */
	public boolean addToCache(String key, Result[] Results) {
		if (map != null) {
			map.put(key, Results); 
			Write();
			return true;
		}
		return false;
	}

	/**
	 * read cache from persistent layer
	 */
	@SuppressWarnings("unchecked")
	public void Read() {
		FileInputStream freader;
		try {
			freader = new FileInputStream("res" + File.separator + "cache" + File.separator + "" + name);
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);

			try {
				map = (HashMap<String, Result[]>) objectInputStream
						.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			// System.out.println("The name is " + map.get("name"));
			objectInputStream.close();
		} catch (FileNotFoundException e) {
			map = new HashMap<String, Result[]>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * persistence
	 */
	public void Write() {
		try {
			FileOutputStream outStream = new FileOutputStream("res" + File.separator + "cache" + File.separator + "" + name);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					outStream);

			objectOutputStream.writeObject(map);
			outStream.close();
			// System.out.println("successful");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}