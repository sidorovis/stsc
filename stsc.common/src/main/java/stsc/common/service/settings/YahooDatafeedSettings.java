package stsc.common.service.settings;

import org.apache.commons.lang3.tuple.Pair;

public interface YahooDatafeedSettings {

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
	 * in characters for stock names to download from yahoo
	 */
	public Pair<Integer, Integer> stockNameSizePair();

	/**
	 * stock names patterns to download
	 */
	public Pair<String, String> patternNamePair();

}
