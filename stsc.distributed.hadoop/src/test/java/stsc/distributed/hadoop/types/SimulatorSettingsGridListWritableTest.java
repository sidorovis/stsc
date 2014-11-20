package stsc.distributed.hadoop.types;

import java.io.IOException;

import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.io.DataOutputByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.testhelper.TestGridSimulatorSettings;

public class SimulatorSettingsGridListWritableTest {

	@Test
	public void testSimulatorSettingsGridListWritable() throws IOException, BadParameterException {
		final SimulatorSettingsGridList list = TestGridSimulatorSettings.getGridList();

		final DataOutputByteBuffer output = new DataOutputByteBuffer();
		final DataInputByteBuffer input = new DataInputByteBuffer();

		final SimulatorSettingsGridListWritable ssgl = new SimulatorSettingsGridListWritable(list);

		ssgl.write(output);
		input.reset(output.getData());

		final SimulatorSettingsGridListWritable ssglCopy = new SimulatorSettingsGridListWritable();
		ssglCopy.readFields(input);

		final SimulatorSettingsGridList listCopy = ssglCopy.getGridList(list.getStockStorage());
		Assert.assertEquals(list.getPeriod().toString(), listCopy.getPeriod().toString());
		Assert.assertEquals(list.getStockInitializers().size(), listCopy.getStockInitializers().size());
		Assert.assertEquals(list.getEodInitializers().size(), listCopy.getEodInitializers().size());
	}
}
