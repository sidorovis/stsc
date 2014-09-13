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

	private TradingStrategy tradingStrategy;

	private SimulatorSettingsWritable ssw;
	private StatisticsWritable sw;

	// private StockStorage stockStorage;

	public TradingStrategyWritable(TradingStrategy ts) {
		this.tradingStrategy = ts;
	}

	protected TradingStrategyWritable() {
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
		ssw = new SimulatorSettingsWritable();
		ssw.readFields(in);
		sw = new StatisticsWritable();
		sw.readFields(in);
	}

	public TradingStrategy getTradingStrategy(final StockStorage stockStorage) throws BadAlgorithmException {
		return tradingStrategy = new TradingStrategy(ssw.getSimulatorSettings(stockStorage), sw.getStatistics());
	}
}
