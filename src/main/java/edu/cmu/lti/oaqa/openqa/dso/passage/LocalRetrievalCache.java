package edu.cmu.lti.oaqa.openqa.dso.passage;

import info.ephyra.search.Result;

public abstract class LocalRetrievalCache {
	public abstract LocalRetrievalCache getInstance();
	public abstract boolean isInCache(String query);
	public abstract Result[] getResults(String query);
	public abstract boolean addToCache(String key, Result[] Results);
}
