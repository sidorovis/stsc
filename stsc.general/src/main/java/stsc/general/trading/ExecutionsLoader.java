package stsc.general.trading;

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

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockExecution;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;

final class ExecutionsLoader {

	private static final class PropertyNames {
		public static String INCLUDES_LINE = "Includes";
		public static String STOCK_EXECUTIONS_LINE = "StockExecutions";
		public static String EOD_EXECUTIONS_LINE = "EodExecutions";
	}

	private static final class Regexps {
		public static final Pattern loadLine = Pattern.compile("^(\\w+)\\((.*)\\)$");
		public static final Pattern subAlgoParameter = Pattern.compile("^([^\\(]+)\\((.*)\\)(\\s)*$");
		public static final Pattern integerParameter = Pattern.compile("^(.+)=(.+)[iI]$");
		public static final Pattern doubleParameter = Pattern.compile("^(.+)=(.+)[dD]$");
		public static final Pattern stringParameter = Pattern.compile("^(.+)=(.+)$");
		public static final Pattern subExecutionParameter = Pattern.compile("^(.+)$");
	}

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/algorithmLoader.log4j2.xml");
	}
	private static Logger logger = LogManager.getLogger("ExecutionsLoader");

	public File configPath = new File("./config/algs.ini");
	private String configFileFolder;
	final private AlgorithmSettingsImpl settings;
	private AlgorithmsStorage algorithmsStorage;
	final private ExecutionsStorage executionsStorage = new ExecutionsStorage();

	final private Set<String> openedPropertyFileNames = new HashSet<>();

	final private Set<String> registeredStockExecutions = new HashSet<>();
	final private HashMap<String, String> namedStockExecutions = new HashMap<>();

	final private Set<String> eodExecutions = new HashSet<>();

	ExecutionsLoader(FromToPeriod period, String config) throws BadAlgorithmException {
		this.settings = new AlgorithmSettingsImpl(period);
		this.algorithmsStorage = AlgorithmsStorage.getInstance();
		loadAlgorithms(config);
	}

	ExecutionsLoader(File configPath, FromToPeriod period) throws BadAlgorithmException {
		this.configPath = configPath;
		this.settings = new AlgorithmSettingsImpl(period);
		this.algorithmsStorage = AlgorithmsStorage.getInstance();
		loadAlgorithms();
	}

	ExecutionsLoader(File configPath, FromToPeriod period, String algoPackageName) throws BadAlgorithmException {
		this.configPath = configPath;
		this.settings = new AlgorithmSettingsImpl(period);
		this.algorithmsStorage = AlgorithmsStorage.getInstance(algoPackageName);
		loadAlgorithms();
	}

	private void loadAlgorithms() throws BadAlgorithmException {
		logger.info("start executions loader");
		configFileFolder = new File(configPath.getParent()).toString() + File.separatorChar;
		logger.debug("configuration path: {}", configFileFolder);
		openedPropertyFileNames.add(configPath.getName());
		try (FileInputStream in = new FileInputStream(configPath)) {
			final Properties p = new Properties();
			logger.debug("main properties file '{}' opened", configFileFolder);
			p.load(in);
			processProperties(p);
		} catch (IOException e) {
			throw new BadAlgorithmException(e.getMessage());
		}
		logger.info("stop executions loader");
	}

	private void loadAlgorithms(String config) {
		final Properties p = new Properties();
		for (String line : config.split(System.lineSeparator())) {
			String[] value = line.split("=");
			if (value.length == 2) {
				p.setProperty(value[0], value[1]);
			}
		}
	}

	private void processProperties(final Properties p) throws FileNotFoundException, IOException, BadAlgorithmException {
		processIncludes(p);
		processStocksLoadLines(p);
		processEodLoadLines(p);
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

	private void processStocksLoadLines(final Properties p) throws BadAlgorithmException {
		final String stockNames = p.getProperty(PropertyNames.STOCK_EXECUTIONS_LINE);
		if (stockNames == null)
			return;
		for (String rawExecutionName : stockNames.split(",")) {
			final String executionName = rawExecutionName.trim();
			final String loadLine = p.getProperty(executionName + ".loadLine");
			if (loadLine == null)
				throw new BadAlgorithmException("bad stock execution registration, no " + executionName + ".loadLine property");
			checkNewStockExecution(executionName);
			final String generatedName = processStockExecution(loadLine);
			namedStockExecutions.put(executionName, generatedName);
			registeredStockExecutions.add(generatedName);
		}
	}

	private void checkNewStockExecution(final String executionName) throws BadAlgorithmException {
		if (namedStockExecutions.containsKey(executionName))
			throw new BadAlgorithmException("algorithm " + executionName + " already registered");
	}

	private String processStockExecution(String loadLine) throws BadAlgorithmException {
		final Matcher loadLineMatch = Regexps.loadLine.matcher(loadLine);
		if (loadLineMatch.matches()) {
			return processStockSubExecution(loadLineMatch);
		} else
			throw new BadAlgorithmException("bad algorithm load line: " + loadLine);
	}

	private String processStockSubExecution(Matcher match) throws BadAlgorithmException {
		final List<String> params = parseParams(match.group(2).trim());
		return processStockExecution(match.group(1).trim(), params);
	}

	private String processStockExecution(String algorithmName, final List<String> params) throws BadAlgorithmException {
		final Class<? extends StockAlgorithm> stockAlgorithm = algorithmsStorage.getStock(algorithmName);
		if (stockAlgorithm == null)
			throw new BadAlgorithmException("there is no such algorithm like " + algorithmName);

		final AlgorithmSettings algorithmSettings = generateStockAlgorithmSettings(params);

		final String executionName = generateExecutionName(algorithmName, algorithmSettings);
		if (registeredStockExecutions.contains(executionName))
			return executionName;
		final StockExecution execution = new StockExecution(executionName, stockAlgorithm, algorithmSettings);
		executionsStorage.addStockExecution(execution);
		return executionName;
	}

	private void processEodLoadLines(final Properties p) throws BadAlgorithmException {
		final String eodNames = p.getProperty(PropertyNames.EOD_EXECUTIONS_LINE);
		if (eodNames == null)
			return;
		for (String rawExecutionName : eodNames.split(",")) {
			final String executionName = rawExecutionName.trim();
			final String loadLine = p.getProperty(executionName + ".loadLine");
			if (loadLine == null)
				throw new BadAlgorithmException("bad eod algorithm execution registration, no " + executionName + ".loadLine property");
			checkNewEodExecution(executionName);
			processEodExecution(executionName, loadLine);
			eodExecutions.add(executionName);
		}
	}

	private void checkNewEodExecution(final String executionName) throws BadAlgorithmException {
		if (eodExecutions.contains(executionName))
			throw new BadAlgorithmException("eod algorithm " + executionName + " already registered");
	}

	private void processEodExecution(final String executionName, final String loadLine) throws BadAlgorithmException {
		final Matcher loadLineMatch = Regexps.loadLine.matcher(loadLine);
		if (loadLineMatch.matches()) {
			processEodSubExecution(executionName, loadLineMatch);
		} else
			throw new BadAlgorithmException("bad algorithm load line: " + loadLine);
	}

	private void processEodSubExecution(final String executionName, final Matcher match) throws BadAlgorithmException {
		final List<String> params = parseParams(match.group(2).trim());
		processEodExecution(executionName, match.group(1).trim(), params);
	}

	private void processEodExecution(String executionName, String algorithmName, final List<String> params) throws BadAlgorithmException {
		final Class<? extends EodAlgorithm> eodAlgorithm = algorithmsStorage.getEod(algorithmName);
		if (eodAlgorithm == null)
			throw new BadAlgorithmException("there is no such algorithm like " + algorithmName);

		final AlgorithmSettings algorithmSettings = generateStockAlgorithmSettings(params);

		final EodExecution execution = new EodExecution(executionName, eodAlgorithm, algorithmSettings);
		executionsStorage.addEodExecution(execution);
	}

	private AlgorithmSettings generateStockAlgorithmSettings(final List<String> params) throws BadAlgorithmException {
		final AlgorithmSettingsImpl algorithmSettings = settings.clone();

		for (final String parameter : params) {
			final Matcher subAlgoMatch = Regexps.subAlgoParameter.matcher(parameter);
			final Matcher integerMatch = Regexps.integerParameter.matcher(parameter);
			final Matcher doubleMatch = Regexps.doubleParameter.matcher(parameter);
			final Matcher stringMatch = Regexps.stringParameter.matcher(parameter);
			final Matcher subExecutionMatch = Regexps.subExecutionParameter.matcher(parameter);
			if (subAlgoMatch.matches()) {
				final String subName = processStockSubExecution(subAlgoMatch);
				registeredStockExecutions.add(subName);
				algorithmSettings.addSubExecutionName(subName);
			} else if (integerMatch.matches()) {
				algorithmSettings.setInteger(integerMatch.group(1).trim(), Integer.valueOf(integerMatch.group(2).trim()));
			} else if (doubleMatch.matches()) {
				algorithmSettings.setDouble(doubleMatch.group(1).trim(), Double.valueOf(doubleMatch.group(2).trim()));
			} else if (stringMatch.matches()) {
				algorithmSettings.setString(stringMatch.group(1).trim(), stringMatch.group(2).trim());
			} else if (subExecutionMatch.matches()) {
				final String subExecutionName = subExecutionMatch.group(1).trim();
				final String executionCode = namedStockExecutions.get(subExecutionName);
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
		final String name = Integer.valueOf(algorithmName.toLowerCase().hashCode() * 31).toString()
				+ Integer.valueOf(algorithmSettings.toString().hashCode()).toString();
		return name;
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

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

	public HashMap<String, String> getNamedExecutions() {
		return namedStockExecutions;
	}
}
