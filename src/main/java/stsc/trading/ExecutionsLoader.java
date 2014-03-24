package stsc.trading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;
import org.apache.logging.log4j.Logger;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockAlgorithmExecution;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;

public class ExecutionsLoader {

	public static final class PropertyNames {
		public static String INCLUDES_LINE = "Includes";
		public static String STOCK_EXECUTIONS_LINE = "StockExecutions";
		public static String EOD_EXECUTIONS_LINE = "EodExecutions";
	}

	public static final class Regexps {
		public static final Pattern loadLine = Pattern.compile("^(\\w+)\\((.*)\\)$");
		public static final Pattern subAlgoParameter = Pattern.compile("^([^\\(]+)\\((.*)\\)(\\s)*$");
		public static final Pattern dataParameter = Pattern.compile("^(.+)=(.+)$");
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

	public ExecutionsLoader(final List<String> stockNames, final AlgorithmsStorage algorithmsStorage)
			throws FileNotFoundException, IOException, BadAlgorithmException {
		this.executionsStorage = new ExecutionsStorage(stockNames);
		this.algorithmsStorage = algorithmsStorage;
		loadAlgorithms();
	}

	private void loadAlgorithms() throws FileNotFoundException, IOException, BadAlgorithmException {
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

	private void processProperties(final Properties p) throws FileNotFoundException, IOException, BadAlgorithmException {
		processIncludes(p);
		processStockExecutions(p);
	}

	private void processIncludes(final Properties p) throws FileNotFoundException, IOException, BadAlgorithmException {
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

	private void processStockExecutions(final Properties p) throws BadAlgorithmException {
		final String executionsNames = p.getProperty(PropertyNames.STOCK_EXECUTIONS_LINE);
		if (executionsNames == null)
			return;
		for (String executionName : executionsNames.split(",")) {
			final String loadLine = p.getProperty(executionName + ".loadLine");
			if (loadLine == null) {
				logger.warn("bad execution registration, no {}.loadLine property", executionName);
				continue;
			}
			processStockExecution(executionName, loadLine);
		}
	}

	private void processStockExecution(final String executionName, final String loadLine) throws BadAlgorithmException {
		final Matcher loadLineMatch = Regexps.loadLine.matcher(loadLine);
		if (loadLineMatch.matches()) {
			final String algorithmName = loadLineMatch.group(1).trim();
			final String paramsString = loadLineMatch.group(2).trim();
			processSubExecution(algorithmName, paramsString);
		}
	}

	private String processSubExecution(final String algorithmName, final String paramsString)
			throws BadAlgorithmException {
		int inBracketsStack = 0;
		int lastParamIndex = 0;
		final ArrayList<String> params = new ArrayList<>();
		for (int i = 0; i < paramsString.length(); ++i) {
			if (paramsString.charAt(i) == '(') {
				inBracketsStack += 1;
			} else if (paramsString.charAt(i) == ')') {
				inBracketsStack -= 1;
			} else if (paramsString.charAt(i) == ',' && inBracketsStack == 0) {
				params.add(paramsString.substring(lastParamIndex, i).trim());
				lastParamIndex = i + 1;
			}
		}
		if (lastParamIndex != paramsString.length()) {
			params.add(paramsString.substring(lastParamIndex, paramsString.length()).trim());
		}
		return processStockExecution(algorithmName, params);
	}

	private String processStockExecution(final String algorithmName, final List<String> params)
			throws BadAlgorithmException {
		final Class<? extends StockAlgorithm> stockAlgo = algorithmsStorage.getStock(algorithmName);
		if (stockAlgo == null)
			throw new BadAlgorithmException("there is no such algorithm like " + algorithmName);

		final AlgorithmSettings algorithmSettings = new AlgorithmSettings();

		for (final String parameter : params) {
			final Matcher subAlgoMatch = Regexps.subAlgoParameter.matcher(parameter);
			final Matcher dataMatch = Regexps.dataParameter.matcher(parameter);
			if (subAlgoMatch.matches()) {
				final String subAlgoName = subAlgoMatch.group(1).trim();
				final String subAlgoParams = subAlgoMatch.group(2).trim();
				algorithmSettings.addSubExecutionName(processSubExecution(subAlgoName, subAlgoParams));
			} else if (dataMatch.matches()) {
				algorithmSettings.set(dataMatch.group(1).trim(), dataMatch.group(2).trim());
			}
		}
		final String executionName = generateExecutionName(algorithmName, algorithmSettings);
		final Class<? extends StockAlgorithm> stockAlgorithm = algorithmsStorage.getStock(algorithmName);
		final StockAlgorithmExecution stockAlgorithmExecution = new StockAlgorithmExecution(executionName,
				stockAlgorithm, algorithmSettings);
		executionsStorage.addStockAlgorithmExecution(stockAlgorithmExecution);
		return executionName;
	}

	private String generateExecutionName(String algorithmName, AlgorithmSettings algorithmSettings) {
		final String name = new Integer(algorithmName.hashCode() * 31).toString()
				+ new Integer(algorithmSettings.hashCode()).toString();
		return name;
	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

}
