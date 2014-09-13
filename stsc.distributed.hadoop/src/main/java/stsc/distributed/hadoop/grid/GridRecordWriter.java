package stsc.distributed.hadoop.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.general.strategy.TradingStrategy;

public class GridRecordWriter extends RecordWriter<SimulatorSettingsWritable, StatisticsWritable> {

	private final StockStorage stockStorage = HadoopStaticDataSingleton.getStockStorage();
	private final List<TradingStrategy> tradingStrategies = Collections.synchronizedList(new ArrayList<TradingStrategy>());

	@Override
	public void write(SimulatorSettingsWritable key, StatisticsWritable value) throws IOException, InterruptedException {
		try {
			tradingStrategies.add(new TradingStrategy(key.getSimulatorSettings(stockStorage), value.getStatistics()));
		} catch (BadAlgorithmException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void close(TaskAttemptContext context) throws IOException, InterruptedException {
		final Path file = new Path("/user/vagrant/output.txt");
		final FileSystem fs = file.getFileSystem(context.getConfiguration());
		if (fs.isDirectory(file)) {
			fs.delete(file, true);
		}
		if (fs.isFile(file)) {
			fs.delete(file, true);
		}
		final FSDataOutputStream fileOut = fs.create(file, false);
		fileOut.writeUTF(String.valueOf(tradingStrategies.size()) + "\n");
		for (TradingStrategy ts : tradingStrategies) {
			fileOut.writeUTF(ts.getSettings().getId() + "\n");
			fileOut.writeUTF(ts.getSettings().toString());
			fileOut.writeUTF(ts.getStatistics().toString());
		}
		fileOut.close();
	}

}
