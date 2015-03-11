package stsc.database.service.schemas;

import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import stsc.common.service.statistics.StatisticType;
import stsc.common.service.statistics.DownloaderLogger;
import stsc.database.service.storages.YahooDownloaderDatabaseStorage;

public class OrmliteYahooDownloaderLogger implements DownloaderLogger {

	private final Logger logger;
	private final YahooDownloaderDatabaseStorage storage;
	private final String settingName;
	private final int processId;
	private final Date startDate;

	public OrmliteYahooDownloaderLogger(final Logger logger, final YahooDownloaderDatabaseStorage storage, final String settingName,
			final int processId, final Date startDate) {
		this.logger = logger;
		logger.info("logger initialized. processId: " + processId + ", startDate: " + startDate);
		this.settingName = settingName;
		this.storage = storage;
		this.processId = processId;
		this.startDate = startDate;
	}

	@Override
	public boolean log(StatisticType statisticType, String message) {
		final OrmliteYahooDownloaderStatistics s = new OrmliteYahooDownloaderStatistics(settingName);
		s.setProcessId(processId);
		s.setStartDate(startDate);
		s.setStatisticType(statisticType);
		s.setMessage(message);
		try {
			return storage.setStatistics(s).isCreated();
		} catch (SQLException e) {
			log().error("logging to database failed: ", e);
		}
		return false;
	}

	@Override
	public Logger log() {
		return logger;
	}

}
