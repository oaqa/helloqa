package edu.cmu.lti.oaqa.openqa.dso.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang.StringEscapeUtils;

import edu.jhu.nlp.wikipedia.InfoBox;
import edu.jhu.nlp.wikipedia.WikiTextParser;

public class InfoBoxResult {
	private String incident;
	private String location;
	private String target;
	private String date;
	private String perps;
	private String fatalities;
	private String injuries;
	private String type;

	public InfoBoxResult(String infoText, String incident) {
		if (infoText == null) {
			return;
		}

		setIncident(incident);
		String[] lines = infoText.split("\n");
		for (String line : lines) {
			line = StringEscapeUtils.escapeHtml(line);
			line = line.replaceAll("\\[", "");
			line = line.replaceAll("\\]", "");
			line = line.replaceAll("&amp;lt;br&amp;gt;", ", ");
			if (line.contains("&amp")) {
				line = line.substring(0, line.indexOf("&amp"));
			}

			String lineWithoutBlank = line.replaceAll(" ", "");
			lineWithoutBlank = line.replaceAll(" ", "");
			line = line.replaceAll("=", "");
			if (lineWithoutBlank.contains("|location=")) {
				setLocation(line.replace("|location", "").trim());
			} else if (lineWithoutBlank.contains("|target=")) {
				setTarget(line.replace("|target", "").trim());
			} else if (lineWithoutBlank.contains("|date=")) {
				setDate(line.replace("|date", "").trim());
			} else if (lineWithoutBlank.contains("|perps=")) {
				setPerps(line.replace("|perps", "").trim());
			} else if (lineWithoutBlank.contains("|fatalities=")) {
				setFatalities(line.replace("|fatalities", "").trim());
			} else if (lineWithoutBlank.contains("|injuries=")) {
				setInjuries(line.replace("|injuries", "").trim());
			} else if (lineWithoutBlank.contains("|type=")) {
				setType(line.replace("|type", "").trim());
			} else if(lineWithoutBlank.contains("|susperps=")) {
				setPerps(line.replace("|susperps", "").trim());
			}
		}
	}

	public String getIncident() {
		return this.incident;
	}

	private void setIncident(String incident) {
		this.incident = incident;
	}

	public String getLocation() {
		return this.location;
	}

	private void setLocation(String location) {
		this.location = location;
	}

	public String getTarget() {
		return this.target;
	}

	private void setTarget(String target) {
		this.target = target;
	}

	public String getDate() {
		return this.date;
	}

	private void setDate(String date) {
		this.date = date;
	}

	public String getPerps() {
		return this.perps;
	}

	private void setPerps(String perps) {
		this.perps = perps;
	}

	public String getFatalities() {
		return this.fatalities;
	}

	private void setFatalities(String fatalities) {
		this.fatalities = fatalities;
	}

	public String getInjuries() {
		return this.injuries;
	}

	private void setInjuries(String injuries) {
		this.injuries = injuries;
	}

	public String getType() {
		return this.type;
	}

	private void setType(String type) {
		this.type = type;
	}

	// private static final String[] IncidentAttributes={"Location", "Date",
	// "Preps", "Fatalities", "Injuries", "AttackType", "Target"};
	public String getAttributes(String attribute) {
		if (attribute.equals("Location")) {
			return this.location;
		} else if (attribute.equals("Date")) {
			return this.date;
		} else if (attribute.equals("Preps")) {
			return this.perps;
		} else if (attribute.equals("Fatalities")) {
			return this.injuries;
			//return this.fatalities; #we're actually looking for injuries.
		} else if (attribute.equals("Injuries")) {
			return this.injuries;
		} else if (attribute.equals("AttackType")) {
			return this.type;
		} else if (attribute.equals("Target")) {
			return this.target;
		}
		return "Error not found!!!";
	}

	public static String getInfoBoxRawText(String pageStr) {
		pageStr = pageStr.replaceAll(" ", "_");
		String infoBoxStr = "";
		boolean redirect = false;
		try {
			URL url = new URL(
					"http://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles="
							+ pageStr
							+ "&rvprop=timestamp|user|comment|content");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			BufferedReader in;
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			StringBuffer wikiText = new StringBuffer();
			String str;
			while ((str = in.readLine()) != null) {
				wikiText.append(str + "\n");
				if (str.contains("#REDIRECT [[")) {
					str = str.substring(str.indexOf("#REDIRECT [["));
					str = str.replace("#REDIRECT [[", "");
					pageStr = str.substring(0, str.indexOf("]]"));
					pageStr = pageStr.replaceAll(" ", "_");
					redirect = true;
					break;
				}
			}
			in.close();

			url = new URL(
					"http://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles="
							+ pageStr
							+ "&rvprop=timestamp|user|comment|content");
			// url = new
			// URL("http://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=2007_Kirkuk_bombings&rvprop=timestamp|user|comment|content");

			conn = (HttpURLConnection) url.openConnection();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			wikiText = new StringBuffer();

			while ((str = in.readLine()) != null) {
				if (str.contains("{{Infobox")) {
					str = str.replace("{{Infobox", "\n{{Infobox");
				}
				wikiText.append(str + "\n");
			}
			in.close();

			WikiTextParser wtp = new WikiTextParser(wikiText.toString());
			InfoBox infoBox = wtp.getInfoBox();
			if (infoBox != null) {
				infoBoxStr = infoBox.dumpRaw();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return infoBoxStr;
	}
}
