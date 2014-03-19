package stsc.trading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;
import org.apache.logging.log4j.Logger;

import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;
import sun.misc.Regexp;

public class ExecutionsLoader {

	public static final class PropertyNames {
		public static String INCLUDES_LINE = "Includes";
		public static String ALGORITHMS_LINE = "Algorithms";
	}

	public static final class Regexps {
		public static final Pattern loadLine = Pattern.compile("^(\\w+)\\((.*)\\)$");
	}

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/algorithmLoader.log4j2.xml");
	}
	private static Logger logger = LogManager.getLogger("ExecutionsLoader");

	public static String configFilePath = "./config/algs.ini";
	private String configFileFolder;

	private ExecutionsStorage executionsStorage;
	private AlgorithmsStorage algorithmsStorage;
	private Set<String> openedPropertyFileNames = new HashSet<>();

	public ExecutionsLoader(final List<String> stockNames, AlgorithmsStorage algorithmsStorage)
			throws FileNotFoundException, IOException {
		this.executionsStorage = new ExecutionsStorage(stockNames);
		this.algorithmsStorage = algorithmsStorage;
		loadAlgorithms();
	}

	private void loadAlgorithms() throws FileNotFoundException, IOException {
		logger.info("start executions loader");
		final File configFile = new File(configFilePath);
		configFileFolder = new File(configFile.getParent()).toString() + File.separatorChar;
		logger.debug("configuration path: {}", configFileFolder);
		openedPropertyFileNames.add(configFile.getName());
		try (FileInputStream in = new FileInputStream(configFilePath)) {
			final Properties p = new Properties();
			logger.debug("main properties file '{}' opened", configFileFolder);
			p.load(in);
			processProperties(p);
		}
		logger.info("stop executions loader");
	}

	private void processProperties(final Properties p) throws FileNotFoundException, IOException {
		processIncludes(p);
		processAlgoritms(p);
	}

	private void processIncludes(final Properties p) throws FileNotFoundException, IOException {
		final String includes = p.getProperty(PropertyNames.INCLUDES_LINE);
		if (includes == null)
			return;
		final String[] includesFileNames = includes.split(",");
		for (String rawFileName : includesFileNames) {
			final String fileName = rawFileName.trim();
			if (openedPropertyFileNames.contains(fileName))
				continue;
			openedPropertyFileNames.add(fileName);
			try (FileInputStream in = new FileInputStream(configFileFolder + fileName)) {
				final Properties includeProperty = new Properties();
				logger.debug("read include property file '{}'", fileName);
				includeProperty.load(in);
				processProperties(includeProperty);
			}
		}
	}

	private void processAlgoritms(final Properties p) {
		final String algorithmsNames = p.getProperty(PropertyNames.ALGORITHMS_LINE);
		if (algorithmsNames == null)
			return;
		for (String algoName : algorithmsNames.split(",")) {
			final String loadLine = p.getProperty(algoName + ".loadLine");
			if (loadLine == null) {
				logger.warn("bad algorithm registration, no {}.loadLine property", algoName);
				continue;
			}
			processAlgorithmLine(algoName, loadLine);
		}
	}

	private void processAlgorithmLine(final String algoName, final String loadLine) {
		final Matcher loadLineMatch = Regexps.loadLine.matcher(loadLine);
		if (loadLineMatch.matches()) {
			System.out.println(loadLineMatch.group(1).trim());
			System.out.println(loadLineMatch.group(2).trim());
		}
	}

	
	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

}
