package com.github.mozvip.mediainfo;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MediaInfo {

	private int width;
	private int height;
	private BigDecimal fps;
	private Duration duration;
	
	private Set<Locale> audioLanguages = new HashSet<>();
	private Set<Locale> subtitles = new HashSet<>();

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public BigDecimal getFps() {
		return fps;
	}

	public void setFps(BigDecimal fps) {
		this.fps = fps;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	public void addSubtitle( Locale locale ) {
		subtitles.add( locale );
	}

	public void addAudioLanguage( Locale locale ) {
		audioLanguages.add( locale );
	}
	
	public Set<Locale> getAudioLanguages() {
		return audioLanguages;
	}
	
	public Set<Locale> getSubtitles() {
		return subtitles;
	}

}
