package stsc.algorithms.stock.indices.adx;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.ListOfDoubleAdapter;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AdxWithSmaDi extends StockAlgorithm {

	private final Integer N;

	private final String adxDiName;
	private final AdxDi adxDi;

	private final String adxDiMinusAdapterName;
	private final ListOfDoubleAdapter adxDiMinusListOfDoubleAdapter;

	private final String adxDiPlusAdapterName;
	private final ListOfDoubleAdapter adxDiPlusListOfDoubleAdapter;

	private final String adxSmaDiMinusName;
	private final Sma adxSmaDiMinus;
	private final String adxSmaDiPlusName;
	private final Sma adxSmaDiPlus;

	public AdxWithSmaDi(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 14).getValue();
		this.adxDiName = init.getExecutionName() + "_AdxDi";
		this.adxDi = new AdxDi(new StockAlgorithmInit(adxDiName, init, init.getSettings()));

		this.adxDiMinusAdapterName = adxDiName + "_MinusAdapter";
		this.adxDiMinusListOfDoubleAdapter = createAdapter(init, adxDiMinusAdapterName, 0);
		this.adxDiPlusAdapterName = adxDiName + "_PlusAdapter";
		this.adxDiPlusListOfDoubleAdapter = createAdapter(init, adxDiPlusAdapterName, 1);

		this.adxSmaDiMinusName = adxDiMinusAdapterName + "_Sma";
		this.adxSmaDiMinus = createSma(init, adxSmaDiMinusName, adxDiMinusAdapterName);
		this.adxSmaDiPlusName = adxDiPlusAdapterName + "_Sma";
		this.adxSmaDiPlus = createSma(init, adxSmaDiPlusName, adxDiPlusAdapterName);
	}

	private Sma createSma(StockAlgorithmInit init, String adxSmaDiName, String adxDiAdapterName) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", N);
		settings.addSubExecutionName(adxDiAdapterName);
		final StockAlgorithmInit smaInit = new StockAlgorithmInit(adxSmaDiName, init, settings);
		return new Sma(smaInit);
	}

	private ListOfDoubleAdapter createAdapter(StockAlgorithmInit init, String adxDiAdapterName, int i) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("I", i);
		settings.addSubExecutionName(adxDiName);
		final StockAlgorithmInit adapterInit = new StockAlgorithmInit(adxDiAdapterName, init, settings);
		return new ListOfDoubleAdapter(adapterInit);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(ListOfDoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		adxDi.process(day);
		adxDiMinusListOfDoubleAdapter.process(day);
		adxDiPlusListOfDoubleAdapter.process(day);
		adxSmaDiMinus.process(day);
		adxSmaDiPlus.process(day);
		final double smaMinus = getSignal(adxSmaDiMinusName, day.getDate()).getSignal(DoubleSignal.class).value;
		final double smaPlus = getSignal(adxSmaDiPlusName, day.getDate()).getSignal(DoubleSignal.class).value;
		addSignal(day.getDate(), new ListOfDoubleSignal().add(smaMinus).add(smaPlus));
	}
}
