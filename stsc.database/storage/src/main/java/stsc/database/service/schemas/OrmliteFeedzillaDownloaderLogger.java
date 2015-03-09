package stsc.database.service.schemas;

import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import stsc.common.service.statistics.FeedzillaDownloaderLogger;
import stsc.common.service.statistics.StatisticType;
import stsc.database.service.storages.FeedzillaDownloaderDatabaseStorage;

public class OrmliteFeedzillaDownloaderLogger implements FeedzillaDownloaderLogger {

	private final Logger logger;
	private final FeedzillaDownloaderDatabaseStorage storage;
	private final String settingName;
	private final int processId;
	private final Date startDate;

	public OrmliteFeedzillaDownloaderLogger(final Logger logger, final FeedzillaDownloaderDatabaseStorage storage,
			final String settingName, final int processId, final Date startDate) {
		this.logger = logger;
		logger.info("logger initialized. processId: " + processId + ", startDate: " + startDate);
		this.settingName = settingName;
		this.storage = storage;
		this.processId = processId;
		this.startDate = startDate;
	}

	@Override
	public boolean log(StatisticType statisticType, String message) {
		final OrmliteFeedzillaDownloaderStatistics s = new OrmliteFeedzillaDownloaderStatistics(settingName);
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
