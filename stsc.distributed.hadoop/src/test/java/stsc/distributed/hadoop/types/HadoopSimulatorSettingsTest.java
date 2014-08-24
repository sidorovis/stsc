package stsc.distributed.hadoop.types;

import java.io.IOException;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.io.DataOutputByteBuffer;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.testhelper.TestGeneticSimulatorSettings;
import junit.framework.Assert;
import junit.framework.TestCase;

public class HadoopSimulatorSettingsTest extends TestCase {

	private final DataOutputByteBuffer output = new DataOutputByteBuffer();
	private final DataInputByteBuffer input = new DataInputByteBuffer();

	public void testHadoopSimulatorSettings() throws IOException, BadAlgorithmException {
		final SimulatorSettingsGeneticList list = TestGeneticSimulatorSettings.getGeneticList();

		final SimulatorSettings ss = list.generateRandom();
		final HadoopSimulatorSettings hss = new HadoopSimulatorSettings(ss);

		hss.write(output);
		input.reset(output.getData());

		final HadoopSimulatorSettings hssCopy = new HadoopSimulatorSettings();
		hssCopy.readFields(input);

		final SimulatorSettings settingsCopy = hssCopy.getSimulatorSettings(list.getStockStorage());
		Assert.assertEquals(ss.stringHashCode(), settingsCopy.stringHashCode());
	}
}
