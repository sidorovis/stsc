package stsc.distributed.hadoop.types;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.io.DataOutputByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.testhelper.TestGeneticSimulatorSettings;

public class SimulatorSettingsGeneticListWritableTest {

	@Test
	public void testSimulatorSettingsGeneticListWritable() throws BadAlgorithmException, IOException, BadParameterException {
		final SimulatorSettingsGeneticList list = TestGeneticSimulatorSettings.getGeneticList();

		final DataOutputByteBuffer output = new DataOutputByteBuffer();
		final DataInputByteBuffer input = new DataInputByteBuffer();

		final SimulatorSettingsGeneticListWritable ssgl = new SimulatorSettingsGeneticListWritable(list);

		ssgl.write(output);
		input.reset(output.getData());

		final SimulatorSettingsGeneticListWritable ssglCopy = new SimulatorSettingsGeneticListWritable();
		ssglCopy.readFields(input);

		final SimulatorSettingsGeneticList listCopy = ssglCopy.getGeneticList(list.getStockStorage());

		final List<GeneticExecutionInitializer> stocks = list.getStockInitializers();
		final List<GeneticExecutionInitializer> stocksCopy = listCopy.getStockInitializers();
		final List<GeneticExecutionInitializer> eods = list.getEodInitializers();
		final List<GeneticExecutionInitializer> eodsCopy = listCopy.getEodInitializers();

		Assert.assertEquals(stocks.size(), stocksCopy.size());
		Assert.assertEquals(eods.size(), eodsCopy.size());

		Assert.assertEquals(stocks.get(0).algorithmName, stocksCopy.get(0).algorithmName);
		Assert.assertEquals(stocks.get(1).algorithmName, stocksCopy.get(1).algorithmName);
	}
}
