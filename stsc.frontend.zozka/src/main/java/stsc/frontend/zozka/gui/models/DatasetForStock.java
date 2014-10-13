package stsc.frontend.zozka.gui.models;

import java.util.Collections;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.OHLCDataset;

import stsc.common.Day;
import stsc.common.FromToPeriod;
import stsc.common.stocks.Stock;

public class DatasetForStock extends AbstractXYDataset implements OHLCDataset {

	private static final long serialVersionUID = -190317966728843101L;

	private final Stock stock;
	private final int fromIndex;
	private final int toIndex;

	public DatasetForStock(Stock stock, FromToPeriod period) {
		this.stock = stock;
		this.fromIndex = Math.abs(Collections.binarySearch(stock.getDays(), new Day(period.getFrom())));
		this.toIndex = Math.abs(Collections.binarySearch(stock.getDays(), new Day(period.getTo())));
	}

	private Day get(int index) {
		return stock.getDays().get(fromIndex + index);
	}

	@Override
	public int getItemCount(int series) {
		if (toIndex - fromIndex - 1 <= 0)
			return 0;
		return toIndex - fromIndex - 1;
	}

	@Override
	public Number getX(int series, int item) {
		return new Long(get(item).getDate().getTime());
	}

	@Override
	public Number getY(int series, int item) {
		return getClose(series, item);
	}

	@Override
	public Number getHigh(int series, int item) {
		return get(item).getPrices().getHigh();
	}

	@Override
	public double getHighValue(int series, int item) {
		return getHigh(series, item).doubleValue();
	}

	@Override
	public Number getLow(int series, int item) {
		return get(item).getPrices().getLow();
	}

	@Override
	public double getLowValue(int series, int item) {
		return getLow(series, item).doubleValue();
	}

	@Override
	public Number getOpen(int series, int item) {
		return get(item).getPrices().getOpen();
	}

	@Override
	public double getOpenValue(int series, int item) {
		return getOpen(series, item).doubleValue();
	}

	@Override
	public Number getClose(int series, int item) {
		return get(item).getPrices().getClose();
	}

	@Override
	public double getCloseValue(int series, int item) {
		return getClose(series, item).doubleValue();
	}

	@Override
	public Number getVolume(int series, int item) {
		return get(item).getVolume();
	}

	@Override
	public double getVolumeValue(int series, int item) {
		return getVolume(series, item).doubleValue();
	}

	@Override
	public int getSeriesCount() {
		return 1;
	}

	@Override
	public Comparable<String> getSeriesKey(int series) {
		return "";
	}

}
