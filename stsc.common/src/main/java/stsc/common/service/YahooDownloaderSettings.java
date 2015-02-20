package stsc.common.service;

public interface YahooDownloaderSettings {

	/**
	 * parallel downloading threads amount
	 */
	public int threadAmount();

	/**
	 * will download only existed stocks
	 */
	public boolean downloadOnlyExisted();

	/**
	 * will download by pattern not by length (if downloadOnlyExisted() ==
	 * false)
	 */
	public boolean downloadByPattern();

	/**
	 * if downloadOnlyExisted() == false this options install permutation size
	 * in characters for stock names to download from Yahoo.
	 */
	public int stockNameFrom();

	public int stockNameTo();

	/**
	 * stock names patterns to download
	 */
	public String patternNameFrom();

	public String patternNameTo();

	public int intervalBetweenExecutions();
	
}
