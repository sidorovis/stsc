package stsc.frontend.zozka.gui.models;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;
import stsc.general.simulator.multistarter.AlgorithmParameters;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.genetic.AlgorithmSettingsGeneticList;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.grid.AlgorithmSettingsGridIterator;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;

public final class ExecutionDescription {

	private AlgorithmType algorithmType;
	private String executionName;
	private String algorithmName;

	private final ObservableList<NumberAlgorithmParameter> numberAlgorithms;
	private final ObservableList<TextAlgorithmParameter> textAlgorithms;

	public ExecutionDescription(AlgorithmType algorithmType, String executionName, String algorithmName) {
		this.algorithmType = algorithmType;
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		this.numberAlgorithms = FXCollections.observableArrayList();
		this.textAlgorithms = FXCollections.observableArrayList();
	}

	public String getExecutionName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public ObservableList<NumberAlgorithmParameter> getNumberAlgorithms() {
		return numberAlgorithms;
	}

	public ObservableList<TextAlgorithmParameter> getTextAlgorithms() {
		return textAlgorithms;
	}

	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}

	public void setExecutionName(String executionName) {
		this.executionName = executionName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public GridExecutionInitializer createGridExecution(FromToPeriod period) throws BadParameterException {
		final AlgorithmSettingsGridIterator settings = new AlgorithmSettingsGridIterator(period, false, generateParameters());
		return new GridExecutionInitializer(executionName, algorithmName, settings);
	}

	public GeneticExecutionInitializer createGeneticExecution(FromToPeriod period) throws BadParameterException {
		final AlgorithmSettingsGeneticList settings = new AlgorithmSettingsGeneticList(period, generateParameters());
		return new GeneticExecutionInitializer(executionName, algorithmName, settings);
	}

	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}

	private AlgorithmParameters generateParameters() throws BadParameterException {
		final AlgorithmParameters parameters = new AlgorithmParameters();
		fillNumberParameters(parameters);
		fillTextParameters(parameters);
		return parameters;
	}

	private void fillTextParameters(AlgorithmParameters parameters) throws BadParameterException {
		for (TextAlgorithmParameter p : textAlgorithms) {
			if (p.getType().equals(ParameterType.STRING)) {
				final String name = p.parameterNameProperty().get();
				final List<String> domen = TextAlgorithmParameter.createDomenRepresentation(p.domenProperty().get());
				parameters.getStrings().add(new MpString(name, domen));
			} else if (p.getType().equals(ParameterType.SUB_EXECUTION)) {
				final String name = p.parameterNameProperty().get();
				final List<String> domen = TextAlgorithmParameter.createDomenRepresentation(p.domenProperty().get());
				parameters.getSubExecutions().add(new MpSubExecution(name, domen));
			}
		}
	}

	private void fillNumberParameters(AlgorithmParameters parameters) throws BadParameterException {
		for (NumberAlgorithmParameter p : numberAlgorithms) {
			if (p.getType().equals(ParameterType.INTEGER)) {
				final String name = p.parameterNameProperty().get();
				final Integer from = Integer.valueOf(p.getFrom());
				final Integer to = Integer.valueOf(p.getTo());
				final Integer step = Integer.valueOf(p.getStep());
				parameters.getIntegers().add(new MpInteger(name, from, to, step));
			} else if (p.getType().equals(ParameterType.DOUBLE)) {
				final String name = p.parameterNameProperty().get();
				final Double from = Double.valueOf(p.getFrom());
				final Double to = Double.valueOf(p.getTo());
				final Double step = Double.valueOf(p.getStep());
				parameters.getDoubles().add(new MpDouble(name, from, to, step));
			}
		}
	}

	@Override
	public String toString() {
		return String.valueOf(executionName) + " (" + String.valueOf(algorithmName) + ")";
	}

}
