package stsc.common.service.statistics;

import java.util.Date;

public interface YahooDownloaderStatistics {

	/**
	 * Get timestamp when execution started
	 */
	public Date startDate();

	/**
	 * Get process id
	 */
	public int processId();

	/**
	 * Type of statistics (analog like log4j have it)
	 */
	public StatisticType statisticType();

	/**
	 * String message
	 */
	public String message();

}
