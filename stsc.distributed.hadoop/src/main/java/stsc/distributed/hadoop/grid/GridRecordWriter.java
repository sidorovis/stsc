package stsc.distributed.hadoop.grid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.mapred.FileOutputCommitter;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.general.strategy.TradingStrategy;

class GridRecordWriter extends RecordWriter<SimulatorSettingsWritable, StatisticsWritable> {

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
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
			bw.append(String.valueOf(tradingStrategies.size()) + "\n");
			for (TradingStrategy ts : tradingStrategies) {
				bw.append(ts.getSettings().getId() + "\n");
				bw.append(ts.getSettings().toString());
				bw.append(ts.getStatistics().toString());
			}
		}
	}

}

class GridOutputFormat extends OutputFormat<SimulatorSettingsWritable, StatisticsWritable> {

	@Override
	public RecordWriter<SimulatorSettingsWritable, StatisticsWritable> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new GridRecordWriter();
	}

	@Override
	public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new FileOutputCommitter();
	}

}
