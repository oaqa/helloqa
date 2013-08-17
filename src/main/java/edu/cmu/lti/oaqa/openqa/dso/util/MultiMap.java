package edu.cmu.lti.oaqa.openqa.dso.util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class MultiMap <K,V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Hashtable<K, List<V>> multiMap = new Hashtable<K, List<V>>();

	public MultiMap(K key,V val) {
		this.add(key, val);
	}
	
	public MultiMap() {
	}

	public void add(K key, V val) {
		List<V> list;
		if (multiMap.containsKey(key)) {
			list = multiMap.get(key);
			if(!list.contains(val))
				list.add(val);
		} else {
			list = new ArrayList<V>();
			list.add(val);
			multiMap.put(key, list);
		}
	}

	public List<V> get(K key) {
		List<V> list = null;
		if (multiMap.containsKey(key)) {
			list = multiMap.get(key);
		}
		return list;
	}

	public boolean contains(K key) {
		if (multiMap.containsKey(key)) {
			return true;
		}
		return false;
	}

	public void removeAll() {
		multiMap.clear();
	}

	public boolean isEmpty() {
		return multiMap.isEmpty();
	}

	public String toString() {
		if(multiMap==null)
			return "";
		String val = "";
		Enumeration<K> e = multiMap.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			val = val + key + ":[";
			int isFirst = 1;
			List<V> resultsCache = multiMap.get(key);
			for (V resultTmp : resultsCache) {
				if (isFirst != 1) {
					val = val + "," + resultTmp.toString();
				} else {
					val = val + resultTmp.toString();
					isFirst = 0;
				}

			}
			val = val + "]\n";
		}
		return val;
	}

}
