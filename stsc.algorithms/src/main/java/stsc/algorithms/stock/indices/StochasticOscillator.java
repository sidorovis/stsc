package stsc.algorithms.stock.indices;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.Input;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class StochasticOscillator extends StockAlgorithm {

	private final String lnInputName;
	private final Input lnInput;

	private final String lnName;
	private final MinForNDays ln;

	private final String hnInputName;
	private final Input hnInput;

	private final String hnName;
	private final MaxForNDays hn;

	public StochasticOscillator(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);

		this.lnInputName = init.getExecutionName() + "_LnInput";
		this.lnInput = createLnInput(init);

		this.lnName = init.getExecutionName() + "_Ln";
		this.ln = createLn(init);

		this.hnInputName = init.getExecutionName() + "_HnInput";
		this.hnInput = createHnInput(init);

		this.hnName = init.getExecutionName() + "_Hn";
		this.hn = createHn(init);
	}

	private Input createLnInput(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setString("e", "low");
		return new Input(init.createInit(lnInputName, settings));
	}

	private MinForNDays createLn(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("P", init.getSettings().getIntegerSetting("P", 5).getValue());
		settings.setInteger("SP", init.getSettings().getIntegerSetting("SP", 0).getValue());
		settings.addSubExecutionName(lnInputName);
		return new MinForNDays(init.createInit(lnName, settings));
	}

	private Input createHnInput(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setString("e", "high");
		return new Input(init.createInit(hnInputName, settings));
	}

	private MaxForNDays createHn(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("P", init.getSettings().getIntegerSetting("P", 5).getValue());
		settings.setInteger("SP", init.getSettings().getIntegerSetting("SP", 0).getValue());
		settings.addSubExecutionName(hnInputName);
		return new MaxForNDays(init.createInit(hnName, settings));
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		lnInput.process(day);
		ln.process(day);
		hnInput.process(day);
		hn.process(day);
		final double ctValue = day.getPrices().getClose();
		final double lnValue = getSignal(lnName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		final double hnValue = getSignal(hnName, day.getDate()).getSignal(DoubleSignal.class).getValue();

		if (Double.compare(hnValue, lnValue) == 0) {
			addSignal(day.getDate(), new DoubleSignal(50.0));
		} else {
			addSignal(day.getDate(), new DoubleSignal(100.0 * (ctValue - lnValue) / (hnValue - lnValue)));
		}
	}

}
