package stsc.distributed.hadoop.grid;

import org.apache.hadoop.fs.Path;

import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.statistic.cost.function.CostWeightedProductFunction;

public class HadoopSettings {

	private static HadoopSettings hadoopSettings = new HadoopSettings();

	public boolean copyOriginalDatafeedPath = true;
	public String originalDatafeedPath = "./test_data/";
	public String datafeedHdfsPath = "./yahoo_datafeed/";

	public String outputPathOnHdfs = "./output_data/";
	public String outputPathOnLocal = "./";
	public String outputFileName = "output.txt";

	public int inputSplitSize = 1;

	public String[] inputSplitLocations = new String[] { "this" };

	public CostFunction costFunction;
	public StrategySelector strategySelector;

	public boolean copyAnswerToLocal = true;

	private HadoopSettings() {
		costFunction = generateDefaultCostFunction();
		strategySelector = new StatisticsByCostSelector(150, costFunction);
	}

	private CostFunction generateDefaultCostFunction() {
		final CostWeightedProductFunction cf = new CostWeightedProductFunction();
		cf.addParameter("getWinProb", 2.5);
		cf.addParameter("getAvLoss", -1.0);
		cf.addParameter("getAvWin", 1.0);
		cf.addParameter("getStartMonthAvGain", 1.2);
		cf.addParameter("ddDurationAvGain", -1.2);
		cf.addParameter("ddValueAvGain", -1.2);
		return cf;
	}

	public Path getHadoopHdfsPath() {
		return new Path(datafeedHdfsPath);
	}

	public Path getHdfsOutputPath() {
		return new Path(outputPathOnHdfs + outputFileName);
	}

	public Path getLocalOutputPath() {
		return new Path(outputPathOnLocal + outputFileName);
	}

	public static HadoopSettings getInstance() {
		return hadoopSettings;
	}

}
