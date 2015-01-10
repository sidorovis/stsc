package stsc.news.feedzilla;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class FeedzillaDatafeedSettings {

	private final String jdbcDriver;
	private final String jdbcUrl;

	public FeedzillaDatafeedSettings() throws IOException {
		this("feedzilla_developer.properties");
	}

	public FeedzillaDatafeedSettings(String propertyFileName) throws IOException {
		try (InputStream inputStream = FeedzillaDatafeedSettings.class.getResourceAsStream(propertyFileName)) {
			final Properties properties = new Properties();
			properties.load(inputStream);
			jdbcDriver = properties.getProperty("jdbc.driver");
			jdbcUrl = properties.getProperty("jdbc.url");
		}
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

}
