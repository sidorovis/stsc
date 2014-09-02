package stsc.distributed.hadoop.types;

import java.util.List;

import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

public class SimulatorSettingsGridListWritable extends MapEasyWritable {

	private static final String PERIOD_FROM = "periodFrom";
	private static final String PERIOD_TO = "periodTo";

	private static final String STOCK_PREFIX = "stockInit.";
	private static final String EOD_PREFIX = "eodInit.";

	private static final String INITIALIZERS_SIZE = "size";

	private static final String EXEC_NAME = "exec";
	private static final String ALGO_NAME = "algo";

	protected SimulatorSettingsGridListWritable() {
	}

	// List -> Writable
	public SimulatorSettingsGridListWritable(final SimulatorSettingsGridList list) {
		this();
		saveList(list);
	}

	private void saveList(SimulatorSettingsGridList list) {
		longs.put(PERIOD_FROM, list.getPeriod().getFrom().getTime());
		longs.put(PERIOD_TO, list.getPeriod().getTo().getTime());
		saveExecutionList(list.getStockInitializers(), STOCK_PREFIX);
		saveExecutionList(list.getEodInitializers(), EOD_PREFIX);
	}

	private void saveExecutionList(List<GridExecutionInitializer> initializers, String prefix) {
		integers.put(prefix + INITIALIZERS_SIZE, initializers.size());
		for (int i = 0; i < initializers.size(); ++i) {
			saveExecutionInitializer(initializers.get(i), prefix, i);
		}
	}

	private void saveExecutionInitializer(GridExecutionInitializer initializer, String prefix, int index) {
		final String prefixExec = prefix + String.valueOf(index) + "." + EXEC_NAME;
		strings.put(prefixExec, initializer.algorithmName);
		strings.put(prefix + String.valueOf(index) + "." + ALGO_NAME, initializer.algorithmName);
		saveParameters(prefixExec, initializer.iterator.getParameters());
	}

}
