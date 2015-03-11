package stsc.common.service.statistics;

import org.apache.logging.log4j.Logger;

public interface DownloaderLogger {

	/**
	 * log message with statistic type (if logger is turned on to log it)
	 * 
	 * @return true if message was successfully logged
	 */
	public boolean log(StatisticType statisticType, String message);

	public Logger log();

}
