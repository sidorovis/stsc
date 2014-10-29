package stsc.frontend.zozka.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.gui.models.ExecutionDescription;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

public class SimulatorSettingsModel {

	private final ObservableList<ExecutionDescription> model;

	public SimulatorSettingsModel() {
		model = FXCollections.observableArrayList();
	}

	public ObservableList<ExecutionDescription> getModel() {
		return model;
	}

	public boolean isEmpty() {
		return model.isEmpty();
	}

	public int size() {
		return model.size();
	}

	public void clear() {
		model.clear();
	}

	public void add(ExecutionDescription ed) {
		model.add(ed);
	}

	public void saveToFile(File f) throws FileNotFoundException, IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f))) {
			os.writeInt(model.size());
			for (ExecutionDescription executionDescription : model) {
				executionDescription.writeExternal(os);
			}
		}
	}

	public void set(int index, ExecutionDescription newEd) {
		model.set(index, newEd);
	}

	public void loadFromFile(File f) throws FileNotFoundException, IOException, ClassNotFoundException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f))) {
			final int size = is.readInt();
			model.clear();
			for (int i = 0; i < size; ++i) {
				final ExecutionDescription ed = ExecutionDescription.createForLoadFromFile();
				ed.readExternal(is);
				model.add(ed);
			}
		}
	}

	public SimulatorSettingsGridList generateGridSettings(StockStorage stockStorage, FromToPeriod period) throws BadParameterException {
		final List<GridExecutionInitializer> stocks = new ArrayList<>();
		final List<GridExecutionInitializer> eods = new ArrayList<>();
		for (ExecutionDescription executionDescription : model) {
			if (executionDescription.getAlgorithmType().isStock()) {
				stocks.add(executionDescription.createGridExecution(period));
			} else {
				eods.add(executionDescription.createGridExecution(period));
			}
		}
		return new SimulatorSettingsGridList(stockStorage, period, stocks, eods, false);
	}

	public SimulatorSettingsGeneticList generateGeneticSettings(StockStorage stockStorage, FromToPeriod period)
			throws BadParameterException {
		final List<GeneticExecutionInitializer> stocks = new ArrayList<>();
		final List<GeneticExecutionInitializer> eods = new ArrayList<>();
		for (ExecutionDescription executionDescription : model) {
			if (executionDescription.getAlgorithmType().isStock()) {
				stocks.add(executionDescription.createGeneticExecution(period));
			} else {
				eods.add(executionDescription.createGeneticExecution(period));
			}
		}
		return new SimulatorSettingsGeneticList(stockStorage, period, stocks, eods);
	}
}
