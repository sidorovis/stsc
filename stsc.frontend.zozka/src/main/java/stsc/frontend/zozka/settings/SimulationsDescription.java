package stsc.frontend.zozka.settings;

import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

public class SimulationsDescription {

	private String datafeedPath = "./";
	private StockStorage stockStorage;
	private FromToPeriod period;

	private ObservableList<ExecutionDescription> executionDescriptions = FXCollections.observableArrayList();

	public ObservableList<ExecutionDescription> getExecutionDescriptions() {
		return executionDescriptions;
	}

	public String getDatafeedPath() {
		return datafeedPath;
	}

	public void setDatafeedPath(String datafeed) {
		datafeedPath = datafeed;
	}

	public void setPeriod(Date from, Date to) {
		period = new FromToPeriod(from, to);
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public void setStockStorage(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	public SimulatorSettingsGridList getGrid() {
		final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(stockStorage, period);
		for (ExecutionDescription ed : executionDescriptions) {
			GridExecutionInitializer ei = ed.createGridExecution(period);
			if (ed.isStockAlgorithm()) {
				factory.addStock(ei);
			} else {
				factory.addEod(ei);
			}
		}
		return factory.getList();
	}

	public SimulatorSettingsGeneticList getGenetic() {
		final SimulatorSettingsGeneticFactory factory = new SimulatorSettingsGeneticFactory(stockStorage, period);
		for (ExecutionDescription ed : executionDescriptions) {
			GeneticExecutionInitializer ei = ed.createGeneticExecution(period);
			if (ed.isStockAlgorithm()) {
				factory.addStock(ei);
			} else {
				factory.addEod(ei);
			}
		}
		return factory.getList();
	}
}
