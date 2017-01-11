package com.github.mozvip.mediainfo;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaInfoWrapper {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( MediaInfoWrapper.class );
	
	private Path pathToMediaInfo;
	private Map<String, Locale> languageToLocaleMap = new HashMap<>();
	
	public static final class Builder {
		
		private Path pathToMediaInfo;
		
		public Builder pathToMediaInfo( Path pathToMediaInfo ) {
			this.pathToMediaInfo = pathToMediaInfo;
			return this;
		}

		public MediaInfoWrapper build() {
			return new MediaInfoWrapper(pathToMediaInfo);
		}
		
	}

	public static Builder Builder() {
		return new Builder();
	}	
	
	private MediaInfoWrapper( Path pathToMediaInfo ) {
		this.pathToMediaInfo = pathToMediaInfo;
		
		Locale[] availableLocales = Locale.getAvailableLocales();

		for (Locale locale : availableLocales) {
			String language = locale.getDisplayLanguage().toLowerCase();
			if (languageToLocaleMap.containsKey( language ) ) {
				if (locale.getCountry().isEmpty()) {
					languageToLocaleMap.put( language, locale);
				}
			} else {
				languageToLocaleMap.put( language, locale);
			}
		}
		languageToLocaleMap.put( "jap", new Locale("ja_JP"));
		languageToLocaleMap.put( "frf", new Locale("fr_FR"));
	}
	
	public Locale getLocaleForLanguage( String language ) {
		Locale locale = languageToLocaleMap.get(language.toLowerCase());
		if (locale != null) {
			return locale;
		} else {
			LOGGER.warn("Unrecognized language : {}", language);
		}
		return null;
	}
	
	public MediaInfo getMediaInfo( Path videoFilePath ) throws IOException, InterruptedException {
		if ( pathToMediaInfo == null || !Files.isRegularFile( pathToMediaInfo ) && Files.isExecutable( pathToMediaInfo )) {
			return null;
		}

		Path targetFile = Paths.get( videoFilePath.toAbsolutePath().toString() +".mediainfo.html" );
		Document html = null;

		if (!Files.exists(targetFile)) {
			ProcessBuilder pb = new ProcessBuilder( pathToMediaInfo.toAbsolutePath().toString(), "--Output=HTML", videoFilePath.toAbsolutePath().toString() );
			pb.redirectOutput( Redirect.to( targetFile.toFile() ) );
			Process p = pb.start();
			p.waitFor();
		}
		
		html = Jsoup.parse( targetFile.toFile(), "UTF-8" );
		
		MediaInfo mediaInfo = new MediaInfo();
		
		Elements generalTables = html.select("table:has(h2:contains(General))");
		Element generalTable = generalTables != null ? generalTables.first() : null;
		if ( generalTable != null ) {
			String durationStr = generalTable.select("td:has(i:contains(Duration)) + td").first().text();
			
			
			
		}
		
		Elements videoTables = html.select("table:has(h2:contains(Video))");
		Element videoTable = videoTables != null ? videoTables.first() : null;
		if (videoTable != null) {
			String width = videoTable.select("td:has(i:contains(Width)) + td").first().text();
			String height = videoTable.select("td:has(i:contains(Height)) + td").first().text();
			
			mediaInfo.setWidth( extractDimension(width) );
			mediaInfo.setHeight( extractDimension(height) );
		}
				
		Elements audioElements = html.select("table:has(h2:contains(Audio))");
		for (Element audioElement : audioElements) {
			Elements languageElements = audioElement.select("td:has(i:contains(Language)) + td");
			if (!languageElements.isEmpty()) {
				String l = languageElements.first().text();
				Locale locale = getLocaleForLanguage(l);
				if (locale != null) {
					mediaInfo.addAudioLanguage(locale);
				}
			}
		}

		Elements textElements = html.select("table:has(h2:contains(Text))");
		for (Element textElement : textElements) {
			Elements languageElements = textElement.select("td:has(i:contains(Language)) + td");
			if (!languageElements.isEmpty()) {
				String l = languageElements.first().text();
				Locale locale = getLocaleForLanguage(l);
				if (locale != null) {
					mediaInfo.addSubtitle(locale);
				}
			}
		}

		return mediaInfo;
	}

	private int extractDimension(String str) {
		Scanner scanner = new Scanner( str );
		scanner.findInLine("([\\d\\s]+) pixels");
		MatchResult result = scanner.match();
		
		String intValue = result.group(1).replaceAll("\\D", "");
		
		return Integer.parseInt( intValue );
	}	

}
