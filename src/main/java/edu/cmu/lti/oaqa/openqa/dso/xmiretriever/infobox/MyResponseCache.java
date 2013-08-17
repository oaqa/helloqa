package edu.cmu.lti.oaqa.openqa.dso.xmiretriever.infobox;

import java.io.File;
import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.Map;

import edu.cmu.lti.oaqa.openqa.dso.xmiretriever.HashUtil;

public class MyResponseCache extends ResponseCache {
	private static final String prefix = "res" + File.separator + "cache" + File.separator + "infoboxcache";
	private static final String ext = ".cache";

	static {
		new File(prefix).mkdirs();
	}
	
	public CacheResponse get(URI uri, String rqstMethod, Map rqstHeaders)
			throws IOException {
//		System.err.println("GETTING FROM CACHE");
		String filename = HashUtil.getHash(uri.toString());
		// get the response from a cached file if available
	
		MyCacheResponse myCacheResponse = new MyCacheResponse(prefix + File.separator + filename + ext);
		if(myCacheResponse.getIsError()){
//			System.err.println("CACHE MISS");
			return null;
		}else{
//			System.err.println("CACHE HIT");
			return myCacheResponse;
		}
	
	}

	public CacheRequest put(URI uri, URLConnection conn) throws IOException {
		String filename = HashUtil.getHash(uri.toString());
//		System.err.println("PUTTING TO CACHE");
		 MyCacheRequest myCacheRequest = new MyCacheRequest(prefix + File.separator + filename + ext,
				conn.getHeaderFields());
		 if(myCacheRequest.isError())
			 return null;
		 else
			 return myCacheRequest;

	}
}