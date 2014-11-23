package stsc.distributed.hadoop.grid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.strategy.TradingStrategy;

//@formatter:off
/**
 * 1) Copy Datafeed from local to hdfs (<local>/"./test_data/" -> <hdfs>/"./yahoo_datafeed/");
 * 2) Start separated tasks.
 * 3) Load results from Hdfs.
 */
//@formatter:on

public class GridHadoopStarter extends Configured implements Tool, HadoopStarter {

	private final List<TradingStrategy> tradingStrategies = new ArrayList<TradingStrategy>();

	@Override
	public List<TradingStrategy> searchOnHadoop() throws Exception {
		String[] args = new String[0];
		ToolRunner.run(this, args);
		return tradingStrategies;
	}

	@Override
	public int run(String[] args) throws Exception {
		final Job job = Job.getInstance(this.getConf());
		final HadoopSettings hs = HadoopSettings.getInstance();
		if (hs.copyOriginalDatafeedPath) {
			checkAndCopyDatafeed(hs.originalDatafeedPath, hs.getHadoopDatafeedHdfsPath());
		}
		hs.getStockStorage(FileSystem.get(this.getConf()), hs.getHadoopDatafeedHdfsPath());
		job.setJobName(GridHadoopStarter.class.getSimpleName());
		job.setJarByClass(GridHadoopStarter.class);

		job.setMapperClass(SimulatorMapper.class);
		job.setReducerClass(SimulatorReducer.class);

		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(TradingStrategyWritable.class);

		job.setOutputKeyClass(SimulatorSettingsWritable.class);
		job.setOutputValueClass(StatisticsWritable.class);

		job.setInputFormatClass(GridInputFormat.class);
		job.setOutputFormatClass(GridOutputFormat.class);

		job.waitForCompletion(true);
		loadTradingStrategies();
		if (hs.copyAnswerToLocal) {
			copyAnswerToLocal();
		}
		return 0;
	}

	private void loadTradingStrategies() throws IOException, BadAlgorithmException {
		final FileSystem hdfs = FileSystem.get(this.getConf());
		final HadoopSettings hs = HadoopSettings.getInstance();
		final Path out = hs.getHdfsOutputPath();
		if (hdfs.exists(out)) {
			final FSDataInputStream fileIn = hdfs.open(out);
			final int size = fileIn.readInt();
			for (int i = 0; i < size; ++i) {
				final TradingStrategyWritable tsw = new TradingStrategyWritable();
				tsw.readFields(fileIn);
				tradingStrategies.add(tsw.getTradingStrategy(hs.getStockStorage()));
			}
		}
	}

	private void checkAndCopyDatafeed(String localPath, Path path) throws IOException {
		final FileSystem hdfs = FileSystem.get(this.getConf());
		if (!hdfs.exists(path)) {
			hdfs.mkdirs(path);
			File folder = new File(localPath);
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				hdfs.copyFromLocalFile(new Path(file.getPath()), new Path(path + "/" + file.getName()));
			}
		}
	}

	private void copyAnswerToLocal() throws IllegalArgumentException, IOException {
		final FileSystem hdfs = FileSystem.get(this.getConf());
		final Path out = HadoopSettings.getInstance().getHdfsOutputPath();
		final Path localOut = HadoopSettings.getInstance().getLocalOutputPath();
		if (hdfs.exists(out)) {
			hdfs.copyToLocalFile(true, out, localOut, true);
		}
	}
}
