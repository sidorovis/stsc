package stsc.trading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import stsc.storage.SignalsStorage;

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
		public static final Pattern subExecutionParameter = Pattern.compile("^(.+)$");
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

	private Set<String> registeredExecutions = new HashSet<>();
	private HashMap<String, String> namedExecutions = new HashMap<>();

	public ExecutionsLoader(List<String> stockNames, AlgorithmsStorage algorithmsStorage, Broker broker,
			SignalsStorage signalsStorage) throws FileNotFoundException, IOException, BadAlgorithmException {
		this.executionsStorage = new ExecutionsStorage(stockNames);
		this.algorithmsStorage = algorithmsStorage;
		loadAlgorithms();
		this.executionsStorage.initializeExecutions(signalsStorage, broker);
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
		for (String rawExecutionName : executionsNames.split(",")) {
			final String executionName = rawExecutionName.trim();
			final String loadLine = p.getProperty(executionName + ".loadLine");
			if (loadLine == null)
				throw new BadAlgorithmException("bad execution registration, no " + executionName
						+ ".loadLine property");
			checkNewExecution(executionName);
			final String generatedName = processStockExecution(loadLine);
			namedExecutions.put(executionName, generatedName);
			registeredExecutions.add(generatedName);
		}
	}

	private String processStockExecution(String loadLine) throws BadAlgorithmException {
		final Matcher loadLineMatch = Regexps.loadLine.matcher(loadLine);
		if (loadLineMatch.matches()) {
			final String algorithmName = loadLineMatch.group(1).trim();
			final String paramsString = loadLineMatch.group(2).trim();
			return processSubExecution(algorithmName, paramsString);
		} else
			throw new BadAlgorithmException("bad algorithm load line: " + loadLine);
	}

	private void checkNewExecution(final String executionName) throws BadAlgorithmException {
		if (registeredExecutions.contains(executionName))
			throw new BadAlgorithmException("algorithm " + executionName + " already registered");
	}

	private String processSubExecution(String algorithmName, String paramsString) throws BadAlgorithmException {
		final List<String> params = parseParams(paramsString);
		return processStockExecution(algorithmName, params);
	}

	private List<String> parseParams(final String paramsString) {
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

		return params;
	}

	private String processStockExecution(String algorithmName, final List<String> params) throws BadAlgorithmException {
		final Class<? extends StockAlgorithm> stockAlgorithm = algorithmsStorage.getStock(algorithmName);
		if (stockAlgorithm == null)
			throw new BadAlgorithmException("there is no such algorithm like " + algorithmName);

		final AlgorithmSettings algorithmSettings = generateAlgorithmSettings(params);

		final String executionName = generateExecutionName(algorithmName, algorithmSettings);
		if (registeredExecutions.contains(executionName))
			return executionName;
		final StockAlgorithmExecution execution = new StockAlgorithmExecution(executionName, stockAlgorithm,
				algorithmSettings);
		executionsStorage.addStockAlgorithmExecution(execution);
		return executionName;
	}

	private AlgorithmSettings generateAlgorithmSettings(final List<String> params) throws BadAlgorithmException {
		final AlgorithmSettings algorithmSettings = new AlgorithmSettings();

		for (final String parameter : params) {
			final Matcher subAlgoMatch = Regexps.subAlgoParameter.matcher(parameter);
			final Matcher dataMatch = Regexps.dataParameter.matcher(parameter);
			final Matcher subExecutionMatch = Regexps.subExecutionParameter.matcher(parameter);
			if (subAlgoMatch.matches()) {
				final String subName = processSubExecution(subAlgoMatch.group(1).trim(), subAlgoMatch.group(2).trim());
				registeredExecutions.add(subName);
				algorithmSettings.addSubExecutionName(subName);
			} else if (dataMatch.matches()) {
				algorithmSettings.set(dataMatch.group(1).trim(), dataMatch.group(2).trim());
			} else if (subExecutionMatch.matches()) {
				final String subExecutionName = subExecutionMatch.group(1).trim();
				final String executionCode = namedExecutions.get(subExecutionName);
				if (executionCode != null)
					algorithmSettings.addSubExecutionName(executionCode);
				else
					throw new BadAlgorithmException("unknown sub execution name: " + parameter);
			} else
				throw new BadAlgorithmException("bad sub execution line: " + parameter);
		}
		return algorithmSettings;
	}

	private static String generateExecutionName(String algorithmName, AlgorithmSettings algorithmSettings) {
		final String name = new Integer(algorithmName.hashCode() * 31).toString()
				+ new Integer(algorithmSettings.toString().hashCode()).toString();
		return name;
	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

}
