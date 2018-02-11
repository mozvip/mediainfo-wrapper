package com.github.mozvip.mediainfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.mediainfo.MediaInfo;
import com.github.mozvip.mediainfo.MediaInfoWrapper;

public class MediaInfoWrapperTest {
	
	static MediaInfoWrapper wrapper;
	
	@BeforeClass
	public static void init() {
		String MEDIAINFO_PATH = System.getenv("MEDIAINFO_PATH");
		Assert.assertNotNull(MEDIAINFO_PATH);
		wrapper = MediaInfoWrapper.Builder().pathToMediaInfo(Paths.get(MEDIAINFO_PATH)).build();
	}

	@Test
	public void test() throws IOException, InterruptedException, URISyntaxException {
		
		URL resource = getClass().getClassLoader().getResource("SampleVideo_1280x720_1mb.mkv");
		
		Path videoFilePath = Paths.get(resource.toURI());
		MediaInfo mediaInfo = wrapper.getMediaInfo(videoFilePath);
		
		Assert.assertEquals(1280, mediaInfo.getWidth());
	}
	
	@Test
	public void testLanguageToLocale() {
		Locale japanese = wrapper.getLocaleForLanguage("jap");
		Assert.assertNotNull( japanese );
	}

}
