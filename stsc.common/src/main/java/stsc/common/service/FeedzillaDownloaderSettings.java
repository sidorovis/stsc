package stsc.common.service;

public interface FeedzillaDownloaderSettings {

	/**
	 * amount of days from today (by local time) that we will download from
	 * Feedzilla
	 */
	public int daysBackDownloadFrom();

	/**
	 * we will download that endless
	 */
	public boolean endlessCycle();

	/**
	 * time in seconds we wait for articles
	 */
	public int articlesWaitTime();

	/**
	 * feed folder where we will download everything
	 */
	public String feedFolder();

	/**
	 * sleep interval in seconds to sleep between cycle runs
	 */
	public int intervalBetweenExecutions();

}
