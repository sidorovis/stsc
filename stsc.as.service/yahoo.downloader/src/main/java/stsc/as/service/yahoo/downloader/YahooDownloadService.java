package stsc.as.service.yahoo.downloader;

import java.util.TimeZone;

import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

public class YahooDownloadService {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private YahooDownloadService() {

	}

	public static void main(String[] args) {

	}

}
