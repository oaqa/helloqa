package edu.cmu.lti.oaqa.openqa.dso.passage;

import info.ephyra.io.MsgPrinter;
import info.ephyra.nlp.LingPipe;
import info.ephyra.nlp.SnowballStemmer;
import info.ephyra.search.Result;
import info.ephyra.search.searchers.KnowledgeMiner;
import info.ephyra.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;


import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 * <p>
 * A <code>KnowledgeMiner</code> that deploys the Indri IR system to search a
 * local text corpus. The search results are sentence-aligned passages.
 * </p>
 * 
 * <p>
 * It runs as a separate thread, so several queries can be performed in
 * parallel.
 * </p>
 * 
 * <p>
 * This class extends the class <code>KnowledgeMiner</code>.
 * </p>
 * 
 * @author Nico Schlaefer
 * @version 2010-02-09
 */
public class IndriSentencesKM extends KnowledgeMiner {

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	/** Maximum total number of search results. */
	private static final int MAX_RESULTS_TOTAL = 100;
	/** Maximum number of search results per query. */
	private static final int MAX_RESULTS_PERQUERY = 100;
	/** Maximum number of documents fetched at a time. */
	private static final int MAX_DOCS = 100;
	/** Maximum number of unique results. */
	private static final int MAX_UNIQUE = 100;
	/** Maximum length of sentence-aligned passages. */
	private static final int MAX_PASSAGE_LENGTH = 1000;

	private static final double mu = 1000;

	/**
	 * <p>
	 * Regular expression that matches characters that cause problems in Indri
	 * queries and thus should be removed from query strings.
	 * </p>
	 * 
	 * <p>
	 * Indri allows the following characters:
	 * <ul>
	 * <li>'\u0080'..'\u00ff'</li>
	 * <li>'a'..'z'</li>
	 * <li>'A'..'Z'</li>
	 * <li>'0'..'9'</li>
	 * <li>'_'</li>
	 * <li>'-'</li>
	 * <li>'.' (only allowed if in between digits)</li>
	 * <li>whitespaces</li>
	 * <li>'"'</li>
	 * </ul>
	 * However, for some of the special characters Indri fails to retrieve
	 * results and therefore they are excluded.
	 * </p>
	 */
	private static final String FORBIDDEN_CHAR = "[^\\w\\.\\s\"]";

	/** Directories of Indri indices. */
	private String[] indriDirs;
	/** URLs of Indri servers. */
	private String[] indriUrls;

	private String[] locations;
	
	private static List<String> keyPhrases;

	public void setKeyPhrases(List<String> keyPhrases) {
		IndriSentencesKM.keyPhrases = keyPhrases;
	}

	/**
	 * Gets a list of all Indri index directories that have been specified with
	 * environment variables 'INDRI_INDEX', 'INDRI_INDEX2', 'INDRI_INDEX3' etc.
	 * One environment variable can specify multiple indices which are queried
	 * with the same knowledge miner.
	 * 
	 * @return Indri index directories grouped by knowledge miners
	 */
	public static String[][] getIndriIndices() {
		ArrayList<String[]> indices = new ArrayList<String[]>();

		String index = System.getenv("INDRI_INDEX");
		if (index != null && index.length() > 0)
			indices.add(index.split(";"));
		for (int i = 2;; i++) {
			index = System.getenv("INDRI_INDEX" + i);
			if (index != null && index.length() > 0)
				indices.add(index.split(";"));
			else
				break;
		}

		return indices.toArray(new String[indices.size()][]);
	}

	/**
	 * Gets a list of all Indri server URLs that have been specified with
	 * environment variables 'INDRI_SERVER', 'INDRI_SERVER2', 'INDRI_SERVER3'
	 * etc. One environment variable can specify multiple servers which are
	 * queried with the same knowledge miner.
	 * 
	 * @return Indri server URLs grouped by knowledge miners
	 */
	public static String[][] getIndriServers() {
		ArrayList<String[]> servers = new ArrayList<String[]>();

		String server = System.getenv("INDRI_SERVER");
		if (server != null && server.length() > 0)
			servers.add(server.split(";"));
		for (int i = 2;; i++) {
			server = System.getenv("INDRI_SERVER" + i);
			if (server != null && server.length() > 0)
				servers.add(server.split(";"));
			else
				break;
		}

		return servers.toArray(new String[servers.size()][]);
	}

