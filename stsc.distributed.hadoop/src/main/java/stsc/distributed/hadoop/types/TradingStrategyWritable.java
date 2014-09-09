package stsc.distributed.hadoop.types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;
import stsc.general.strategy.TradingStrategy;

public class TradingStrategyWritable implements Writable {

	private StockStorage stockStorage;
	private TradingStrategy tradingStrategy;

	public TradingStrategyWritable(TradingStrategy ts) {
		this.tradingStrategy = ts;
	}

	protected TradingStrategyWritable() {
	}

	public void setStockStorage(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	public TradingStrategyWritable(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
		this.tradingStrategy = null;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		final SimulatorSettings settings = tradingStrategy.getSettings();
		final Statistics statistics = tradingStrategy.getStatistics();
		final SimulatorSettingsWritable ssw = new SimulatorSettingsWritable(settings);
		ssw.write(out);
		final StatisticsWritable sw = new StatisticsWritable(statistics);
		sw.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		final SimulatorSettingsWritable ssw = new SimulatorSettingsWritable();
		ssw.readFields(in);
		final StatisticsWritable sw = new StatisticsWritable();
		sw.readFields(in);
		try {
			tradingStrategy = new TradingStrategy(ssw.getSimulatorSettings(stockStorage), sw.getStatistics());
		} catch (BadAlgorithmException e) {
			throw new IOException(e.getMessage());
		}
	}

	public TradingStrategy getTradingStrategy() {
		return tradingStrategy;
	}
}
