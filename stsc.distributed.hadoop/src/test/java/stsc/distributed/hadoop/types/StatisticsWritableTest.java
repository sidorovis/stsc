package stsc.distributed.hadoop.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.io.DataOutputByteBuffer;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import junit.framework.TestCase;

public class StatisticsWritableTest extends TestCase {
	public void testStatisticsWritable() throws IOException {
		final Map<String, Double> list = new HashMap<>();
		list.put("getAvGain", 10.45);
		list.put("getAvWinAvLoss", 62.13);
		list.put("getPeriod", 16.0);
		final Statistics s = new Statistics(list);
		assertEquals(10.45, s.getAvGain(), Settings.doubleEpsilon);
		assertEquals(62.13, s.getAvWinAvLoss(), Settings.doubleEpsilon);
		assertEquals(16, s.getPeriod(), Settings.doubleEpsilon);

		final DataOutputByteBuffer output = new DataOutputByteBuffer();
		final DataInputByteBuffer input = new DataInputByteBuffer();

		final StatisticsWritable sw = new StatisticsWritable(s);

		sw.write(output);
		input.reset(output.getData());

		final StatisticsWritable swCopy = new StatisticsWritable();
		swCopy.readFields(input);

		final Statistics sCopy = swCopy.getStatistics();

		assertEquals(10.45, sCopy.getAvGain(), Settings.doubleEpsilon);
		assertEquals(62.13, sCopy.getAvWinAvLoss(), Settings.doubleEpsilon);
		assertEquals(16, sCopy.getPeriod(), Settings.doubleEpsilon);

	}
}