	/**
	 * Returns a representation of the query string that is suitable for Indri.
	 * 
	 * @param qs
	 *            query string
	 * @return query string for Indri
	 */
	public String transformQueryString(String qs) {
		// drop 's
		qs = qs.replaceAll("'s\\b", "");

		// drop characters that are not properly supported by Indri
		// ('.' is allowed in between digits, and should be replaced by a blank
		// if in between characters that are not upper case letters)
		qs = qs.replaceAll("&\\w++;", " ");
		qs = qs.replaceAll(FORBIDDEN_CHAR, " ");
		qs = qs.replaceAll("_", " ");
		String dotsRemoved = "";
		for (int i = 0; i < qs.length(); i++)
			if (qs.charAt(i) != '.') {
				dotsRemoved += qs.charAt(i);
			} else if (i > 0 && i < qs.length() - 1) {
				if (Character.isDigit(qs.charAt(i - 1))
						&& Character.isDigit(qs.charAt(i + 1)))
					dotsRemoved += qs.charAt(i);
				else if (!Character.isUpperCase(qs.charAt(i - 1))
						|| !Character.isUpperCase(qs.charAt(i + 1)))
					dotsRemoved += " ";
			}
		qs = dotsRemoved;

		// replace ... OR ... by #or(... ...)
		Matcher m = Pattern
				.compile(
						"((\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++) OR )++"
								+ "(\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++)")
				.matcher(qs);
		while (m.find())
			qs = qs.replace(m.group(0), "#or(" + m.group(0) + ")");
		qs = qs.replace(" OR", "");

		// replace ... AND ... by #combine(... ...)
		m = Pattern
				.compile(
						"((\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++) AND )++"
								+ "(\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++)")
				.matcher(qs);
		while (m.find())
			qs = qs.replace(m.group(0), "#combine(" + m.group(0) + ")");
		qs = qs.replace(" AND", "");

		// replace "..." by #1(...)
		m = Pattern.compile("\"([^\"]*+)\"").matcher(qs);
		while (m.find())
			qs = qs.replace(m.group(0), "#1(" + m.group(1) + ")");

		// form passage query
		// qs = "#combine[passage50:25](" + qs + ")";
		// #combine[passage50:25](country game croquet originated)
		qs = transformQuery(qs);
		return qs;
	}

	private String transformQuery(String query) {
		query = query.trim();
		if (IndriSentencesKM.keyPhrases.size() == 0) {
			return normalIndriQuery(query);
		}
		return sequenceQuery(query);
	}

