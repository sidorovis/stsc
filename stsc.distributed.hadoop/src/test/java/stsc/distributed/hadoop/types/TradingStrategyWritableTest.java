package stsc.distributed.hadoop.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.io.DataOutputByteBuffer;

import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.statistic.Statistics;
import stsc.general.strategy.TradingStrategy;
import stsc.general.testhelper.TestGeneticSimulatorSettings;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class TradingStrategyWritableTest extends TestCase {

	private SimulatorSettings getSettings() throws BadAlgorithmException {
		final SimulatorSettingsGeneticList list = TestGeneticSimulatorSettings.getGeneticList();
		return list.generateRandom();
	}

	private Statistics getStatistics() {
		final Map<String, Double> list = new HashMap<>();
		list.put("getAvGain", 10.45);
		list.put("getAvWinAvLoss", 62.13);
		list.put("getPeriod", 16.0);
		return new Statistics(list);
	}

	public void testTradingStrategyWritable() throws BadAlgorithmException, IOException {
		final TradingStrategy ts = new TradingStrategy(getSettings(), getStatistics());

		final DataOutputByteBuffer output = new DataOutputByteBuffer();
		final DataInputByteBuffer input = new DataInputByteBuffer();

		final TradingStrategyWritable tsw = new TradingStrategyWritable(ts);

		tsw.write(output);
		input.reset(output.getData());

		final TradingStrategyWritable tswCopy = new TradingStrategyWritable(StockStorageMock.getStockStorage());
		tswCopy.readFields(input);

		final TradingStrategy tsCopy = tswCopy.getTradingStrategy();
		assertEquals(ts.getAvGain(), tsCopy.getAvGain(), Settings.doubleEpsilon);
		assertEquals(ts.getSettings().stringHashCode(), tsCopy.getSettings().stringHashCode());
		assertEquals(ts.getStatistics().getPeriod(), tsCopy.getStatistics().getPeriod());
	}
}
