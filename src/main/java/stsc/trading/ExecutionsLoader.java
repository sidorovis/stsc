package stsc.trading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;
import org.apache.logging.log4j.Logger;

import stsc.storage.ExecutionsStorage;
import sun.misc.Regexp;

public class ExecutionsLoader {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/algorithmLoader.log4j2.xml");
	}
	private static Logger logger = LogManager.getLogger("AlgorithmsLoader");

	public static String configFilePath = "./config/algs.ini";
	private File configFileFolder;

	private ExecutionsStorage executionsStorage;

	public ExecutionsLoader(final List<String> stockNames) throws FileNotFoundException, IOException {
		executionsStorage = new ExecutionsStorage(stockNames);
		loadAlgorithms();
	}

	private void loadAlgorithms() throws FileNotFoundException, IOException {
		logger.info("start algorithm loader");
		configFileFolder = new File(configFilePath).getParentFile();
		try (FileInputStream in = new FileInputStream(configFilePath)) {
			final Properties p = new Properties();
			logger.debug("properties file found");
			p.load(in);
			processProperties(p);
		}
		logger.info("stop algorithm loader");
	}

	private void processProperties(final Properties p) throws FileNotFoundException, IOException {
		processIncludes(p);
		final String algorithmsNames = p.getProperty("Algorithms");
		for (String algoName : algorithmsNames.split(",")) {
			final String loadLine = p.getProperty(algoName + ".loadLine");
			processAlgorithmLine(algoName, loadLine);
		}
	}

	private void processIncludes(final Properties p) throws FileNotFoundException, IOException {
		final String[] includesFileNames = p.getProperty("Includes").split(",");
		for (String fileName : includesFileNames) {
			try (FileInputStream in = new FileInputStream(configFileFolder + fileName.trim())) {
				final Properties includeProperty = new Properties();
				logger.debug("read include property file '{}'", fileName);
				includeProperty.load(in);
				processProperties(includeProperty);
			}
		}
	}

	private void processAlgorithmLine(final String algoName, final String loadLine) {
		final Regexp divideLoadLine = new Regexp("(\\w+)\\((.*)\\)");

	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

}