	private static String normalIndriQuery(String query) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("#combine[passage50:25](" + query + ")");
		return buffer.toString();
	}

	private String sequenceQuery(String query) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("#weight[passage50:25](");

		buffer.append(" 0.8 #combine(");
		buffer.append(query);
		buffer.append(")");

		buffer.append(" 0.15 #combine(");
		for (int i = 0; i < keyPhrases.size(); i++) {
			buffer.append(" #1(");
			buffer.append(normalize(keyPhrases.get(i)));
			buffer.append(")");
		}
		buffer.append(")");

		buffer.append(" 0.05 #combine(");
		for (int i = 0; i < keyPhrases.size(); i++) {
			buffer.append(" #uw8(");
			buffer.append(normalize(keyPhrases.get(i)));
			buffer.append(")");
		}
		buffer.append(")");

		buffer.append(")");
		System.out.println(buffer.toString());
		return buffer.toString();
	}

	/**
	 * Creates a new Indri knowledge miner and sets the directories of indices
	 * or the URLs of servers.
	 * 
	 * @param locations
	 *            directories of indices or URLs of servers
	 * @param isServers
	 *            <code>true</code> iff the first parameter provides URLs of
	 *            servers
	 */
	public IndriSentencesKM(String[] locations) { 
		this.locations = locations;
		List<String> urls = new ArrayList<String>();
		List<String> localLocations = new ArrayList<String>();
		for(String location : locations){
			if(location.startsWith("indrid://")){
				// server
				urls.add(location.substring("indrid://".length()));
			}else{
				// local
				localLocations.add(location);
			}
		}
		
		indriUrls = (String[]) urls.toArray(new String[urls.size()]);
		indriDirs = (String[]) localLocations.toArray(new String[localLocations.size()]);
	}

	/**
	 * Returns the maximum total number of search results.
	 * 
	 * @return maximum total number of search results
	 */
	protected int getMaxResultsTotal() {
		return MAX_RESULTS_TOTAL;
	}

	/**
	 * Returns the maximum number of search results per query.
	 * 
	 * @return maximum total number of search results
	 */
	protected int getMaxResultsPerQuery() {
		return MAX_RESULTS_PERQUERY;
	}

	private static String normalize(String s) {
		// drop 's
		s = s.replaceAll("'s\\b", "");

		// drop characters that are not properly supported by Indri
		// ('.' is allowed in between digits, and should be replaced by a blank
		// if in between characters that are not upper case letters)
		s = s.replaceAll("&\\w++;", " ");
		s = s.replaceAll(FORBIDDEN_CHAR, " ");
		s = s.replaceAll("(_|\")", " ");
		String dotsRemoved = "";
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) != '.') {
				dotsRemoved += s.charAt(i);
			} else if (i > 0 && i < s.length() - 1) {
				if (Character.isDigit(s.charAt(i - 1))
						&& Character.isDigit(s.charAt(i + 1)))
					dotsRemoved += s.charAt(i);
				else if (!Character.isUpperCase(s.charAt(i - 1))
						|| !Character.isUpperCase(s.charAt(i + 1)))
					dotsRemoved += " ";
			}
		s = dotsRemoved;

		LingPipe.createTokenizer();
		SnowballStemmer.create();

		s = s.toLowerCase();
		String tokens[] = LingPipe.tokenize(s);
		for (int t = 0; t < tokens.length; t++)
			tokens[t] = SnowballStemmer.stem(tokens[t]);
		s = StringUtils.concatWithSpaces(tokens);

		return s;
	}

	private static double computeIDF(double termDoc, double totalDoc) {
		double score = 0;
		score = Math.log((totalDoc - termDoc + 0.5) / (termDoc + 0.5));
		return score;
	}

	public double[] getKeytermIDF(List<String> keyterms) {
		try {
			QueryEnvironment env = new QueryEnvironment();
			// add Indri indices or servers
			if (indriDirs != null && indriDirs.length > 0) {
				for (String indriDir : indriDirs)
					env.addIndex(indriDir);
			} 
			if (indriUrls != null && indriUrls.length > 0) {
				for (String indriUrl : indriUrls)
					env.addServer(indriUrl);
			} 
			if (indriDirs == null && indriDirs.length == 0 && indriUrls == null && indriUrls.length == 0)
			{
				MsgPrinter.printErrorMsg("Directories of Indri indices or "
						+ "URLs of Indri servers required.");
				System.exit(1);
			}

			double[] keytermIDF = new double[keyterms.size()];
			for (int i = 0; i < keyterms.size(); i++) {
				keytermIDF[i] = computeIDF(env.documentCount(keyterms.get(i)), env
						.documentCount());
			}
			env.close();
			
			return keytermIDF;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Queries the Indri indices or servers and returns an array containing up
	 * to <code>MAX_RESULTS_PERQUERY</code> search results.
	 * 
	 * @return Indri search results
	 */
	protected Result[] doSearch() {
		try {
			// create query environment
			QueryEnvironment env = new QueryEnvironment();
			// add smoothing parameters
			String[] rules = { "method:dirichlet", "mu:" + mu };
			env.setScoringRules(rules);

			// add Indri indices or servers
			if (indriDirs != null && indriDirs.length > 0) {
				for (String indriDir : indriDirs)
					env.addIndex(indriDir);
			} 
			if (indriUrls != null && indriUrls.length > 0) {
				for (String indriUrl : indriUrls)
					env.addServer(indriUrl);
			} 
			if (indriDirs == null && indriDirs.length == 0 && indriUrls == null && indriUrls.length == 0)
			{
				MsgPrinter.printErrorMsg("Directories of Indri indices or "
						+ "URLs of Indri servers required.");
				System.exit(1);
			}

			String qs = transformQueryString(query.getQueryString());
			
			//Structured Annotations Heuristics
			qs.replaceAll("city", "#any:geopolitical_entity #any:location");
			
			Result[] docresults;
			LocalRetrievalCache cache = LocalRetrievalCache.getInstance();

			// *** cache **/
			if (cache.isInCache(qs)) {
				docresults = cache.getResults(qs);
			} else {
				String backupqs = qs;
				// run an Indri query, returning up to MAX_RESULTS_PERQUERY
				// results

				ScoredExtentResult[] results = env.runQuery(qs,
						MAX_RESULTS_PERQUERY);
				LOGGER.info("  Indri query: " + qs + " => retrieved "
						+ results.length + " docs.");
				// qs=QueryString;
				// extract terms from query
				List<String> queryTerms = new ArrayList<String>();
				qs = qs.split("\\(", 2)[1];
				qs = qs.substring(0, qs.length() - 1);
				String groupRegex = "#1\\(([^\\)]*+)\\)";
				Pattern groupP = Pattern.compile(groupRegex);
				Matcher groupM = groupP.matcher(qs);
				while (groupM.find())
					queryTerms.add(normalize(groupM.group(1).trim()));
				qs = qs.replaceAll(groupRegex, "").trim();
				String[] keywords = qs.split("\\s++");
				for (String keyword : keywords)
					queryTerms.add(normalize(keyword));

				// get passages and document numbers
				Set<String> distinctSents = new HashSet<String>();
				String[] passages = new String[results.length];
				for (int i = 0; i < results.length; i += MAX_DOCS) {
					// fetch MAX_DOCS documents at a time (for memory
					// efficiency)
					ScoredExtentResult[] partResults = new ScoredExtentResult[Math
							.min(MAX_DOCS, results.length - i)];
					for (int j = i; j < i + partResults.length; j++)
						partResults[j - i] = results[j];

					ParsedDocument[] documents = env.documents(partResults);

					for (int j = 0; j < partResults.length; j++) {
						int tokenBegin = partResults[j].begin;
						int tokenEnd = partResults[j].end;
						int byteBegin = documents[j].positions[tokenBegin].begin;
						int byteEnd = documents[j].positions[tokenEnd - 1].end;
						byte[] doc = documents[j].text.getBytes("UTF-8");

						// get retrieved passage
						byte[] p = new byte[byteEnd - byteBegin];
						//System.out.println(j);
						for (int offset = byteBegin; offset < byteEnd; offset++){
							if(offset<doc.length){
								p[offset - byteBegin] = doc[offset];
							}			
						}
						String passage = new String(p).replaceAll("\\s++", " ")
								.trim();

						
						// extend to full lines
						int lineBegin = byteBegin;
						int lineEnd = byteEnd;
						while (lineBegin > 0 && lineBegin<doc.length && doc[lineBegin] != '\n')
							lineBegin--;
						while (lineEnd < doc.length && doc[lineEnd] != '\n')
							lineEnd++;
						byte[] l = new byte[lineEnd - lineBegin];
						for (int offset = lineBegin; offset < lineEnd; offset++){
							if(offset<doc.length){
							l[offset - lineBegin] = doc[offset];
							}
						}
						String expanded = new String(l);

						// split into sentences
						String[] lines = expanded.split("\n");
						LingPipe.createSentenceDetector();
						List<String> sentenceL = new ArrayList<String>();
						for (String line : lines) {
							String[] sentences = LingPipe.sentDetect(line);
							for (String sentence : sentences) {
								sentence = sentence.replaceAll("\\s++", " ")
										.trim();
								if (sentence.length() > 0)
									sentenceL.add(sentence);
							}
						}
						expanded = expanded.replaceAll("\\s++", " ").trim();
						String[] sentences = sentenceL
								.toArray(new String[sentenceL.size()]);

						// cut off sentences that do not overlap with passage
						int passageBegin = expanded.indexOf(passage);
						if (passageBegin == -1) {
							System.err.println("Passage not mapped.");
							// patch to prevent dying on linux machines when Indri faces a problem with a 
							// passage that has invalid characters
							passages[i+j] = "BAD_PASSAGE"; 
//							System.exit(1);
						}
						int passageEnd = passageBegin + passage.length();
						int[] sentenceBegins = new int[sentences.length];
						for (int s = 1; s < sentences.length; s++) {
							expanded = expanded.substring(sentences[s - 1]
									.length());
							int offset = 0;
							while (Character.isWhitespace(expanded
									.charAt(offset)))
								offset++;
							expanded = expanded.substring(offset);
							sentenceBegins[s] = sentenceBegins[s - 1]
									+ sentences[s - 1].length() + offset;
						}
						List<String> remaining = new ArrayList<String>();
						List<String> normalized = new ArrayList<String>();
						for (int s = 0; s < sentences.length; s++)
							if (sentenceBegins[s] + sentences[s].length() > passageBegin
									&& sentenceBegins[s] < passageEnd) {
								remaining.add(sentences[s]);
								int beginDiff = passageBegin
										- sentenceBegins[s];
								String contained = sentences[s].substring(Math
										.max(beginDiff, 0), Math.min(
										sentences[s].length(), beginDiff
												+ passage.length()));
								normalized.add(normalize(contained));
							}

						// cut off sentences that do not contain keywords
						sentences = remaining.toArray(new String[remaining
								.size()]);
						remaining.clear();
						int firstSentence = sentences.length;
						int lastSentence = -1;
						for (int s = 0; s < sentences.length; s++)
							for (String queryTerm : queryTerms)
								if (normalized.get(s).contains(queryTerm)) {
									firstSentence = Math.min(firstSentence, s);
									lastSentence = Math.max(lastSentence, s);
									break;
								} else if (s + 1 < sentences.length) {
									// query term may span more than one
									// sentence
									String norm1 = normalized.get(s);
									String norm2 = normalized.get(s + 1);
									int index = (norm1 + " " + norm2)
											.indexOf(queryTerm);
									if (index < 0 || index >= norm1.length())
										continue;
									firstSentence = Math.min(firstSentence, s);
									lastSentence = Math
											.max(lastSentence, s + 1);
								}
						if (lastSentence == -1) {
							// System.err.println("Warning: Query terms not mapped: "
							// + StringUtils.concat(sentences, "<BR>"));
							remaining.add("BAD_PASSAGE");
						}
						for (int s = firstSentence; s <= lastSentence; s++)
							remaining.add(sentences[s]);
						sentences = remaining.toArray(new String[remaining
								.size()]);

						// remove markup and drop duplicate sentences
						remaining.clear();
						Set<String> thisDistinctSents = new HashSet<String>();
						for (int s = 0; s < sentences.length; s++) {
							sentences[s] = sentences[s].replaceAll("<[^>]++>",
									" ").replaceAll("\\s++", " ").trim();
							String lower = sentences[s].toLowerCase();
							if (!distinctSents.contains(lower)
									&& thisDistinctSents.add(lower))
								remaining.add(sentences[s]);
						}
						sentences = remaining.toArray(new String[remaining
								.size()]);

						// concatenate sentences into sentence-aligned passage
						String sentencePassage = StringUtils
								.concatWithSpaces(sentences);
						if (sentencePassage.length() == 0) {
							// System.err.println("Warning: Empty passage.");
							sentencePassage = "BAD_PASSAGE";
						} else if (sentencePassage.length() > MAX_PASSAGE_LENGTH) {
							// System.err.println("Warning: Long passage ("
							// + sentencePassage.length() + " characters): "
							// + sentencePassage);
							sentencePassage = "BAD_PASSAGE";
						}
						if (!"BAD_PASSAGE".equals(sentencePassage))
							for (String sentence : sentences)
								distinctSents.add(sentence.toLowerCase());

						passages[j + i] = sentencePassage;
					}
				}
				String[] docNos = env.documentMetadata(results, "docno");

				// create results
				Result[] finalResults = getResults(passages, docNos, false);
				for (int i = 0; i < results.length; i++)
					finalResults[i].setScore((float) results[i].score);

				// drop duplicates
				Set<String> passageS = new HashSet<String>();
				List<Result> unique = new ArrayList<Result>();

				for (Result result : finalResults) {
					String passage = result.getAnswer();
					if (unique.size() < MAX_UNIQUE
							&& !"BAD_PASSAGE".equals(passage)
							&& passageS.add(passage.toLowerCase()))
						unique.add(result);
				}
				if (finalResults.length == MAX_RESULTS_PERQUERY
						&& unique.size() < MAX_UNIQUE)
					LOGGER.warn("Warning: Number of distinct passages: "
							+ unique.size());

				docresults = unique.toArray(new Result[unique.size()]);
				cache.addToCache(backupqs, docresults);
			}
			// close query environment
			env.close();
			/** cache ***/
			return docresults;

		} catch (Exception e) {
			MsgPrinter.printSearchError(e); // print search error message
			e.printStackTrace();

			MsgPrinter.printErrorMsg("\nSearch failed.");
			System.exit(1);

			return null;
		}
	}

	/**
	 * Returns a new instance of <code>IndriKM</code>. A new instance is created
	 * for each query.
	 * 
	 * @return new instance of <code>IndriKM</code>
	 */
	public KnowledgeMiner getCopy() {
		return new IndriSentencesKM(locations);
	}
}
