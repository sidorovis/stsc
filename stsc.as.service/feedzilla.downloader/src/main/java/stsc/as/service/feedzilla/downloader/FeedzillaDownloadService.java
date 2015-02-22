package stsc.as.service.feedzilla.downloader;

import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.ApplicationHelper;
import stsc.common.service.StopableApp;

final class FeedzillaDownloadService implements StopableApp {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private final Logger logger = LogManager.getLogger(FeedzillaDownloadService.class.getName());
	
	public FeedzillaDownloadService() {

	}

	public static void main(String[] args) {
		try {
			final StopableApp app = new FeedzillaDownloadService();
			ApplicationHelper.createHelper(app);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void log(Level logLevel, String message) {
		logger.error("log(Level, String): " + logLevel.getName() + " " + message);
	}

}
