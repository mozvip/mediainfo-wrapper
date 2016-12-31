package fr.mozvip.mediainfo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

public class MediaInfoWrapperTest {
	
	private static final String MEDIAINFO_PATH = "d:/apps/mediainfo/mediainfo.exe";
	
	static MediaInfoWrapper wrapper;
	
	@BeforeClass
	public static void init() {
		wrapper = MediaInfoWrapper.Builder().pathToMediaInfo(Paths.get(MEDIAINFO_PATH)).build();
	}

	@Test
	public void test() throws IOException, InterruptedException {
		MediaInfo mediaInfo = wrapper.getMediaInfo(Paths.get("\\\\DLINK-4T\\Volume_1\\movies\\28 Days Later... 2002 1080p DTS multisub HighCode.mkv"));
		
		for (Locale locale : mediaInfo.getSubtitles()) {
			System.out.println( locale );
		}
		
	}

}
